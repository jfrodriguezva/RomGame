package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

private val RETOS = listOf(
    "Salta 3 veces", "Da 2 vueltas", "Toca tus pies", "Aplaude 5 veces",
    "Camina de puntitas", "Haz como un avión",
)

/** Dado de retos — material independiente, libre (movimiento dirigido por consigna). */
@Composable
fun DadoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("dado")!!
    var reto by remember { mutableStateOf("Tira el dado para empezar") }

    GameShell(
        juego = juego,
        consigna = reto,
        onVolver = onVolver,
        acciones = {
            Button(onClick = {
                services.sound.tocar(Efecto.CLICK)
                reto = RETOS.random()
            }) { Text("Tirar dado 🎲") }
        },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("🎲", fontSize = 96.sp)
        }
    }
}
