package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.MaterialOrdenar

private val PASOS = listOf("💦" to "Mojar", "🧼" to "Jabón", "🤲" to "Tallar", "🚰" to "Enjuagar", "🧻" to "Secar")

/** Lavarse las manos — patrón MaterialOrdenar (secuencia de higiene). */
@Composable
fun LavadoManosScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("lavado-manos")!!,
        n = PASOS.size,
        tamanoPara = { 64.dp },
        render = { posicion, _ ->
            val (emoji, nombre) = PASOS[posicion - 1]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(emoji, fontSize = 22.sp)
                    Text(nombre, fontSize = 9.sp)
                }
            }
        },
        consigna = "Ordena los pasos para lavarse las manos",
        onVolver = onVolver,
    )
}
