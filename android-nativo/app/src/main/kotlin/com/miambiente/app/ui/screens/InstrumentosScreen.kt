package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val INSTRUMENTOS = listOf(
    ItemQuiz("🥁", "tambor", "Tambor"),
    ItemQuiz("🎸", "guitarra", "Guitarra"),
    ItemQuiz("🎹", "piano", "Piano"),
    ItemQuiz("🎺", "trompeta", "Trompeta"),
    ItemQuiz("🎻", "violin", "Violín"),
)

/** Instrumentos musicales — patrón MaterialQuiz. */
@Composable
fun InstrumentosScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("instrumentos")!!,
        items = INSTRUMENTOS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 5)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        onVolver = onVolver,
    )
}
