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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/** Mazo real de 54 cartas únicas — más grande que cualquier tablero (16
 * casillas), así que no todos los cantos caen en el tablero propio, igual
 * que en la lotería mexicana real. */
internal val MAZO_LOTERIA = listOf(
    "🐶", "🐱", "🐰", "🦋", "🌸", "⭐", "🍎", "🚗", "🎈", "🐸",
    "🐢", "🐘", "🦁", "🐵", "🐷", "🐔", "🐟", "🦀", "🐝", "🕷️",
    "🌻", "🌵", "🌈", "☀️", "🌙", "⚡", "🔥", "💧", "🍌", "🍇",
    "🍉", "🍕", "🍦", "🎂", "🚀", "✈️", "🚲", "⛵", "🚂", "🎸",
    "🥁", "🎺", "⚽", "🏀", "🎯", "🎲", "📚", "✏️", "🔑", "💎",
    "👑", "🧦", "🪁", "🧸",
)

private fun tablero(vararg indices: Int): List<String> = indices.map { MAZO_LOTERIA[it] }

/** 8 tableros distintos (16 cartas cada uno, subconjuntos del mazo de 54):
 * al empezar partida se elige uno al azar, así no siempre es el mismo. */
internal val TABLEROS_LOTERIA: List<List<String>> = listOf(
    tablero(0, 1, 2, 3, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
    tablero(4, 5, 6, 7, 8, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31),
    tablero(32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47),
    tablero(48, 49, 50, 51, 52, 53, 0, 4, 8, 12, 16, 20, 24, 28, 32, 36),
    tablero(1, 5, 9, 13, 17, 21, 25, 29, 33, 37, 41, 45, 49, 53, 2, 6),
    tablero(3, 7, 11, 15, 19, 23, 27, 31, 35, 39, 43, 47, 51, 0, 10, 20),
    tablero(30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45),
    tablero(46, 47, 48, 49, 50, 51, 52, 53, 0, 1, 2, 3, 4, 5, 6, 7),
)

internal fun mazoBarajado(semilla: List<String> = MAZO_LOTERIA): List<String> = semilla.shuffled()

internal fun tableroAleatorio(tableros: List<List<String>> = TABLEROS_LOTERIA): List<String> = tableros.random()

/** Consume una carta del mazo restante (sin reemplazo): la baraja no se
 * vuelve a barajar a medias, se agota carta por carta hasta el final. */
internal fun siguienteCanto(mazoRestante: List<String>): Pair<String, List<String>>? =
    if (mazoRestante.isEmpty()) null else mazoRestante.first() to mazoRestante.drop(1)

internal fun cartonCompleto(tablero: List<String>, marcados: Set<String>): Boolean =
    tablero.all { it in marcados }

/**
 * Lotería mexicana — mazo grande de 54 cartas únicas y varios tableros de
 * 4×4, distinta de Bingo (que reutiliza un pool chico como cartón y mazo a
 * la vez). Se canta una carta a la vez del mazo barajado, sin repetir,
 * hasta ganar (tablero completo) o agotar el mazo.
 */
@Composable
fun LoteriaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("loteria")!!

    var tableroActual by remember { mutableStateOf(tableroAleatorio()) }
    var marcados by remember { mutableStateOf(setOf<String>()) }
    val primerMazo = remember { mazoBarajado() }
    var cantado by remember { mutableStateOf(primerMazo.first()) }
    var mazoRestante by remember { mutableStateOf(primerMazo.drop(1)) }
    var agotado by remember { mutableStateOf(false) }

    fun reiniciar() {
        tableroActual = tableroAleatorio()
        marcados = emptySet()
        val nuevoMazo = mazoBarajado()
        cantado = nuevoMazo.first()
        mazoRestante = nuevoMazo.drop(1)
        agotado = false
    }

    fun avanzarCanto() {
        if (agotado) return
        val resultado = siguienteCanto(mazoRestante)
        if (resultado == null) {
            agotado = true
            services.sound.tocar(Efecto.WRONG)
        } else {
            val (carta, resto) = resultado
            cantado = carta
            mazoRestante = resto
        }
    }

    fun tocar(emoji: String) {
        if (agotado || emoji in marcados) return
        if (emoji != cantado) {
            services.sound.tocar(Efecto.WRONG)
            return
        }
        services.sound.tocar(Efecto.CORRECT)
        marcados = marcados + emoji
        if (cartonCompleto(tableroActual, marcados)) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch {
                delay(1400)
                reiniciar()
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (agotado) "Se acabó el mazo — inténtalo de nuevo" else "Busca: $cantado en tu tablero",
        onVolver = onVolver,
        acciones = {
            if (agotado) {
                Button(onClick = ::reiniciar) { Text("Jugar de nuevo") }
            } else {
                Button(onClick = ::avanzarCanto) { Text("🎴 Siguiente carta (${mazoRestante.size} quedan)") }
            }
        },
    ) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .shadow(3.dp, RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFB5732F)),
            ) { Text(cantado, fontSize = 40.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .size(260.dp)
                    .shadow(5.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFFBF2))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(tableroActual) { emoji ->
                    val marcado = emoji in marcados
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (marcado) Color(0xFF8BBF6A) else Color.White)
                            .clickable(enabled = !marcado) { tocar(emoji) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(emoji, fontSize = 22.sp)
                        if (marcado) Text("✓", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}
