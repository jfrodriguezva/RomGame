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
    ItemClasificar("🐒", "mono", "selva"), ItemClasificar("🦜", "loro", "selva"),
    ItemClasificar("🐫", "camello", "desierto"), ItemClasificar("🦂", "escorpion", "desierto"),
    ItemClasificar("🐠", "pez", "oceano"), ItemClasificar("🐬", "delfin", "oceano"),
    // "🐻❄️" en vez de "🐻‍❄️": sin unión (ZWJ) entre los dos caracteres, para
    // que se vean siempre los dos símbolos aunque la fuente no tenga el
    // glifo combinado de "oso polar" (el bug real de iconos que no aparecen).
    ItemClasificar("🐧", "pinguino", "polo"), ItemClasificar("🐻❄️", "oso-polar", "polo"),
)

/** ¿Dónde vive? — patrón MaterialClasificar (selva / desierto / océano / polo). */
@Composable
fun HabitatScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("habitat")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("selva", "Selva", Color(0xFFE9F0E4)),
            DefCanasta("desierto", "Desierto", Color(0xFFFDF1E4)),
            DefCanasta("oceano", "Océano", Color(0xFFEAF1F8)),
            DefCanasta("polo", "Polo", Color(0xFFE8F2F5)),
        ),
        render = { emoji -> Text(emoji, fontSize = 32.sp) },
        consigna = "Arrastra cada animal a su hábitat",
        onVolver = onVolver,
    )
}
