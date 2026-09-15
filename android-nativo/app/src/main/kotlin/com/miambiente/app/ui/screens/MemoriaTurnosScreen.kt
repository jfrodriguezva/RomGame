package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
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

private val EMOJIS = listOf("🐶", "🐱", "🐰", "🦋", "🌸", "⭐")

private data class CartaTurno(val id: Int, val emoji: String)

/** Memoria por turnos — material independiente (esperar el turno, memoria, aceptar el resultado). */
@Composable
fun MemoriaTurnosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("memoria-turnos")!!

    var cartas by remember { mutableStateOf((EMOJIS + EMOJIS).mapIndexed { i, e -> CartaTurno(i, e) }.shuffled()) }
    var volteadas by remember { mutableStateOf(setOf<Int>()) }
    var encontradas by remember { mutableStateOf(setOf<Int>()) }
    var turno by remember { mutableStateOf("jugador") }
    var puntosJugador by remember { mutableStateOf(0) }
    var puntosCpu by remember { mutableStateOf(0) }
    var bloqueado by remember { mutableStateOf(false) }

    fun jugar(a: Int, b: Int) {
        bloqueado = true
        scope.launch {
            volteadas = setOf(a, b)
            delay(700)
            if (cartas[a].emoji == cartas[b].emoji) {
                services.sound.tocar(Efecto.CORRECT)
                encontradas = encontradas + a + b
                if (turno == "jugador") puntosJugador++ else puntosCpu++
                if (encontradas.size == cartas.size) {
                    services.sound.tocar(Efecto.WIN)
                    if (puntosJugador >= puntosCpu) scope.launch { services.progress.completarNivel(juego.id, 1) }
                }
            } else {
                services.sound.tocar(Efecto.WRONG)
                turno = if (turno == "jugador") "cpu" else "jugador"
            }
            volteadas = emptySet()
            bloqueado = false
        }
    }

    fun tocarJugador(id: Int) {
        if (bloqueado || turno != "jugador" || id in encontradas || id in volteadas) return
        val previa = volteadas
        if (previa.isEmpty()) volteadas = setOf(id) else jugar(previa.first(), id)
    }

    LaunchedEffect(turno, bloqueado, encontradas.size) {
        if (turno == "cpu" && !bloqueado && encontradas.size < cartas.size) {
            delay(700)
            val disponibles = cartas.map { it.id }.filter { it !in encontradas }
            val a = disponibles.random()
            val b = disponibles.filter { it != a }.random()
            jugar(a, b)
        }
    }

    GameShell(
        juego = juego,
        consigna = if (encontradas.size == cartas.size) {
            if (puntosJugador >= puntosCpu) "¡Ganaste $puntosJugador a $puntosCpu!" else "Ganó la computadora $puntosCpu a $puntosJugador"
        } else if (turno == "jugador") "Tu turno" else "Turno de la computadora",
        onVolver = onVolver,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)) {
            items(cartas, key = { it.id }) { carta ->
                val visible = carta.id in volteadas || carta.id in encontradas
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (visible) Color.White else Color(0xFFE0B586))
                        .clickable { tocarJugador(carta.id) },
                    contentAlignment = Alignment.Center,
                ) { Text(if (visible) carta.emoji else "❓", fontSize = 28.sp, modifier = Modifier.padding(14.dp)) }
            }
        }
    }
}
