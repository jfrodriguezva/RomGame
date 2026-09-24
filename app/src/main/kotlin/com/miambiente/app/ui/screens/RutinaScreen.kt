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

private val PASOS = listOf("⏰" to "Despertar", "👕" to "Vestirse", "🥣" to "Desayunar", "🏫" to "Escuela")

/** La rutina de la mañana — patrón MaterialOrdenar (secuencia de vida diaria). */
@Composable
fun RutinaScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("rutina")!!,
        n = PASOS.size,
        tamanoPara = { 76.dp },
        render = { posicion, _ ->
            val (emoji, nombre) = PASOS[posicion - 1]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(emoji, fontSize = 26.sp)
                    Text(nombre, fontSize = 10.sp)
                }
            }
        },
        consigna = "Ordena la rutina de la mañana",
        onVolver = onVolver,
    )
}
