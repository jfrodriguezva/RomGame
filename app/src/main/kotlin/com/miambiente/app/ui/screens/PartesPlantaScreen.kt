package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val PARTES = listOf(
    ItemQuiz("🌱", "raiz", "Raíz"), ItemQuiz("🌿", "tallo", "Tallo"),
    ItemQuiz("🍃", "hoja", "Hoja"), ItemQuiz("🌸", "flor", "Flor"),
    ItemQuiz("🍎", "fruto", "Fruto"),
)

/** Partes de la planta — patrón MaterialQuiz. */
@Composable
fun PartesPlantaScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("partes-planta")!!,
        items = PARTES,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 5)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        onVolver = onVolver,
    )
}
