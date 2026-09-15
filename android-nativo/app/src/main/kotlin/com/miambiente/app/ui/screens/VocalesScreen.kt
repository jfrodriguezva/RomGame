package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val VOCALES = listOf(
    ItemQuiz("🌳 Árbol", "a", "A"), ItemQuiz("🐘 Elefante", "e", "E"),
    ItemQuiz("🧊 Iglú", "i", "I"), ItemQuiz("🐻 Oso", "o", "O"),
    ItemQuiz("🍇 Uvas", "u", "U"),
)

/** Las vocales — patrón MaterialQuiz (¿con cuál empieza?). */
@Composable
fun VocalesScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("vocales")!!,
        items = VOCALES,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 5)) },
        render = { texto, grande -> Text(texto, fontSize = if (grande) 32.sp else 18.sp) },
        pregunta = "¿Con qué vocal empieza?",
        onVolver = onVolver,
    )
}
