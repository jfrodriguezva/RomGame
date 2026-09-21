package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch
import kotlin.random.Random

internal data class Laberinto(val filas: Int, val columnas: Int, val caminos: Set<Int>, val entrada: Int, val salida: Int)

/** Genera un laberinto perfecto con DFS: siempre tiene solución y cambia en cada ronda. */
internal fun generarLaberinto(filasCeldas: Int = 5, columnasCeldas: Int = 5, semilla: Int = 1): Laberinto {
    val filas = filasCeldas * 2 + 1
    val columnas = columnasCeldas * 2 + 1
    fun indice(f: Int, c: Int) = f * columnas + c
    val rnd = Random(semilla)
    val visitadas = mutableSetOf(0 to 0)
    val pila = mutableListOf(0 to 0)
    val caminos = mutableSetOf(indice(1, 1))
    while (pila.isNotEmpty()) {
        val (f, c) = pila.last()
        val opciones = listOf(f - 1 to c, f + 1 to c, f to c - 1, f to c + 1)
            .filter { (nf, nc) -> nf in 0 until filasCeldas && nc in 0 until columnasCeldas && (nf to nc) !in visitadas }
        if (opciones.isEmpty()) { pila.removeAt(pila.lastIndex); continue }
        val (nf, nc) = opciones.random(rnd)
        caminos += indice(1 + nf * 2, 1 + nc * 2)
        caminos += indice(1 + f * 2 + (nf - f), 1 + c * 2 + (nc - c))
        visitadas += nf to nc
        pila += nf to nc
    }
    val entrada = indice(1, 0)
    val salida = indice(filas - 2, columnas - 1)
    caminos += entrada; caminos += salida
    return Laberinto(filas, columnas, caminos, entrada, salida)
}

@Composable
fun LaberintoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("laberinto")!!
    var ronda by remember { mutableIntStateOf(1) }
    var mapa by remember(ronda) { mutableStateOf(generarLaberinto(5, 5, ronda * 7919)) }
    var posicion by remember(ronda) { mutableIntStateOf(mapa.entrada) }
    var visitadas by remember(ronda) { mutableStateOf(setOf(mapa.entrada)) }
    var pasos by remember(ronda) { mutableIntStateOf(0) }
    var completo by remember(ronda) { mutableStateOf(false) }

    fun mover(delta: Int) {
        if (completo) return
        val destino = posicion + delta
        if (destino !in mapa.caminos) { services.sound.tocar(Efecto.WRONG); return }
        posicion = destino; visitadas = visitadas + destino; pasos++; services.sound.tocar(Efecto.CLICK)
        if (destino == mapa.salida) {
            completo = true; services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, ronda.coerceAtMost(100)) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Salida encontrada en $pasos pasos!" else "Laberinto ${mapa.columnas}×${mapa.filas} · Pasos $pasos",
        onVolver = onVolver,
        acciones = { Button(onClick = { ronda++ }) { Text(if (completo) "Siguiente laberinto" else "Otro laberinto") } },
    ) {
        Column(Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFF3F342C)).padding(3.dp)) {
                repeat(mapa.filas) { f ->
                    Row {
                        repeat(mapa.columnas) { c ->
                            val i = f * mapa.columnas + c
                            Box(
                                Modifier.size(27.dp).background(if (i in mapa.caminos) if (i in visitadas) Color(0xFFF1E4C8) else Color.White else Color(0xFF3F342C)),
                                contentAlignment = Alignment.Center,
                            ) { Text(when(i){ posicion->"🐭"; mapa.salida->"🧀"; else->"" }, fontSize=15.sp) }
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 12.dp)) {
                Button(onClick = { mover(-mapa.columnas) }) { Text("▲") }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { mover(-1) }) { Text("◀") }
                    Text("  $pasos  ", modifier = Modifier.align(Alignment.CenterVertically), fontWeight = FontWeight.Bold)
                    Button(onClick = { mover(1) }) { Text("▶") }
                }
                Button(onClick = { mover(mapa.columnas) }) { Text("▼") }
            }
        }
    }
}
