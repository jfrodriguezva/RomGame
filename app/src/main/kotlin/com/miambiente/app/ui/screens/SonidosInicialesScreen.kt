package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val PALABRAS = listOf(
    ItemQuiz("🐘", "elefante", "Elefante"), ItemQuiz("🏠", "casa", "Casa"),
    ItemQuiz("☀️", "sol", "Sol"), ItemQuiz("🐟", "pez", "Pez"),
    ItemQuiz("🌙", "luna", "Luna"), ItemQuiz("🎈", "globo", "Globo"),
)

/** Veo veo — patrón MaterialQuiz (conciencia fonológica, simplificado a nomenclatura). */
@Composable
fun SonidosInicialesScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("sonidos-iniciales")!!,
        items = PALABRAS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        pregunta = "¿Cómo se llama esto?",
        onVolver = onVolver,
    )
}
