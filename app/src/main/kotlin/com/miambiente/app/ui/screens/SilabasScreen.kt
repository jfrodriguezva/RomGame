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
    ItemClasificar("☀️", "sol", "1"), ItemClasificar("🍞", "pan", "1"), ItemClasificar("💡", "luz", "1"),
    ItemClasificar("🏠", "casa", "2"), ItemClasificar("🐱", "gato", "2"), ItemClasificar("🛏️", "cama", "2"),
    ItemClasificar("🪟", "ventana", "3"), ItemClasificar("🍎", "manzana", "3"), ItemClasificar("🍉", "sandia", "3"),
)

/** Cuenta las sílabas — patrón MaterialClasificar (conciencia fonológica). */
@Composable
fun SilabasScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("silabas")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("1", "1 sílaba", Color(0xFFFBE9E7)),
            DefCanasta("2", "2 sílabas", Color(0xFFEAF1F8)),
            DefCanasta("3", "3 sílabas", Color(0xFFF3ECF8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 34.sp) },
        consigna = "Escucha y clasifica por golpes de voz",
        onVolver = onVolver,
    )
}
