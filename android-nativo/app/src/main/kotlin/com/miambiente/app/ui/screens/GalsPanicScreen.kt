package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
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
import com.miambiente.app.ui.materials.MarcoArcade
import com.miambiente.app.ui.materials.PadDireccional
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private const val COLS = 12
private const val FILAS = 16
private const val CELDA = 22f
private const val ANCHO = COLS * CELDA
private const val ALTO = FILAS * CELDA
private const val VIDAS_INICIALES = 3
private const val OBJETIVO = 0.75f

internal data class EnemigoQix(val x: Float, val y: Float, val velX: Float, val velY: Float)

internal fun vecinos4(indice: Int, cols: Int, filas: Int): List<Int> {
    val fila = indice / cols
    val col = indice % cols
    return buildList {
        if (fila > 0) add(indice - cols)
        if (fila < filas - 1) add(indice + cols)
        if (col > 0) add(indice - 1)
        if (col < cols - 1) add(indice + 1)
    }
}

internal fun celdaBorde(indice: Int, cols: Int, filas: Int): Boolean {
    val fila = indice / cols
    val col = indice % cols
    return fila == 0 || fila == filas - 1 || col == 0 || col == cols - 1
}

internal fun bordeInicial(cols: Int, filas: Int): Set<Int> =
    (0 until cols * filas).filter { celdaBorde(it, cols, filas) }.toSet()

/**
 * Sella un trazo dibujado sobre territorio libre: junta el trazo al área
 * segura y reclama cualquier bolsa de celdas que haya quedado separada del
 * resto del campo — salvo la(s) que todavía tengan un enemigo adentro
 * (igual que en el arcade real: el enemigo "protege" su bolsa de territorio
 * hasta que se mueve de ahí). Partición en componentes conexas por BFS, no
 * un simple "lo que no se alcanza desde el borde", porque el borde mismo ya
 * está sellado desde el inicio y no sirve como semilla de búsqueda.
 */
internal fun sellarTrazo(
    safeActual: Set<Int>,
    trazo: Set<Int>,
    cols: Int,
    filas: Int,
    celdasEnemigos: Set<Int> = emptySet(),
): Set<Int> {
    val nuevoSafe = safeActual + trazo
    val total = cols * filas
    val porVisitar = (0 until total).filterNotTo(mutableSetOf()) { it in nuevoSafe }
    val reclamadas = mutableSetOf<Int>()
    while (porVisitar.isNotEmpty()) {
        val semilla = porVisitar.first()
        val componente = mutableSetOf(semilla)
        val pila = ArrayDeque<Int>()
        pila.addLast(semilla)
        while (pila.isNotEmpty()) {
            val actual = pila.removeLast()
            for (v in vecinos4(actual, cols, filas)) {
                if (v !in nuevoSafe && componente.add(v)) pila.addLast(v)
            }
        }
        porVisitar -= componente
        if (componente.none { it in celdasEnemigos }) reclamadas += componente
    }
    return nuevoSafe + reclamadas
}

internal fun porcentajeReclamado(safe: Set<Int>, cols: Int, filas: Int): Float {
    val interior = cols * filas - bordeInicial(cols, filas).size
    if (interior == 0) return 1f
    val reclamadoInterior = safe.count { !celdaBorde(it, cols, filas) }
    return reclamadoInterior.toFloat() / interior
}

/**
 * Mosaico sorpresa — copia de la modalidad Qix/Gals Panic: el borde del
 * tablero empieza reclamado; el jugador se mueve libremente por lo seguro,
 * y al entrar a territorio libre va dejando un trazo. Si vuelve a pisar
 * territorio seguro, el trazo se sella y la(s) bolsa(s) de territorio que
 * quedaron separadas del resto se reclaman de una vez (salvo la que tenga
 * un enemigo adentro). Si un enemigo toca el trazo mientras se dibuja, se
 * pierde una vida y el trazo se borra. Termina al reclamar el 75% o al
 * quedarse sin vidas.
 */
@Composable
fun MosaicoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("mosaico")!!

    var safe by remember { mutableStateOf(bordeInicial(COLS, FILAS)) }
    var trazo by remember { mutableStateOf(setOf<Int>()) }
    var jugador by remember { mutableStateOf(COLS / 2) } // celda de arriba, en el borde
    var ultimaSegura by remember { mutableStateOf(COLS / 2) }
    var enemigos by remember {
        mutableStateOf(
            listOf(
                EnemigoQix(ANCHO * 0.3f, ALTO * 0.4f, 70f, 55f),
                EnemigoQix(ANCHO * 0.7f, ALTO * 0.6f, -60f, 70f),
            ),
        )
    }
    var vidas by remember { mutableStateOf(VIDAS_INICIALES) }
    var terminado by remember { mutableStateOf(false) }
    var gano by remember { mutableStateOf(false) }

    fun reiniciar() {
        safe = bordeInicial(COLS, FILAS)
        trazo = emptySet()
        jugador = COLS / 2
        ultimaSegura = COLS / 2
        enemigos = listOf(
            EnemigoQix(ANCHO * 0.3f, ALTO * 0.4f, 70f, 55f),
            EnemigoQix(ANCHO * 0.7f, ALTO * 0.6f, -60f, 70f),
        )
        vidas = VIDAS_INICIALES
        terminado = false
        gano = false
    }

    fun perderVida() {
        services.sound.tocar(Efecto.WRONG)
        trazo = emptySet()
        jugador = ultimaSegura
        vidas -= 1
        if (vidas <= 0) terminado = true
    }

    fun mover(delta: Int) {
        if (terminado) return
        val col = jugador % COLS
        val destino = jugador + delta
        if (destino !in 0 until COLS * FILAS) return
        if (delta == 1 && col == COLS - 1) return
        if (delta == -1 && col == 0) return

        if (destino in safe) {
            jugador = destino
            if (trazo.isNotEmpty()) {
                val celdasEnemigos = enemigos.map { celdaDe(it.x, it.y) }.toSet()
                safe = sellarTrazo(safe, trazo, COLS, FILAS, celdasEnemigos)
                trazo = emptySet()
                services.sound.tocar(Efecto.CORRECT)
                val pct = porcentajeReclamado(safe, COLS, FILAS)
                if (pct >= OBJETIVO) {
                    terminado = true
                    gano = true
                    services.sound.tocar(Efecto.WIN)
                    scope.launch { services.progress.completarNivel(juego.id, 1) }
                }
            }
            ultimaSegura = jugador
        } else {
            jugador = destino
            trazo = trazo + destino
            services.sound.tocar(Efecto.CLICK)
        }
    }

    LaunchedEffect(terminado) {
        if (terminado) return@LaunchedEffect
        var anterior = withFrameNanos { it }
        while (true) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.032f)
            anterior = ahora
            if (terminado) break

            enemigos = enemigos.map { e ->
                var x = e.x + e.velX * dt
                var y = e.y + e.velY * dt
                var vx = e.velX
                var vy = e.velY
                if (x < CELDA / 2f) { x = CELDA / 2f; vx = abs(vx) }
                if (x > ANCHO - CELDA / 2f) { x = ANCHO - CELDA / 2f; vx = -abs(vx) }
                if (y < CELDA / 2f) { y = CELDA / 2f; vy = abs(vy) }
                if (y > ALTO - CELDA / 2f) { y = ALTO - CELDA / 2f; vy = -abs(vy) }
                e.copy(x = x, y = y, velX = vx, velY = vy)
            }

            val celdasEnemigosAhora = enemigos.map { celdaDe(it.x, it.y) }
            if (celdasEnemigosAhora.any { it == jugador && jugador !in safe } || celdasEnemigosAhora.any { it in trazo }) {
                perderVida()
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            terminado && gano -> "¡Mosaico revelado!"
            terminado -> "Los guardianes te encontraron"
            else -> {
                val pct = (porcentajeReclamado(safe, COLS, FILAS) * 100).roundToInt()
                val meta = (OBJETIVO * 100).roundToInt()
                "Reclamado $pct% de $meta% · Vidas $vidas"
            }
        },
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Nuevo mosaico") } },
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            MarcoArcade(colorFondo = Color(0xFF11152E)) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = ANCHO.dp, height = ALTO.dp),
            ) {
                for (i in 0 until COLS * FILAS) {
                    val fila = i / COLS
                    val col = i % COLS
                    val color = when {
                        i in trazo -> Color(0xFFE0C23C)
                        i in safe -> Color(0xFF82C8A0)
                        else -> Color(0xFF26384A)
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = (col * CELDA).dp, y = (fila * CELDA).dp)
                            .size((CELDA - 1f).dp)
                            .background(color),
                    )
                }
                enemigos.forEach { e ->
                    Box(
                        modifier = Modifier
                            .offset(x = (e.x - CELDA / 2f).dp, y = (e.y - CELDA / 2f).dp)
                            .size(CELDA.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD9433A)),
                        contentAlignment = Alignment.Center,
                    ) { Text("👾", fontSize = 12.sp) }
                }
                val filaJ = jugador / COLS
                val colJ = jugador % COLS
                Box(
                    modifier = Modifier
                        .offset(x = (colJ * CELDA).dp, y = (filaJ * CELDA).dp)
                        .size(CELDA.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                )
            }
            }
            Text(
                "Dibuja sobre lo oscuro y vuelve a lo verde para sellarlo",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
            )
            PadDireccional(
                onArriba = { mover(-COLS) },
                onAbajo = { mover(COLS) },
                onIzquierda = { mover(-1) },
                onDerecha = { mover(1) },
            )
        }
    }
}

private fun celdaDe(x: Float, y: Float): Int {
    val col = (x / CELDA).toInt().coerceIn(0, COLS - 1)
    val fila = (y / CELDA).toInt().coerceIn(0, FILAS - 1)
    return fila * COLS + col
}
