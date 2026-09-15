package com.miambiente.app.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

private const val GRAVEDAD = 220f // dp/s²
private const val IMPULSO = -340f // dp/s al tocar

/**
 * El globo volador — física real (gravedad + impulso), no incrementos
 * fijos por tic: cada cuadro se mide el tiempo real transcurrido
 * (`withFrameNanos`, sincronizado con la pantalla) y se integra
 * velocidad y posición como un juego de verdad, no una animación por
 * pasos discretos.
 */
@Composable
fun GloboScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("globo")!!

    var altura by remember { mutableStateOf(300f) }
    var velocidad by remember { mutableStateOf(0f) }
    var toques by remember { mutableStateOf(0) }
    var caido by remember { mutableStateOf(false) }
    val meta = 20

    fun reiniciar() {
        altura = 300f; velocidad = 0f; toques = 0; caido = false
    }

    LaunchedEffect(caido) {
        var anterior = withFrameNanos { it }
        while (!caido) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.05f)
            anterior = ahora
            velocidad += GRAVEDAD * dt
            altura += velocidad * dt
            if (altura < 20f) { altura = 20f; velocidad = 0f }
            if (altura > 580f) caido = true
        }
    }

    GameShell(
        juego = juego,
        consigna = if (caido) "¡Se cayó! Toca para intentar de nuevo" else "Toca el globo: $toques / $meta",
        onVolver = onVolver,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(caido) {
                    detectTapGestures {
                        if (caido) {
                            reiniciar()
                        } else {
                            services.sound.tocar(Efecto.CLICK)
                            velocidad = IMPULSO
                            toques++
                            if (toques >= meta) {
                                services.sound.tocar(Efecto.WIN)
                                scope.launch { services.progress.completarNivel(juego.id, 1) }
                                reiniciar()
                            }
                        }
                    }
                },
        ) {
            Box(Modifier.offset(y = altura.dp)) { Text("🎈", fontSize = 56.sp) }
        }
    }
}
