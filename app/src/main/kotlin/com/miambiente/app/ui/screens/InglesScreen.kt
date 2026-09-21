package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val PALABRAS = listOf(
    ItemQuiz("🐕", "dog", "Dog"), ItemQuiz("🐈", "cat", "Cat"),
    ItemQuiz("☀️", "sun", "Sun"), ItemQuiz("🌙", "moon", "Moon"),
    ItemQuiz("💧", "water", "Water"), ItemQuiz("🍎", "apple", "Apple"),
    ItemQuiz("🏠", "house", "House"), ItemQuiz("📖", "book", "Book"),
)

/** Primeras palabras en inglés — patrón MaterialQuiz. */
@Composable
fun InglesScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("ingles")!!,
        items = PALABRAS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        pregunta = "How do you say this in English?",
        onVolver = onVolver,
    )
}
