package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val ANIMALES = listOf(
    ItemQuiz("🐄", "vaca", "Vaca"), ItemQuiz("🐶", "perro", "Perro"),
    ItemQuiz("🐱", "gato", "Gato"), ItemQuiz("🦆", "pato", "Pato"),
    ItemQuiz("🐑", "oveja", "Oveja"), ItemQuiz("🐷", "cerdo", "Cerdo"),
)

/** Sonidos de animales — patrón MaterialQuiz. */
@Composable
fun AnimalesScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("animales")!!,
        items = ANIMALES,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        onVolver = onVolver,
    )
}
