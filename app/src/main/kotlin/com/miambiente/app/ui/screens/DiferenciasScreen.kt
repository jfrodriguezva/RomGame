package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.dificultadDiferencias
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.CasillaEmoji
import com.miambiente.app.ui.materials.rememberMaterialState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// De más distintos a más parecidos: los niveles altos usan los del final.
private val PARES = listOf(
    "🍎" to "🍌", "🐶" to "🐟", "⚽" to "🚗", "🌞" to "🌙", "🍎" to "🍏",
    "🐶" to "🐺", "⚽" to "🏀", "🌞" to "🌝", "🚗" to "🚙", "😀" to "😃",
)

/**
 * ¿Qué es distinto? — discriminación visual fina. Con el nivel crece la
 * cuadrícula, hay más rondas y los pares se parecen más entre sí.
 */
@Composable
fun DiferenciasScreen(onVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("diferencias")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val d = remember(estado.nivel) { dificultadDiferencias(estado.nivel) }

    var ronda by remember(estado.nivel) { mutableIntStateOf(0) }
    val (grilla, distinta) = remember(estado.nivel, ronda) {
        val rnd = Random(estado.nivel * 31 + ronda)
        // Mitad de la lista según el nivel: primero pares muy distintos.
        val desde = ((estado.nivel - 1) * PARES.size / 200).coerceIn(0, PARES.size / 2)
        val (igual, diferente) = PARES.subList(desde, PARES.size).random(rnd)
        val total = d.lado * d.lado
        val posicion = (0 until total).random(rnd)
        (0 until total).map { if (it == posicion) diferente else igual } to posicion
    }
    var acertada by remember(estado.nivel, ronda) { mutableStateOf(false) }

    fun tocar(i: Int) {
        if (acertada || estado.logrado) return
        if (i != distinta) { estado.intento("Esa es igual a las demás"); return }
        acertada = true
        estado.acierto("¡Esa es!")
        if (ronda + 1 >= d.rondas) estado.completar() else scope.launch { delay(900); ronda++ }
    }

    GameShell(
        juego = juego,
        consigna = if (estado.logrado) "¡Encontraste todas!" else "Encuentra lo diferente (${ronda + 1} de ${d.rondas})",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(d.lado),
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            items(grilla.size) { i ->
                CasillaEmoji(
                    emoji = grilla[i],
                    modifier = Modifier.padding(6.dp),
                    tamanoFuente = if (d.lado >= 5) 26.sp else 32.sp,
                    acertado = acertada && i == distinta,
                    onClick = { tocar(i) },
                )
            }
        }
    }
}
