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

private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

/** Los días de la semana — patrón MaterialOrdenar (secuencia fija, no por tamaño). */
@Composable
fun DiasSemanaScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("dias-semana")!!,
        n = DIAS.size,
        tamanoPara = { 64.dp },
        render = { posicion, _ ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(DIAS[posicion - 1], fontSize = 13.sp)
            }
        },
        consigna = "Ordena los días de la semana",
        onVolver = onVolver,
    )
}
