package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    var completo by remember { mutableStateOf(false) }
    val meta = 20

    fun reiniciar() {
        altura = 300f; velocidad = 0f; toques = 0; caido = false; completo = false
    }

    LaunchedEffect(caido) {
        var anterior = withFrameNanos { it }
        while (!caido && !completo) {
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
        consigna = when { completo -> "¡Mantuviste el globo en el aire!"; caido -> "¡Se cayó! Intenta de nuevo"; else -> "Toca solamente el globo: $toques / $meta" },
        onVolver = onVolver,
        acciones = if (caido || completo) ({ Button(onClick = ::reiniciar) { Text("Reintentar") } }) else null,
    ) {
        Box(
            Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFDCEEFA), Color(0xFFF3FAFF)))),
        ) {
            Box(
                Modifier.offset(y = altura.dp).clickable(enabled = !caido && !completo) {
                    services.sound.tocar(Efecto.CLICK)
                    velocidad = IMPULSO
                    toques++
                    if (toques >= meta) {
                        completo = true
                        services.sound.tocar(Efecto.WIN)
                        scope.launch { services.progress.completarNivel(juego.id, 1) }
                    }
                },
            ) { Text("🎈", fontSize = 56.sp) }
        }
    }
}
