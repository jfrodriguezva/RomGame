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
    ItemClasificar("🏋️", "pesa", "pesado"), ItemClasificar("🪨", "roca", "pesado"),
    ItemClasificar("🐘", "elefante", "pesado"),
    ItemClasificar("🪶", "pluma", "ligero"), ItemClasificar("🎈", "globo", "ligero"),
    ItemClasificar("🦋", "mariposa", "ligero"),
)

/** Pesado o ligero — patrón MaterialClasificar (sentido bárico). */
@Composable
fun PesoScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("peso")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("pesado", "Pesado", Color(0xFFF3ECF8)),
            DefCanasta("ligero", "Ligero", Color(0xFFE8F2F5)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "¿Pesa mucho o poco?",
        onVolver = onVolver,
    )
}
