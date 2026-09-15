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

private data class Carta(val id: Int, val emoji: String)

/** Juego de memoria — material independiente (memoria visual y concentración). */
@Composable
fun MemoramaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("memorama")!!

    var cartas by remember { mutableStateOf((EMOJIS + EMOJIS).mapIndexed { i, e -> Carta(i, e) }.shuffled()) }
    var volteadas by remember { mutableStateOf(setOf<Int>()) }
    var encontradas by remember { mutableStateOf(setOf<Int>()) }
    var bloqueado by remember { mutableStateOf(false) }

    fun reiniciar() {
        cartas = (EMOJIS + EMOJIS).mapIndexed { i, e -> Carta(i, e) }.shuffled()
        volteadas = emptySet()
        encontradas = emptySet()
    }

    fun tocar(id: Int) {
        if (bloqueado || id in encontradas || id in volteadas) return
        val nuevas = volteadas + id
        volteadas = nuevas
        if (nuevas.size == 2) {
            bloqueado = true
            scope.launch {
                delay(700)
                val (a, b) = nuevas.toList()
                if (cartas.first { it.id == a }.emoji == cartas.first { it.id == b }.emoji) {
                    services.sound.tocar(Efecto.CORRECT)
                    encontradas = encontradas + a + b
                    if (encontradas.size == cartas.size) {
                        services.sound.tocar(Efecto.WIN)
                        scope.launch { services.progress.completarNivel(juego.id, 1) }
                    }
                } else {
                    services.sound.tocar(Efecto.WRONG)
                }
                volteadas = emptySet()
                bloqueado = false
            }
        }
    }

    LaunchedEffect(encontradas) {
        if (encontradas.size == cartas.size) {
            delay(1600)
            reiniciar()
        }
    }

    GameShell(
        juego = juego,
        consigna = if (encontradas.size == cartas.size) "¡Encontraste todas las parejas!" else "Encuentra las parejas",
        onVolver = onVolver,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)) {
            items(cartas, key = { it.id }) { carta ->
                val visible = carta.id in volteadas || carta.id in encontradas
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (visible) Color.White else Color(0xFFA97FC7))
                        .clickable { tocar(carta.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (visible) carta.emoji else "❓", fontSize = 28.sp, modifier = Modifier.padding(14.dp))
                }
            }
        }
    }
}
