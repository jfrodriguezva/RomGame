package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val CLIMA = listOf(
    ItemQuiz("☀️", "soleado", "Soleado"), ItemQuiz("☁️", "nublado", "Nublado"),
    ItemQuiz("🌧️", "lluvioso", "Lluvioso"), ItemQuiz("❄️", "nevado", "Nevado"),
    ItemQuiz("⛈️", "tormenta", "Tormenta"), ItemQuiz("🌬️", "ventoso", "Ventoso"),
)

/** El tiempo — patrón MaterialQuiz (nomenclatura del clima). */
@Composable
fun TiempoScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("tiempo")!!,
        items = CLIMA,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        pregunta = "¿Qué tiempo hace?",
        onVolver = onVolver,
    )
}
