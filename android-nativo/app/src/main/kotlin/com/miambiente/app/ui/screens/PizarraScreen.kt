package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

private val COLORES = listOf(
    Color(0xFF3F342C), Color(0xFFD9433A), Color(0xFF3E7AA3), Color(0xFF4C7A3A),
    Color(0xFFE0C23C), Color(0xFF7A4FA3), Color(0xFFE08A3A),
)

private data class Trazo(val puntos: List<Offset>, val color: Color)

/** La pizarra grande — trazo libre real con Canvas nativo, sin niveles ni puntaje. */
@Composable
fun PizarraScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("pizarra")!!
    var trazos by remember { mutableStateOf(listOf<Trazo>()) }
    var actual by remember { mutableStateOf<Trazo?>(null) }
    var colorActivo by remember { mutableStateOf(COLORES[0]) }

    GameShell(
        juego = juego,
        consigna = "Dibuja lo que quieras",
        onVolver = onVolver,
        acciones = { TextButton(onClick = { trazos = emptyList() }) { Text("Borrar todo") } },
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
            ) {
                COLORES.forEach { color ->
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { colorActivo = color },
                    )
                }
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .pointerInput(colorActivo) {
                        detectDragGestures(
                            onDragStart = { punto -> actual = Trazo(listOf(punto), colorActivo) },
                            onDragEnd = {
                                actual?.let { trazos = trazos + it }
                                actual = null
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                actual = actual?.let { it.copy(puntos = it.puntos + change.position) }
                            },
                        )
                    },
            ) {
                (trazos + listOfNotNull(actual)).forEach { trazo ->
                    for (i in 0 until trazo.puntos.size - 1) {
                        drawLine(trazo.color, trazo.puntos[i], trazo.puntos[i + 1], strokeWidth = 12f, cap = StrokeCap.Round)
                    }
                }
            }
        }
    }
}
