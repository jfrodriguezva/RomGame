package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val LETRAS = listOf(
    ItemQuiz("🌳", "a", "A de árbol"), ItemQuiz("⚽", "b", "B de balón"),
    ItemQuiz("🏠", "c", "C de casa"), ItemQuiz("🎲", "d", "D de dado"),
    ItemQuiz("🐘", "e", "E de elefante"), ItemQuiz("🌙", "l", "L de luna"),
    ItemQuiz("✋", "m", "M de mano"), ItemQuiz("⚽", "p", "P de pelota"),
    ItemQuiz("☀️", "s", "S de sol"), ItemQuiz("🚂", "t", "T de tren"),
)

/** El abecedario — patrón MaterialQuiz (letra ↔ palabra). */
@Composable
fun AbecedarioScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("abecedario")!!,
        items = LETRAS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        pregunta = "¿Con qué letra empieza?",
        onVolver = onVolver,
    )
}
