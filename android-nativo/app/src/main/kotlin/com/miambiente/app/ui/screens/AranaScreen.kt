package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val REVEAL_EMOJIS = listOf("🏰", "🦄", "👑", "🌈", "🧚", "🎀", "🐬", "🦋", "🍰", "🌸")
private const val UMBRAL_VICTORIA = 0.85f
private const val VIDAS_INICIALES = 3

private fun emojiDeNivel(nivel: Int) = REVEAL_EMOJIS[(nivel - 1) % REVEAL_EMOJIS.size]

/**
 * La araña pintora — puerto real de app/games/arana/page.tsx, no una
 * reinvención con otra mecánica.
 *
 * Bug real de fondo, encontrado al leer el juego original en la versión
 * web ("sigue fallando, no es nada al juego" — el reporte más honesto
 * de todos: la versión nativa anterior de verdad NO SE PARECÍA al juego
 * real): la mecánica real no es "tocar 16 casillas fijas una por una".
 * Es arrastrar el dedo desde una casilla junto al borde ya revelado,
 * trazando un contorno, y al soltar se revela por INUNDACIÓN (flood
 * fill) todo lo que ese contorno encierra — como recorrer con el dedo el
 * borde de un dibujo. Además hay arañas MALAS que se mueven solas por el
 * tablero: si el trazo las toca mientras se arrastra, se pierde una vida
 * (❤️×3). La imagen escondida no es una araña — es uno de 10 dibujos
 * (castillo, unicornio, corona...) que cambia por nivel; la araña 🕷️ es
 * el enemigo, no el premio. 11 niveles reales con `phasedInt` (mismo
 * motor de progresión que el resto de la app), tablero de 4×4 hasta
 * 14×16, más rápido y con más arañas malas conforme sube el nivel.
 */
@Composable
fun AranaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("arana")!!
    val densidad = LocalDensity.current

    var nivel by remember { mutableStateOf(1) }
    var generacion by remember { mutableStateOf(0) }

    val filas = phasedInt(nivel, listOf(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14))
    val columnas = phasedInt(nivel, listOf(4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 16))
    val emoji = emojiDeNivel(nivel)
    val enemyMs = phasedInt(nivel, listOf(1300, 1180, 1070, 960, 860, 770, 680, 590, 500, 420, 340)).toLong()
    val enemyCount = phasedInt(nivel, listOf(1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 5))
    val ladoMayor = maxOf(filas, columnas)
    val cellDp = when {
        ladoMayor <= 4 -> 64
        ladoMayor <= 6 -> 52
        ladoMayor <= 8 -> 42
        ladoMayor <= 10 -> 34
        ladoMayor <= 13 -> 26
        else -> 21
    }
    val cellPx = with(densidad) { cellDp.dp.toPx() }

    var revelado by remember(nivel, generacion) { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var trail by remember(nivel, generacion) { mutableStateOf(listOf<Pair<Int, Int>>()) }
    var arrastrando by remember(nivel, generacion) { mutableStateOf(false) }
    var enemigos by remember(nivel, generacion) {
        mutableStateOf((1..enemyCount).map { (0 until filas).random() to (0 until columnas).random() })
    }
    var vidas by remember(nivel, generacion) { mutableStateOf(VIDAS_INICIALES) }
    var golpeado by remember(nivel, generacion) { mutableStateOf(false) }

    fun esSeguro(r: Int, c: Int): Boolean {
        if (r !in 0 until filas || c !in 0 until columnas) return true
        return (r to c) in revelado
    }

    fun vecinosSeguros(r: Int, c: Int) =
        esSeguro(r - 1, c) || esSeguro(r + 1, c) || esSeguro(r, c - 1) || esSeguro(r, c + 1)

    fun perderVida() {
        services.sound.tocar(Efecto.WRONG)
        services.haptics.vibrar(Patron.ERROR)
        golpeado = true
        scope.launch { delay(350); golpeado = false }
        trail = emptyList()
        arrastrando = false
        if (vidas - 1 <= 0) {
            scope.launch { delay(500); generacion++ }
        } else {
            vidas -= 1
        }
    }

    fun celdaDesde(offset: Offset): Pair<Int, Int> {
        val c = (offset.x / cellPx).toInt().coerceIn(0, columnas - 1)
        val r = (offset.y / cellPx).toInt().coerceIn(0, filas - 1)
        return r to c
    }

    fun intentarIniciar(r: Int, c: Int) {
        if ((r to c) in revelado || (r to c) in enemigos || !vecinosSeguros(r, c)) return
        arrastrando = true
        trail = listOf(r to c)
        services.sound.tocar(Efecto.CLICK)
    }

    fun intentarExtender(r: Int, c: Int) {
        if (!arrastrando || (r to c) in revelado) return
        val ultima = trail.lastOrNull() ?: return
        if (ultima == r to c) return
        val adyacente = kotlin.math.abs(ultima.first - r) + kotlin.math.abs(ultima.second - c) == 1
        if (!adyacente || (r to c) in trail) return
        if ((r to c) in enemigos) { perderVida(); return }
        trail = trail + (r to c)
    }

    fun inundarDesde(trazo: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        val alcanzable = mutableSetOf<Pair<Int, Int>>()
        val pila = ArrayDeque<Pair<Int, Int>>()
        for (r in 0 until filas) for (c in 0 until columnas) {
            val esBorde = r == 0 || c == 0 || r == filas - 1 || c == columnas - 1
            val clave = r to c
            if (esBorde && clave !in revelado && clave !in trazo) { pila.addLast(clave); alcanzable.add(clave) }
        }
        while (pila.isNotEmpty()) {
            val (r, c) = pila.removeLast()
            for ((dr, dc) in listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)) {
                val nr = r + dr; val nc = c + dc
                if (nr !in 0 until filas || nc !in 0 until columnas) continue
                val clave = nr to nc
                if (clave in revelado || clave in trazo || clave in alcanzable) continue
                alcanzable.add(clave); pila.addLast(clave)
            }
        }
        val nuevo = revelado.toMutableSet()
        for (r in 0 until filas) for (c in 0 until columnas) {
            val clave = r to c
            if (clave !in revelado && (clave in trazo || clave !in alcanzable)) nuevo.add(clave)
        }
        return nuevo
    }

    fun terminarArrastre() {
        val trazoFinal = trail
        arrastrando = false
        if (trazoFinal.size >= 3) {
            val (fr, fc) = trazoFinal.last()
            if (vecinosSeguros(fr, fc)) {
                revelado = inundarDesde(trazoFinal.toSet())
                services.sound.tocar(Efecto.CORRECT)
            }
        }
        trail = emptyList()
    }

    LaunchedEffect(nivel, generacion) {
        while (true) {
            delay(enemyMs)
            enemigos = enemigos.map { (r, c) ->
                val (dr, dc) = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0).random()
                val nr = r + dr; val nc = c + dc
                if (nr !in 0 until filas || nc !in 0 until columnas || (nr to nc) in revelado) r to c else nr to nc
            }
            if (arrastrando && trail.isNotEmpty() && enemigos.any { it in trail }) perderVida()
        }
    }

    val totalCeldas = filas * columnas
    val gano = totalCeldas > 0 && revelado.size.toFloat() / totalCeldas >= UMBRAL_VICTORIA

    LaunchedEffect(gano) {
        if (gano) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            delay(1800)
            nivel = (nivel + 1).coerceAtMost(100)
        }
    }

    GameShell(
        juego = juego,
        consigna = if (gano) "¡La araña descubrió el dibujo!" else "Arrastra junto al borde para descubrir el dibujo — cuidado con la araña mala",
        celebrar = gano,
        onVolver = onVolver,
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("❤️".repeat(vidas) + "🖤".repeat((VIDAS_INICIALES - vidas).coerceAtLeast(0)), fontSize = 18.sp)
            Text(
                "Nivel $nivel · Descubierto: ${(revelado.size * 100 / totalCeldas.coerceAtLeast(1))}%",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
            )
            Box(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .then(if (golpeado) Modifier.border(4.dp, Color(0xFFD9433A)) else Modifier),
            ) {
                Box(
                    modifier = Modifier
                        .size(width = cellDp.dp * columnas, height = cellDp.dp * filas)
                        .pointerInput(nivel, generacion, filas, columnas) {
                            detectDragGestures(
                                onDragStart = { pos -> val (r, c) = celdaDesde(pos); intentarIniciar(r, c) },
                                onDragEnd = { terminarArrastre() },
                                onDragCancel = { terminarArrastre() },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val (r, c) = celdaDesde(change.position)
                                    intentarExtender(r, c)
                                },
                            )
                        },
                ) {
                    Text(emoji, fontSize = (cellDp * 1.2).sp, modifier = Modifier.align(Alignment.Center))
                    Column(modifier = Modifier.fillMaxSize()) {
                        for (r in 0 until filas) {
                            Row(modifier = Modifier.weight(1f)) {
                                for (c in 0 until columnas) {
                                    val clave = r to c
                                    val estaRevelada = clave in revelado
                                    val enTrazo = clave in trail
                                    val tieneEnemigo = clave in enemigos
                                    val puedeIniciar = !arrastrando && !estaRevelada && !tieneEnemigo && vecinosSeguros(r, c)
                                    val alfaPulso by animateFloatAsState(if (puedeIniciar) 0.55f else 1f, label = "pulsoInicio")
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .border(0.5.dp, Color.White.copy(alpha = 0.4f))
                                            .background(
                                                when {
                                                    estaRevelada -> Color.Transparent
                                                    enTrazo -> Color(0xFFE0C23C).copy(alpha = 0.85f)
                                                    puedeIniciar -> Color(0xFF6B5FA8).copy(alpha = alfaPulso)
                                                    else -> Color(0xFF6B5FA8)
                                                },
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (tieneEnemigo) Text("🕷️", fontSize = (cellDp * 0.55).sp)
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
