package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.MaterialOrdenar

/** Barras numéricas — patrón MaterialOrdenar (cantidad concreta del 1 al 10). */
@Composable
fun BarrasNumericasScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("barras-numericas")!!,
        n = 10,
        tamanoPara = { 60.dp },
        render = { posicion, _ ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Row {
                    repeat(posicion) { i ->
                        Box(
                            Modifier
                                .size(width = 5.dp, height = 20.dp)
                                .background(if (i % 2 == 0) Color(0xFFD9433A) else Color(0xFF3E7AA3)),
                        )
                    }
                }
            }
        },
        consigna = "Ordena las barras del 1 al 10",
        onVolver = onVolver,
    )
}
