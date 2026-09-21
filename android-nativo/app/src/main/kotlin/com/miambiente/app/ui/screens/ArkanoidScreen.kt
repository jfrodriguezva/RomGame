package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonMantenerArcade
import com.miambiente.app.ui.materials.MarcadorArcade
import com.miambiente.app.ui.materials.MarcoArcade
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val ANCHO = 300f
private const val ALTO = 420f
private const val RADIO_PELOTA = 7f
private const val ANCHO_PALETA = 64f
private const val ALTO_PALETA = 12f
private const val Y_PALETA = ALTO - 30f
private const val VIDAS_INICIALES = 3
private const val VELOCIDAD_PALETA_BOTON = 260f
private const val COLS_LADRILLOS = 6
private const val FILAS_MAX = 8
private const val ANCHO_LADRILLO = (ANCHO - 20f) / COLS_LADRILLOS
private const val ALTO_LADRILLO = 16f
private const val VELOCIDAD_POWERUP = 90f
private const val RADIO_POWERUP = 9f
private const val DURACION_POWERUP_NANOS = 8_000_000_000L
private const val PROBABILIDAD_POWERUP = 0.18f

private val COLORES_FILA = listOf(Color(0xFFD9433A), Color(0xFFE0925C), Color(0xFFE0C23C), Color(0xFF6FBF73), Color(0xFF3E9BE0))
private val COLOR_INDESTRUCTIBLE = Color(0xFF8A8A8A)

internal data class Ladrillo(
    val id: Int, val fila: Int, val col: Int, val x: Float, val y: Float, val color: Color,
    val vidas: Int = 1, val esIndestructible: Boolean = false,
)

internal data class BolaState(val x: Float, val y: Float, val velX: Float, val velY: Float)

internal enum class TipoPowerUp { PALETA_ANCHA, PALETA_ANGOSTA, MULTIBOLA, BOLA_RAPIDA, BOLA_LENTA, VIDA_EXTRA }
internal data class PowerUp(val id: Int, val x: Float, val y: Float, val tipo: TipoPowerUp)

/** Más niveles, más completo: cada nivel agrega una fila (hasta el tope) y,
 * desde el nivel 3, las filas de arriba son ladrillos reforzados (2 golpes). */
internal fun filasParaNivel(nivel: Int): Int = (3 + (nivel - 1)).coerceAtMost(FILAS_MAX)

internal fun velocidadParaNivel(nivel: Int): Float = 160f + (nivel - 1).coerceAtMost(10) * 14f

/**
 * 5 formas de nivel que rotan según `(nivel - 1) % 5` — el índice 0 es
 * exactamente "filas completas" (el diseño original, así el nivel 1 no
 * cambia y los tests existentes de `ladrillosParaNivel` siguen pasando):
 * 0 filas completas, 1 marco (borde hueco al centro), 2 pirámide (se angosta
 * hacia abajo, la fila 0 siempre queda completa), 3 diamante (hueco central
 * que se ensancha y luego cierra), 4 tablero de ajedrez.
 */
internal fun layoutParaNivel(nivel: Int, filas: Int = filasParaNivel(nivel)): List<List<Boolean>> {
    val patron = (nivel - 1) % 5
    return List(filas) { f ->
        List(COLS_LADRILLOS) { c ->
            when (patron) {
                0 -> true
                1 -> f == 0 || f == filas - 1 || c == 0 || c == COLS_LADRILLOS - 1
                2 -> {
                    val huecoBordes = f.coerceAtMost((COLS_LADRILLOS - 1) / 2)
                    c >= huecoBordes && c < COLS_LADRILLOS - huecoBordes
                }
                3 -> {
                    val mitad = filas / 2f
                    val huecoCentro = (mitad - abs(f - mitad)).toInt().coerceAtLeast(0)
                    c < COLS_LADRILLOS / 2 - huecoCentro || c >= COLS_LADRILLOS / 2 + huecoCentro
                }
                else -> (f + c) % 2 == 0
            }
        }
    }
}

internal fun ladrillosParaNivel(nivel: Int): List<Ladrillo> {
    val filas = filasParaNivel(nivel)
    val filasReforzadas = if (nivel >= 3) (filas / 3).coerceAtLeast(1) else 0
    val layout = layoutParaNivel(nivel, filas)
    val esMarco = (nivel - 1) % 5 == 1
    val lista = mutableListOf<Ladrillo>()
    var id = 0
    for (f in 0 until filas) for (c in 0 until COLS_LADRILLOS) {
        if (!layout[f][c]) continue
        val reforzado = f < filasReforzadas
        // En el patrón "marco", una de cada tres celdas del borde es
        // indestructible: rebota la pelota para siempre pero no se rompe ni
        // da puntos — variedad real de layout, no solo más filas.
        val indestructible = esMarco && (f == 0 || f == filas - 1 || c == 0 || c == COLS_LADRILLOS - 1) && (f + c) % 3 == 0
        lista.add(
            Ladrillo(
                id = id++, fila = f, col = c,
                x = 10f + c * ANCHO_LADRILLO, y = 30f + f * (ALTO_LADRILLO + 4f),
                color = if (indestructible) COLOR_INDESTRUCTIBLE else COLORES_FILA[f % COLORES_FILA.size],
                vidas = if (reforzado) 2 else 1,
                esIndestructible = indestructible,
            ),
        )
    }
    return lista
}

/** ¿El círculo de la pelota se solapa con el rectángulo del ladrillo/paleta? */
internal fun circuloChocaRect(bx: Float, by: Float, r: Float, rx: Float, ry: Float, rw: Float, rh: Float): Boolean {
    val cx = bx.coerceIn(rx, rx + rw)
    val cy = by.coerceIn(ry, ry + rh)
    val dx = bx - cx
    val dy = by - cy
    return dx * dx + dy * dy <= r * r
}

/**
 * Resuelve de qué lado rebota la pelota contra un ladrillo: el eje con
 * MENOR penetración es el que acaba de cruzarse (llegó por ese lado), así
 * que ese es el que se invierte — resolución estándar de colisión círculo
 * contra rectángulo (AABB), no siempre "rebota hacia abajo" como antes.
 */
internal fun resolverReboteLadrillo(
    bx: Float, by: Float, r: Float, rx: Float, ry: Float, rw: Float, rh: Float, velX: Float, velY: Float,
): Pair<Float, Float> {
    val centroX = rx + rw / 2f
    val centroY = ry + rh / 2f
    val penetracionX = (rw / 2f + r) - abs(bx - centroX)
    val penetracionY = (rh / 2f + r) - abs(by - centroY)
    return if (penetracionX < penetracionY) (-velX) to velY else velX to (-velY)
}

/** Determinístico dado un valor de azar externo — así es testeable sin
 * depender de `Random` real (umbral: por debajo cae power-up, si no, no). */
internal fun deberiaCaerPowerUp(azar: Float): Boolean = azar < PROBABILIDAD_POWERUP

private fun colorPowerUp(tipo: TipoPowerUp): Color = when (tipo) {
    TipoPowerUp.PALETA_ANCHA -> Color(0xFF6FBF73)
    TipoPowerUp.PALETA_ANGOSTA -> Color(0xFFD9433A)
    TipoPowerUp.MULTIBOLA -> Color(0xFF3E9BE0)
    TipoPowerUp.BOLA_RAPIDA -> Color(0xFFE0C23C)
    TipoPowerUp.BOLA_LENTA -> Color(0xFFA97FC7)
    TipoPowerUp.VIDA_EXTRA -> Color(0xFFE0925C)
}

private fun emojiPowerUp(tipo: TipoPowerUp): String = when (tipo) {
    TipoPowerUp.PALETA_ANCHA -> "↔️"
    TipoPowerUp.PALETA_ANGOSTA -> "🔻"
    TipoPowerUp.MULTIBOLA -> "✨"
    TipoPowerUp.BOLA_RAPIDA -> "⚡"
    TipoPowerUp.BOLA_LENTA -> "🐌"
    TipoPowerUp.VIDA_EXTRA -> "❤️"
}

/**
 * Arkanoid / rompe ladrillos — arcade clásico, generado de cero. Física
 * real integrada por cuadro (mismo patrón que Globo/Burbujas/Carreras:
 * `withFrameNanos`, no incrementos fijos), paleta controlada con botones
 * de mantener presionado (homologado con el resto de los arcade, antes
 * era arrastre), ángulo de rebote según en qué parte de la paleta pega la
 * pelota, ladrillos que desaparecen al romperse, power-ups reales que caen
 * de un ladrillo roto (paleta ancha/angosta, multi-bola, bola rápida/lenta,
 * vida extra), varios patrones de nivel (no siempre filas completas) y
 * rebote lateral real contra los ladrillos (AABB, no siempre hacia abajo).
 */
@Composable
fun ArkanoidScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("arkanoid")!!

    var nivel by remember { mutableStateOf(1) }
    var ladrillos by remember { mutableStateOf(ladrillosParaNivel(1)) }
    var paletaX by remember { mutableStateOf(ANCHO / 2f) }
    var anchoPaleta by remember { mutableStateOf(ANCHO_PALETA) }
    var anchoPaletaExpiraEn by remember { mutableStateOf(0L) }
    var bolas by remember { mutableStateOf(listOf(BolaState(ANCHO / 2f, Y_PALETA - RADIO_PELOTA - 2f, 110f, -velocidadParaNivel(1)))) }
    var powerUps by remember { mutableStateOf(listOf<PowerUp>()) }
    var siguienteIdPowerUp by remember { mutableStateOf(0) }
    var lanzada by remember { mutableStateOf(false) }
    var vidas by remember { mutableStateOf(VIDAS_INICIALES) }
    var puntaje by remember { mutableStateOf(0) }
    var terminado by remember { mutableStateOf(false) }
    var mostrandoNivel by remember { mutableStateOf(false) }

    fun prepararLanzamiento() {
        paletaX = ANCHO / 2f
        anchoPaleta = ANCHO_PALETA
        anchoPaletaExpiraEn = 0L
        bolas = listOf(BolaState(ANCHO / 2f, Y_PALETA - RADIO_PELOTA - 2f, 110f, -velocidadParaNivel(nivel)))
        powerUps = emptyList()
        lanzada = false
    }

    fun reiniciar() {
        nivel = 1
        ladrillos = ladrillosParaNivel(1)
        prepararLanzamiento()
        vidas = VIDAS_INICIALES
        puntaje = 0
        terminado = false
        mostrandoNivel = false
    }

    fun lanzar() {
        if (!lanzada && !terminado && !mostrandoNivel) {
            lanzada = true
            services.sound.tocar(Efecto.CLICK)
        }
    }

    LaunchedEffect(terminado, mostrandoNivel) {
        if (terminado || mostrandoNivel) return@LaunchedEffect
        var anterior = withFrameNanos { it }
        while (true) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.032f)
            anterior = ahora
            if (terminado || mostrandoNivel) break

            if (anchoPaletaExpiraEn != 0L && ahora > anchoPaletaExpiraEn) {
                anchoPaleta = ANCHO_PALETA
                anchoPaletaExpiraEn = 0L
            }

            if (!lanzada) {
                bolas = bolas.map { it.copy(x = paletaX) }
                continue
            }

            val paletaIzq = paletaX - anchoPaleta / 2f

            // Power-ups cayendo: si la paleta los atrapa, se aplica su
            // efecto; si no, siguen cayendo hasta salir de la pantalla.
            val powerUpsVivos = mutableListOf<PowerUp>()
            for (p in powerUps) {
                val ny = p.y + VELOCIDAD_POWERUP * dt
                if (circuloChocaRect(p.x, ny, RADIO_POWERUP, paletaIzq, Y_PALETA, anchoPaleta, ALTO_PALETA)) {
                    when (p.tipo) {
                        TipoPowerUp.PALETA_ANCHA -> { anchoPaleta = ANCHO_PALETA * 1.5f; anchoPaletaExpiraEn = ahora + DURACION_POWERUP_NANOS }
                        TipoPowerUp.PALETA_ANGOSTA -> { anchoPaleta = ANCHO_PALETA * 0.65f; anchoPaletaExpiraEn = ahora + DURACION_POWERUP_NANOS }
                        TipoPowerUp.MULTIBOLA -> {
                            val base = bolas.firstOrNull()
                            if (base != null) {
                                bolas = bolas + BolaState(base.x, base.y, base.velX + 40f, base.velY) +
                                    BolaState(base.x, base.y, base.velX - 40f, base.velY)
                            }
                        }
                        TipoPowerUp.BOLA_RAPIDA -> bolas = bolas.map { it.copy(velX = it.velX * 1.3f, velY = it.velY * 1.3f) }
                        TipoPowerUp.BOLA_LENTA -> bolas = bolas.map { it.copy(velX = it.velX * 0.75f, velY = it.velY * 0.75f) }
                        TipoPowerUp.VIDA_EXTRA -> vidas += 1
                    }
                    services.sound.tocar(Efecto.CORRECT)
                } else if (ny < ALTO) {
                    powerUpsVivos.add(p.copy(y = ny))
                }
            }
            powerUps = powerUpsVivos

            var ladrillosActuales = ladrillos
            val bolasVivas = mutableListOf<BolaState>()
            for (bola0 in bolas) {
                var bx = bola0.x + bola0.velX * dt
                var by = bola0.y + bola0.velY * dt
                var vx = bola0.velX
                var vy = bola0.velY

                if (bx - RADIO_PELOTA < 0f) { bx = RADIO_PELOTA; vx = abs(vx) }
                if (bx + RADIO_PELOTA > ANCHO) { bx = ANCHO - RADIO_PELOTA; vx = -abs(vx) }
                if (by - RADIO_PELOTA < 0f) { by = RADIO_PELOTA; vy = abs(vy) }

                // Paleta: el ángulo de salida depende de en qué parte se le
                // pega, no siempre el mismo rebote — así se puede apuntar.
                if (vy > 0 && circuloChocaRect(bx, by, RADIO_PELOTA, paletaIzq, Y_PALETA, anchoPaleta, ALTO_PALETA)) {
                    val desvio = ((bx - paletaX) / (anchoPaleta / 2f)).coerceIn(-1f, 1f)
                    val rapidez = kotlin.math.hypot(vx.toDouble(), vy.toDouble()).toFloat()
                    vx = desvio * rapidez
                    vy = -kotlin.math.sqrt((rapidez * rapidez - vx * vx).coerceAtLeast(rapidez * rapidez * 0.3f))
                    by = Y_PALETA - RADIO_PELOTA
                    services.sound.tocar(Efecto.CLICK)
                }

                val golpeado = ladrillosActuales.firstOrNull { l -> circuloChocaRect(bx, by, RADIO_PELOTA, l.x, l.y, ANCHO_LADRILLO - 3f, ALTO_LADRILLO) }
                if (golpeado != null) {
                    val (nvx, nvy) = resolverReboteLadrillo(bx, by, RADIO_PELOTA, golpeado.x, golpeado.y, ANCHO_LADRILLO - 3f, ALTO_LADRILLO, vx, vy)
                    vx = nvx; vy = nvy
                    if (!golpeado.esIndestructible) {
                        val vidasRestantes = golpeado.vidas - 1
                        if (vidasRestantes <= 0) {
                            if (deberiaCaerPowerUp(kotlin.random.Random.nextFloat())) {
                                powerUps = powerUps + PowerUp(siguienteIdPowerUp++, golpeado.x + ANCHO_LADRILLO / 2f, golpeado.y, TipoPowerUp.entries.random())
                            }
                            ladrillosActuales = ladrillosActuales.filter { it.id != golpeado.id }
                        } else {
                            ladrillosActuales = ladrillosActuales.map { if (it.id == golpeado.id) it.copy(vidas = vidasRestantes) else it }
                        }
                        puntaje += 10
                        services.sound.tocar(Efecto.CORRECT)
                    } else {
                        services.sound.tocar(Efecto.CLICK)
                    }
                }

                if (by - RADIO_PELOTA <= ALTO) bolasVivas.add(BolaState(bx, by, vx, vy))
            }
            ladrillos = ladrillosActuales

            if (ladrillos.none { !it.esIndestructible }) {
                services.sound.tocar(Efecto.WIN)
                scope.launch { services.progress.completarNivel(juego.id, nivel) }
                mostrandoNivel = true
                scope.launch {
                    delay(1600)
                    nivel += 1
                    ladrillos = ladrillosParaNivel(nivel)
                    prepararLanzamiento()
                    mostrandoNivel = false
                }
                break
            }

            if (bolasVivas.isEmpty()) {
                vidas -= 1
                if (vidas <= 0) {
                    terminado = true
                    services.sound.tocar(Efecto.WRONG)
                } else {
                    prepararLanzamiento()
                }
            } else {
                bolas = bolasVivas
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            mostrandoNivel -> "¡Nivel $nivel superado! Vas al nivel ${nivel + 1}"
            terminado -> "Juego terminado — Nivel $nivel, puntaje: $puntaje"
            !lanzada -> "Toca para lanzar la pelota"
            else -> "Mantén ⬅️ o ➡️ para mover la paleta"
        },
        celebrar = mostrandoNivel,
        onVolver = onVolver,
        acciones = { if (terminado) androidx.compose.material3.Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MarcadorArcade("Nivel $nivel  ·  ${"❤️".repeat(vidas)}  ·  Puntaje: $puntaje")
            MarcoArcade(colorFondo = Color(0xFF1E2233), modifier = Modifier.padding(top = 10.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = ANCHO.dp, height = ALTO.dp)
                    .pointerInput(terminado, mostrandoNivel) { detectTapGestures { lanzar() } },
            ) {
                ladrillos.forEach { l ->
                    Box(
                        modifier = Modifier
                            .offset(x = l.x.dp, y = l.y.dp)
                            .size(width = (ANCHO_LADRILLO - 3f).dp, height = ALTO_LADRILLO.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(l.color)
                            // Ladrillos reforzados (2 golpes, desde el nivel
                            // 3): un borde blanco los distingue — se les
                            // pega dos veces antes de romperse de verdad.
                            .then(if (l.vidas >= 2) Modifier.border(1.5f.dp, Color.White) else Modifier),
                    )
                }
                powerUps.forEach { p ->
                    Box(
                        modifier = Modifier
                            .offset(x = (p.x - RADIO_POWERUP).dp, y = (p.y - RADIO_POWERUP).dp)
                            .size((RADIO_POWERUP * 2).dp)
                            .clip(CircleShape)
                            .background(colorPowerUp(p.tipo)),
                        contentAlignment = Alignment.Center,
                    ) { Text(emojiPowerUp(p.tipo), fontSize = 9.sp) }
                }
                Box(
                    modifier = Modifier
                        .offset(x = (paletaX - anchoPaleta / 2f).dp, y = Y_PALETA.dp)
                        .size(width = anchoPaleta.dp, height = ALTO_PALETA.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF8BBF6A)),
                )
                bolas.forEach { bola ->
                    Box(
                        modifier = Modifier
                            .offset(x = (bola.x - RADIO_PELOTA).dp, y = (bola.y - RADIO_PELOTA).dp)
                            .size((RADIO_PELOTA * 2).dp)
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                }
            }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(60.dp), modifier = Modifier.padding(top = 10.dp)) {
                BotonMantenerArcade("⬅️") { dt -> paletaX = (paletaX - VELOCIDAD_PALETA_BOTON * dt).coerceIn(anchoPaleta / 2f, ANCHO - anchoPaleta / 2f) }
                BotonMantenerArcade("➡️") { dt -> paletaX = (paletaX + VELOCIDAD_PALETA_BOTON * dt).coerceIn(anchoPaleta / 2f, ANCHO - anchoPaleta / 2f) }
            }
        }
    }
}
