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

private val PASOS = listOf("💧" to "Verter", "🍽️" to "Servir", "🧥" to "Abotonar")

/** Vida práctica — patrón MaterialOrdenar (secuencia de ejercicios de vida práctica). */
@Composable
fun VidaPracticaScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("vida-practica")!!,
        n = PASOS.size,
        tamanoPara = { 80.dp },
        render = { posicion, _ ->
            val (emoji, nombre) = PASOS[posicion - 1]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(emoji, fontSize = 28.sp)
                    Text(nombre, fontSize = 10.sp)
                }
            }
        },
        consigna = "Ordena los ejercicios de vida práctica",
        onVolver = onVolver,
    )
}
