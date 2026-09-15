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
    ItemClasificar("🐶", "perro", "vivo"), ItemClasificar("🌳", "arbol", "vivo"),
    ItemClasificar("🦋", "mariposa", "vivo"), ItemClasificar("🐟", "pez", "vivo"),
    ItemClasificar("🪨", "piedra", "no-vivo"), ItemClasificar("☁️", "nube", "no-vivo"),
    ItemClasificar("🚗", "carro", "no-vivo"), ItemClasificar("⭐", "estrella", "no-vivo"),
)

/** ¿Vivo o no vivo? — patrón MaterialClasificar, con arrastre real a las canastas. */
@Composable
fun SeresVivosScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("seres-vivos")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("vivo", "Vivo", Color(0xFFEAF3EF)),
            DefCanasta("no-vivo", "No vivo", Color(0xFFF3ECF8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "Arrastra cada uno a su canasta",
        onVolver = onVolver,
    )
}
