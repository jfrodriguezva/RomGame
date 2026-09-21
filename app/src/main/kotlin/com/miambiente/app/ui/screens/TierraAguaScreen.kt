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
    ItemClasificar("⛰️", "montana", "tierra"), ItemClasificar("🏝️", "isla", "tierra"),
    ItemClasificar("🏜️", "desierto", "tierra"),
    ItemClasificar("🌊", "mar", "agua"), ItemClasificar("🏞️", "rio", "agua"),
    ItemClasificar("💧", "lago", "agua"),
)

/** Formas de tierra y agua — patrón MaterialClasificar (vocabulario geográfico). */
@Composable
fun TierraAguaScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("tierra-agua")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("tierra", "Tierra", Color(0xFFE9F0E4)),
            DefCanasta("agua", "Agua", Color(0xFFEAF1F8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 36.sp) },
        consigna = "Arrastra cada uno a tierra o agua",
        onVolver = onVolver,
    )
}
