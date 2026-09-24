package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.materials.MaterialOrdenar

private val PASOS = listOf("💧" to "Verter", "🍽️" to "Servir", "🧵" to "Abotonar")

/** Vida práctica — patrón MaterialOrdenar (secuencia de ejercicios de vida práctica). */
@Composable
fun VidaPracticaScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("vida-practica")!!
    val colores = coloresDe(juego.area)

    MaterialOrdenar(
        juego = juego,
        n = PASOS.size,
        tamanoPara = { 84.dp },
        render = { posicion, _ ->
            val (emoji, nombre) = PASOS[posicion - 1]
            Box(modifier = Modifier.fillMaxSize().padding(6.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(emoji, fontSize = 34.sp)
                    Text(nombre, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colores.texto)
                }
            }
        },
        consigna = "Ordena los ejercicios de vida práctica",
        onVolver = onVolver,
    )
}
