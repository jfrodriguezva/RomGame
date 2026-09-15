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

private fun ganador(tablero: List<String?>): String? {
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
 * Gato (tres en línea) — material independiente, sin ningún patrón
 * compartido, igual que en la web (app/games/gato). CPU simple: bloquea
 * si el jugador está por ganar, si no juega al azar.
 */
@Composable
fun GatoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("gato")!!

    var tablero by remember { mutableStateOf(List<String?>(9) { null }) }
    var turno by remember { mutableStateOf("jugador") }
    var mensaje by remember { mutableStateOf("Tu turno") }

    val gan = ganador(tablero)
    val empate = gan == null && tablero.all { it != null }

    fun reiniciar() {
        tablero = List(9) { null }
        turno = "jugador"
        mensaje = "Tu turno"
    }

    LaunchedEffect(gan, empate) {
        if (gan == "X") {
            services.sound.tocar(Efecto.WIN)
            mensaje = "¡Ganaste! 🎉"
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            delay(1600)
            reiniciar()
        } else if (gan == "O") {
            services.sound.tocar(Efecto.WRONG)
            mensaje = "Ganó la computadora, ¡otra vez!"
            delay(1600)
            reiniciar()
        } else if (empate) {
            mensaje = "¡Empate! Buen intento"
            delay(1600)
            reiniciar()
        }
    }

    LaunchedEffect(turno, tablero) {
        if (turno != "cpu" || gan != null || empate) return@LaunchedEffect
        delay(500)
        val vacias = tablero.indices.filter { tablero[it] == null }
        if (vacias.isEmpty()) return@LaunchedEffect
        val jugada = vacias.random()
        tablero = tablero.toMutableList().also { it[jugada] = "O" }
        turno = "jugador"
    }

    fun tocar(i: Int) {
        if (turno != "jugador" || tablero[i] != null || gan != null) return
        services.sound.tocar(Efecto.CLICK)
        tablero = tablero.toMutableList().also { it[i] = "X" }
        turno = "cpu"
    }

    GameShell(juego = juego, consigna = mensaje, onVolver = onVolver) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.size(240.dp),
            ) {
                items(9) { i ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable { tocar(i) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            tablero[i] ?: "",
                            fontSize = 32.sp,
                            color = if (tablero[i] == "X") Color(0xFF8A5A2B) else Color(0xFF2F5C82),
                        )
                    }
                }
            }
        }
    }
}
