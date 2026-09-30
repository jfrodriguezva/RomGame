package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.dificultadObjetos
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.CasillaEmoji
import com.miambiente.app.ui.materials.rememberMaterialState
import kotlin.random.Random

private val DISTRACTORES = listOf("🍎", "🍌", "🍇", "🥕", "🌸", "🍄", "🌵", "🍉")

/**
 * Encuentra los objetos — búsqueda visual y atención selectiva. La
 * cuadrícula y la cantidad de estrellas escondidas crecen con el nivel.
 */
@Composable
fun ObjetosScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("objetos")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val objetivo = "⭐"

    val grilla = remember(estado.nivel) {
        val d = dificultadObjetos(estado.nivel)
        val rnd = Random(estado.nivel)
        val total = d.lado * d.lado
        val posiciones = (0 until total).shuffled(rnd).take(d.objetivos).toSet()
        d.lado to (0 until total).map { if (it in posiciones) objetivo else DISTRACTORES.random(rnd) }
    }
    val (lado, casillas) = grilla
    var encontrados by remember(estado.nivel) { mutableStateOf(setOf<Int>()) }
    val faltan = casillas.count { it == objetivo } - encontrados.size

    fun tocar(i: Int) {
        if (i in encontrados || estado.logrado) return
        if (casillas[i] != objetivo) { estado.intento("Esa no es una $objetivo"); return }
        encontrados = encontrados + i
        estado.acierto("¡Una más!")
        if (encontrados.size == casillas.count { it == objetivo }) estado.completar()
    }

    GameShell(
        juego = juego,
        consigna = if (faltan == 0) "¡Las encontraste todas!" else "Encuentra las $objetivo: faltan $faltan",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(lado), contentPadding = PaddingValues(16.dp)) {
            items(casillas.size) { i ->
                CasillaEmoji(
                    emoji = if (i in encontrados) "✅" else casillas[i],
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
