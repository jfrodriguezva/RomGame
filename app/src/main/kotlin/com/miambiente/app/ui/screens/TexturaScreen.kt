package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.DefCanasta
import com.miambiente.app.ui.materials.ItemClasificar
import com.miambiente.app.ui.materials.MaterialClasificar

private val POOL = listOf(
    ItemClasificar("🪨", "piedra", "aspero"), ItemClasificar("🧱", "ladrillo", "aspero"),
    ItemClasificar("🌰", "bellota", "aspero"),
    ItemClasificar("🪞", "espejo", "liso"), ItemClasificar("🧊", "hielo", "liso"),
    ItemClasificar("🥚", "huevo", "liso"),
)

/** Áspero o liso — patrón MaterialClasificar (sentido táctil). */
@Composable
fun TexturaScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("textura")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("aspero", "Áspero", Color(0xFFFDF1E4)),
            DefCanasta("liso", "Liso", Color(0xFFEAF1F8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "Toca con los ojos: ¿pincha o resbala?",
        onVolver = onVolver,
    )
}
