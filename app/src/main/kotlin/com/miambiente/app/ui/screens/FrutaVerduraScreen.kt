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
    ItemClasificar("🍎", "manzana", "fruta"), ItemClasificar("🍌", "platano", "fruta"),
    ItemClasificar("🍇", "uvas", "fruta"), ItemClasificar("🍓", "fresa", "fruta"),
    ItemClasificar("🥕", "zanahoria", "verdura"), ItemClasificar("🥦", "brocoli", "verdura"),
    ItemClasificar("🥬", "lechuga", "verdura"), ItemClasificar("🌽", "elote", "verdura"),
)

/** Fruta o verdura — patrón MaterialClasificar (primera clasificación botánica). */
@Composable
fun FrutaVerduraScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("fruta-verdura")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("fruta", "Fruta", Color(0xFFFBE9E7)),
            DefCanasta("verdura", "Verdura", Color(0xFFE9F0E4)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "Arrastra cada una a su canasta",
        onVolver = onVolver,
    )
}
