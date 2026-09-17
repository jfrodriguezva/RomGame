package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

internal fun ganador(tablero: List<String?>): String? {
    val lineas = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        listOf(0, 4, 8), listOf(2, 4, 6),
    )
    for (l in lineas) {
        val (a, b, c) = l
        if (tablero[a] != null && tablero[a] == tablero[b] && tablero[b] == tablero[c]) return tablero[a]
    }
    return null
}

/**
 * Minimax real (no al azar): la CPU explora todo el árbol de jugadas
 * posibles y elige la que maximiza su resultado asumiendo que el jugador
 * también juega lo mejor que puede — la misma técnica de cualquier motor
 * de tres-en-línea perfecto. Antes la CPU jugaba con `vacias.random()`
 * (bug real reportado: "que la respuesta no sea aleatoria, sino que
 * realmente piense"). El puntaje usa la profundidad para preferir ganar
 * rápido y perder tarde, no solo "ganar en algún momento".
 */
internal fun mejorJugadaCpu(tablero: List<String?>): Int {
    val vacias = tablero.indices.filter { tablero[it] == null }
    if (vacias.size == 9) return vacias.random() // la primera jugada es simétrica; cualquier casilla es igual de buena
    var mejorValor = Int.MIN_VALUE
    val mejores = mutableListOf<Int>()
    for (i in vacias) {
        val siguiente = tablero.toMutableList().also { it[i] = "O" }
        val valor = minimax(siguiente, profundidad = 1, esMaximizando = false)
        when {
            valor > mejorValor -> { mejorValor = valor; mejores.clear(); mejores.add(i) }
            valor == mejorValor -> mejores.add(i)
        }
    }
    return mejores.random()
}

private fun minimax(tablero: List<String?>, profundidad: Int, esMaximizando: Boolean): Int {
    val g = ganador(tablero)
    if (g == "O") return 10 - profundidad
    if (g == "X") return profundidad - 10
    val vacias = tablero.indices.filter { tablero[it] == null }
    if (vacias.isEmpty()) return 0
    return if (esMaximizando) {
        vacias.maxOf { i -> minimax(tablero.toMutableList().also { it[i] = "O" }, profundidad + 1, false) }
    } else {
        vacias.minOf { i -> minimax(tablero.toMutableList().also { it[i] = "X" }, profundidad + 1, true) }
    }
}

/**
 * Gato (tres en línea) — dos modos: contra la computadora (minimax real,
 * arriba) o dos jugadores locales tomando turnos en la misma tablet, para
 * jugar en compañía de verdad en vez de siempre contra la máquina.
 */
@Composable
fun GatoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("gato")!!

    var dosJugadores by remember { mutableStateOf(false) }
    var tablero by remember { mutableStateOf(List<String?>(9) { null }) }
    var turno by remember { mutableStateOf("jugador1") }
    var mensaje by remember { mutableStateOf("Turno de ❌") }

    val gan = ganador(tablero)
    val empate = gan == null && tablero.all { it != null }

    fun mensajeTurno(t: String) = when {
        !dosJugadores && t == "jugador1" -> "Tu turno"
        !dosJugadores -> "Piensa la computadora…"
        t == "jugador1" -> "Turno de ❌"
        else -> "Turno de ⭕"
    }

    fun reiniciar() {
        tablero = List(9) { null }
        turno = "jugador1"
        mensaje = mensajeTurno("jugador1")
    }

    fun cambiarModo(activarDosJugadores: Boolean) {
        dosJugadores = activarDosJugadores
        reiniciar()
    }

    LaunchedEffect(gan, empate) {
        if (gan == "X") {
            services.sound.tocar(Efecto.WIN)
            mensaje = if (dosJugadores) "¡Ganó ❌! 🎉" else "¡Ganaste! 🎉"
            if (!dosJugadores) scope.launch { services.progress.completarNivel(juego.id, 1) }
            delay(1600)
            reiniciar()
        } else if (gan == "O") {
            services.sound.tocar(if (dosJugadores) Efecto.WIN else Efecto.WRONG)
            mensaje = if (dosJugadores) "¡Ganó ⭕! 🎉" else "Ganó la computadora, ¡otra vez!"
            delay(1600)
            reiniciar()
        } else if (empate) {
            mensaje = "¡Empate! Buen intento"
            delay(1600)
            reiniciar()
        }
    }

    LaunchedEffect(turno, tablero, dosJugadores) {
        if (dosJugadores || turno != "cpu" || gan != null || empate) return@LaunchedEffect
        delay(500)
        val vacias = tablero.indices.filter { tablero[it] == null }
        if (vacias.isEmpty()) return@LaunchedEffect
        val jugada = mejorJugadaCpu(tablero)
        tablero = tablero.toMutableList().also { it[jugada] = "O" }
        turno = "jugador1"
        mensaje = mensajeTurno("jugador1")
    }

    fun tocar(i: Int) {
        if (tablero[i] != null || gan != null) return
        if (!dosJugadores && turno != "jugador1") return
        val marca = if (turno == "jugador1") "X" else "O"
        services.sound.tocar(Efecto.CLICK)
        tablero = tablero.toMutableList().also { it[i] = marca }
        turno = if (dosJugadores) {
            val siguiente = if (turno == "jugador1") "jugador2" else "jugador1"
            mensaje = mensajeTurno(siguiente)
            siguiente
        } else {
            mensaje = mensajeTurno("cpu")
            "cpu"
        }
    }

    GameShell(juego = juego, consigna = mensaje, onVolver = onVolver) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 20.dp),
            ) {
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.size(240.dp).shadow(4.dp, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(Color(0xFFEFE7DA)).padding(6.dp),
            ) {
                items(9) { i ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize()
                            .shadow(1.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable { tocar(i) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            tablero[i] ?: "",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (tablero[i] == "X") Color(0xFF8A5A2B) else Color(0xFF2F5C82),
                        )
                    }
                }
            }
        }
    }
}
