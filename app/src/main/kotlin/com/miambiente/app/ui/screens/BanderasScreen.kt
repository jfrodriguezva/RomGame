package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val BANDERAS = listOf(
    ItemQuiz("🇲🇽", "mexico", "México"), ItemQuiz("🇺🇸", "eeuu", "Estados Unidos"),
    ItemQuiz("🇨🇦", "canada", "Canadá"), ItemQuiz("🇪🇸", "espana", "España"),
    ItemQuiz("🇦🇷", "argentina", "Argentina"), ItemQuiz("🇧🇷", "brasil", "Brasil"),
    ItemQuiz("🇫🇷", "francia", "Francia"), ItemQuiz("🇯🇵", "japon", "Japón"),
)

/** Banderas del mundo — patrón MaterialQuiz. */
@Composable
fun BanderasScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("banderas")!!,
        items = BANDERAS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6)) },
        render = { emoji, grande -> Text(emoji, fontSize = if (grande) 88.sp else 44.sp) },
        pregunta = "¿De qué país es esta bandera?",
        onVolver = onVolver,
    )
}
