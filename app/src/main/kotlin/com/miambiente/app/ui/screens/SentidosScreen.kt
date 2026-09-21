package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val SENTIDOS = listOf(
    ItemQuiz("👁️", "vista", "Vista"), ItemQuiz("👂", "oido", "Oído"),
    ItemQuiz("👃", "olfato", "Olfato"), ItemQuiz("👅", "gusto", "Gusto"),
    ItemQuiz("✋", "tacto", "Tacto"),
)

/** Los cinco sentidos — patrón MaterialQuiz. */
@Composable
fun SentidosScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("sentidos")!!,
        items = SENTIDOS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 5)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        onVolver = onVolver,
    )
}
