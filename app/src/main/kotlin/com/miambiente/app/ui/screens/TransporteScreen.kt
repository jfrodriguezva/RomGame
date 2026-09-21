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
    ItemClasificar("🚗", "carro", "tierra"), ItemClasificar("🚲", "bicicleta", "tierra"),
    ItemClasificar("🚂", "tren", "tierra"),
    ItemClasificar("✈️", "avion", "aire"), ItemClasificar("🚁", "helicoptero", "aire"),
    ItemClasificar("🎈", "globo", "aire"),
    ItemClasificar("🚢", "barco", "agua"), ItemClasificar("🛶", "canoa", "agua"),
    ItemClasificar("🚤", "lancha", "agua"),
)

/** Medios de transporte — patrón MaterialClasificar (tierra / aire / agua). */
@Composable
fun TransporteScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("transporte")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("tierra", "Tierra", Color(0xFFE9F0E4)),
            DefCanasta("aire", "Aire", Color(0xFFEAF1F8)),
            DefCanasta("agua", "Agua", Color(0xFFEAF3EF)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "Arrastra cada uno a donde se mueve",
        onVolver = onVolver,
    )
}
