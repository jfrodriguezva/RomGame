package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.MaterialOrdenar

/** La escalera marrón — patrón MaterialOrdenar (seriación por grosor, con arrastre real). */
@Composable
fun EscaleraMarronScreen(onVolver: () -> Unit) {
    val n = phasedInt(1, listOf(3, 4, 4, 5, 5, 6, 7, 7, 7, 7, 7))
    MaterialOrdenar(
        juego = buscarJuego("escalera-marron")!!,
        n = n,
        tamanoPara = { posicion -> (20 + (n - posicion + 1) * 10).dp },
        render = { _, _ ->
            Box(Modifier.fillMaxWidth().fillMaxHeight(0.6f).clip(RoundedCornerShape(4.dp)).background(Color(0xFF8A5A2B)))
        },
        consigna = "Arrastra del más ancho al más delgado",
        onVolver = onVolver,
    )
}
