package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

internal data class CartaSol(val palo: Int, val valor: Int) // palo: 0 picas, 1 corazones, 2 diamantes, 3 treboles

internal fun esRojoSol(palo: Int) = palo == 1 || palo == 2
internal fun nombreValorSol(v: Int) = when (v) { 1 -> "A"; 11 -> "J"; 12 -> "Q"; 13 -> "K"; else -> "$v" }
private val SIMBOLO_PALO = listOf("♠", "♥", "♦", "♣")

internal fun barajaNuevaSol(): List<CartaSol> = (0..3).flatMap { p -> (1..13).map { CartaSol(p, it) } }

/** ¿Las cartas de `col` desde `indice` hasta el final forman una secuencia válida (alterna color, baja de 1 en 1)? */
internal fun secuenciaValidaSol(col: List<Pair<CartaSol, Boolean>>, indice: Int): Boolean {
    if (indice !in col.indices || !col[indice].second) return false
    for (i in indice until col.size - 1) {
        val (a, _) = col[i]
        val (b, _) = col[i + 1]
        if (esRojoSol(a.palo) == esRojoSol(b.palo)) return false
        if (a.valor != b.valor + 1) return false
    }
    return true
}

/** ¿Se puede soltar una carta `carta` (cabeza de la pila que se mueve) sobre el tope de `destino`? */
internal fun puedeColocarEnColumnaSol(carta: CartaSol, destino: List<Pair<CartaSol, Boolean>>): Boolean {
    if (destino.isEmpty()) return carta.valor == 13
    val (tope, bocaArriba) = destino.last()
    return bocaArriba && esRojoSol(tope.palo) != esRojoSol(carta.palo) && tope.valor == carta.valor + 1
}

internal fun puedeColocarEnFundacionSol(carta: CartaSol, valorActual: Int): Boolean = carta.valor == valorActual + 1

/**
 * Solitario (Klondike) real: 7 columnas, robo de a 1, fundaciones por
 * palo del As al Rey, secuencias alternando color — no una versión
 * simplificada. Se juega por toques: tocar una carta la selecciona (junto
 * con la secuencia válida encima de ella), tocar un destino intenta el
 * movimiento.
 */
@Composable
fun SolitarioScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("solitario")!!

    var columnas by remember { mutableStateOf(listOf<List<Pair<CartaSol, Boolean>>>()) }
    var fundaciones by remember { mutableStateOf(List(4) { 0 }) }
    var mazo by remember { mutableStateOf(listOf<CartaSol>()) }
    var descarte by remember { mutableStateOf(listOf<CartaSol>()) }
    var seleccion by remember { mutableStateOf<Pair<Int, Int>?>(null) } // columna, índice — o (-1, 0) para descarte
    var mensaje by remember { mutableStateOf("Arma las 4 fundaciones, del As al Rey") }
    var ganado by remember { mutableStateOf(false) }

    fun repartir() {
        val baraja = barajaNuevaSol().shuffled().toMutableList()
        val nuevasColumnas = (0..6).map { i ->
            (0..i).map { j -> val c = baraja.removeAt(0); c to (j == i) }
        }
        columnas = nuevasColumnas
        fundaciones = List(4) { 0 }
        mazo = baraja
        descarte = emptyList()
        seleccion = null
        ganado = false
        mensaje = "Arma las 4 fundaciones, del As al Rey"
    }

    if (columnas.isEmpty()) repartir()

    fun revisarVictoria() {
        if (fundaciones.all { it == 13 }) {
            ganado = true
            mensaje = "¡Ganaste! 🎉"
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    fun destaparTope(colIdx: Int, cols: List<List<Pair<CartaSol, Boolean>>>): List<List<Pair<CartaSol, Boolean>>> {
        val col = cols[colIdx]
        if (col.isNotEmpty() && !col.last().second) {
            return cols.toMutableList().also { it[colIdx] = col.dropLast(1) + (col.last().first to true) }
        }
        return cols
    }

    fun tocarDescarte() {
        if (descarte.isEmpty()) return
        seleccion = if (seleccion == (-1 to 0)) null else (-1 to 0)
        services.sound.tocar(Efecto.CLICK)
    }

    fun tocarMazo() {
        if (mazo.isEmpty()) {
            if (descarte.isEmpty()) return
            mazo = descarte.reversed()
            descarte = emptyList()
        } else {
            descarte = descarte + mazo.first()
            mazo = mazo.drop(1)
        }
        services.sound.tocar(Efecto.CLICK)
        seleccion = null
    }

    fun tocarFundacion(palo: Int) {
        val (colSel, idxSel) = seleccion ?: return
        val carta = if (colSel == -1) descarte.lastOrNull() else columnas[colSel].getOrNull(idxSel)?.first
        if (carta == null || carta.palo != palo) { services.sound.tocar(Efecto.WRONG); return }
        val esUnaSola = colSel == -1 || idxSel == columnas[colSel].size - 1
        if (!esUnaSola || !puedeColocarEnFundacionSol(carta, fundaciones[palo])) { services.sound.tocar(Efecto.WRONG); return }
        services.sound.tocar(Efecto.CORRECT)
        fundaciones = fundaciones.toMutableList().also { it[palo] = carta.valor }
        if (colSel == -1) descarte = descarte.dropLast(1) else {
            columnas = destaparTope(colSel, columnas.toMutableList().also { it[colSel] = it[colSel].dropLast(1) })
        }
        seleccion = null
        revisarVictoria()
    }

    fun tocarColumna(colDestino: Int, indiceTocado: Int) {
        val actual = seleccion
        if (actual == null) {
            val col = columnas[colDestino]
            if (indiceTocado in col.indices && secuenciaValidaSol(col, indiceTocado)) {
                seleccion = colDestino to indiceTocado
                services.sound.tocar(Efecto.CLICK)
            }
            return
        }
        val (colSel, idxSel) = actual
        if (colSel == colDestino) { seleccion = null; return }
        val paquete = if (colSel == -1) {
            descarte.lastOrNull()?.let { listOf(it to true) } ?: emptyList()
        } else {
            columnas[colSel].subList(idxSel, columnas[colSel].size)
        }
        if (paquete.isEmpty() || !puedeColocarEnColumnaSol(paquete.first().first, columnas[colDestino])) {
            services.sound.tocar(Efecto.WRONG)
            seleccion = null
            return
        }
        services.sound.tocar(Efecto.CORRECT)
        val nuevasColumnas = columnas.toMutableList()
        nuevasColumnas[colDestino] = columnas[colDestino] + paquete
        if (colSel == -1) descarte = descarte.dropLast(1) else nuevasColumnas[colSel] = columnas[colSel].dropLast(paquete.size)
        columnas = if (colSel == -1) nuevasColumnas else destaparTope(colSel, nuevasColumnas)
        seleccion = null
    }

    GameShell(
        juego = juego,
        consigna = mensaje,
        celebrar = ganado,
        onVolver = onVolver,
        acciones = { if (ganado) Button(onClick = ::repartir) { Text("Jugar de nuevo") } },
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                // Mazo y descarte
                CartaDorso(habilitado = mazo.isNotEmpty() || descarte.isNotEmpty(), onClick = ::tocarMazo, vacio = mazo.isEmpty())
                Box(modifier = Modifier.size(width = 40.dp, height = 56.dp)) {
                    descarte.lastOrNull()?.let { c ->
                        CartaSolVista(c, seleccionada = seleccion == (-1 to 0), onClick = ::tocarDescarte)
                    }
                }
                Box(Modifier.size(1.dp)) // separador
                (0..3).forEach { palo ->
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 56.dp)
                            .shadow(1.dp, RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFD9CDB4), RoundedCornerShape(6.dp))
                            .clickable { tocarFundacion(palo) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (fundaciones[palo] == 0) SIMBOLO_PALO[palo] else "${nombreValorSol(fundaciones[palo])}${SIMBOLO_PALO[palo]}",
                            color = if (esRojoSol(palo)) Color(0xFFA23B3B) else Color(0xFF3F342C),
                            fontSize = 13.sp,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                columnas.forEachIndexed { colIdx, col ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (col.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(width = 40.dp, height = 56.dp)
                                    .border(1.dp, Color(0xFFD9CDB4).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .clickable { tocarColumna(colIdx, 0) },
                            )
                        } else {
                            Column {
                                col.forEachIndexed { i, (carta, bocaArriba) ->
                                    Box(modifier = Modifier.offset(y = (i * 18).dp)) {
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

@Composable
internal fun CartaDorso(habilitado: Boolean, onClick: () -> Unit, vacio: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 56.dp)
            .shadow(if (vacio) 0.dp else 2.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(if (vacio) Color.Transparent else Color(0xFF3E7AA3))
            .border(1.dp, Color(0xFFD9CDB4).copy(alpha = if (vacio) 0.6f else 0f), RoundedCornerShape(6.dp))
            .clickable(enabled = habilitado) { onClick() },
        contentAlignment = Alignment.Center,
    ) { if (vacio) Text("↺", fontSize = 16.sp, color = Color(0xFFA39A8C)) }
}

@Composable
internal fun CartaSolVista(carta: CartaSol, seleccionada: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 56.dp)
            .shadow(3.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(if (seleccionada) 2.dp else 1.dp, if (seleccionada) Color(0xFFE0C23C) else Color(0xFFD9CDB4), RoundedCornerShape(6.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                nombreValorSol(carta.valor),
                fontSize = 12.sp,
                color = if (esRojoSol(carta.palo)) Color(0xFFA23B3B) else Color(0xFF3F342C),
            )
            Text(
                SIMBOLO_PALO[carta.palo],
                fontSize = 12.sp,
                color = if (esRojoSol(carta.palo)) Color(0xFFA23B3B) else Color(0xFF3F342C),
            )
        }
    }
}
