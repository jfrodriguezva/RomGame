package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal data class FichaDamas(val fila: Int, val col: Int, val esJugador: Boolean, val esDama: Boolean = false)
internal data class MovidaDamas(val ficha: FichaDamas, val filaDestino: Int, val colDestino: Int, val capturada: FichaDamas? = null)

internal fun casillaJugable(fila: Int, col: Int) = (fila + col) % 2 == 1

internal fun tableroInicialDamas(): List<FichaDamas> {
    val fichas = mutableListOf<FichaDamas>()
    for (fila in 0..2) for (col in 0..7) if (casillaJugable(fila, col)) fichas.add(FichaDamas(fila, col, esJugador = false))
    for (fila in 5..7) for (col in 0..7) if (casillaJugable(fila, col)) fichas.add(FichaDamas(fila, col, esJugador = true))
    return fichas
}

private fun direccionesDamas(f: FichaDamas): List<Pair<Int, Int>> {
    val avance = if (f.esJugador) -1 else 1
    return if (f.esDama) listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1) else listOf(avance to -1, avance to 1)
}

/** Movidas de una ficha: si hay captura disponible para ELLA, solo se devuelven capturas (regla real de damas). */
internal fun movidasDeFicha(ficha: FichaDamas, tablero: List<FichaDamas>): List<MovidaDamas> {
    val ocupadas = tablero.associateBy { it.fila to it.col }
    val simples = mutableListOf<MovidaDamas>()
    val capturas = mutableListOf<MovidaDamas>()
    for ((df, dc) in direccionesDamas(ficha)) {
        val nf = ficha.fila + df
        val nc = ficha.col + dc
        if (nf !in 0..7 || nc !in 0..7) continue
        val ocupante = ocupadas[nf to nc]
        if (ocupante == null) {
            simples.add(MovidaDamas(ficha, nf, nc))
        } else if (ocupante.esJugador != ficha.esJugador) {
            val sf = nf + df
            val sc = nc + dc
            if (sf in 0..7 && sc in 0..7 && ocupadas[sf to sc] == null) {
                capturas.add(MovidaDamas(ficha, sf, sc, ocupante))
            }
        }
    }
    return if (capturas.isNotEmpty()) capturas else simples
}

/** Todas las movidas legales de un bando: la captura es obligatoria en todo el tablero si alguna ficha puede capturar. */
internal fun movidasLegales(tablero: List<FichaDamas>, esJugador: Boolean): List<MovidaDamas> {
    val propias = tablero.filter { it.esJugador == esJugador }
    val porFicha = propias.map { it to movidasDeFicha(it, tablero) }
    val hayCaptura = porFicha.any { (_, movs) -> movs.any { it.capturada != null } }
    return if (hayCaptura) {
        porFicha.flatMap { (_, movs) -> movs.filter { it.capturada != null } }
    } else {
        porFicha.flatMap { (_, movs) -> movs }
    }
}

internal fun aplicarMovidaDamas(tablero: List<FichaDamas>, movida: MovidaDamas): List<FichaDamas> {
    val sinCapturada = tablero.filter { it != movida.ficha && it != movida.capturada }
    var nuevaFicha = movida.ficha.copy(fila = movida.filaDestino, col = movida.colDestino)
    val coronaEnFila = if (nuevaFicha.esJugador) 0 else 7
    if (!nuevaFicha.esDama && nuevaFicha.fila == coronaEnFila) nuevaFicha = nuevaFicha.copy(esDama = true)
    return sinCapturada + nuevaFicha
}

private fun evaluarDamas(tablero: List<FichaDamas>): Int =
    tablero.sumOf { f -> (if (f.esDama) 3 else 1) * (if (f.esJugador) -1 else 1) }

private fun minimaxDamas(tablero: List<FichaDamas>, profundidad: Int, turnoJugador: Boolean, esMaximizando: Boolean): Int {
    val movidas = movidasLegales(tablero, turnoJugador)
    if (profundidad == 0 || movidas.isEmpty()) return evaluarDamas(tablero)
    val valores = movidas.map { m -> minimaxDamas(aplicarMovidaDamas(tablero, m), profundidad - 1, !turnoJugador, !esMaximizando) }
    return if (esMaximizando) valores.max() else valores.min()
}

/** Elige la mejor movida para la computadora (minimax a 3 capas, prioriza capturas por diseño de `movidasLegales`). */
internal fun mejorMovidaDamas(tablero: List<FichaDamas>): MovidaDamas {
    val movidas = movidasLegales(tablero, esJugador = false)
    return movidas.maxBy { m -> minimaxDamas(aplicarMovidaDamas(tablero, m), 2, turnoJugador = true, esMaximizando = false) }
}

/**
 * Damas inglesas reales: captura obligatoria, cadena de capturas múltiples
 * con la misma ficha, y coronación a dama al llegar a la fila opuesta —
 * no una versión simplificada. La IA usa minimax real, no movidas al azar.
 */
@Composable
fun DamasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("damas")!!

    var tablero by remember { mutableStateOf(tableroInicialDamas()) }
    var turnoJugador by remember { mutableStateOf(true) }
    var seleccionada by remember { mutableStateOf<FichaDamas?>(null) }
    var encadenando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("Tu turno — toca una ficha") }
    var ganador by remember { mutableStateOf<String?>(null) }

    fun reiniciar() {
        tablero = tableroInicialDamas()
        turnoJugador = true
        seleccionada = null
        encadenando = false
        ganador = null
        mensaje = "Tu turno — toca una ficha"
    }

    fun verificarFin() {
        val fichasJugador = tablero.count { it.esJugador }
        val fichasCpu = tablero.count { !it.esJugador }
        if (fichasCpu == 0 || movidasLegales(tablero, esJugador = false).isEmpty()) {
            ganador = "jugador"; mensaje = "¡Ganaste! 🎉"
        } else if (fichasJugador == 0 || movidasLegales(tablero, esJugador = true).isEmpty()) {
            ganador = "cpu"; mensaje = "Ganó la computadora, ¡otra vez!"
        }
    }

    fun jugarMovidaJugador(movida: MovidaDamas) {
        services.sound.tocar(if (movida.capturada != null) Efecto.CORRECT else Efecto.CLICK)
        tablero = aplicarMovidaDamas(tablero, movida)
        val nuevaFicha = tablero.first { it.fila == movida.filaDestino && it.col == movida.colDestino }
        val siguientesCapturas = if (movida.capturada != null) movidasDeFicha(nuevaFicha, tablero).filter { it.capturada != null } else emptyList()
        if (siguientesCapturas.isNotEmpty()) {
            seleccionada = nuevaFicha
            encadenando = true
            mensaje = "¡Sigue capturando con la misma ficha!"
        } else {
            seleccionada = null
            encadenando = false
            turnoJugador = false
            mensaje = "Turno de la computadora"
        }
    }

    fun tocarCasilla(fila: Int, col: Int) {
        if (!turnoJugador || ganador != null) return
        val ocupante = tablero.find { it.fila == fila && it.col == col }
        val actual = seleccionada
        if (actual != null) {
            val destino = movidasDeFicha(actual, tablero).find { it.filaDestino == fila && it.colDestino == col }
            if (destino != null) { jugarMovidaJugador(destino); verificarFin(); return }
        }
        if (!encadenando && ocupante != null && ocupante.esJugador) {
            seleccionada = ocupante
            services.sound.tocar(Efecto.CLICK)
        }
    }

    LaunchedEffect(turnoJugador, ganador) {
        if (turnoJugador || ganador != null) return@LaunchedEffect
        delay(700)
        var piezaTurno: FichaDamas? = null
        while (true) {
            val movida = if (piezaTurno == null) {
                mejorMovidaDamas(tablero)
            } else {
                movidasDeFicha(piezaTurno, tablero).filter { it.capturada != null }
                    .maxByOrNull { minimaxDamas(aplicarMovidaDamas(tablero, it), 2, true, false) }
                    ?: break
            }
            services.sound.tocar(if (movida.capturada != null) Efecto.WRONG else Efecto.CLICK)
            tablero = aplicarMovidaDamas(tablero, movida)
            delay(450)
            val nuevaFicha = tablero.first { it.fila == movida.filaDestino && it.col == movida.colDestino }
            val siguientes = if (movida.capturada != null) movidasDeFicha(nuevaFicha, tablero).filter { it.capturada != null } else emptyList()
            if (siguientes.isEmpty()) break
            piezaTurno = nuevaFicha
        }
        verificarFin()
        if (ganador == null) { turnoJugador = true; mensaje = "Tu turno" }
    }

    val destinosResaltados = seleccionada?.let { movidasDeFicha(it, tablero) } ?: emptyList()

    GameShell(
        juego = juego,
        consigna = mensaje,
        celebrar = ganador == "jugador",
        onVolver = onVolver,
        acciones = { if (ganador != null) Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tú: ${tablero.count { it.esJugador }} · Computadora: ${tablero.count { !it.esJugador }}", fontSize = 12.sp)
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                for (fila in 0..7) {
                    Row {
                        for (col in 0..7) {
                            val jugable = casillaJugable(fila, col)
                            val ficha = tablero.find { it.fila == fila && it.col == col }
                            val esSeleccionada = seleccionada?.fila == fila && seleccionada?.col == col
                            val esDestino = destinosResaltados.any { it.filaDestino == fila && it.colDestino == col }
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(if (jugable) Color(0xFF8A5A2B) else Color(0xFFF3E8D0))
                                    .then(if (esSeleccionada) Modifier.border(2.dp, Color(0xFFE0C23C)) else Modifier)
                                    .then(if (jugable) Modifier.clickable { tocarCasilla(fila, col) } else Modifier),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (esDestino) {
                                    Box(Modifier.size(12.dp).clip(RoundedCornerShape(50)).background(Color(0xFF8BBF6A).copy(alpha = 0.85f)))
                                }
                                if (ficha != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .shadow(2.dp, RoundedCornerShape(50))
                                            .clip(RoundedCornerShape(50))
                                            .background(if (ficha.esJugador) Color(0xFFD9433A) else Color(0xFF3F342C))
                                            .then(if (esSeleccionada) Modifier.border(2.dp, Color(0xFFE0C23C), RoundedCornerShape(50)) else Modifier),
                                        contentAlignment = Alignment.Center,
                                    ) { if (ficha.esDama) Text("♛", fontSize = 14.sp, color = Color(0xFFE0C23C)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
