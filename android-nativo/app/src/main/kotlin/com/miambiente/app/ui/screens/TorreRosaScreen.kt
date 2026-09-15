package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.MaterialOrdenar

/** Torre rosa — patrón MaterialOrdenar (seriación por tamaño, con arrastre real). */
@Composable
fun TorreRosaScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("torre-rosa")!!
    val n = phasedInt(1, listOf(3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 10))

    MaterialOrdenar(
        juego = juego,
        n = n,
        tamanoPara = { posicion -> (24 + (n - posicion + 1) * 12).dp },
        render = { _, tamano ->
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0669C)))
        },
        consigna = "Arrastra del más grande al más chico",
        onVolver = onVolver,
    )
}
