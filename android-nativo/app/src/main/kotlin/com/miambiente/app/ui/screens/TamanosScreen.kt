package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.DefCanasta
import com.miambiente.app.ui.materials.ItemClasificar
import com.miambiente.app.ui.materials.MaterialClasificar

private val POOL = listOf(
    ItemClasificar(72.dp, "g1", "grande"), ItemClasificar(64.dp, "g2", "grande"),
    ItemClasificar(44.dp, "m1", "mediano"), ItemClasificar(38.dp, "m2", "mediano"),
    ItemClasificar(20.dp, "c1", "chico"), ItemClasificar(16.dp, "c2", "chico"),
)

/** Grande, mediano o chico — patrón MaterialClasificar en tres canastas. */
@Composable
fun TamanosScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("tamanos")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("grande", "Grande", Color(0xFFE9F0E4)),
            DefCanasta("mediano", "Mediano", Color(0xFFEAF1F8)),
            DefCanasta("chico", "Chico", Color(0xFFF3ECF8)),
        ),
        render = { lado -> Box(Modifier.size(lado).clip(CircleShape).background(Color(0xFFE0669C))) },
        consigna = "Arrastra cada uno según su tamaño",
        onVolver = onVolver,
    )
}
