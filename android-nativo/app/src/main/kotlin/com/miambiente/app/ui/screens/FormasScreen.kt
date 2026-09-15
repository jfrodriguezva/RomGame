package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val FIGURAS = listOf(
    ItemQuiz("circulo", "circulo", "Círculo"),
    ItemQuiz("cuadrado", "cuadrado", "Cuadrado"),
    ItemQuiz("triangulo", "triangulo", "Triángulo"),
)

/** Gabinete de figuras — patrón MaterialQuiz. */
@Composable
fun FormasScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("formas")!!,
        items = FIGURAS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3)) },
        render = { id, _ -> FormaVisual(id, tamano = 120.dp) },
        onVolver = onVolver,
    )
}

@Composable
private fun FormaVisual(id: String, tamano: androidx.compose.ui.unit.Dp) {
    when (id) {
        "circulo" -> Box(Modifier.size(tamano).clip(CircleShape).background(Color(0xFFF3DBE3)))
        "cuadrado" -> Box(Modifier.size(tamano).clip(RoundedCornerShape(4.dp)).background(Color(0xFFF3DBE3)))
        else -> Canvas(modifier = Modifier.size(tamano)) {
            val camino = Path().apply {
                moveTo(size.width / 2, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(camino, color = Color(0xFFF3DBE3))
        }
    }
}
