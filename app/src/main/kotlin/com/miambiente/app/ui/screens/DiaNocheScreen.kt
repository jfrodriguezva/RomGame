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
    ItemClasificar("☀️", "sol", "dia"), ItemClasificar("🌈", "arcoiris", "dia"),
    ItemClasificar("🐓", "gallo", "dia"),
    ItemClasificar("🌙", "luna", "noche"), ItemClasificar("⭐", "estrella", "noche"),
    ItemClasificar("🦉", "buho", "noche"),
)

/** Día y noche — patrón MaterialClasificar. */
@Composable
fun DiaNocheScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("dia-noche")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("dia", "Día", Color(0xFFF6F0E4)),
            DefCanasta("noche", "Noche", Color(0xFFEAF1F8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "Arrastra cada uno a día o noche",
        onVolver = onVolver,
    )
}
