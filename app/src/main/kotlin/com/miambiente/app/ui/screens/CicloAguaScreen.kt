package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.MaterialOrdenar

private val ETAPAS = listOf("☀️" to "Sol", "☁️" to "Nube", "🌧️" to "Lluvia", "🏞️" to "Río")

/** El ciclo del agua — patrón MaterialOrdenar (secuencia de un proceso natural). */
@Composable
fun CicloAguaScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("ciclo-agua")!!,
        n = ETAPAS.size,
        tamanoPara = { 72.dp },
        render = { posicion, _ ->
            val (emoji, _) = ETAPAS[posicion - 1]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 32.sp)
            }
        },
        consigna = "Ordena el ciclo del agua",
        onVolver = onVolver,
    )
}
