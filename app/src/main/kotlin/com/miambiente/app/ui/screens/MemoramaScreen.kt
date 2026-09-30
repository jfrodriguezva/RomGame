package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.parejasMemorama
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.CartaMemorama
import com.miambiente.app.ui.materials.rememberMaterialState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Antes solo 6 parejas con cartas grandes (grilla de 4 columnas). Se pidió
// "mucho más pequeño para que puedan ser muchas más tarjetas" — ahora son
// hasta 24 parejas (48 cartas) en una grilla adaptable de cartas chicas,
// a las que se llega subiendo de nivel.
private val EMOJIS = listOf(
    "🐶", "🐱", "🐰", "🦋", "🌸", "⭐", "🐸", "🐢", "🦊", "🐼",
    "🐨", "🦁", "🐵", "🐷", "🐔", "🦆", "🐝", "🐞", "🌻", "🍓",
    "🍉", "🚗", "⚽", "🎈",
)

private data class Carta(val id: Int, val emoji: String)

/**
 * Juego de memoria — memoria visual y concentración. Las parejas crecen con
 * el nivel (`parejasMemorama`): de 3 al empezar hasta 24; antes eran 24
 * desde el primer intento, demasiadas para un niño de 3 años.
 */
@Composable
fun MemoramaScreen(onVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("memorama")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    val cartas = remember(estado.nivel) {
        val rnd = Random(estado.nivel)
        val elegidos = EMOJIS.shuffled(rnd).take(parejasMemorama(estado.nivel))
        (elegidos + elegidos).mapIndexed { i, e -> Carta(i, e) }.shuffled(rnd)
    }
    var volteadas by remember(estado.nivel) { mutableStateOf(setOf<Int>()) }
    var encontradas by remember(estado.nivel) { mutableStateOf(setOf<Int>()) }
    var bloqueado by remember(estado.nivel) { mutableStateOf(false) }

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
                    encontradas = encontradas + a + b
                    estado.acierto("¡Pareja!")
                    if (encontradas.size == cartas.size) estado.completar()
                } else {
                    estado.intento("No son iguales. Recuerda dónde estaban")
                }
                volteadas = emptySet()
                bloqueado = false
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (encontradas.size == cartas.size) "¡Encontraste todas las parejas!" else "Encuentra las ${cartas.size / 2} parejas",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        // Cartas más grandes mientras son pocas.
        val minimo = if (cartas.size <= 12) 96.dp else if (cartas.size <= 24) 72.dp else 56.dp
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = minimo),
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(cartas, key = { it.id }) { carta ->
                val encontrada = carta.id in encontradas
                val visible = carta.id in volteadas || encontrada
                CartaMemorama(
                    emoji = carta.emoji,
                    visible = visible,
                    encontrada = encontrada,
                    tamanoEmoji = if (cartas.size <= 12) 36.sp else if (cartas.size <= 24) 26.sp else 18.sp,
                    tamanoDorso = 16.sp,
                    onClick = { tocar(carta.id) },
                )
            }
        }
    }
}
