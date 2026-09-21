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

private val ESTACIONES = listOf("🌸" to "Primavera", "☀️" to "Verano", "🍂" to "Otoño", "❄️" to "Invierno")

/** Las estaciones del año — patrón MaterialOrdenar (secuencia fija, no por tamaño). */
@Composable
fun EstacionesScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("estaciones")!!,
        n = ESTACIONES.size,
        tamanoPara = { 76.dp },
        render = { posicion, _ ->
            val (emoji, nombre) = ESTACIONES[posicion - 1]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(emoji, fontSize = 28.sp)
                    Text(nombre, fontSize = 11.sp)
                }
            }
        },
        consigna = "Ordena las estaciones del año",
        onVolver = onVolver,
    )
}
