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

private val ETAPAS = listOf("🥚" to "Huevo", "🐛" to "Oruga", "🦋💤" to "Crisálida", "🦋" to "Mariposa")

/** El ciclo de la mariposa — patrón MaterialOrdenar (seriación por tiempo, no por tamaño). */
@Composable
fun CicloVidaScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("ciclo-vida")!!,
        n = ETAPAS.size,
        tamanoPara = { 76.dp },
        render = { posicion, _ ->
            val (emoji, nombre) = ETAPAS[posicion - 1]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(emoji, fontSize = 24.sp)
                    Text(nombre, fontSize = 10.sp)
                }
            }
        },
        consigna = "Ordena el ciclo de vida de la mariposa",
        onVolver = onVolver,
    )
}
