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
    ItemClasificar("🔥", "fuego", "caliente"), ItemClasificar("☕", "cafe", "caliente"),
    ItemClasificar("🌞", "sol", "caliente"),
    ItemClasificar("❄️", "nieve", "frio"), ItemClasificar("🧊", "hielo", "frio"),
    ItemClasificar("🍦", "helado", "frio"),
)

/** Caliente o frío — patrón MaterialClasificar (sentido térmico). */
@Composable
fun TemperaturaScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("temperatura")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("caliente", "Caliente", Color(0xFFFBE9E7)),
            DefCanasta("frio", "Frío", Color(0xFFEAF1F8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "¿Está caliente o frío?",
        onVolver = onVolver,
    )
}
