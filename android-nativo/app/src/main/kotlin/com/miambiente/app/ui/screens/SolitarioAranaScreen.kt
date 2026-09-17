package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

/** ¿La columna desde `indice` hasta el final es una secuencia boca arriba que baja de 1 en 1 (un solo palo, sin alternar color)? */
internal fun secuenciaValidaArana(col: List<Pair<CartaSol, Boolean>>, indice: Int): Boolean {
    if (indice !in col.indices || !col[indice].second) return false
    for (i in indice until col.size - 1) {
        if (col[i].first.valor != col[i + 1].first.valor + 1) return false
    }
    return true
}

internal fun puedeColocarEnColumnaArana(carta: CartaSol, destino: List<Pair<CartaSol, Boolean>>): Boolean {
    if (destino.isEmpty()) return true
    val (tope, bocaArriba) = destino.last()
    return bocaArriba && tope.valor == carta.valor + 1
}

/** ¿Los últimos 13 cartas de la columna son K→A en secuencia? Si sí, es un juego completo. */
internal fun juegoCompletoEnCola(col: List<Pair<CartaSol, Boolean>>): Boolean {
    if (col.size < 13) return false
    val inicio = col.size - 13
    if (col[inicio].first.valor != 13) return false
    return secuenciaValidaArana(col, inicio)
}

/**
 * Solitario araña (variante de un solo palo, la más jugable): 10 columnas,
 * 104 cartas (8 juegos completos), se reparten de a 10 del mazo, y cada
 * secuencia K→As completa se retira sola. La versión anterior de "la
 * araña" (en Movimiento) no era este juego — esta es la versión real de
 * cartas que se pidió.
 */
@Composable
fun SolitarioAranaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("arana-cartas")!!
    val metaJuegos = 8

    var columnas by remember { mutableStateOf(listOf<List<Pair<CartaSol, Boolean>>>()) }
    var mazo by remember { mutableStateOf(listOf<CartaSol>()) }
    var completados by remember { mutableStateOf(0) }
    var seleccion by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var mensaje by remember { mutableStateOf("Arma secuencias del Rey al As") }
    var ganado by remember { mutableStateOf(false) }

    fun repartir() {
        val baraja = (1..8).flatMap { (1..13).map { v -> CartaSol(0, v) } }.shuffled().toMutableList()
        val nuevasColumnas = (0..9).map { col ->
            val cuantas = if (col < 4) 6 else 5
            (0 until cuantas).map { j -> val c = baraja.removeAt(0); c to (j == cuantas - 1) }
        }
        columnas = nuevasColumnas
        mazo = baraja
        completados = 0
        seleccion = null
        ganado = false
        mensaje = "Arma secuencias del Rey al As"
    }

    if (columnas.isEmpty()) repartir()

    fun destaparTope(colIdx: Int, cols: List<List<Pair<CartaSol, Boolean>>>): List<List<Pair<CartaSol, Boolean>>> {
        val col = cols[colIdx]
        if (col.isNotEmpty() && !col.last().second) {
            return cols.toMutableList().also { it[colIdx] = col.dropLast(1) + (col.last().first to true) }
        }
        return cols
    }

    fun quitarJuegoCompleto(colIdx: Int, cols: List<List<Pair<CartaSol, Boolean>>>): List<List<Pair<CartaSol, Boolean>>> {
        val col = cols[colIdx]
        if (!juegoCompletoEnCola(col)) return cols
        completados++
        services.sound.tocar(Efecto.WIN)
        if (completados >= metaJuegos) {
            ganado = true
            mensaje = "¡Ganaste, completaste las $metaJuegos secuencias! 🎉"
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
        val recortada = col.dropLast(13)
        return destaparTope(colIdx, cols.toMutableList().also { it[colIdx] = recortada })
    }

    fun tocarMazo() {
        if (mazo.isEmpty() || columnas.any { it.isEmpty() }) { services.sound.tocar(Efecto.WRONG); return }
        val diez = mazo.take(10)
        var nuevasColumnas = columnas.mapIndexed { i, col -> col + (diez[i] to true) }
        mazo = mazo.drop(10)
        (nuevasColumnas.indices).forEach { i -> nuevasColumnas = quitarJuegoCompleto(i, nuevasColumnas) }
        columnas = nuevasColumnas
        seleccion = null
        services.sound.tocar(Efecto.CLICK)
    }

    fun tocarColumna(colDestino: Int, indiceTocado: Int) {
        val actual = seleccion
        if (actual == null) {
            val col = columnas[colDestino]
            if (indiceTocado in col.indices && secuenciaValidaArana(col, indiceTocado)) {
                seleccion = colDestino to indiceTocado
                services.sound.tocar(Efecto.CLICK)
            }
            return
        }
        val (colSel, idxSel) = actual
        if (colSel == colDestino) { seleccion = null; return }
        val paquete = columnas[colSel].subList(idxSel, columnas[colSel].size)
        if (!puedeColocarEnColumnaArana(paquete.first().first, columnas[colDestino])) {
            services.sound.tocar(Efecto.WRONG)
            seleccion = null
            return
        }
        services.sound.tocar(Efecto.CORRECT)
        var nuevasColumnas = columnas.toMutableList()
        nuevasColumnas[colDestino] = columnas[colDestino] + paquete
        nuevasColumnas[colSel] = columnas[colSel].dropLast(paquete.size)
        nuevasColumnas = destaparTope(colSel, nuevasColumnas).toMutableList()
        nuevasColumnas = quitarJuegoCompleto(colDestino, nuevasColumnas).toMutableList()
        columnas = nuevasColumnas
        seleccion = null
    }

    GameShell(
        juego = juego,
        consigna = if (!ganado) "$mensaje ($completados de $metaJuegos)" else mensaje,
        celebrar = ganado,
        onVolver = onVolver,
        acciones = { if (ganado) Button(onClick = ::repartir) { Text("Jugar de nuevo") } },
    ) {
        Column(Modifier.fillMaxSize().padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CartaDorso(habilitado = mazo.isNotEmpty(), onClick = ::tocarMazo, vacio = mazo.isEmpty())
                Text("Mazo: ${mazo.size / 10} repartos", fontSize = 11.sp)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFE0C23C).copy(alpha = 0.3f)).padding(horizontal = 10.dp, vertical = 4.dp),
                ) { Text("✅ $completados / $metaJuegos", fontSize = 11.sp) }
            }
            // Ancho fijo por columna dentro de una fila con scroll: en un
            // celular angosto, 10 columnas repartidas por `weight` quedaban
            // más angostas que la carta de 40dp que tenían adentro, así que
            // se encimaban — el mismo bug de fondo que el de la pizarra.
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                columnas.forEachIndexed { colIdx, col ->
                    Box(modifier = Modifier.width(42.dp)) {
                        if (col.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(width = 40.dp, height = 56.dp)
                                    .border(1.dp, Color(0xFFD9CDB4).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .clickable { tocarColumna(colIdx, 0) },
                            )
                        } else {
                            // Mismo bug de fondo que en Solitario: `Column`
                            // ya apila las cartas, y el `offset` encima
                            // duplicaba el desplazamiento. Con `Box` el
                            // offset es la única posición.
                            Box {
                                col.forEachIndexed { i, (carta, bocaArriba) ->
                                    Box(modifier = Modifier.offset(y = (i * 15).dp)) {
                                        if (bocaArriba) {
                                            CartaSolVista(carta, seleccionada = seleccion == (colIdx to i), onClick = { tocarColumna(colIdx, i) })
                                        } else {
                                            CartaDorso(habilitado = false, onClick = {}, vacio = false)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
