package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.CasillaEmoji
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val DISTRACTORES = listOf("🍎", "🍌", "🍇", "🥕", "🌸", "🍄", "🌵", "🍉")

/** Encuentra los objetos — material independiente (búsqueda visual, atención selectiva). */
@Composable
fun ObjetosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("objetos")!!
    val objetivo = "⭐"

    fun nuevaGrilla(): List<String> {
        val posiciones = (0 until 16).shuffled().take(4).toSet()
        return (0 until 16).map { if (it in posiciones) objetivo else DISTRACTORES.random() }
    }

    var grilla by remember { mutableStateOf(nuevaGrilla()) }
    var encontrados by remember { mutableStateOf(setOf<Int>()) }

    fun tocar(i: Int) {
        if (grilla[i] != objetivo || i in encontrados) return
        services.sound.tocar(Efecto.CORRECT)
        encontrados = encontrados + i
        if (encontrados.count { grilla[it] == objetivo } == grilla.count { it == objetivo }) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch { delay(1000); grilla = nuevaGrilla(); encontrados = emptySet() }
        }
    }

    GameShell(juego = juego, consigna = "Encuentra todas las $objetivo entre los demás", onVolver = onVolver) {
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(16.dp)) {
            items(grilla.size) { i ->
                CasillaEmoji(
                    emoji = if (i in encontrados) "✅" else grilla[i],
                    modifier = Modifier.padding(6.dp),
                    tamanoFuente = 26.sp,
                    acertado = i in encontrados,
                    habilitado = i !in encontrados,
                    onClick = { tocar(i) },
                )
            }
        }
    }
}
