package com.miambiente.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.CasillaEspecial
import com.miambiente.app.ui.materials.MaterialTablero

private val CASILLAS = listOf(
    CasillaEspecial(4, 14, "🪜", "¡Escalera! Subes a la 14"),
    CasillaEspecial(17, 4, "🐍", "¡Serpiente! Bajas a la 4"),
    CasillaEspecial(22, 32, "🪜", "¡Escalera! Subes a la 32"),
    CasillaEspecial(36, 20, "🐍", "¡Serpiente! Bajas a la 20"),
)

/** Serpientes y escaleras — patrón MaterialTablero (dado + casillas especiales). */
@Composable
fun SerpientesScreen(onVolver: () -> Unit) {
    MaterialTablero(
        juego = buscarJuego("serpientes")!!,
        casillas = 40,
        especiales = CASILLAS,
        colorFicha = Color(0xFF4C7A3A),
        onVolver = onVolver,
    )
}
