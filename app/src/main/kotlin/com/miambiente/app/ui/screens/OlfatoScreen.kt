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
    ItemClasificar("🌸", "flor", "bien"), ItemClasificar("🍰", "pastel", "bien"),
    ItemClasificar("🍋", "limon", "bien"),
    ItemClasificar("🧦", "calcetin", "mal"), ItemClasificar("🗑️", "basura", "mal"),
    ItemClasificar("🦨", "zorrillo", "mal"),
)

/** Huele bien o mal — patrón MaterialClasificar (sentido olfativo). */
@Composable
fun OlfatoScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("olfato")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("bien", "Huele bien", Color(0xFFE9F0E4)),
            DefCanasta("mal", "Huele mal", Color(0xFFF3ECF8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "¿Huele bien o mal?",
        onVolver = onVolver,
    )
}
