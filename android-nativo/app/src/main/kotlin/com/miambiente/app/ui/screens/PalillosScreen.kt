package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay

private const val ANCHO = 300f
private const val ALTO_MONTON = 260f
private const val ALTO_BANDEJA = 60f
private const val ALTO = ALTO_MONTON + ALTO_BANDEJA
private const val GROSOR_VARILLA = 6f
internal const val N_VARILLAS = 24

private val COLORES_VARILLA = listOf(
    Color(0xFFD9433A), Color(0xFFE0925C), Color(0xFFE0C23C),
    Color(0xFF6FBF73), Color(0xFF3E9BE0), Color(0xFFA97FC7),
)

/** Punto simple (no `androidx.compose.ui.geometry.Offset`) para que la
 * geometría de intersección sea una función pura testeable sin depender de
 * Compose. */
internal data class Punto(val x: Float, val y: Float)

// `duenio`: null = sigue en el montón; 0 = la sacó el jugador 1; 1 = la
// sacó el jugador 2 (o la computadora). El ángulo NO cambia al arrastrar
// — simplificación consciente: el jugador traslada la varilla en línea
// recta, no la rota, para que el gesto sea simple para una mano de niño.
internal data class Varilla(
    val id: Int, val cx: Float, val cy: Float, val angulo: Float,
    val largo: Float, val color: Color, val duenio: Int? = null,
)

internal fun extremosDeVarilla(v: Varilla): Pair<Punto, Punto> {
    val rad = Math.toRadians(v.angulo.toDouble())
    val dx = (v.largo / 2f * kotlin.math.cos(rad)).toFloat()
    val dy = (v.largo / 2f * kotlin.math.sin(rad)).toFloat()
    return Punto(v.cx - dx, v.cy - dy) to Punto(v.cx + dx, v.cy + dy)
}

/**
 * ¿Se cruzan los segmentos p1-p2 y p3-p4? Algoritmo estándar de
 * orientación de 3 puntos (con los 4 casos colineales): no hay ningún
 * patrón previo de geometría continua de este tipo en el proyecto (lo más
 * cercano es colisión círculo-rectángulo de Arkanoid), así que se
 * documenta el "por qué" aquí. Tocarse en un solo extremo también cuenta
 * como cruce — más estricto, más fiel al espíritu real del juego (rozar ya
 * cuenta como "molestar" la varilla).
 */
internal fun segmentosSeCruzan(p1: Punto, p2: Punto, p3: Punto, p4: Punto): Boolean {
    fun orientacion(a: Punto, b: Punto, c: Punto): Int {
        val v = (b.y - a.y) * (c.x - b.x) - (b.x - a.x) * (c.y - b.y)
        return when {
            v > 0f -> 1
            v < 0f -> -1
            else -> 0
        }
    }
    fun enSegmento(a: Punto, b: Punto, c: Punto): Boolean =
        c.x <= maxOf(a.x, b.x) && c.x >= minOf(a.x, b.x) && c.y <= maxOf(a.y, b.y) && c.y >= minOf(a.y, b.y)

    val o1 = orientacion(p1, p2, p3)
    val o2 = orientacion(p1, p2, p4)
    val o3 = orientacion(p3, p4, p1)
    val o4 = orientacion(p3, p4, p2)

    if (o1 != o2 && o3 != o4) return true
    if (o1 == 0 && enSegmento(p1, p2, p3)) return true
    if (o2 == 0 && enSegmento(p1, p2, p4)) return true
    if (o3 == 0 && enSegmento(p3, p4, p1)) return true
    if (o4 == 0 && enSegmento(p3, p4, p2)) return true
    return false
}

internal fun varillasSeCruzan(a: Varilla, b: Varilla): Boolean {
    val (a1, a2) = extremosDeVarilla(a)
    val (b1, b2) = extremosDeVarilla(b)
    return segmentosSeCruzan(a1, a2, b1, b2)
}

internal fun varillasQueLaCruzan(v: Varilla, resto: List<Varilla>): List<Varilla> =
    resto.filter { it.id != v.id && varillasSeCruzan(v, it) }

/** Baja mientras más varillas cruce: 0 cruces → 95%, muchas → un piso de 20%
 * (nunca imposible, para que la IA no se vea "perfecta"). */
internal fun probabilidadExito(cruces: Int): Float = (1f / (1 + cruces)).coerceIn(0.2f, 0.95f)

/** La IA elige la varilla libre con menos varillas cruzándola — la más
 * "segura" de sacar, sin necesitar un árbol de jugadas completo (con 24
 * varillas superpuestas sería enorme). */
internal fun elegirVarillaIA(libres: List<Varilla>): Varilla? =
    libres.minByOrNull { varillasQueLaCruzan(it, libres).size }

/** ¿Ya salió del todo del área del montón? Los dos extremos deben quedar
 * dentro de la franja de abajo ("tu bandeja"), no solo rozar el borde. */
internal fun fueraDelMonton(v: Varilla): Boolean {
    val (p1, p2) = extremosDeVarilla(v)
    return p1.y > ALTO_MONTON && p2.y > ALTO_MONTON
}

/**
 * Genera un montón creíble de N_VARILLAS sin física de n-cuerpos real:
 * posiciones dispersas alrededor del centro (suma de dos números al azar,
 * el truco clásico para aproximar una campana sin librería de estadística)
 * y ángulo aleatorio — las varillas SÍ se superponen entre sí a propósito,
 * como en el juego real.
 */
internal fun montonInicial(azar: kotlin.random.Random = kotlin.random.Random.Default): List<Varilla> {
    val centroX = ANCHO / 2f
    val centroY = ALTO_MONTON / 2f
    return List(N_VARILLAS) { i ->
        val dispersionX = (azar.nextFloat() + azar.nextFloat() - 1f) * 55f
        val dispersionY = (azar.nextFloat() + azar.nextFloat() - 1f) * 55f
        Varilla(
            id = i,
            cx = centroX + dispersionX,
            cy = centroY + dispersionY,
            angulo = azar.nextFloat() * 180f,
            largo = 90f,
            color = COLORES_VARILLA[i % COLORES_VARILLA.size],
        )
    }
}

/**
 * Palillos chinos (Mikado) — jugable y con la regla real de "no molestar
 * las demás", con dos simplificaciones conscientes y declaradas: la caída
 * inicial es un montón pre-calculado (no física de colisión entre 24
 * cuerpos rígidos) y el arrastre es traslación en línea recta (no rotación
 * libre de la varilla).
 */
@Composable
fun PalillosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("palillos")!!

    var dosJugadores by remember { mutableStateOf(false) }
    var varillas by remember { mutableStateOf(montonInicial()) }
    var turno by remember { mutableStateOf(0) }
    var puntajes by remember { mutableStateOf(listOf(0, 0)) }
    var fallos by remember { mutableStateOf(listOf(0, 0)) }
    var terminado by remember { mutableStateOf(false) }

    fun reiniciar() {
        varillas = montonInicial()
        turno = 0
        puntajes = listOf(0, 0)
        fallos = listOf(0, 0)
        terminado = false
    }

    fun cambiarModo(activarDosJugadores: Boolean) {
        dosJugadores = activarDosJugadores
        reiniciar()
    }

    fun registrarExito(v: Varilla) {
        varillas = varillas.map { if (it.id == v.id) it.copy(duenio = turno) else it }
        puntajes = puntajes.mapIndexed { i, p -> if (i == turno) p + 1 else p }
        fallos = fallos.mapIndexed { i, f -> if (i == turno) 0 else f }
        services.sound.tocar(Efecto.CORRECT)
        if (varillas.none { it.duenio == null }) terminado = true
        // El turno se queda igual: en Mikado real se sigue jugando mientras se acierte.
    }

    fun registrarFallo() {
        services.sound.tocar(Efecto.WRONG)
        fallos = fallos.mapIndexed { i, f -> if (i == turno) f + 1 else f }
        val ambosAtascados = fallos.all { it >= 3 }
        if (ambosAtascados || varillas.none { it.duenio == null }) {
            terminado = true
        } else {
            turno = 1 - turno
        }
    }

    fun manejarArrastre(v: Varilla, dx: Float, dy: Float): Boolean {
        val candidata = v.copy(cx = v.cx + dx, cy = v.cy + dy)
        val resto = varillas.filter { it.id != v.id && it.duenio == null }
        if (varillasQueLaCruzan(candidata, resto).isNotEmpty()) return false
        varillas = varillas.map { if (it.id == v.id) candidata else it }
        return true
    }

    // La computadora juega sola en su turno: elige la varilla más "segura"
    // (menos cruces) y tira una moneda cargada según cuántas la cruzan —
    // sigue jugando mientras acierte, igual que un jugador humano.
    LaunchedEffect(turno, terminado, dosJugadores) {
        if (dosJugadores || terminado || turno != 1) return@LaunchedEffect
        while (turno == 1 && !terminado) {
            delay(900)
            val libres = varillas.filter { it.duenio == null }
            val elegida = elegirVarillaIA(libres) ?: break
            val cruces = varillasQueLaCruzan(elegida, libres).size
            if (kotlin.random.Random.nextFloat() < probabilidadExito(cruces)) {
                registrarExito(elegida)
            } else {
                registrarFallo()
            }
        }
    }

    val libres = varillas.filter { it.duenio == null }

    GameShell(
        juego = juego,
        consigna = when {
            terminado -> when {
                puntajes[0] > puntajes[1] -> "¡Ganaste! ${puntajes[0]} contra ${puntajes[1]}"
                puntajes[1] > puntajes[0] -> if (dosJugadores) "Ganó el jugador 2: ${puntajes[1]} a ${puntajes[0]}" else "Ganó la computadora: ${puntajes[1]} a ${puntajes[0]}"
                else -> "Empate: ${puntajes[0]} a ${puntajes[1]}"
            }
            dosJugadores -> if (turno == 0) "Turno del jugador 1 — arrastra una varilla a tu bandeja" else "Turno del jugador 2 — arrastra una varilla a tu bandeja"
            turno == 0 -> "Tu turno — arrastra una varilla suelta a tu bandeja, sin tocar las demás"
            else -> "Turno de la computadora…"
        },
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                if (dosJugadores) "🥢 J1: ${puntajes[0]}  ·  🥢 J2: ${puntajes[1]}" else "🥢 Tú: ${puntajes[0]}  ·  🥢 Computadora: ${puntajes[1]}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp),
            )
            Box(modifier = Modifier.padding(top = 8.dp).horizontalScroll(rememberScrollState())) {
                Box(
                    modifier = Modifier
                        .size(width = ANCHO.dp, height = ALTO.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF3ECDD)),
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = ALTO_MONTON.dp)
                            .size(width = ANCHO.dp, height = 2.dp)
                            .background(Color(0xFFB5732F)),
                    )
                    Text(
                        "tu bandeja ⬇️",
                        fontSize = 10.sp,
                        color = Color(0xFFB5732F),
                        modifier = Modifier.offset(x = 8.dp, y = (ALTO_MONTON + 4f).dp),
                    )
                    libres.forEach { v ->
                        Box(
                            modifier = Modifier
                                .offset(x = (v.cx - v.largo / 2f).dp, y = (v.cy - GROSOR_VARILLA / 2f).dp)
                                .size(width = v.largo.dp, height = GROSOR_VARILLA.dp)
                                .rotate(v.angulo)
                                .clip(RoundedCornerShape((GROSOR_VARILLA / 2f).dp))
                                .background(v.color)
                                .pointerInput(v.id, turno, dosJugadores, terminado) {
                                    if (terminado) return@pointerInput
                                    if (!dosJugadores && turno != 0) return@pointerInput
                                    detectDragGestures(
                                        onDragEnd = {
                                            val actual = varillas.first { it.id == v.id }
                                            if (actual.duenio == null && fueraDelMonton(actual)) registrarExito(actual)
                                        },
                                    ) { change, dragAmount ->
                                        change.consume()
                                        val actual = varillas.first { it.id == v.id }
                                        if (actual.duenio == null) {
                                            val ok = manejarArrastre(actual, dragAmount.x, dragAmount.y)
                                            if (!ok) registrarFallo()
                                        }
                                    }
                                },
                        )
                    }
                }
            }
        }
    }
}
