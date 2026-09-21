package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.MaterialOrdenar

/** Números en orden — patrón MaterialOrdenar (sucesión numérica). */
@Composable
fun NumerosScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("numeros")!!,
        n = 10,
        tamanoPara = { 56.dp },
        render = { posicion, _ ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("$posicion", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        },
        consigna = "Conecta los números en orden",
        onVolver = onVolver,
    )
}
