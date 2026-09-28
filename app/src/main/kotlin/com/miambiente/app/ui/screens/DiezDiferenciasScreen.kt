package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.CasillaEmoji
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BANCO_ESCENA = listOf("🌳", "🌸", "⭐", "🔺", "🔵", "🟢", "🟡", "🟣", "🍀", "🍁", "🦋", "🐝")
private const val COLUMNAS = 6
private const val CELDAS = 30

internal data class ParDeImagenes(val a: List<String>, val b: List<String>, val diferencias: Set<Int>)

internal fun nuevaRonda(): ParDeImagenes {
    val a = (0 until CELDAS).map { BANCO_ESCENA.random() }
    val diferencias = (0 until CELDAS).shuffled().take(10).toSet()
    val b = a.mapIndexed { i, emoji -> if (i in diferencias) BANCO_ESCENA.filterNot { it == emoji }.random() else emoji }
    return ParDeImagenes(a, b, diferencias)
}

/** Encuentra las diferencias — dos escenas casi iguales con diez diferencias para tocar. */
@Composable
fun DiezDiferenciasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("diez-diferencias")!!

    var ronda by remember { mutableStateOf(nuevaRonda()) }
    var encontradas by remember { mutableStateOf(setOf<Int>()) }

    fun tocar(i: Int) {
        if (i in encontradas) return
        if (i in ronda.diferencias) {
            services.sound.tocar(Efecto.CORRECT)
            encontradas = encontradas + i
            if (encontradas.size == ronda.diferencias.size) {
                services.sound.tocar(Efecto.WIN)
                scope.launch { services.progress.completarNivel(juego.id, 1) }
                scope.launch { delay(1200); ronda = nuevaRonda(); encontradas = emptySet() }
            }
        } else {
            services.sound.tocar(Efecto.WRONG)
        }
    }

    GameShell(
        juego = juego,
        consigna = "Compara las dos imágenes y toca las diez diferencias",
        nota = "Diferencias ${encontradas.size}/10",
        onVolver = onVolver,
    ) {
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            Text("Imagen A", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(4.dp))
            RejillaComparar(ronda.a, encontradas, Modifier.weight(1f), ::tocar)
            Text("Imagen B", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(4.dp))
            RejillaComparar(ronda.b, encontradas, Modifier.weight(1f), ::tocar)
        }
    }
}

@Composable
private fun RejillaComparar(celdas: List<String>, encontradas: Set<Int>, modifier: Modifier, onTocar: (Int) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(COLUMNAS), modifier = modifier.fillMaxWidth()) {
        items(celdas.size) { i ->
            CasillaEmoji(
                emoji = if (i in encontradas) "✅" else celdas[i],
                modifier = Modifier.padding(3.dp),
                tamanoFuente = 18.sp,
                acertado = i in encontradas,
                onClick = { onTocar(i) },
            )
        }
    }
}
