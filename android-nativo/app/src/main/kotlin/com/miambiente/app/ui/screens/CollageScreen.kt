package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

private val ESTAMPAS = listOf("🌸", "⭐", "🦋", "🐶", "🚗", "🎈", "🌈", "☀️")

private data class Estampa(val id: Int, val emoji: String, var pos: Offset)

/** Collage libre — arrastrar estampas donde quieras, sin niveles ni puntaje. */
@Composable
fun CollageScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("collage")!!
    var colocadas by remember { mutableStateOf(listOf<Estampa>()) }
    var siguienteId by remember { mutableStateOf(0) }

    GameShell(
        juego = juego,
        consigna = "Toca una estampa y luego el lienzo",
        onVolver = onVolver,
        acciones = { TextButton(onClick = { colocadas = emptyList() }) { Text("Borrar todo") } },
    ) {
        Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color(0xFFFDFAF5))) {
            colocadas.forEach { estampa ->
                Box(
                    modifier = Modifier
                        .graphicsLayer { translationX = estampa.pos.x; translationY = estampa.pos.y }
                        .pointerInput(estampa.id) {
                            detectDragGestures { change, arrastre ->
                                change.consume()
                                colocadas = colocadas.map {
                                    if (it.id == estampa.id) it.copy(pos = it.pos + arrastre) else it
                                }
                            }
                        },
                ) { Text(estampa.emoji, fontSize = 36.sp) }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.92f))
                    .padding(6.dp),
            ) {
                ESTAMPAS.forEach { emoji ->
                    Text(
                        emoji,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(6.dp).pointerInput(emoji) {
                            detectTapGestures {
                                colocadas = colocadas + Estampa(siguienteId, emoji, Offset(200f, 400f))
                                siguienteId++
                            }
                        },
                    )
                }
            }
        }
    }
}
