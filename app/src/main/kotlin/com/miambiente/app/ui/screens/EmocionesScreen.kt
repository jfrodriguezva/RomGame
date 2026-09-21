package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val EMOCIONES = listOf(
    ItemQuiz("😊", "feliz", "Feliz"), ItemQuiz("😢", "triste", "Triste"),
    ItemQuiz("😠", "enojado", "Enojado"), ItemQuiz("😨", "asustado", "Asustado"),
    ItemQuiz("😲", "sorprendido", "Sorprendido"), ItemQuiz("😴", "cansado", "Cansado"),
)

/** ¿Cómo te sientes? — patrón MaterialQuiz (reconocer emociones). */
@Composable
fun EmocionesScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("emociones")!!,
        items = EMOCIONES,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        pregunta = "¿Cómo se siente?",
        onVolver = onVolver,
    )
}
