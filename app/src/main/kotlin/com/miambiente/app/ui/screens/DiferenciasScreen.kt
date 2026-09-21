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

private val PARES = listOf("🍎" to "🍏", "🐶" to "🐺", "⚽" to "🏀", "🌞" to "🌝", "🚗" to "🚙")

/** ¿Qué es distinto? — material independiente (discriminación visual fina). */
@Composable
fun DiferenciasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("diferencias")!!

    fun nuevaGrilla(): Pair<List<String>, Int> {
        val (igual, distinto) = PARES.random()
        val posicionDistinta = (0 until 9).random()
        return (0 until 9).map { if (it == posicionDistinta) distinto else igual } to posicionDistinta
    }

    var estado by remember { mutableStateOf(nuevaGrilla()) }
    val (grilla, distinta) = estado
    var acertada by remember { mutableStateOf(false) }

    fun tocar(i: Int) {
        if (i == distinta) {
            services.sound.tocar(Efecto.CORRECT)
            acertada = true
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch { delay(900); estado = nuevaGrilla(); acertada = false }
        } else {
            services.sound.tocar(Efecto.WRONG)
        }
    }

    GameShell(juego = juego, consigna = "Encuentra lo diferente", onVolver = onVolver) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(32.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            items(grilla.size) { i ->
                CasillaEmoji(
                    emoji = grilla[i],
                    modifier = Modifier.padding(8.dp),
                    tamanoFuente = 32.sp,
                    acertado = acertada && i == distinta,
                    onClick = { tocar(i) },
                )
            }
        }
    }
}
