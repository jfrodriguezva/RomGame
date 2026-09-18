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
import kotlinx.coroutines.withContext

// `id` al final con valor por defecto: no rompe las pruebas existentes.
// Permite animar el movimiento (ver AnimatedPieza) sin perder de vista
// qué canica es cuál al saltar.
internal data class FichaChina(val fila: Int, val col: Int, val esJugador: Boolean, val id: Int = 0)
internal data class MovidaChina(val ficha: FichaChina, val filaDestino: Int, val colDestino: Int)

private val DIRECCIONES_CHINAS = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)

// Casa de cada bando: un triángulo de 6 canicas en esquinas opuestas del
// tablero. La meta de cada quien es la casa DEL OTRO.
internal val CASA_JUGADOR = listOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1, 1 to 2)
internal val CASA_CPU = listOf(6 to 5, 6 to 6, 6 to 7, 7 to 5, 7 to 6, 7 to 7)

internal fun tableroInicialChinas(): List<FichaChina> =
    CASA_JUGADOR.mapIndexed { i, (f, c) -> FichaChina(f, c, esJugador = true, id = i) } +
        CASA_CPU.mapIndexed { i, (f, c) -> FichaChina(f, c, esJugador = false, id = i + CASA_JUGADOR.size) }

private fun saltosDesde(origen: Pair<Int, Int>, ocupadas: Set<Pair<Int, Int>>, visitados: MutableSet<Pair<Int, Int>>): Set<Pair<Int, Int>> {
    val alcanzables = mutableSetOf<Pair<Int, Int>>()
    for ((df, dc) in DIRECCIONES_CHINAS) {
        val medio = origen.first + df to origen.second + dc
        val destino = origen.first + 2 * df to origen.second + 2 * dc
        if (destino.first !in 0..7 || destino.second !in 0..7) continue
        if (medio !in ocupadas) continue
        if (destino in ocupadas) continue
        if (!visitados.add(destino)) continue
        alcanzables.add(destino)
        alcanzables.addAll(saltosDesde(destino, ocupadas, visitados))
    }
    return alcanzables
}

/**
 * Destinos válidos de una canica: un paso simple a una celda vacía
 * adyacente, O una cadena de uno o más saltos (sobre cualquier canica,
 * propia o rival — en damas chinas no se captura, solo se salta por
 * encima) que puede terminar en cualquier punto de la cadena, no solo al
 * final — por eso se junta todo lo alcanzable en un solo turno.
 */
internal fun destinosChinas(ficha: FichaChina, tablero: List<FichaChina>): List<Pair<Int, Int>> {
    val ocupadas = tablero.map { it.fila to it.col }.toSet()
    val origen = ficha.fila to ficha.col
    val pasos = DIRECCIONES_CHINAS
        .map { (df, dc) -> ficha.fila + df to ficha.col + dc }
        .filter { (f, c) -> f in 0..7 && c in 0..7 && (f to c) !in ocupadas }
    val saltos = saltosDesde(origen, ocupadas, mutableSetOf(origen))
    return (pasos + saltos).distinct()
}

internal fun aplicarMovidaChinas(tablero: List<FichaChina>, movida: MovidaChina): List<FichaChina> =
    tablero.filter { it != movida.ficha } + movida.ficha.copy(fila = movida.filaDestino, col = movida.colDestino)

internal fun ganoJugadorChinas(tablero: List<FichaChina>): Boolean {
    val ocupadasPorJugador = tablero.filter { it.esJugador }.map { it.fila to it.col }.toSet()
    return CASA_CPU.all { it in ocupadasPorJugador }
}

internal fun ganoCpuChinas(tablero: List<FichaChina>): Boolean {
    val ocupadasPorCpu = tablero.filter { !it.esJugador }.map { it.fila to it.col }.toSet()
    return CASA_JUGADOR.all { it in ocupadasPorCpu }
}

/**
 * IA simple (no minimax: las cadenas de saltos hacen el árbol de jugadas
 * enorme para 6 canicas). Es un algoritmo voraz: para cada canica que
 * todavía no llegó a la casa contraria, prueba todos sus destinos
 * posibles (pasos y cadenas de salto) y elige el movimiento que más la
 * acerca a esa esquina — suficiente para un rival que se mueve con
 * intención real, sin el costo de explorar todas las cadenas posibles.
 */
internal fun mejorMovidaChinas(tablero: List<FichaChina>): MovidaChina? {
    val propias = tablero.filter { !it.esJugador }
    val enCasa = propias.filter { (it.fila to it.col) in CASA_JUGADOR }.toSet()
    val candidatas = propias.filter { it !in enCasa }.ifEmpty { propias }
    var mejor: MovidaChina? = null
    var mejorPuntaje = Int.MIN_VALUE
    for (ficha in candidatas) {
        for ((f, c) in destinosChinas(ficha, tablero)) {
            val progreso = (ficha.fila + ficha.col) - (f + c)
            if (progreso > mejorPuntaje) {
                mejorPuntaje = progreso
                mejor = MovidaChina(ficha, f, c)
            }
        }
    }
    return mejor
}

/**
 * Damas chinas — versión simplificada honesta: no es el tablero real de
 * estrella de 6 puntas (eso son 121 casillas con geometría hexagonal), es
 * un tablero cuadrado de 8×8 con una "casa" de 6 canicas en cada esquina
 * opuesta, pero con las reglas reales del juego: mover un paso, o saltar
 * en cadena por encima de cualquier canica (sin capturarla) para cruzar
 * el tablero más rápido. Gana quien mete sus 6 canicas en la casa
 * contraria primero.
 */
@Composable
fun DamasChinasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("damas-chinas")!!

    var dosJugadores by remember { mutableStateOf(false) }
    var tablero by remember { mutableStateOf(tableroInicialChinas()) }
    var turnoJugador by remember { mutableStateOf(true) }
    var seleccionada by remember { mutableStateOf<FichaChina?>(null) }
    var mensaje by remember { mutableStateOf("Tu turno — toca una canica roja") }
    var ganador by remember { mutableStateOf<String?>(null) }

    fun mensajeInicio() = if (dosJugadores) "Turno de las canicas rojas" else "Tu turno — toca una canica roja"

    fun reiniciar() {
        tablero = tableroInicialChinas()
        turnoJugador = true
        seleccionada = null
        ganador = null
        mensaje = mensajeInicio()
    }

    // Se pidió poder jugar entre más de una persona: con `dosJugadores`
    // activo nadie mueve solo, las rojas y las verdes se controlan por
    // toques, alternando.
    fun cambiarModo(activarDosJugadores: Boolean) {
        dosJugadores = activarDosJugadores
        reiniciar()
    }

    fun verificarFin() {
        if (ganoJugadorChinas(tablero)) { ganador = "jugador"; mensaje = if (dosJugadores) "¡Ganaron las rojas! 🎉" else "¡Ganaste, cruzaste todas tus canicas! 🎉" }
        else if (ganoCpuChinas(tablero)) { ganador = "cpu"; mensaje = if (dosJugadores) "¡Ganaron las verdes! 🎉" else "Ganó la computadora, ¡otra vez!" }
    }

    fun tocarCasilla(fila: Int, col: Int) {
        if (ganador != null) return
        if (!dosJugadores && !turnoJugador) return
        val ocupante = tablero.find { it.fila == fila && it.col == col }
        val actual = seleccionada
        if (actual != null && actual.esJugador == turnoJugador && (fila to col) in destinosChinas(actual, tablero)) {
            services.sound.tocar(Efecto.CORRECT)
            tablero = aplicarMovidaChinas(tablero, MovidaChina(actual, fila, col))
            seleccionada = null
            verificarFin()
            if (ganador == null) {
                turnoJugador = !turnoJugador
                mensaje = when {
                    dosJugadores && turnoJugador -> "Turno de las canicas rojas"
                    dosJugadores -> "Turno de las canicas verdes"
                    else -> "Turno de la computadora"
                }
            }
            return
        }
        if (ocupante != null && ocupante.esJugador == turnoJugador) {
            seleccionada = ocupante
            services.sound.tocar(Efecto.CLICK)
        }
    }

    LaunchedEffect(turnoJugador, ganador, dosJugadores) {
        if (dosJugadores || turnoJugador || ganador != null) return@LaunchedEffect
        delay(600)
        // Igual que en Damas: la IA corre fuera del hilo principal para
        // no arriesgar un ANR ("algunos juegos traban la app y la
        // reinician") — acá cada canica evalúa cadenas de salto
        // recursivas, y son 6 canicas por turno.
        val movida = withContext(Dispatchers.Default) { mejorMovidaChinas(tablero) }
        if (movida != null) {
            services.sound.tocar(Efecto.CLICK)
            tablero = aplicarMovidaChinas(tablero, movida)
        }
        verificarFin()
        if (ganador == null) { turnoJugador = true; mensaje = "Tu turno" }
    }

    val destinosResaltados = seleccionada?.let { destinosChinas(it, tablero) } ?: emptyList()

    GameShell(
        juego = juego,
        consigna = mensaje,
        celebrar = ganador == "jugador",
        onVolver = onVolver,
        acciones = { if (ganador != null) Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 10.dp)) {
                FilterChip(
                    selected = !dosJugadores,
                    onClick = { if (dosJugadores) cambiarModo(false) },
                    label = { Text("🤖 Vs. computadora") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD9433A), selectedLabelColor = Color.White),
                )
                FilterChip(
                    selected = dosJugadores,
                    onClick = { if (!dosJugadores) cambiarModo(true) },
                    label = { Text("👫 Dos jugadores") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF3E9B7A), selectedLabelColor = Color.White),
                )
            }
            Text(
                (if (dosJugadores) "Rojas" else "Tú") + ": ${tablero.count { it.esJugador && (it.fila to it.col) in CASA_CPU }}/6 en casa · " +
                    (if (dosJugadores) "Verdes" else "Computadora") + ": ${tablero.count { !it.esJugador && (it.fila to it.col) in CASA_JUGADOR }}/6 en casa",
                fontSize = 12.sp,
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState())
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .size(38.dp * 8),
            ) {
                Column {
                    for (fila in 0..7) {
                        Row {
                            for (col in 0..7) {
                                val esDestino = (fila to col) in destinosResaltados
                                val esCasaJugador = (fila to col) in CASA_JUGADOR
                                val esCasaCpu = (fila to col) in CASA_CPU
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            when {
                                                esCasaJugador -> Color(0xFFF0D9D6)
                                                esCasaCpu -> Color(0xFFD6E8DF)
                                                else -> Color(0xFFF3E8D0)
                                            },
                                        )
                                        .border(0.5.dp, Color(0xFFD9CDB4))
                                        .clickable { tocarCasilla(fila, col) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (esDestino) {
                                        Box(Modifier.size(12.dp).clip(RoundedCornerShape(50)).background(Color(0xFF4C7A3A).copy(alpha = 0.6f)))
                                    }
                                }
                            }
                        }
                    }
                }
                // Capa de canicas con posición animada — se ven saltar de
                // verdad en vez de desaparecer y aparecer de golpe.
                tablero.forEach { ficha ->
                    val esSeleccionada = seleccionada?.id == ficha.id
                    AnimatedPieza(id = ficha.id, fila = ficha.fila, col = ficha.col, tamanoCelda = 38.dp) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .shadow(2.dp, RoundedCornerShape(50))
                                    .clip(RoundedCornerShape(50))
                                    .background(if (ficha.esJugador) Color(0xFFD9433A) else Color(0xFF3E9B7A))
                                    .then(if (esSeleccionada) Modifier.border(2.dp, Color(0xFFE0C23C), RoundedCornerShape(50)) else Modifier),
                            )
                        }
                    }
                }
            }
        }
    }
}
