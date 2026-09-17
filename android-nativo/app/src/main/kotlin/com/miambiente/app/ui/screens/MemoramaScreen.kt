package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.CartaMemorama
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(cartas, key = { it.id }) { carta ->
                val encontrada = carta.id in encontradas
                val visible = carta.id in volteadas || encontrada
                CartaMemorama(emoji = carta.emoji, visible = visible, encontrada = encontrada, onClick = { tocar(carta.id) })
            }
        }
    }
}
