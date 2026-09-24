package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

@Composable
private fun ManoRps(etiqueta: String, emoji: String?, resaltado: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(etiqueta, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(if (resaltado) 8.dp else 3.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, if (resaltado) Color(0xFF4C7A3A) else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center,
        ) { Text(emoji ?: "❔", fontSize = 34.sp) }
    }
}

/**
 * Piedra, papel o tijera — material independiente.
 *
 * Modo dos jugadores (se pidió poder jugar entre más de una persona):
 * a diferencia de Gato o Damas, acá las dos jugadas tienen que ser
 * SIMULTÁNEAS/secretas para que el juego tenga sentido — si Jugador 2 ve
 * la pantalla antes de elegir, ya sabe qué eligió Jugador 1 y gana
 * siempre. Por eso no es un simple `dosJugadores` con turnos alternos
 * como en los otros juegos: hay una fase intermedia real de "pásale el
 * dispositivo", donde la jugada de Jugador 1 se esconde de la pantalla
 * hasta que Jugador 2 también eligió la suya.
 */
@Composable
fun RpsScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("rps")!!

    var dosJugadores by remember { mutableStateOf(false) }
    // fase solo importa en modo dos jugadores: elige1 -> pasa -> elige2 -> resultado
    var fase by remember { mutableStateOf("elige1") }
    var jugada by remember { mutableStateOf<String?>(null) }
    var cpu by remember { mutableStateOf<String?>(null) }
    var mensaje by remember { mutableStateOf("Elige piedra, papel o tijera") }
    var puntos1 by remember { mutableStateOf(0) }
    var puntos2 by remember { mutableStateOf(0) }

    fun cambiarModo(activarDosJugadores: Boolean) {
        dosJugadores = activarDosJugadores
        jugada = null; cpu = null
        fase = "elige1"
        puntos1 = 0; puntos2 = 0
        mensaje = if (activarDosJugadores) "Jugador 1: elige piedra, papel o tijera" else "Elige piedra, papel o tijera"
    }

    fun jugar(opcion: String) {
        if (dosJugadores) {
            when (fase) {
                "elige1" -> {
                    services.sound.tocar(Efecto.CLICK)
                    jugada = opcion
                    fase = "pasa"
                    mensaje = "Jugador 1 ya eligió. Pásale el dispositivo a Jugador 2"
                }
                "elige2" -> {
                    services.sound.tocar(Efecto.CLICK)
                    cpu = opcion
                    fase = "resultado"
                }
                else -> return
            }
        } else {
            if (jugada != null) return
            services.sound.tocar(Efecto.CLICK)
            jugada = opcion
            cpu = OPCIONES.map { it.first }.random()
        }
    }

    fun continuarPase() {
        fase = "elige2"
        mensaje = "Jugador 2: elige piedra, papel o tijera"
    }

    LaunchedEffect(jugada, cpu, fase) {
        if (dosJugadores) {
            if (fase != "resultado") return@LaunchedEffect
            val j = jugada ?: return@LaunchedEffect
            val c = cpu ?: return@LaunchedEffect
            if (j == c) {
                mensaje = "¡Empate! Otra vez"
            } else if (gana(j, c)) {
                services.sound.tocar(Efecto.WIN)
                puntos1++
                mensaje = "¡Ganó Jugador 1! 🎉"
            } else {
                services.sound.tocar(Efecto.WIN)
                puntos2++
                mensaje = "¡Ganó Jugador 2! 🎉"
            }
            delay(1800)
            jugada = null; cpu = null; fase = "elige1"
            mensaje = "Jugador 1: elige piedra, papel o tijera"
        } else {
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
    }

    GameShell(juego = juego, consigna = mensaje, onVolver = onVolver) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 20.dp)) {
                FilterChip(
                    selected = !dosJugadores,
                    onClick = { if (dosJugadores) cambiarModo(false) },
                    label = { Text("🤖 Vs. computadora") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF2F5C82), selectedLabelColor = Color.White),
                )
                FilterChip(
                    selected = dosJugadores,
                    onClick = { if (!dosJugadores) cambiarModo(true) },
                    label = { Text("👫 Dos jugadores") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8A5A2B), selectedLabelColor = Color.White),
                )
            }

            if (dosJugadores && fase == "pasa") {
                // Pantalla de traspaso: esconde la jugada de Jugador 1 a
                // propósito mientras el dispositivo cambia de manos.
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔄", fontSize = 48.sp)
                    Text(
                        "Jugador 1 ya eligió",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text("Pásale el dispositivo a Jugador 2", fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))
                    Button(onClick = ::continuarPase) { Text("Listo, es mi turno") }
                }
            } else {
                if (dosJugadores) {
                    Text("Jugador 1: $puntos1 · Jugador 2: $puntos2", fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                }
                val revelar = !dosJugadores || fase == "resultado"
                val ganoUno = revelar && jugada != null && cpu != null && jugada != cpu && gana(jugada!!, cpu!!)
                val ganoDos = revelar && jugada != null && cpu != null && jugada != cpu && !ganoUno

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    ManoRps(
                        if (dosJugadores) "Jugador 1" else "Tú",
                        if (revelar) jugada?.let { op -> OPCIONES.first { it.first == op }.second } else null,
                        resaltado = ganoUno,
                    )
                    Text("VS", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFFB0A48F))
                    ManoRps(
                        if (dosJugadores) "Jugador 2" else "Rival",
                        if (revelar) cpu?.let { op -> OPCIONES.first { it.first == op }.second } else null,
                        resaltado = ganoDos,
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OPCIONES.forEach { (id, emoji) ->
                        val interaccion = remember { MutableInteractionSource() }
                        val presionado by interaccion.collectIsPressedAsState()
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .scale(if (presionado) 0.9f else 1f)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable(interactionSource = interaccion, indication = null) { jugar(id) },
                            contentAlignment = Alignment.Center,
                        ) { Text(emoji, fontSize = 28.sp) }
                    }
                }
            }
        }
    }
}
