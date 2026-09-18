package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.AnimatedPieza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal enum class TipoPieza { PEON, TORRE, CABALLO, ALFIL, REINA, REY }
// `id` al final con valor por defecto: no rompe las pruebas existentes.
// Es lo que permite animar el movimiento (ver AnimatedPieza) sin perder
// de vista qué pieza es cuál al moverse.
internal data class PiezaAjedrez(val fila: Int, val col: Int, val tipo: TipoPieza, val esJugador: Boolean, val id: Int = 0)
internal data class MovidaAjedrez(val pieza: PiezaAjedrez, val filaDestino: Int, val colDestino: Int, val captura: PiezaAjedrez? = null)

private fun enTablero(f: Int, c: Int) = f in 0..7 && c in 0..7

internal fun tableroInicialAjedrez(): List<PiezaAjedrez> {
    val orden = listOf(TipoPieza.TORRE, TipoPieza.CABALLO, TipoPieza.ALFIL, TipoPieza.REINA, TipoPieza.REY, TipoPieza.ALFIL, TipoPieza.CABALLO, TipoPieza.TORRE)
    val fichas = mutableListOf<PiezaAjedrez>()
    var id = 0
    for (col in 0..7) {
        fichas.add(PiezaAjedrez(0, col, orden[col], esJugador = false, id = id++))
        fichas.add(PiezaAjedrez(1, col, TipoPieza.PEON, esJugador = false, id = id++))
        fichas.add(PiezaAjedrez(6, col, TipoPieza.PEON, esJugador = true, id = id++))
        fichas.add(PiezaAjedrez(7, col, orden[col], esJugador = true, id = id++))
    }
    return fichas
}

/**
 * Movidas pseudo-legales (sin filtrar por jaque propio) de una pieza —
 * las reglas reales de cada tipo, no una simplificación: peón con doble
 * paso inicial y captura diagonal, torre/alfil/reina deslizándose hasta
 * chocar, caballo saltando, rey un paso. Sin enroque ni al paso todavía
 * (documentado como alcance real, no un descuido).
 */
internal fun movidasPseudoLegales(pieza: PiezaAjedrez, tablero: List<PiezaAjedrez>): List<MovidaAjedrez> {
    val ocupadas = tablero.associateBy { it.fila to it.col }
    val movidas = mutableListOf<MovidaAjedrez>()

    fun intentar(f: Int, c: Int): Boolean {
        if (!enTablero(f, c)) return false
        val ocupante = ocupadas[f to c]
        if (ocupante == null) { movidas.add(MovidaAjedrez(pieza, f, c)); return true }
        if (ocupante.esJugador != pieza.esJugador) movidas.add(MovidaAjedrez(pieza, f, c, ocupante))
        return false
    }

    when (pieza.tipo) {
        TipoPieza.PEON -> {
            val dir = if (pieza.esJugador) -1 else 1
            val filaInicial = if (pieza.esJugador) 6 else 1
            if (enTablero(pieza.fila + dir, pieza.col) && ocupadas[pieza.fila + dir to pieza.col] == null) {
                movidas.add(MovidaAjedrez(pieza, pieza.fila + dir, pieza.col))
                if (pieza.fila == filaInicial && ocupadas[pieza.fila + 2 * dir to pieza.col] == null) {
                    movidas.add(MovidaAjedrez(pieza, pieza.fila + 2 * dir, pieza.col))
                }
            }
            for (dc in intArrayOf(-1, 1)) {
                val nf = pieza.fila + dir
                val nc = pieza.col + dc
                val objetivo = ocupadas[nf to nc]
                if (enTablero(nf, nc) && objetivo != null && objetivo.esJugador != pieza.esJugador) {
                    movidas.add(MovidaAjedrez(pieza, nf, nc, objetivo))
                }
            }
        }
        TipoPieza.CABALLO -> {
            val saltos = listOf(-2 to -1, -2 to 1, -1 to -2, -1 to 2, 1 to -2, 1 to 2, 2 to -1, 2 to 1)
            for ((df, dc) in saltos) intentar(pieza.fila + df, pieza.col + dc)
        }
        TipoPieza.REY -> {
            for (df in -1..1) for (dc in -1..1) if (df != 0 || dc != 0) intentar(pieza.fila + df, pieza.col + dc)
        }
        TipoPieza.TORRE, TipoPieza.ALFIL, TipoPieza.REINA -> {
            val direcciones = when (pieza.tipo) {
                TipoPieza.TORRE -> listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
                TipoPieza.ALFIL -> listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
                else -> listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1, -1 to -1, -1 to 1, 1 to -1, 1 to 1)
            }
            for ((df, dc) in direcciones) {
                var nf = pieza.fila + df
                var nc = pieza.col + dc
                while (intentar(nf, nc)) { nf += df; nc += dc }
            }
        }
    }
    return movidas
}

internal fun casillaAtacadaPor(fila: Int, col: Int, atacanteEsJugador: Boolean, tablero: List<PiezaAjedrez>): Boolean =
    tablero.filter { it.esJugador == atacanteEsJugador }
        .any { atacante -> movidasPseudoLegales(atacante, tablero).any { it.filaDestino == fila && it.colDestino == col } }

internal fun aplicarMovidaAjedrez(tablero: List<PiezaAjedrez>, movida: MovidaAjedrez): List<PiezaAjedrez> {
    val sinCaptura = tablero.filter { it != movida.pieza && it != movida.captura }
    var nuevaPieza = movida.pieza.copy(fila = movida.filaDestino, col = movida.colDestino)
    if (nuevaPieza.tipo == TipoPieza.PEON) {
        val filaFinal = if (nuevaPieza.esJugador) 0 else 7
        if (nuevaPieza.fila == filaFinal) nuevaPieza = nuevaPieza.copy(tipo = TipoPieza.REINA)
    }
    return sinCaptura + nuevaPieza
}

internal fun reyEnJaque(tablero: List<PiezaAjedrez>, esJugador: Boolean): Boolean {
    val rey = tablero.find { it.esJugador == esJugador && it.tipo == TipoPieza.REY } ?: return false
    return casillaAtacadaPor(rey.fila, rey.col, !esJugador, tablero)
}

/** Movidas legales de verdad: pseudo-legales que además no dejan el propio rey en jaque. */
internal fun movidasLegalesAjedrez(tablero: List<PiezaAjedrez>, esJugador: Boolean): List<MovidaAjedrez> =
    tablero.filter { it.esJugador == esJugador }
        .flatMap { movidasPseudoLegales(it, tablero) }
        .filter { m -> !reyEnJaque(aplicarMovidaAjedrez(tablero, m), esJugador) }

private fun valorPieza(tipo: TipoPieza) = when (tipo) {
    TipoPieza.PEON -> 1
    TipoPieza.CABALLO, TipoPieza.ALFIL -> 3
    TipoPieza.TORRE -> 5
    TipoPieza.REINA -> 9
    TipoPieza.REY -> 0
}

private fun evaluarAjedrez(tablero: List<PiezaAjedrez>): Int = tablero.sumOf { p -> valorPieza(p.tipo) * (if (p.esJugador) -1 else 1) }

private fun minimaxAjedrez(tablero: List<PiezaAjedrez>, profundidad: Int, turnoJugador: Boolean, esMaximizando: Boolean): Int {
    val movidas = movidasLegalesAjedrez(tablero, turnoJugador)
    if (movidas.isEmpty()) return if (reyEnJaque(tablero, turnoJugador)) (if (esMaximizando) -500 else 500) else 0
    if (profundidad == 0) return evaluarAjedrez(tablero)
    val valores = movidas.map { m -> minimaxAjedrez(aplicarMovidaAjedrez(tablero, m), profundidad - 1, !turnoJugador, !esMaximizando) }
    return if (esMaximizando) valores.max() else valores.min()
}

/** Elige la mejor movida para la computadora (minimax real a 2 capas, no al azar). */
internal fun mejorMovidaAjedrez(tablero: List<PiezaAjedrez>): MovidaAjedrez {
    val movidas = movidasLegalesAjedrez(tablero, esJugador = false)
    return movidas.maxBy { m -> minimaxAjedrez(aplicarMovidaAjedrez(tablero, m), 1, true, false) }
}

private fun simboloPieza(p: PiezaAjedrez): String = when (p.tipo) {
    TipoPieza.PEON -> if (p.esJugador) "♙" else "♟"
    TipoPieza.TORRE -> if (p.esJugador) "♖" else "♜"
    TipoPieza.CABALLO -> if (p.esJugador) "♘" else "♞"
    TipoPieza.ALFIL -> if (p.esJugador) "♗" else "♝"
    TipoPieza.REINA -> if (p.esJugador) "♕" else "♛"
    TipoPieza.REY -> if (p.esJugador) "♔" else "♚"
}

/**
 * Ajedrez real: movimientos de cada pieza según sus reglas, jaque real
 * (no se puede dejar el propio rey atacado), jaque mate y ahogado
 * detectados de verdad, con IA de minimax (no al azar). Sin enroque ni
 * captura al paso todavía — alcance real documentado, no un descuido; la
 * promoción es automática a reina.
 */
@Composable
fun AjedrezScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("ajedrez")!!

    var tablero by remember { mutableStateOf(tableroInicialAjedrez()) }
    var turnoJugador by remember { mutableStateOf(true) }
    var seleccionada by remember { mutableStateOf<PiezaAjedrez?>(null) }
    var dosJugadores by remember { mutableStateOf(false) }
    var pensando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("Tu turno — toca una pieza") }
    var resultado by remember { mutableStateOf<String?>(null) }

    fun mensajeTurno(esJugador: Boolean) = when {
        !dosJugadores && esJugador -> "Tu turno"
        !dosJugadores -> "Turno de la computadora"
        esJugador -> "Turno de blancas"
        else -> "Turno de negras"
    }

    fun reiniciar() {
        tablero = tableroInicialAjedrez()
        turnoJugador = true
        seleccionada = null
        resultado = null
        mensaje = mensajeTurno(true)
    }

    // Se pidió poder jugar entre más de una persona: con `dosJugadores`
    // activo nadie mueve solo — blancas y negras se controlan por
    // toques, alternando.
    fun cambiarModo(activarDosJugadores: Boolean) {
        dosJugadores = activarDosJugadores
        reiniciar()
    }

    fun revisarFin(despuesDeTurnoJugador: Boolean) {
        val turnoSiguienteEsJugador = !despuesDeTurnoJugador
        val movidas = movidasLegalesAjedrez(tablero, turnoSiguienteEsJugador)
        if (movidas.isEmpty()) {
            val enJaque = reyEnJaque(tablero, turnoSiguienteEsJugador)
            resultado = when {
                enJaque && turnoSiguienteEsJugador -> "cpu"
                enJaque -> "jugador"
                else -> "empate"
            }
            mensaje = when (resultado) {
                "jugador" -> if (dosJugadores) "¡Jaque mate, ganaron blancas! 🎉" else "¡Jaque mate, ganaste! 🎉"
                "cpu" -> if (dosJugadores) "¡Jaque mate, ganaron negras! 🎉" else "Jaque mate — ganó la computadora"
                else -> "¡Ahogado! Empate"
            }
            if (resultado == "jugador" && !dosJugadores) scope.launch { services.progress.completarNivel(juego.id, 1) }
            services.sound.tocar(if (resultado == "jugador" || (dosJugadores && resultado == "cpu")) Efecto.WIN else Efecto.WRONG)
        } else {
            mensaje = when {
                turnoSiguienteEsJugador && reyEnJaque(tablero, true) -> "¡Jaque! ${mensajeTurno(true)}"
                !turnoSiguienteEsJugador && reyEnJaque(tablero, false) -> "¡Jaque! ${mensajeTurno(false)}"
                else -> mensajeTurno(turnoSiguienteEsJugador)
            }
        }
    }

    fun tocarCasilla(fila: Int, col: Int) {
        if (resultado != null || pensando) return
        if (!dosJugadores && !turnoJugador) return
        val ocupante = tablero.find { it.fila == fila && it.col == col }
        val actual = seleccionada
        if (actual != null) {
            val movida = movidasLegalesAjedrez(tablero, esJugador = turnoJugador)
                .find { it.pieza == actual && it.filaDestino == fila && it.colDestino == col }
            if (movida != null) {
                services.sound.tocar(if (movida.captura != null) Efecto.CORRECT else Efecto.CLICK)
                tablero = aplicarMovidaAjedrez(tablero, movida)
                seleccionada = null
                val eraTurnoJugador = turnoJugador
                turnoJugador = !turnoJugador
                revisarFin(despuesDeTurnoJugador = eraTurnoJugador)
                return
            }
        }
        if (ocupante != null && ocupante.esJugador == turnoJugador) {
            seleccionada = ocupante
            services.sound.tocar(Efecto.CLICK)
        } else {
            seleccionada = null
        }
    }

    LaunchedEffect(turnoJugador, resultado, dosJugadores) {
        if (dosJugadores || turnoJugador || resultado != null) return@LaunchedEffect
        pensando = true
        delay(500)
        val movida = withContext(Dispatchers.Default) { mejorMovidaAjedrez(tablero) }
        services.sound.tocar(if (movida.captura != null) Efecto.WRONG else Efecto.CLICK)
        tablero = aplicarMovidaAjedrez(tablero, movida)
        pensando = false
        turnoJugador = true
        revisarFin(despuesDeTurnoJugador = false)
    }

    val destinosResaltados = seleccionada?.let { movidasLegalesAjedrez(tablero, esJugador = turnoJugador).filter { m -> m.pieza == it } } ?: emptyList()

    GameShell(
        juego = juego,
        consigna = mensaje,
        celebrar = resultado == "jugador",
        onVolver = onVolver,
        acciones = { if (resultado != null) Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        // Mismo bug de fondo que "se corta la pantalla" en Solitario:
        // faltaba scroll vertical en el contenedor completo.
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 10.dp)) {
                FilterChip(
                    selected = !dosJugadores,
                    onClick = { if (dosJugadores) cambiarModo(false) },
                    label = { Text("🤖 Vs. computadora") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8A5A2B), selectedLabelColor = Color.White),
                )
                FilterChip(
                    selected = dosJugadores,
                    onClick = { if (!dosJugadores) cambiarModo(true) },
                    label = { Text("👫 Dos jugadores") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF3F342C), selectedLabelColor = Color.White),
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState())
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .size(40.dp * 8),
            ) {
                Column {
                    for (fila in 0..7) {
                        Row {
                            for (col in 0..7) {
                                val claro = (fila + col) % 2 == 0
                                val esSeleccionada = seleccionada?.fila == fila && seleccionada?.col == col
                                val esDestino = destinosResaltados.any { it.filaDestino == fila && it.colDestino == col }
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(if (claro) Color(0xFFF3E8D0) else Color(0xFF8A5A2B))
                                        .then(if (esSeleccionada) Modifier.border(2.dp, Color(0xFFE0C23C)) else Modifier)
                                        .clickable { tocarCasilla(fila, col) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (esDestino) {
                                        Box(Modifier.size(12.dp).clip(RoundedCornerShape(50)).background(Color(0xFF8BBF6A).copy(alpha = 0.85f)))
                                    }
                                }
                            }
                        }
                    }
                }
                // Capa de fichas con posición animada — desliza al moverse
                // en vez de desaparecer de una casilla y aparecer en otra.
                tablero.forEach { pieza ->
                    AnimatedPieza(id = pieza.id, fila = pieza.fila, col = pieza.col, tamanoCelda = 40.dp) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            // Un solo color de tinta para las dos: el
                            // símbolo Unicode ya distingue blanco (hueco) de
                            // negro (sólido) por su forma. Pintarlas de
                            // colores distintos hacía que las piezas del
                            // jugador casi desaparecieran sobre casillas claras.
                            Text(simboloPieza(pieza), fontSize = 26.sp, color = Color(0xFF2A2118))
                        }
                    }
                }
            }
        }
    }
}
