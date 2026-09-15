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

/** Ordenar bloques — patrón MaterialOrdenar (seriación genérica por tamaño). */
@Composable
fun BloquesScreen(onVolver: () -> Unit) {
    val n = phasedInt(1, listOf(3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 10))
    MaterialOrdenar(
        juego = buscarJuego("bloques")!!,
        n = n,
        tamanoPara = { posicion -> (18 + (n - posicion + 1) * 9).dp },
        render = { _, _ ->
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)).background(Color(0xFF6FA6CC)))
        },
        consigna = "Arrastra del más chico al más grande",
        onVolver = onVolver,
    )
}
