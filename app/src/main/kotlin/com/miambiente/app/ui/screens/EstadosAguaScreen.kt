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
    ItemClasificar("🧊", "hielo", "solido"), ItemClasificar("❄️", "nieve", "solido"),
    ItemClasificar("💧", "gota", "liquido"), ItemClasificar("🌊", "ola", "liquido"),
    ItemClasificar("☁️", "nube", "gas"), ItemClasificar("💨", "vapor", "gas"),
)

/** Estados del agua — patrón MaterialClasificar (sólido / líquido / gas). */
@Composable
fun EstadosAguaScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("estados-agua")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("solido", "Sólido", Color(0xFFEAF1F8)),
            DefCanasta("liquido", "Líquido", Color(0xFFE8F2F5)),
            DefCanasta("gas", "Gas", Color(0xFFF6F0E4)),
        ),
        render = { emoji -> Text(emoji, fontSize = 32.sp) },
        consigna = "Arrastra cada uno a su estado",
        onVolver = onVolver,
    )
}
