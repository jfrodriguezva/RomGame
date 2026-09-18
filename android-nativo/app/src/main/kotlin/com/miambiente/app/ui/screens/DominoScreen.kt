package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Dominó doble-6 real: valores del 0 al 6 en cada mitad, como el juego de mesa de verdad. */
private const val VALOR_MAXIMO = 6

internal data class Ficha(val id: Int, val a: Int, val b: Int)

/** Genera el set doble-6 completo: todas las parejas a<=b entre 0 y 6 (28 fichas). */
internal fun setCompleto(): List<Ficha> {
    var id = 0
    val fichas = mutableListOf<Ficha>()
    for (a in 0..VALOR_MAXIMO) for (b in a..VALOR_MAXIMO) fichas.add(Ficha(id++, a, b))
    return fichas
}

internal fun encaja(f: Ficha, extremo: Int) = f.a == extremo || f.b == extremo
internal fun otroLado(f: Ficha, extremo: Int) = if (f.a == extremo) f.b else f.a

private val COLOR_PIP = Color(0xFF3A3630)
private val COLOR_FICHA = Color(0xFFFFFBF2)
private val COLOR_BORDE_FICHA = Color(0xFFD9CDB4)

private fun posicionesPip(valor: Int): List<Pair<Float, Float>> = when (valor) {
    0 -> emptyList()
    1 -> listOf(0.5f to 0.5f)
    2 -> listOf(0.26f to 0.26f, 0.74f to 0.74f)
    3 -> listOf(0.26f to 0.26f, 0.5f to 0.5f, 0.74f to 0.74f)
    4 -> listOf(0.26f to 0.26f, 0.74f to 0.26f, 0.26f to 0.74f, 0.74f to 0.74f)
    5 -> listOf(0.26f to 0.26f, 0.74f to 0.26f, 0.5f to 0.5f, 0.26f to 0.74f, 0.74f to 0.74f)
    else -> listOf(0.26f to 0.18f, 0.26f to 0.5f, 0.26f to 0.82f, 0.74f to 0.18f, 0.74f to 0.5f, 0.74f to 0.82f)
}

/** Una mitad de ficha: dibuja los puntos reales de dominó, no un número escrito. */
@Composable
private fun MitadDomino(valor: Int, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val radio = size.minDimension * 0.1f
        posicionesPip(valor).forEach { (fx, fy) ->
            drawCircle(COLOR_PIP, radius = radio, center = Offset(size.width * fx, size.height * fy))
        }
    }
}

/** Ficha física completa: dos mitades separadas por una línea, como una de verdad. */
@Composable
private fun TileDomino(f: Ficha, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val forma = RoundedCornerShape(7.dp)
    Row(
        modifier = modifier
            .shadow(2.dp, forma)
            .clip(forma)
            .background(COLOR_FICHA)
            .border(1.5.dp, COLOR_BORDE_FICHA, forma)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    ) {
        MitadDomino(f.a, Modifier.weight(1f).fillMaxHeight())
        Box(Modifier.fillMaxHeight().width(1.5.dp).background(COLOR_BORDE_FICHA))
        MitadDomino(f.b, Modifier.weight(1f).fillMaxHeight())
    }
}

/** Reverso de una ficha boca abajo — para mostrar cuántas tiene el rival sin revelar cuáles. */
@Composable
private fun DorsoFicha(modifier: Modifier = Modifier) {
    val forma = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .shadow(1.dp, forma)
            .clip(forma)
            .background(Color(0xFF3E5C78))
            .border(1.dp, Color(0xFF2C4358), forma),
    )
}

/**
 * Dominó doble-6 con reglas reales (cadena con dos extremos, mano de 7,
 * pozo para robar) — antes "encajaba" 5 dibujos sin números; ahora es el
 * dominó de verdad que se pidió ("hazlo por números, o sea normal").
 * El rival también se ve: avatar + sus fichas boca abajo, no solo un
 * contador de texto.
 */
@Composable
fun DominoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("domino")!!

    fun repartir(): Triple<List<Ficha>, List<Ficha>, List<Ficha>> {
        val barajado = setCompleto().shuffled()
        return Triple(barajado.take(7), barajado.subList(7, 14), barajado.subList(14, 28))
    }

    var dosJugadores by remember { mutableStateOf(false) }
    var manoJugador by remember { mutableStateOf(listOf<Ficha>()) }
    var manoCpu by remember { mutableStateOf(listOf<Ficha>()) }
    var pozo by remember { mutableStateOf(listOf<Ficha>()) }
    var cadena by remember { mutableStateOf(listOf<Ficha>()) }
    var izquierda by remember { mutableStateOf(-1) }
    var derecha by remember { mutableStateOf(-1) }
    var turno by remember { mutableStateOf("jugador") }
    var mensaje by remember { mutableStateOf("Toca una ficha para empezar") }
    var terminado by remember { mutableStateOf(false) }

    fun reiniciar() {
        val (j, c, p) = repartir()
        manoJugador = j; manoCpu = c; pozo = p
        cadena = emptyList(); izquierda = -1; derecha = -1
        turno = "jugador"; terminado = false
        mensaje = "Toca una ficha para empezar"
    }

    // Se pidió poder jugar entre más de una persona: con `dosJugadores`
    // activo, la mano del "rival" (`manoCpu`, el nombre se queda igual
    // para no reescribir toda la lógica) se juega por toques como la
    // propia, y se muestra boca ARRIBA en vez de boca abajo — es la misma
    // pantalla, ambos jugadores la ven, así que no tiene sentido esconder
    // fichas del otro jugador humano como si fuera secreto.
    fun cambiarModo(activarDosJugadores: Boolean) {
        dosJugadores = activarDosJugadores
        reiniciar()
    }

    LaunchedEffect(Unit) { reiniciar() }

    // La ficha se guarda orientada en la cadena: el lado que conecta con el
    // extremo previo siempre queda pegado a él, así las mitades visibles se
    // ven encajadas de verdad (como fichas físicas), no en un orden fijo.
    fun colocar(f: Ficha, enIzquierda: Boolean) {
        if (cadena.isEmpty()) {
            cadena = listOf(f); izquierda = f.a; derecha = f.b
        } else if (enIzquierda) {
            val nuevoExtremo = otroLado(f, izquierda)
            cadena = listOf(Ficha(f.id, nuevoExtremo, izquierda)) + cadena
            izquierda = nuevoExtremo
        } else {
            val nuevoExtremo = otroLado(f, derecha)
            cadena = cadena + Ficha(f.id, derecha, nuevoExtremo)
            derecha = nuevoExtremo
        }
    }

    fun terminarSiCorresponde(): Boolean {
        if (manoJugador.isEmpty()) {
            terminado = true; mensaje = "¡Ganaste! 🎉"
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            return true
        }
        if (manoCpu.isEmpty()) {
            terminado = true; mensaje = if (dosJugadores) "¡Ganó Jugador 2! 🎉" else "Ganó la computadora, ¡otra vez!"
            services.sound.tocar(if (dosJugadores) Efecto.WIN else Efecto.WRONG)
            return true
        }
        val nadiePuede = pozo.isEmpty() &&
            manoJugador.none { encaja(it, izquierda) || encaja(it, derecha) } &&
            manoCpu.none { encaja(it, izquierda) || encaja(it, derecha) }
        if (nadiePuede) {
            terminado = true
            mensaje = when {
                manoJugador.size < manoCpu.size -> if (dosJugadores) "¡Empate a favor de Jugador 1!" else "¡Empate a tu favor!"
                manoJugador.size > manoCpu.size -> if (dosJugadores) "¡Empate a favor de Jugador 2!" else "Nadie puede más — ganó la computadora"
                else -> "¡Empate parejo!"
            }
            return true
        }
        return false
    }

    fun jugarJugador(f: Ficha) {
        if (turno != "jugador" || terminado) return
        val enIzq = cadena.isEmpty() || f.a == izquierda || f.b == izquierda
        val enDer = cadena.isEmpty() || f.a == derecha || f.b == derecha
        if (!enIzq && !enDer) { services.sound.tocar(Efecto.WRONG); return }
        services.sound.tocar(Efecto.CORRECT)
        // Preferencia simple: si encaja a la derecha, ahí va; si no, a la izquierda.
        colocar(f, enIzquierda = !enDer)
        manoJugador = manoJugador.filter { it.id != f.id }
        if (!terminarSiCorresponde()) {
            turno = "cpu"
            mensaje = if (dosJugadores) "Turno de Jugador 2" else "Turno de la computadora"
        }
    }

    // Mano 2 jugada por un segundo humano (solo cuando `dosJugadores`
    // está activo): mismas reglas que `jugarJugador`, nada de IA.
    fun jugarSegundo(f: Ficha) {
        if (!dosJugadores || turno != "cpu" || terminado) return
        val enIzq = cadena.isEmpty() || f.a == izquierda || f.b == izquierda
        val enDer = cadena.isEmpty() || f.a == derecha || f.b == derecha
        if (!enIzq && !enDer) { services.sound.tocar(Efecto.WRONG); return }
        services.sound.tocar(Efecto.CORRECT)
        colocar(f, enIzquierda = !enDer)
        manoCpu = manoCpu.filter { it.id != f.id }
        if (!terminarSiCorresponde()) { turno = "jugador"; mensaje = "Turno de Jugador 1" }
    }

    fun robarSegundo() {
        if (!dosJugadores || turno != "cpu" || terminado || pozo.isEmpty()) return
        manoCpu = manoCpu + pozo.first()
        pozo = pozo.drop(1)
        services.sound.tocar(Efecto.CLICK)
    }

    LaunchedEffect(turno, terminado, dosJugadores) {
        if (dosJugadores || turno != "cpu" || terminado) return@LaunchedEffect
        delay(700)
        var jugable = manoCpu.firstOrNull { encaja(it, izquierda) || encaja(it, derecha) }
        while (jugable == null && pozo.isNotEmpty()) {
            manoCpu = manoCpu + pozo.first(); pozo = pozo.drop(1)
            jugable = manoCpu.firstOrNull { encaja(it, izquierda) || encaja(it, derecha) }
        }
        if (jugable != null) {
            val enDer = cadena.isEmpty() || jugable.a == derecha || jugable.b == derecha
            colocar(jugable, enIzquierda = !enDer)
            manoCpu = manoCpu.filter { it.id != jugable.id }
            services.sound.tocar(Efecto.CLICK)
        }
        if (!terminarSiCorresponde()) { turno = "jugador"; mensaje = "Tu turno" }
    }

    fun robar() {
        if (turno != "jugador" || terminado || pozo.isEmpty()) return
        manoJugador = manoJugador + pozo.first()
        pozo = pozo.drop(1)
        services.sound.tocar(Efecto.CLICK)
    }

    GameShell(
        juego = juego,
        consigna = mensaje,
        celebrar = terminado && manoJugador.isEmpty(),
        onVolver = onVolver,
        acciones = {
            if (terminado) {
                Button(onClick = ::reiniciar) { Text("Jugar de nuevo") }
            } else if (turno == "jugador" && manoJugador.none { encaja(it, izquierda) || encaja(it, derecha) } && cadena.isNotEmpty()) {
                Button(onClick = ::robar, enabled = pozo.isNotEmpty()) { Text("Robar del pozo (${pozo.size})") }
            } else if (dosJugadores && turno == "cpu" && manoCpu.none { encaja(it, izquierda) || encaja(it, derecha) } && cadena.isNotEmpty()) {
                Button(onClick = ::robarSegundo, enabled = pozo.isNotEmpty()) { Text("Robar del pozo (${pozo.size})") }
            }
        },
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                FilterChip(
                    selected = !dosJugadores,
                    onClick = { if (dosJugadores) cambiarModo(false) },
                    label = { Text("🤖 Vs. computadora") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF3E5C78), selectedLabelColor = Color.White),
                )
                FilterChip(
                    selected = dosJugadores,
                    onClick = { if (!dosJugadores) cambiarModo(true) },
                    label = { Text("👫 Dos jugadores") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8A5A2B), selectedLabelColor = Color.White),
                )
            }
            // El rival ahora se ve de verdad: avatar + sus fichas boca abajo
            // (antes solo un texto "Computadora: X fichas") — salvo en modo
            // dos jugadores, donde se muestran boca ARRIBA y se juegan por
            // toques, porque es un segundo humano en la misma pantalla, no
            // un secreto que esconderle a la computadora.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(30.dp).clip(RoundedCornerShape(50)).background(Color(0xFF3E5C78)),
                    contentAlignment = Alignment.Center,
                ) { Text(if (dosJugadores) "🧑" else "🤖", fontSize = 15.sp) }
                Text(
                    when {
                        dosJugadores -> if (turno == "cpu") "Turno de Jugador 2" else "Jugador 2: ${manoCpu.size} fichas"
                        turno == "cpu" && !terminado -> "Computadora pensando…"
                        else -> "Computadora: ${manoCpu.size} fichas"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (dosJugadores) {
                    items(manoCpu, key = { it.id }) { f ->
                        TileDomino(f, modifier = Modifier.size(width = 40.dp, height = 58.dp), onClick = { jugarSegundo(f) })
                    }
                } else {
                    items(manoCpu.size) { DorsoFicha(Modifier.size(width = 22.dp, height = 34.dp)) }
                }
            }

            LazyRow(modifier = Modifier.padding(vertical = 18.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(cadena, key = { it.id }) { f -> TileDomino(f, modifier = Modifier.size(width = 44.dp, height = 60.dp)) }
            }

            Text(if (dosJugadores) "Jugador 1" else "Tu mano", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            // LazyRow (no Row): la mano puede crecer al robar del pozo —
            // con Row simple, fichas de más quedaban fuera de la pantalla
            // sin forma de alcanzarlas (mismo bug que la pizarra).
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(manoJugador, key = { it.id }) { f ->
                    TileDomino(f, modifier = Modifier.size(width = 52.dp, height = 76.dp), onClick = { jugarJugador(f) })
                }
            }
        }
    }
}
