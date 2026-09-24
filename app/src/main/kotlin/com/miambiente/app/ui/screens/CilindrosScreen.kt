package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.MaterialOrdenar

/** Cilindros con botón — patrón MaterialOrdenar (ajuste exacto por tamaño, con arrastre). */
@Composable
fun CilindrosScreen(onVolver: () -> Unit) {
    val n = phasedInt(1, listOf(3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 10))
    MaterialOrdenar(
        juego = buscarJuego("cilindros")!!,
        n = n,
        tamanoPara = { posicion -> (16 + (n - posicion + 1) * 8).dp },
        render = { _, _ -> Box(Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFA97FC7))) },
        consigna = "Arrastra cada cilindro a su hueco exacto",
        onVolver = onVolver,
    )
}
