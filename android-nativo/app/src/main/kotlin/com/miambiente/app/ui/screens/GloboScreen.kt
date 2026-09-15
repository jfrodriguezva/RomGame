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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** El globo volador — no dejes que caiga (ritmo y constancia del toque). */
@Composable
fun GloboScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("globo")!!

    var altura by remember { mutableStateOf(300f) }
    var toques by remember { mutableStateOf(0) }
    var caido by remember { mutableStateOf(false) }
    val meta = 20

    LaunchedEffect(caido) {
        while (!caido) {
            delay(200)
            altura += 18f
            if (altura > 580f) caido = true
        }
    }

    fun reiniciar() {
        altura = 300f
        toques = 0
        caido = false
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
                            altura = (altura - 60f).coerceAtLeast(40f)
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
