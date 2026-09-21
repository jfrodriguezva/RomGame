package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz

private val LETRAS = listOf(
    ItemQuiz("🐒 Mono", "m", "M"), ItemQuiz("🍍 Piña", "p", "P"),
    ItemQuiz("🐍 Serpiente", "s", "S"), ItemQuiz("🦁 León", "l", "L"),
    ItemQuiz("🐯 Tigre", "t", "T"),
)

/** Letras de lija — patrón MaterialQuiz (escucha y relaciona con su sonido). */
@Composable
fun LetrasLijaScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("letras-lija")!!,
        items = LETRAS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 5)) },
        render = { texto, grande -> Text(texto, fontSize = if (grande) 32.sp else 18.sp) },
        pregunta = "¿Con qué letra empieza?",
        onVolver = onVolver,
    )
}
