package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

private val TODOS = listOf("🐶", "🐱", "🐰", "🦋", "🌸", "⭐", "🍎", "🚗", "🎈")

/** Bingo con imágenes — escucha, busca y marca en tu cartón. */
@Composable
fun BingoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("bingo")!!

    var carton by remember { mutableStateOf(TODOS.shuffled().take(9)) }
    var marcados by remember { mutableStateOf(setOf<String>()) }
    var cantado by remember { mutableStateOf(carton.random()) }

    fun nuevoCanto() {
        val faltantes = carton.filter { it !in marcados }
        if (faltantes.isNotEmpty()) cantado = faltantes.random()
    }

    fun tocar(emoji: String) {
        if (emoji != cantado) {
            services.sound.tocar(Efecto.WRONG)
            return
        }
        services.sound.tocar(Efecto.CORRECT)
        marcados = marcados + emoji
        if (marcados.size == carton.size) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch {
                delay(1400)
                carton = TODOS.shuffled().take(9)
                marcados = emptySet()
                cantado = carton.random()
            }
        } else {
            nuevoCanto()
        }
    }

    GameShell(juego = juego, consigna = "Busca: $cantado", onVolver = onVolver) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.size(220.dp)) {
                items(carton) { emoji ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (emoji in marcados) Color(0xFF8BBF6A) else Color.White)
                            .clickable { tocar(emoji) },
                        contentAlignment = Alignment.Center,
                    ) { Text(emoji, fontSize = 26.sp) }
                }
            }
        }
    }
}
