package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val OBJETOS = listOf(
    ItemQuiz("🔑", "llave", "Llave"), ItemQuiz("⚽", "pelota", "Pelota"),
    ItemQuiz("📚", "libro", "Libro"), ItemQuiz("🎈", "globo", "Globo"),
    ItemQuiz("🍎", "manzana", "Manzana"), ItemQuiz("✂️", "tijera", "Tijera"),
)

/** Empareja sombras — patrón MaterialQuiz (reconocer solo por su contorno). */
@Composable
fun SombrasScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("sombras")!!,
        items = OBJETOS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 96.sp else 48.sp) },
        pregunta = "¿De quién es esta sombra?",
        onVolver = onVolver,
    )
}
