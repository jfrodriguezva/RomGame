package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val ANCHO = 300f
private const val ALTO = 420f
private const val RADIO_PELOTA = 7f
private const val ANCHO_PALETA = 64f
private const val ALTO_PALETA = 12f
private const val Y_PALETA = ALTO - 30f
private const val VIDAS_INICIALES = 3
private const val COLS_LADRILLOS = 6
private const val FILAS_LADRILLOS = 5
private const val ANCHO_LADRILLO = (ANCHO - 20f) / COLS_LADRILLOS
private const val ALTO_LADRILLO = 16f

private val COLORES_FILA = listOf(Color(0xFFD9433A), Color(0xFFE0925C), Color(0xFFE0C23C), Color(0xFF6FBF73), Color(0xFF3E9BE0))

internal data class Ladrillo(val id: Int, val fila: Int, val col: Int, val x: Float, val y: Float, val color: Color)

internal fun ladrillosIniciales(): List<Ladrillo> {
    val lista = mutableListOf<Ladrillo>()
    var id = 0
    for (f in 0 until FILAS_LADRILLOS) for (c in 0 until COLS_LADRILLOS) {
        lista.add(Ladrillo(id++, f, c, 10f + c * ANCHO_LADRILLO, 30f + f * (ALTO_LADRILLO + 4f), COLORES_FILA[f % COLORES_FILA.size]))
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
 * Arkanoid / rompe ladrillos — arcade clásico, generado de cero. Física
 * real integrada por cuadro (mismo patrón que Globo/Burbujas/Carreras:
 * `withFrameNanos`, no incrementos fijos), paleta controlada por arrastre
 * horizontal, ángulo de rebote según en qué parte de la paleta pega la
 * pelota (como el juego real, no un rebote siempre igual), y ladrillos
 * que desaparecen al romperse.
 */
@Composable
fun ArkanoidScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("arkanoid")!!

    var ladrillos by remember { mutableStateOf(ladrillosIniciales()) }
    var paletaX by remember { mutableStateOf(ANCHO / 2f) }
    var bolaX by remember { mutableStateOf(ANCHO / 2f) }
    var bolaY by remember { mutableStateOf(Y_PALETA - RADIO_PELOTA - 2f) }
    var velX by remember { mutableStateOf(110f) }
    var velY by remember { mutableStateOf(-160f) }
    var lanzada by remember { mutableStateOf(false) }
    var vidas by remember { mutableStateOf(VIDAS_INICIALES) }
    var puntaje by remember { mutableStateOf(0) }
    var terminado by remember { mutableStateOf(false) }
    var gano by remember { mutableStateOf(false) }

    fun reiniciar() {
        ladrillos = ladrillosIniciales()
        paletaX = ANCHO / 2f
        bolaX = ANCHO / 2f
        bolaY = Y_PALETA - RADIO_PELOTA - 2f
        velX = 110f; velY = -160f
        lanzada = false
        vidas = VIDAS_INICIALES
        puntaje = 0
        terminado = false
        gano = false
    }

    fun lanzar() {
        if (!lanzada && !terminado) {
            lanzada = true
            services.sound.tocar(Efecto.CLICK)
        }
    }

    LaunchedEffect(terminado, gano) {
        if (terminado || gano) return@LaunchedEffect
        var anterior = withFrameNanos { it }
        while (true) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.032f)
            anterior = ahora
            if (terminado || gano) break

            if (!lanzada) {
                bolaX = paletaX
                continue
            }

            bolaX += velX * dt
            bolaY += velY * dt

            if (bolaX - RADIO_PELOTA < 0f) { bolaX = RADIO_PELOTA; velX = abs(velX) }
            if (bolaX + RADIO_PELOTA > ANCHO) { bolaX = ANCHO - RADIO_PELOTA; velX = -abs(velX) }
            if (bolaY - RADIO_PELOTA < 0f) { bolaY = RADIO_PELOTA; velY = abs(velY) }

            // Paleta: el ángulo de salida depende de en qué parte se le pega,
            // no siempre el mismo rebote — así se puede apuntar de verdad.
            if (velY > 0 && circuloChocaRect(bolaX, bolaY, RADIO_PELOTA, paletaX - ANCHO_PALETA / 2f, Y_PALETA, ANCHO_PALETA, ALTO_PALETA)) {
                val desvio = ((bolaX - paletaX) / (ANCHO_PALETA / 2f)).coerceIn(-1f, 1f)
                velX = desvio * 200f
                velY = -abs(velY)
                bolaY = Y_PALETA - RADIO_PELOTA
                services.sound.tocar(Efecto.CLICK)
            }

            val golpeado = ladrillos.firstOrNull { l -> circuloChocaRect(bolaX, bolaY, RADIO_PELOTA, l.x, l.y, ANCHO_LADRILLO - 3f, ALTO_LADRILLO) }
            if (golpeado != null) {
                ladrillos = ladrillos.filter { it.id != golpeado.id }
                velY = -velY
                puntaje += 10
                services.sound.tocar(Efecto.CORRECT)
                if (ladrillos.isEmpty()) {
                    gano = true
                    services.sound.tocar(Efecto.WIN)
                    scope.launch { services.progress.completarNivel(juego.id, 1) }
                }
            }

            if (bolaY - RADIO_PELOTA > ALTO) {
                vidas -= 1
                if (vidas <= 0) {
                    terminado = true
                    services.sound.tocar(Efecto.WRONG)
                } else {
                    lanzada = false
                    velX = 110f; velY = -160f
                    bolaY = Y_PALETA - RADIO_PELOTA - 2f
                }
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            gano -> "¡Rompiste todos los ladrillos! 🎉"
            terminado -> "Juego terminado — Puntaje: $puntaje"
            !lanzada -> "Toca para lanzar la pelota"
            else -> "Mueve la paleta arrastrando"
        },
        celebrar = gano,
        onVolver = onVolver,
        acciones = { if (terminado || gano) androidx.compose.material3.Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("❤️".repeat(vidas) + "  ·  Puntaje: $puntaje", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(width = ANCHO.dp, height = ALTO.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2233))
                    // Un solo pointerInput con las dos detecciones en
                    // paralelo (no dos `.pointerInput` separados): así
                    // Compose las corre sobre el mismo flujo de eventos sin
                    // que una gesto le robe los toques a la otra.
                    .pointerInput(terminado, gano) {
                        coroutineScope {
                            launch { detectTapGestures { lanzar() } }
                            launch {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    paletaX = (paletaX + dragAmount.x).coerceIn(ANCHO_PALETA / 2f, ANCHO - ANCHO_PALETA / 2f)
                                }
                            }
                        }
                    },
            ) {
                ladrillos.forEach { l ->
                    Box(
                        modifier = Modifier
                            .offset(x = l.x.dp, y = l.y.dp)
                            .size(width = (ANCHO_LADRILLO - 3f).dp, height = ALTO_LADRILLO.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(l.color),
                    )
                }
                Box(
                    modifier = Modifier
                        .offset(x = (paletaX - ANCHO_PALETA / 2f).dp, y = Y_PALETA.dp)
                        .size(width = ANCHO_PALETA.dp, height = ALTO_PALETA.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF8BBF6A)),
                )
                Box(
                    modifier = Modifier
                        .offset(x = (bolaX - RADIO_PELOTA).dp, y = (bolaY - RADIO_PELOTA).dp)
                        .size((RADIO_PELOTA * 2).dp)
                        .clip(CircleShape)
                        .background(Color.White),
                )
            }
        }
    }
}
