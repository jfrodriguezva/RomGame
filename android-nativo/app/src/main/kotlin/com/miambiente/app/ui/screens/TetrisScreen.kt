package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonArcade
import com.miambiente.app.ui.materials.MarcadorArcade
import com.miambiente.app.ui.materials.MarcoArcade
import kotlinx.coroutines.delay

internal const val TETRIS_FILAS = 16
internal const val TETRIS_COLS = 9
private val CELDA_TETRIS = 20.dp

internal data class PiezaTetris(val color: Color, val base: List<Pair<Int, Int>>, val n: Int)

internal val PIEZAS_TETRIS = listOf(
    PiezaTetris(Color(0xFF3E9BE0), listOf(1 to 0, 1 to 1, 1 to 2, 1 to 3), 4), // I
    PiezaTetris(Color(0xFFE0C23C), listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1), 2), // O
    PiezaTetris(Color(0xFFA97FC7), listOf(0 to 1, 1 to 0, 1 to 1, 1 to 2), 3), // T
    PiezaTetris(Color(0xFF6FBF73), listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1), 3), // S
    PiezaTetris(Color(0xFFD9433A), listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2), 3), // Z
    PiezaTetris(Color(0xFF3E7AA3), listOf(0 to 0, 1 to 0, 1 to 1, 1 to 2), 3), // J
    PiezaTetris(Color(0xFFE0925C), listOf(0 to 2, 1 to 0, 1 to 1, 1 to 2), 3), // L
)

internal data class EstadoPiezaTetris(val pieza: PiezaTetris, val rotacion: Int, val fila: Int, val col: Int)

/** Rotar 90°: (r,c) en una caja de lado `n` pasa a (c, n-1-r). Se aplica `rotacion` veces. */
internal fun celdasDeTetris(pieza: PiezaTetris, rotacion: Int): List<Pair<Int, Int>> {
    var celdas = pieza.base
    repeat(((rotacion % 4) + 4) % 4) { celdas = celdas.map { (r, c) -> c to (pieza.n - 1 - r) } }
    return celdas
}

internal fun celdasOcupadasTetris(estado: EstadoPiezaTetris): List<Pair<Int, Int>> =
    celdasDeTetris(estado.pieza, estado.rotacion).map { (r, c) -> (estado.fila + r) to (estado.col + c) }

internal fun colisionaTetris(estado: EstadoPiezaTetris, tablero: List<List<Color?>>): Boolean =
    celdasOcupadasTetris(estado).any { (r, c) ->
        c !in 0 until TETRIS_COLS || r >= TETRIS_FILAS || (r >= 0 && tablero[r][c] != null)
    }

private fun tableroVacioTetris(): List<List<Color?>> = List(TETRIS_FILAS) { List(TETRIS_COLS) { null } }

private fun piezaInicial(pieza: PiezaTetris) = EstadoPiezaTetris(pieza, 0, fila = -1, col = TETRIS_COLS / 2 - pieza.n / 2)

/** Dónde caería la pieza si se soltara ahora — la "pieza fantasma" que ayuda a planear sin depender de reflejos, clave para el público infantil de esta app. */
internal fun posicionFantasma(estado: EstadoPiezaTetris, tablero: List<List<Color?>>): EstadoPiezaTetris {
    var candidato = estado
    while (!colisionaTetris(candidato.copy(fila = candidato.fila + 1), tablero)) candidato = candidato.copy(fila = candidato.fila + 1)
    return candidato
}

/**
 * Tetris real, no una versión decorativa: caída continua, las 7 piezas
 * clásicas con sus 4 rotaciones (calculadas por rotación de matriz, no
 * copiadas a mano una por una), líneas completas que se limpian y suben
 * el puntaje, velocidad que aumenta con las líneas hechas, y fin de
 * partida real cuando ya no cabe una pieza nueva. Controles en pantalla
 * (mover, girar, bajar, caída rápida) porque esta app es táctil, sin
 * teclado.
 */
@Composable
fun TetrisScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("tetris")!!

    var tablero by remember { mutableStateOf(tableroVacioTetris()) }
    var siguiente by remember { mutableStateOf(PIEZAS_TETRIS.random()) }
    var actual by remember { mutableStateOf(piezaInicial(PIEZAS_TETRIS.random())) }
    var puntaje by remember { mutableStateOf(0) }
    var lineas by remember { mutableStateOf(0) }
    var terminado by remember { mutableStateOf(false) }
    var estrellaDada by remember { mutableStateOf(false) }

    fun reiniciar() {
        tablero = tableroVacioTetris()
        siguiente = PIEZAS_TETRIS.random()
        actual = piezaInicial(PIEZAS_TETRIS.random())
        puntaje = 0
        lineas = 0
        terminado = false
    }

    fun fijarPieza() {
        val nuevo = tablero.map { it.toMutableList() }
        celdasOcupadasTetris(actual).forEach { (r, c) -> if (r in 0 until TETRIS_FILAS && c in 0 until TETRIS_COLS) nuevo[r][c] = actual.pieza.color }
        val filasLlenas = nuevo.indices.filter { i -> nuevo[i].all { it != null } }
        val tableroTrasLimpiar = if (filasLlenas.isEmpty()) {
            nuevo
        } else {
            services.sound.tocar(Efecto.CORRECT)
            services.haptics.vibrar(Patron.ACIERTO)
            puntaje += when (filasLlenas.size) { 1 -> 100; 2 -> 300; 3 -> 500; else -> 800 }
            lineas += filasLlenas.size
            List(filasLlenas.size) { List(TETRIS_COLS) { null } } + nuevo.filterIndexed { i, _ -> i !in filasLlenas }
        }
        tablero = tableroTrasLimpiar
        val nuevaPieza = piezaInicial(siguiente)
        siguiente = PIEZAS_TETRIS.random()
        // Fin de partida real: si la pieza nueva, ya en su fila de entrada
        // (no arriba del tablero), chocaría contra algo que quedó fijo,
        // ya no hay dónde ponerla.
        if (colisionaTetris(nuevaPieza.copy(fila = 0), tableroTrasLimpiar)) {
            terminado = true
            services.sound.tocar(Efecto.WRONG)
        } else {
            actual = nuevaPieza
        }
    }

    fun bajar() {
        if (terminado) return
        val candidato = actual.copy(fila = actual.fila + 1)
        if (!colisionaTetris(candidato, tablero)) actual = candidato else fijarPieza()
    }

    fun mover(dc: Int) {
        if (terminado) return
        val candidato = actual.copy(col = actual.col + dc)
        if (!colisionaTetris(candidato, tablero)) actual = candidato
    }

    fun rotar() {
        if (terminado) return
        val candidato = actual.copy(rotacion = actual.rotacion + 1)
        when {
            !colisionaTetris(candidato, tablero) -> actual = candidato
            !colisionaTetris(candidato.copy(col = candidato.col - 1), tablero) -> actual = candidato.copy(col = candidato.col - 1)
            !colisionaTetris(candidato.copy(col = candidato.col + 1), tablero) -> actual = candidato.copy(col = candidato.col + 1)
        }
    }

    fun caidaDura() {
        if (terminado) return
        actual = posicionFantasma(actual, tablero)
        services.sound.tocar(Efecto.CLICK)
        fijarPieza()
    }

    LaunchedEffect(terminado) {
        if (terminado) return@LaunchedEffect
        while (true) {
            val velocidad = (700L - lineas.coerceAtMost(18) * 30L).coerceAtLeast(150L)
            delay(velocidad)
            if (terminado) break
            bajar()
        }
    }

    LaunchedEffect(lineas) {
        if (lineas >= 10 && !estrellaDada) {
            estrellaDada = true
            services.progress.completarNivel(juego.id, 1)
        }
    }

    val ocupadasActual = celdasOcupadasTetris(actual).toSet()
    val ocupadasFantasma = celdasOcupadasTetris(posicionFantasma(actual, tablero)).toSet() - ocupadasActual

    GameShell(
        juego = juego,
        consigna = if (terminado) "Juego terminado — Puntaje: $puntaje" else "Acomoda las piezas y completa líneas",
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MarcadorArcade("Puntaje: $puntaje · Líneas: $lineas")
            MarcoArcade(colorFondo = Color(0xFF211D33), modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier.size(width = CELDA_TETRIS * TETRIS_COLS, height = CELDA_TETRIS * TETRIS_FILAS),
                    ) {
                        Column {
                            for (f in 0 until TETRIS_FILAS) {
                                Row {
                                    for (c in 0 until TETRIS_COLS) {
                                        val colorFijo = tablero[f][c]
                                        val esActual = (f to c) in ocupadasActual
                                        val esFantasma = (f to c) in ocupadasFantasma
                                        val color = colorFijo ?: when {
                                            esActual -> actual.pieza.color
                                            esFantasma -> actual.pieza.color.copy(alpha = 0.25f)
                                            else -> Color.Transparent
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(CELDA_TETRIS)
                                                .padding(1.dp)
                                                .background(color, RoundedCornerShape(2.dp)),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Siguiente", fontSize = 11.sp, color = Color.White)
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(CELDA_TETRIS * 4)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF33304A)),
                        ) {
                            // offset por celda: sin esto las 4 celdas de la pieza
                            // se dibujaban todas en el mismo lugar (esquina
                            // superior), en vez de mostrar la forma real.
                            celdasDeTetris(siguiente, 0).forEach { (r, c) ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = CELDA_TETRIS * c, y = CELDA_TETRIS * r)
                                        .size(CELDA_TETRIS)
                                        .padding(1.dp)
                                        .background(siguiente.color, RoundedCornerShape(2.dp)),
                                )
                            }
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 16.dp)) {
                BotonArcade("⬅️") { mover(-1) }
                BotonArcade("🔄") { rotar() }
                BotonArcade("⬇️") { bajar() }
                BotonArcade("⏬") { caidaDura() }
                BotonArcade("➡️") { mover(1) }
            }
        }
    }
}
