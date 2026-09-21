package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val COLORES = listOf(
    ItemQuiz(Color(0xFFD9433A), "rojo", "Rojo"),
    ItemQuiz(Color(0xFF3E7AA3), "azul", "Azul"),
    ItemQuiz(Color(0xFFE0C23C), "amarillo", "Amarillo"),
    ItemQuiz(Color(0xFF4C7A3A), "verde", "Verde"),
    ItemQuiz(Color(0xFFE08A3A), "naranja", "Naranja"),
    ItemQuiz(Color(0xFF7A4FA3), "morado", "Morado"),
)

/** Los colores — patrón MaterialQuiz, tabletas de color reales en vez de nombres. */
@Composable
fun ColoresScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("colores")!!,
        items = COLORES,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6)) },
        render = { color, grande ->
            val lado = if (grande) 120.dp else 56.dp
            Box(Modifier.size(lado).clip(RoundedCornerShape(12.dp)).background(color))
        },
        onVolver = onVolver,
    )
}
