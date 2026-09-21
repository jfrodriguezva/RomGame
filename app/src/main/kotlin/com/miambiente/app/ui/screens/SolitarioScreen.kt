package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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

    // "Al nivel de la PC": doble toque manda la carta de arriba a su
    // fundación si es válido, igual que el doble clic del Solitario de
    // Windows — sin tener que seleccionarla primero y después tocar la
    // fundación exacta.
    fun intentarAutoFundacion(colIdx: Int, idx: Int): Boolean {
        val col = columnas[colIdx]
        if (idx != col.size - 1 || !col[idx].second) return false
        val carta = col[idx].first
        if (!puedeColocarEnFundacionSol(carta, fundaciones[carta.palo])) return false
        services.sound.tocar(Efecto.CORRECT)
        fundaciones = fundaciones.toMutableList().also { it[carta.palo] = carta.valor }
        columnas = destaparTope(colIdx, columnas.toMutableList().also { it[colIdx] = it[colIdx].dropLast(1) })
        seleccion = null
        revisarVictoria()
        return true
    }

    fun intentarAutoFundacionDescarte(): Boolean {
        val carta = descarte.lastOrNull() ?: return false
        if (!puedeColocarEnFundacionSol(carta, fundaciones[carta.palo])) return false
        services.sound.tocar(Efecto.CORRECT)
        fundaciones = fundaciones.toMutableList().also { it[carta.palo] = carta.valor }
        descarte = descarte.dropLast(1)
        seleccion = null
        revisarVictoria()
        return true
    }

    // Auto-completar: solo se activa cuando ya no queda ninguna decisión
    // real por tomar (todas las cartas boca arriba, mazo y descarte
    // vacíos) — igual que el botón del Solitario de Windows, no hace
    // trampa resolviendo un juego que todavía requiere pensar.
    fun puedeAutoCompletar() = mazo.isEmpty() && descarte.isEmpty() && columnas.all { col -> col.all { it.second } } && !ganado

    fun autoCompletar() {
        scope.launch {
            while (true) {
                val colIdx = columnas.indices.firstOrNull { columnas[it].isNotEmpty() && intentarAutoFundacion(it, columnas[it].size - 1) }
                if (colIdx == null) break
                kotlinx.coroutines.delay(140)
            }
        }
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
        acciones = {
            if (ganado) {
                Button(onClick = ::repartir) { Text("Jugar de nuevo") }
            } else if (puedeAutoCompletar()) {
                // "Al nivel de la PC": el botón de auto-completar clásico,
                // solo aparece cuando ya no hay ninguna decisión real que
                // tomar (todo boca arriba, sin mazo ni descarte).
                Button(onClick = ::autoCompletar) { Text("Auto-completar ✨") }
            }
        },
    ) {
        // Bug real reportado ("se corta la pantalla"): esta Column nunca
        // tuvo scroll VERTICAL, solo las filas de adentro tenían scroll
        // horizontal. En una pantalla baja (celular en horizontal, o
        // cualquier alto reducido) la cascada de 7 columnas + fundaciones +
        // encabezado suma más alto de lo que cabe, y el resto simplemente
        // se recortaba fuera de la vista, inalcanzable. verticalScroll en
        // el contenedor completo resuelve esto sin tocar el layout interno.
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)) {
            // Con scroll horizontal (no solo fillMaxWidth): en un celular
            // angosto 7-10 cartas de ancho fijo no caben en una fila que
            // solo se ajusta por peso — mismo bug de fondo ya encontrado y
            // corregido varias veces en la pizarra y en los otros juegos.
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            ) {
                // Mazo y descarte
                CartaDorso(habilitado = mazo.isNotEmpty() || descarte.isNotEmpty(), onClick = ::tocarMazo, vacio = mazo.isEmpty())
                Box(modifier = Modifier.size(width = 40.dp, height = 56.dp)) {
                    descarte.lastOrNull()?.let { c ->
                        CartaSolVista(c, seleccionada = seleccion == (-1 to 0), onClick = ::tocarDescarte, onDobleToque = { intentarAutoFundacionDescarte() })
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
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                            // Bug real reportado ("los solitarios no
                            // funcionan bien"): esto estaba dentro de un
                            // `Column`, que YA apila verticalmente cada
                            // carta debajo de la anterior (56dp completos),
                            // y ENCIMA cada una tenía su propio `offset` —
                            // los dos efectos se sumaban, así que las
                            // cartas quedaban separadas por huecos
                            // crecientes en vez de la cascada apretada y
                            // superpuesta de un solitario real. Con `Box`
                            // (que no apila solo) el offset manual es la
                            // única fuente de posición, y sí se superponen.
                            //
                            // Segundo bug real, más de fondo, del mismo
                            // "se corta la pantalla": `Modifier.offset()`
                            // NO agranda el tamaño medido del `Box` que lo
                            // contiene — así que este `Box`, sin una altura
                            // explícita, reportaba hacia afuera solo la
                            // altura de UNA carta (56dp), sin importar
                            // cuántas cartas tuviera en cascada debajo. El
                            // `verticalScroll` de más afuera entonces
                            // calculaba mal cuánto contenido había, y a
                            // veces ni activaba el scroll aunque la cascada
                            // sí se saliera visualmente de la pantalla.
                            // Con la altura puesta a mano (una carta +
                            // el offset de la última), el Box reporta su
                            // tamaño real y el scroll sabe hasta dónde ir.
                            Box(modifier = Modifier.height(56.dp + 18.dp * (col.size - 1))) {
                                col.forEachIndexed { i, (carta, bocaArriba) ->
                                    Box(modifier = Modifier.offset(y = (i * 18).dp)) {
                                        if (bocaArriba) {
                                            val esTope = i == col.size - 1
                                            CartaSolVista(
                                                carta,
                                                seleccionada = seleccion == (colIdx to i),
                                                onClick = { tocarColumna(colIdx, i) },
                                                onDobleToque = if (esTope) { { intentarAutoFundacion(colIdx, i) } } else null,
                                            )
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
internal fun CartaSolVista(carta: CartaSol, seleccionada: Boolean, onClick: () -> Unit, onDobleToque: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 56.dp)
            .shadow(3.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(if (seleccionada) 2.dp else 1.dp, if (seleccionada) Color(0xFFE0C23C) else Color(0xFFD9CDB4), RoundedCornerShape(6.dp))
            .then(
                if (onDobleToque != null) {
                    // "Al nivel de la PC": un solo toque selecciona (como
                    // antes), doble toque manda a la fundación de una vez
                    // — igual que el doble clic del Solitario de Windows.
                    Modifier.pointerInput(carta) {
                        detectTapGestures(onTap = { onClick() }, onDoubleTap = { onDobleToque() })
                    }
                } else {
                    Modifier.clickable { onClick() }
                },
            ),
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
