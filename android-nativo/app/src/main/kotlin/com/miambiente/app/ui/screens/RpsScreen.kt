package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val OPCIONES = listOf("piedra" to "🪨", "papel" to "📄", "tijera" to "✂️")

private fun gana(a: String, b: String): Boolean =
    (a == "piedra" && b == "tijera") || (a == "papel" && b == "piedra") || (a == "tijera" && b == "papel")

/** Piedra, papel o tijera — material independiente, sin patrón compartido. */
@Composable
fun RpsScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("rps")!!

    var jugada by remember { mutableStateOf<String?>(null) }
    var cpu by remember { mutableStateOf<String?>(null) }
    var mensaje by remember { mutableStateOf("Elige piedra, papel o tijera") }
    var rondas by remember { mutableStateOf(0) }

    fun jugar(opcion: String) {
        if (jugada != null) return
        services.sound.tocar(Efecto.CLICK)
        val rival = OPCIONES.map { it.first }.random()
        jugada = opcion
        cpu = rival
        rondas++
    }

    LaunchedEffect(jugada, cpu) {
        val j = jugada ?: return@LaunchedEffect
        val c = cpu ?: return@LaunchedEffect
        if (j == c) {
            mensaje = "¡Empate! Otra vez"
        } else if (gana(j, c)) {
            services.sound.tocar(Efecto.WIN)
            mensaje = "¡Ganaste! 🎉"
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        } else {
            services.sound.tocar(Efecto.WRONG)
            mensaje = "Ganó la computadora, ¡otra vez!"
        }
        delay(1600)
        jugada = null
        cpu = null
        mensaje = "Elige piedra, papel o tijera"
    }

    GameShell(juego = juego, consigna = mensaje, onVolver = onVolver) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tú", fontSize = 14.sp)
                    Box(
                        Modifier.size(72.dp).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) { Text(jugada?.let { op -> OPCIONES.first { it.first == op }.second } ?: "❔", fontSize = 32.sp) }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Rival", fontSize = 14.sp)
                    Box(
                        Modifier.size(72.dp).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) { Text(cpu?.let { op -> OPCIONES.first { it.first == op }.second } ?: "❔", fontSize = 32.sp) }
                }
            }

            Row(
                modifier = Modifier.padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OPCIONES.forEach { (id, emoji) ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { jugar(id) },
                        contentAlignment = Alignment.Center,
                    ) { Text(emoji, fontSize = 28.sp) }
                }
            }
        }
    }
}
