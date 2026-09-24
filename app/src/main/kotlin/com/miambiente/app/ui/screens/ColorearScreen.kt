package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

private val PALETA = listOf(
    Color(0xFFD9433A), Color(0xFF3E7AA3), Color(0xFF4C7A3A), Color(0xFFE0C23C), Color(0xFF7A4FA3), Color(0xFFE08A3A),
)

/** Colorear — cinco pétalos que se rellenan al tocarlos, sin niveles ni puntaje. */
@Composable
fun ColorearScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("colorear")!!
    var colorActivo by remember { mutableStateOf(PALETA[0]) }
    var colores by remember { mutableStateOf(List(6) { Color(0xFFFDFAF5) }) }

    GameShell(juego = juego, consigna = "Elige un color y toca el dibujo", onVolver = onVolver) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                PALETA.forEach { color ->
                    val activo = color == colorActivo
                    Box(
                        Modifier
                            .size(if (activo) 40.dp else 32.dp)
                            .shadow(if (activo) 6.dp else 2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .then(if (activo) Modifier.border(3.dp, Color(0xFF3F342C), CircleShape) else Modifier)
                            .clickable { colorActivo = color },
                    )
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .size(260.dp)
                        .pointerInput(colorActivo, colores) {
                            detectTapGestures { punto: Offset ->
                                val centro = Offset(size.width / 2f, size.height / 2f)
                                val vector = punto - centro
                                val distancia = kotlin.math.hypot(vector.x, vector.y)
                                if (distancia < 40f) {
                                    colores = colores.toMutableList().also { it[0] = colorActivo }
                                } else {
                                    val angulo = (Math.toDegrees(kotlin.math.atan2(vector.y, vector.x).toDouble()) + 360) % 360
                                    val petalo = (angulo / 72).toInt() + 1
                                    colores = colores.toMutableList().also { it[petalo] = colorActivo }
                                }
                            }
                        },
                ) {
                    val centro = Offset(size.width / 2, size.height / 2)
                    val radioPetalo = size.minDimension * 0.28f
                    for (i in 0 until 5) {
                        val angulo = Math.toRadians(i * 72.0)
                        val posPetalo = Offset(
                            centro.x + (size.minDimension * 0.32f * kotlin.math.cos(angulo)).toFloat(),
                            centro.y + (size.minDimension * 0.32f * kotlin.math.sin(angulo)).toFloat(),
                        )
                        drawCircle(colores[i + 1], radioPetalo, posPetalo)
                        drawCircle(Color(0xFF3F342C), radioPetalo, posPetalo, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                    }
                    drawCircle(colores[0], size.minDimension * 0.18f, centro)
                    drawCircle(Color(0xFF3F342C), size.minDimension * 0.18f, centro, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                }
            }
        }
    }
}
