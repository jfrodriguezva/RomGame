package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

/**
 * Palillos chinos (Mikado) — nunca se había hecho en ninguna versión de
 * la app. Simular la física real de una pila de palillos cayendo es
 * demasiado (sería un motor de físicas aparte); en su lugar, cada
 * palillo tiene una posición fija y un "orden de pila" (`z`) asignado al
 * generarse, y un palillo está libre para tomar solo si ningún palillo
 * con `z` mayor lo cruza — la misma regla real de Mikado ("se puede
 * tomar si no mueve a otro"), aplicada con geometría en vez de físicas.
 * Los libres brillan; tocar uno que no lo está no hace nada (con un
 * sonido de error), igual de honesto que arriesgarse en el juego real.
 */
internal data class Palillo(
    val id: Int,
    val cx: Float,
    val cy: Float,
    val angulo: Float,
    val longitud: Float,
    val valor: Int,
    val color: Color,
    val z: Int,
)

// (color, valor, cuántos hay) — el negro es "el Mikado": uno solo, y el que más vale.
private val TIPOS = listOf(
    Triple(Color(0xFFE0C23C), 5, 6),
    Triple(Color(0xFF4CAF50), 10, 5),
    Triple(Color(0xFFD9433A), 15, 4),
    Triple(Color(0xFF3E7AA3), 20, 3),
    Triple(Color(0xFF262626), 30, 1),
)

internal fun generarPila(): List<Palillo> {
    val base = mutableListOf<Palillo>()
    var id = 0
    TIPOS.forEach { (color, valor, cantidad) -> repeat(cantidad) { base.add(Palillo(id++, 0f, 0f, 0f, 0f, valor, color, 0)) } }
    return base.shuffled().mapIndexed { z, p ->
        p.copy(
            cx = 0.5f + (Random.nextFloat() - 0.5f) * 0.32f,
            cy = 0.5f + (Random.nextFloat() - 0.5f) * 0.32f,
            angulo = Random.nextFloat() * 180f,
            longitud = 0.30f + Random.nextFloat() * 0.12f,
            z = z,
        )
    }
}

internal fun extremos(p: Palillo, w: Float, h: Float): Pair<Offset, Offset> {
    val rad = Math.toRadians(p.angulo.toDouble())
    val mitad = p.longitud * minOf(w, h) / 2f
    val dx = (cos(rad) * mitad).toFloat()
    val dy = (sin(rad) * mitad).toFloat()
    val cx = p.cx * w
    val cy = p.cy * h
    return Offset(cx - dx, cy - dy) to Offset(cx + dx, cy + dy)
}

internal fun seCruzan(a1: Offset, a2: Offset, b1: Offset, b2: Offset): Boolean {
    fun orientacion(p: Offset, q: Offset, r: Offset): Int {
        val v = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)
        return when { v > 1e-4f -> 1; v < -1e-4f -> 2; else -> 0 }
    }
    fun enSegmento(p: Offset, q: Offset, r: Offset) =
        q.x <= maxOf(p.x, r.x) && q.x >= minOf(p.x, r.x) && q.y <= maxOf(p.y, r.y) && q.y >= minOf(p.y, r.y)

    val o1 = orientacion(a1, a2, b1)
    val o2 = orientacion(a1, a2, b2)
    val o3 = orientacion(b1, b2, a1)
    val o4 = orientacion(b1, b2, a2)
    if (o1 != o2 && o3 != o4) return true
    if (o1 == 0 && enSegmento(a1, b1, a2)) return true
    if (o2 == 0 && enSegmento(a1, b2, a2)) return true
    if (o3 == 0 && enSegmento(b1, a1, b2)) return true
    if (o4 == 0 && enSegmento(b1, a2, b2)) return true
    return false
}

internal fun esLibre(p: Palillo, pila: List<Palillo>): Boolean {
    val (a1, a2) = extremos(p, 1f, 1f)
    return pila.none { otro -> otro.id != p.id && otro.z > p.z && seCruzan(a1, a2, extremos(otro, 1f, 1f).first, extremos(otro, 1f, 1f).second) }
}

internal fun distanciaPuntoSegmento(p: Offset, a: Offset, b: Offset): Float {
    val abx = b.x - a.x
    val aby = b.y - a.y
    val largo2 = abx * abx + aby * aby
    val t = if (largo2 < 1e-6f) 0f else (((p.x - a.x) * abx + (p.y - a.y) * aby) / largo2).coerceIn(0f, 1f)
    val proj = Offset(a.x + abx * t, a.y + aby * t)
    return hypot((p.x - proj.x).toDouble(), (p.y - proj.y).toDouble()).toFloat()
}

@Composable
fun PalillosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("palillos")!!

    var dosJugadores by remember { mutableStateOf(false) }
    var pila by remember { mutableStateOf(generarPila()) }
    var turnoUno by remember { mutableStateOf(true) }
    var puntajeUno by remember { mutableStateOf(0) }
    var puntajeDos by remember { mutableStateOf(0) }
    var terminado by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<String?>(null) }

    fun reiniciar() {
        pila = generarPila(); turnoUno = true; puntajeUno = 0; puntajeDos = 0; terminado = false; aviso = null
    }

    fun cambiarModo(activarDosJugadores: Boolean) {
        dosJugadores = activarDosJugadores
        reiniciar()
    }

    val libres = remember(pila) { pila.filter { esLibre(it, pila) }.map { it.id }.toSet() }

    fun tomar(id: Int) {
        val pieza = pila.firstOrNull { it.id == id } ?: return
        if (terminado || id !in libres) return
        pila = pila.filter { it.id != id }
        if (turnoUno) puntajeUno += pieza.valor else puntajeDos += pieza.valor
        services.sound.tocar(if (pieza.valor >= 30) Efecto.WIN else Efecto.CORRECT)
        aviso = null
        if (pila.isEmpty()) {
            terminado = true
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        } else {
            turnoUno = !turnoUno
        }
    }

    // A diferencia de Damas/Damas chinas, este cálculo (máximo de una lista de
    // ~19 elementos) es demasiado barato para justificar Dispatchers.Default —
    // no hay riesgo real de ANR aquí, corre en el hilo principal sin problema.
    LaunchedEffect(turnoUno, pila, dosJugadores, terminado) {
        if (terminado || dosJugadores || turnoUno) return@LaunchedEffect
        delay(700)
        val elegido = pila.filter { it.id in libres }.maxByOrNull { it.valor } ?: return@LaunchedEffect
        tomar(elegido.id)
    }

    fun tocarLienzo(offset: Offset, anchoPx: Float, altoPx: Float) {
        if (terminado) return
        if (!dosJugadores && !turnoUno) return
        val candidato = pila.minByOrNull { distanciaPuntoSegmento(offset, extremos(it, anchoPx, altoPx).first, extremos(it, anchoPx, altoPx).second) }
            ?: return
        val d = distanciaPuntoSegmento(offset, extremos(candidato, anchoPx, altoPx).first, extremos(candidato, anchoPx, altoPx).second)
        if (d > 26f) return
        if (candidato.id in libres) {
            tomar(candidato.id)
        } else {
            services.sound.tocar(Efecto.WRONG)
            aviso = "Ese tiene otro palillo encima — busca uno que brille"
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            terminado -> if (puntajeUno == puntajeDos) "¡Empate! ${puntajeUno} y ${puntajeDos} puntos" else {
                val ganoUno = puntajeUno > puntajeDos
                if (dosJugadores) "¡Gana Jugador ${if (ganoUno) 1 else 2}! $puntajeUno vs $puntajeDos" else if (ganoUno) "¡Ganaste! $puntajeUno vs $puntajeDos" else "Ganó la computadora — $puntajeUno vs $puntajeDos"
            }
            aviso != null -> aviso!!
            dosJugadores -> if (turnoUno) "Turno de Jugador 1 — toca un palillo que brille" else "Turno de Jugador 2 — toca un palillo que brille"
            turnoUno -> "Tu turno — toca un palillo que brille"
            else -> "Turno de la computadora…"
        },
        celebrar = terminado,
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
            Text(
                if (dosJugadores) "Jugador 1: $puntajeUno · Jugador 2: $puntajeDos" else "Tú: $puntajeUno · Computadora: $puntajeDos",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(300.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF4E9D8))
                    .pointerInput(pila, libres, turnoUno, dosJugadores, terminado) {
                        detectTapGestures { offset -> tocarLienzo(offset, size.width.toFloat(), size.height.toFloat()) }
                    },
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    pila.sortedBy { it.z }.forEach { p ->
                        val (a1, a2) = extremos(p, size.width, size.height)
                        val libre = p.id in libres
                        drawLine(
                            color = if (libre) p.color else p.color.copy(alpha = 0.4f),
                            start = a1,
                            end = a2,
                            strokeWidth = if (libre) 10f else 7f,
                            cap = StrokeCap.Round,
                        )
                        if (libre) {
                            drawLine(color = Color.White.copy(alpha = 0.6f), start = a1, end = a2, strokeWidth = 2.2f, cap = StrokeCap.Round)
                        }
                    }
                }
            }
            Text(
                "El palillo negro vale más — tómalo sin mover los demás",
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
