package com.miambiente.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.CasillaEspecial
import com.miambiente.app.ui.materials.MaterialTablero

private val CASILLAS = listOf(
    CasillaEspecial(6, 12, "🦢", "¡Oca! Salta hasta la 12"),
    CasillaEspecial(19, 6, "🌉", "El puente te regresa a la 6"),
    CasillaEspecial(31, 12, "💀", "Caíste en el pozo, vuelves a la 12"),
    CasillaEspecial(42, 30, "🎲", "El laberinto te regresa a la 30"),
)

/** El juego de la oca — patrón MaterialTablero (dado + casillas especiales). */
@Composable
fun OcaScreen(onVolver: () -> Unit) {
    MaterialTablero(
        juego = buscarJuego("oca")!!,
        casillas = 50,
        especiales = CASILLAS,
        colorFicha = Color(0xFF3E7AA3),
        onVolver = onVolver,
    )
}
