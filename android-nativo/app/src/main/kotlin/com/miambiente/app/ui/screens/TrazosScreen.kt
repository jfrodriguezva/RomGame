package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Trazos previos — sigue la línea punteada con el dedo (dibujo real sobre Canvas). */
@Composable
fun TrazosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("trazos")!!

    var trazo by remember { mutableStateOf(listOf<Offset>()) }
    var completado by remember { mutableStateOf(false) }

    fun evaluar() {
        val ultimo = trazo.lastOrNull() ?: return
        if (ultimo.x > 850f && !completado) {
            completado = true
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch { delay(1200); trazo = emptyList(); completado = false }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completado) "¡Bien trazado!" else "Sigue la línea punteada con el dedo",
        onVolver = onVolver,
        acciones = { Button(onClick = { trazo = emptyList() }) { Text("Borrar") } },
    ) {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                change.consume()
                                trazo = trazo + change.position
                                evaluar()
                            },
                        )
                    },
            ) {
                val y = size.height / 2
                drawLine(
                    color = Color(0xFFA39A8C),
                    start = Offset(40f, y),
                    end = Offset(size.width - 40f, y),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f)),
                )
                // Marca de inicio y meta: antes la línea punteada no decía
                // dónde empezar ni cuánto faltaba para terminar.
                drawCircle(Color(0xFF8BBF6A), radius = 18f, center = Offset(40f, y))
                drawCircle(Color(0xFFE0C23C), radius = 22f, center = Offset(850f, y), style = Stroke(width = 6f))
                for (i in 0 until trazo.size - 1) {
                    drawLine(Color(0xFF3E7AA3), trazo[i], trazo[i + 1], strokeWidth = 14f, cap = StrokeCap.Round)
                }
            }
        }
    }
}
