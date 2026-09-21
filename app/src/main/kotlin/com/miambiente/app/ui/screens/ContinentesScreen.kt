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
    ItemClasificar("🦬", "bisonte", "america"), ItemClasificar("🦅", "aguila", "america"),
    ItemClasificar("🦁", "leon", "africa"), ItemClasificar("🐘", "elefante", "africa"),
    ItemClasificar("🐼", "panda", "asia"), ItemClasificar("🐯", "tigre", "asia"),
    ItemClasificar("🦌", "ciervo", "europa"), ItemClasificar("🦔", "erizo", "europa"),
)

/** Los continentes — patrón MaterialClasificar (mapa y sus animales). */
@Composable
fun ContinentesScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("continentes")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("america", "América", Color(0xFFFBE9E7)),
            DefCanasta("africa", "África", Color(0xFFFDF1E4)),
            DefCanasta("asia", "Asia", Color(0xFFF3ECF8)),
            DefCanasta("europa", "Europa", Color(0xFFEAF1F8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 34.sp) },
        consigna = "Arrastra cada animal a su continente",
        onVolver = onVolver,
    )
}
