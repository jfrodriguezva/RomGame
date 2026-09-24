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
    ItemClasificar("🍬", "dulce1", "dulce"), ItemClasificar("🍭", "dulce2", "dulce"),
    ItemClasificar("🍰", "dulce3", "dulce"),
    ItemClasificar("🥨", "salado1", "salado"), ItemClasificar("🍟", "salado2", "salado"),
    ItemClasificar("🧂", "salado3", "salado"),
)

/** Dulce o salado — patrón MaterialClasificar (sentido gustativo). */
@Composable
fun SaborScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("sabor")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("dulce", "Dulce", Color(0xFFFBE9E7)),
            DefCanasta("salado", "Salado", Color(0xFFEAF1F8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "¿Dulce o salado?",
        onVolver = onVolver,
    )
}
