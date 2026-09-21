package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val PACMAN_COLS = 9
internal const val PACMAN_FILAS = 9
private const val TICKS_ENERGIZADO = 42
private const val VIDAS_INICIALES = 3

internal val MAPA_PACMAN = listOf(
    "#########",
    "#o.....o#",
    "#.###.#.#",
    "#.......#",
    "#.#.#.#.#",
    "#.......#",
    "#.###.#.#",
    "#o.....o#",
    "#########",
)

internal val CELDAS_LIBRES_PACMAN: Set<Int> =
    MAPA_PACMAN.flatMapIndexed { f, s -> s.indices.filter { s[it] != '#' }.map { f * PACMAN_COLS + it } }.toSet()

internal val PELLETS_POR_DEFECTO: Set<Int> =
    MAPA_PACMAN.flatMapIndexed { f, s -> s.indices.filter { s[it] == 'o' }.map { f * PACMAN_COLS + it } }.toSet()

private const val JUGADOR_INICIAL = 1 * PACMAN_COLS + 4 // fila 1, col 4 — fila abierta, no es un pellet
private const val CASA_FANTASMAS = 3 * PACMAN_COLS + 4 // centro del laberinto

internal enum class DireccionPacman(val delta: Int) {
    ARRIBA(-PACMAN_COLS), ABAJO(PACMAN_COLS), IZQUIERDA(-1), DERECHA(1),
}

internal fun destinoValido(celda: Int, direccion: DireccionPacman, cols: Int, libres: Set<Int>): Int? {
    val col = celda % cols
    if (direccion == DireccionPacman.IZQUIERDA && col == 0) return null
    if (direccion == DireccionPacman.DERECHA && col == cols - 1) return null
    val destino = celda + direccion.delta
    return if (destino in libres) destino else null
}

internal fun distanciaManhattan(a: Int, b: Int, cols: Int): Int {
    val filaA = a / cols
    val colA = a % cols
    val filaB = b / cols
    val colB = b % cols
    return kotlin.math.abs(filaA - filaB) + kotlin.math.abs(colA - colB)
}

/** El fantasma persigue: entre sus vecinos libres, elige el que más acerca al jugador. */
internal fun moverFantasmaPersiguiendo(fantasma: Int, jugador: Int, cols: Int, filas: Int, libres: Set<Int>): Int {
    val opciones = vecinos4(fantasma, cols, filas).filter { it in libres }
    return opciones.minByOrNull { distanciaManhattan(it, jugador, cols) } ?: fantasma
}

/** Asustado (tras un pellet de poder): huye, elige el vecino que más lo aleja del jugador. */
internal fun moverFantasmaAsustado(fantasma: Int, jugador: Int, cols: Int, filas: Int, libres: Set<Int>): Int {
    val opciones = vecinos4(fantasma, cols, filas).filter { it in libres }
    return opciones.maxByOrNull { distanciaManhattan(it, jugador, cols) } ?: fantasma
}

/**
 * Comepuntos — copia de la modalidad Pac-Man: laberinto en tiempo real (no
 * por turnos como la primera versión), fantasmas que persiguen de verdad
 * cada tic, y pellets de poder en las 4 esquinas abiertas que los vuelven
 * comestibles unos segundos. Simplificación declarada: no hay túnel de
 * envoltura lateral (el laberinto de esta versión tiene muro en todo el
 * borde), a cambio de un ciclo de persecución/huida real.
 */
@Composable
fun ComepuntosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("comepuntos")!!

    fun estadoInicial() = CELDAS_LIBRES_PACMAN - JUGADOR_INICIAL - CASA_FANTASMAS

    var jugador by remember { mutableStateOf(JUGADOR_INICIAL) }
    var direccion by remember { mutableStateOf(DireccionPacman.DERECHA) }
    var siguienteDireccion by remember { mutableStateOf(DireccionPacman.DERECHA) }
    var fantasmas by remember { mutableStateOf(listOf(CASA_FANTASMAS, CASA_FANTASMAS + 1)) }
    var comestibles by remember { mutableStateOf(estadoInicial()) }
    var pellets by remember { mutableStateOf(PELLETS_POR_DEFECTO) }
    var energizadoTicks by remember { mutableStateOf(0) }
    var puntaje by remember { mutableStateOf(0) }
    var vidas by remember { mutableStateOf(VIDAS_INICIALES) }
    var terminado by remember { mutableStateOf(false) }
    var gano by remember { mutableStateOf(false) }

    fun reiniciar() {
        jugador = JUGADOR_INICIAL
        direccion = DireccionPacman.DERECHA
        siguienteDireccion = DireccionPacman.DERECHA
        fantasmas = listOf(CASA_FANTASMAS, CASA_FANTASMAS + 1)
        comestibles = estadoInicial()
        pellets = PELLETS_POR_DEFECTO
        energizadoTicks = 0
        puntaje = 0
        vidas = VIDAS_INICIALES
        terminado = false
        gano = false
    }

    fun reposicionar() {
        jugador = JUGADOR_INICIAL
        fantasmas = listOf(CASA_FANTASMAS, CASA_FANTASMAS + 1)
        energizadoTicks = 0
    }

    fun girar(nueva: DireccionPacman) {
        if (!terminado) siguienteDireccion = nueva
    }

    LaunchedEffect(terminado) {
        if (terminado) return@LaunchedEffect
        while (true) {
            delay(220L)
            if (terminado) break

            destinoValido(jugador, siguienteDireccion, PACMAN_COLS, CELDAS_LIBRES_PACMAN)?.let {
                direccion = siguienteDireccion
                jugador = it
            } ?: destinoValido(jugador, direccion, PACMAN_COLS, CELDAS_LIBRES_PACMAN)?.let { jugador = it }

            if (jugador in comestibles) {
                comestibles = comestibles - jugador
                if (jugador in pellets) {
                    pellets = pellets - jugador
                    energizadoTicks = TICKS_ENERGIZADO
                    services.sound.tocar(Efecto.CORRECT)
                } else {
                    puntaje += 1
                    services.sound.tocar(Efecto.CLICK)
                }
            }

            val energizado = energizadoTicks > 0
            if (energizadoTicks > 0) energizadoTicks -= 1

            fantasmas = fantasmas.map { f ->
                if (energizado) {
                    moverFantasmaAsustado(f, jugador, PACMAN_COLS, PACMAN_FILAS, CELDAS_LIBRES_PACMAN)
                } else {
                    moverFantasmaPersiguiendo(f, jugador, PACMAN_COLS, PACMAN_FILAS, CELDAS_LIBRES_PACMAN)
                }
            }

            if (jugador in fantasmas) {
                if (energizado) {
                    fantasmas = fantasmas.map { if (it == jugador) CASA_FANTASMAS else it }
                    puntaje += 5
                    services.sound.tocar(Efecto.WIN)
                } else {
                    vidas -= 1
                    services.sound.tocar(Efecto.WRONG)
                    if (vidas <= 0) {
                        terminado = true
                    } else {
                        reposicionar()
                    }
                }
            }

            if (!terminado && comestibles.isEmpty()) {
                terminado = true
                gano = true
                services.sound.tocar(Efecto.WIN)
                scope.launch { services.progress.completarNivel(juego.id, 1) }
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            terminado && gano -> "¡Laberinto limpio! Puntaje $puntaje"
            terminado -> "Te alcanzó un fantasma — puntaje $puntaje"
            else -> "Puntos: ${comestibles.size} · Vidas $vidas${if (energizadoTicks > 0) " · ⚡ come fantasmas" else ""}"
        },
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Reintentar") } },
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val energizado = energizadoTicks > 0
            Column(
                modifier = Modifier
                    .size((PACMAN_COLS * 34).dp, (PACMAN_FILAS * 34).dp)
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF11152E)),
            ) {
                for (f in 0 until PACMAN_FILAS) {
                    Row {
                        for (c in 0 until PACMAN_COLS) {
                            val i = f * PACMAN_COLS + c
                            val esMuro = i !in CELDAS_LIBRES_PACMAN
                            Box(
                                modifier = Modifier.size(34.dp).background(if (esMuro) Color(0xFF3858B8) else Color(0xFF11152E)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    when {
                                        i == jugador -> "🟡"
                                        i in fantasmas -> if (energizado) "🔵" else "👻"
                                        i in pellets -> "⭐"
                                        i in comestibles -> "·"
                                        else -> ""
                                    },
                                    color = Color.White,
                                    fontSize = 17.sp,
                                )
                            }
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 12.dp)) {
                BotonPacman("▲") { girar(DireccionPacman.ARRIBA) }
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    BotonPacman("◀") { girar(DireccionPacman.IZQUIERDA) }
                    BotonPacman("▼") { girar(DireccionPacman.ABAJO) }
                    BotonPacman("▶") { girar(DireccionPacman.DERECHA) }
                }
            }
        }
    }
}

@Composable
private fun BotonPacman(texto: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(texto, fontSize = 20.sp) }
}
