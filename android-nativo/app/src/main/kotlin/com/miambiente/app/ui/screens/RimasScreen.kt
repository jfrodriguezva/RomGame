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
    ItemClasificar("🐱", "gato", "ato"), ItemClasificar("🦆", "pato", "ato"), ItemClasificar("👞", "zapato", "ato"),
    ItemClasificar("🌹", "rosa", "osa"), ItemClasificar("🦋", "mariposa", "osa"),
    ItemClasificar("🐭", "raton", "on"), ItemClasificar("🔘", "boton", "on"), ItemClasificar("🚚", "camion", "on"),
)

/** Palabras que riman — patrón MaterialClasificar (conciencia fonológica: rima). */
@Composable
fun RimasScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("rimas")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("ato", "...ato", Color(0xFFFBE9E7)),
            DefCanasta("osa", "...osa", Color(0xFFEAF1F8)),
            DefCanasta("on", "...ón", Color(0xFFF3ECF8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 34.sp) },
        consigna = "Arrastra cada palabra con la que rima",
        onVolver = onVolver,
    )
}
