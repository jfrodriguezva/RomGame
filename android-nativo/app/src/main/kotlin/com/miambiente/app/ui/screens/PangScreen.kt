package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonArcade
import com.miambiente.app.ui.materials.BotonMantenerArcade
import com.miambiente.app.ui.materials.MarcadorArcade
import com.miambiente.app.ui.materials.MarcoArcade
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val ANCHO = 300f
private const val ALTO = 420f
private const val GRAVEDAD = 260f
private const val VEL_ARPON = 700f
private const val ANCHO_JUGADOR = 40f
private const val ALTO_JUGADOR = 14f
private const val Y_JUGADOR = ALTO - 26f
private const val GROSOR_ARPON = 3f
private const val VIDAS_INICIALES = 3
private const val INVULNERABILIDAD_NANOS = 1_000_000_000L
private const val VELOCIDAD_JUGADOR_BOTON = 200f

private val COLORES_TAMANO = mapOf(3 to Color(0xFF3E9BE0), 2 to Color(0xFF6FBF73), 1 to Color(0xFFE0C23C))

internal data class BurbujaState(val id: Int, val x: Float, val y: Float, val velX: Float, val velY: Float, val tamano: Int)
internal data class ArponState(val alturaActual: Float, val retrayendo: Boolean)

/** El radio crece con el tamaño: 1 (chica) → 20, 2 (mediana) → 28, 3 (grande) → 36. */
internal fun radioDe(tamano: Int): Float = 12f + tamano * 8f

/** Al tocar una burbuja con el arpón se divide en dos más chicas con
 * velocidades horizontales opuestas — hasta el tamaño mínimo, que desaparece. */
internal fun dividirBurbuja(b: BurbujaState, siguienteId: Int): List<BurbujaState> =
    if (b.tamano <= 1) {
        emptyList()
    } else {
        listOf(
            b.copy(tamano = b.tamano - 1, velX = 90f, id = siguienteId),
            b.copy(tamano = b.tamano - 1, velX = -90f, id = siguienteId + 1),
        )
    }

/** Más burbujas grandes y algo más rápidas por nivel, mismo criterio de
 * escalado que `velocidadParaNivel` en Arkanoid. */
internal fun burbujasParaNivel(nivel: Int): List<BurbujaState> {
    val cantidad = (2 + nivel).coerceAtMost(6)
    val velocidadBase = 60f + (nivel - 1).coerceAtMost(8) * 8f
    return List(cantidad) { i ->
        val x = 40f + i * ((ANCHO - 80f) / cantidad.coerceAtLeast(1))
        BurbujaState(
            id = i, x = x, y = 50f + (i % 3) * 30f,
            velX = if (i % 2 == 0) velocidadBase else -velocidadBase, velY = 0f, tamano = 3,
        )
    }
}

/**
 * Pang — arpón fijo que sube y se retrae, burbujas que rebotan con
 * gravedad en paredes/techo/suelo y se dividen en dos más chicas al ser
 * tocadas, generado de cero. Física real por cuadro (mismo patrón que
 * Arkanoid: `withFrameNanos`), colisión círculo-rectángulo reutilizada de
 * `ArkanoidScreen.kt` (`circuloChocaRect`), tratando el arpón como un
 * rectángulo angosto en vez de duplicar la fórmula de colisión.
 */
@Composable
fun PangScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("pang")!!

    var nivel by remember { mutableStateOf(1) }
    var burbujas by remember { mutableStateOf(burbujasParaNivel(1)) }
    var siguienteIdBurbuja by remember { mutableStateOf(burbujas.size) }
    var jugadorX by remember { mutableStateOf(ANCHO / 2f) }
    var arpon by remember { mutableStateOf<ArponState?>(null) }
    var vidas by remember { mutableStateOf(VIDAS_INICIALES) }
    var puntaje by remember { mutableStateOf(0) }
    var invulnerableHasta by remember { mutableStateOf(0L) }
    var terminado by remember { mutableStateOf(false) }
    var mostrandoNivel by remember { mutableStateOf(false) }

    fun reiniciar() {
        nivel = 1
        burbujas = burbujasParaNivel(1)
        siguienteIdBurbuja = burbujas.size
        jugadorX = ANCHO / 2f
        arpon = null
        vidas = VIDAS_INICIALES
        puntaje = 0
        invulnerableHasta = 0L
        terminado = false
        mostrandoNivel = false
    }

    fun disparar() {
        if (arpon == null && !terminado && !mostrandoNivel) {
            arpon = ArponState(alturaActual = Y_JUGADOR, retrayendo = false)
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

            if (invulnerableHasta != 0L && ahora > invulnerableHasta) invulnerableHasta = 0L

            arpon?.let { a ->
                arpon = if (!a.retrayendo) {
                    val nuevaAltura = a.alturaActual - VEL_ARPON * dt
                    if (nuevaAltura <= 0f) a.copy(alturaActual = 0f, retrayendo = true) else a.copy(alturaActual = nuevaAltura)
                } else {
                    val nuevaAltura = a.alturaActual + VEL_ARPON * dt
                    if (nuevaAltura >= Y_JUGADOR) null else a.copy(alturaActual = nuevaAltura)
                }
            }

            var perdioVida = false
            val siguientes = mutableListOf<BurbujaState>()
            for (b0 in burbujas) {
                val r = radioDe(b0.tamano)
                var x = b0.x + b0.velX * dt
                var y = b0.y + b0.velY * dt
                var vx = b0.velX
                var vy = b0.velY + GRAVEDAD * dt

                if (x - r < 0f) { x = r; vx = abs(vx) }
                if (x + r > ANCHO) { x = ANCHO - r; vx = -abs(vx) }
                if (y - r < 0f) { y = r; vy = abs(vy) }
                if (y + r > Y_JUGADOR) { y = Y_JUGADOR - r; vy = -abs(vy) * 0.92f }

                val arponActual = arpon
                val golpeadaPorArpon = arponActual != null && circuloChocaRect(
                    x, y, r, jugadorX - GROSOR_ARPON / 2f, arponActual.alturaActual, GROSOR_ARPON, Y_JUGADOR - arponActual.alturaActual,
                )
                if (golpeadaPorArpon) {
                    arpon = arponActual!!.copy(retrayendo = true)
                    siguientes.addAll(dividirBurbuja(b0.copy(x = x, y = y, velX = vx, velY = vy), siguienteIdBurbuja))
                    siguienteIdBurbuja += 2
                    puntaje += 10
                    services.sound.tocar(Efecto.CORRECT)
                    continue
                }

                if (invulnerableHasta == 0L && circuloChocaRect(x, y, r, jugadorX - ANCHO_JUGADOR / 2f, Y_JUGADOR, ANCHO_JUGADOR, ALTO_JUGADOR)) {
                    vidas -= 1
                    invulnerableHasta = ahora + INVULNERABILIDAD_NANOS
                    services.sound.tocar(Efecto.WRONG)
                    perdioVida = true
                }

                siguientes.add(BurbujaState(b0.id, x, y, vx, vy, b0.tamano))
            }
            burbujas = siguientes

            if (perdioVida && vidas <= 0) {
                terminado = true
                break
            }

            if (burbujas.isEmpty()) {
                services.sound.tocar(Efecto.WIN)
                scope.launch { services.progress.completarNivel(juego.id, nivel) }
                mostrandoNivel = true
                scope.launch {
                    delay(1600)
                    nivel += 1
                    burbujas = burbujasParaNivel(nivel)
                    siguienteIdBurbuja = burbujas.size
                    arpon = null
                    mostrandoNivel = false
                }
                break
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            mostrandoNivel -> "¡Nivel $nivel superado! Vas al nivel ${nivel + 1}"
            terminado -> "Juego terminado — Nivel $nivel, puntaje: $puntaje"
            else -> "Mantén ⬅️ o ➡️ para moverte, toca 🔫 para disparar"
        },
        celebrar = mostrandoNivel,
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MarcadorArcade("Nivel $nivel  ·  ${"❤️".repeat(vidas)}  ·  Puntaje: $puntaje")
            MarcoArcade(colorFondo = Color(0xFF1E3A4A), modifier = Modifier.padding(top = 10.dp)) {
                Box(modifier = Modifier.align(Alignment.Center).size(width = ANCHO.dp, height = ALTO.dp)) {
                    arpon?.let { a ->
                        Box(
                            modifier = Modifier
                                .offset(x = (jugadorX - GROSOR_ARPON / 2f).dp, y = a.alturaActual.dp)
                                .size(width = GROSOR_ARPON.dp, height = (Y_JUGADOR - a.alturaActual).dp)
                                .background(Color(0xFFE0C23C)),
                        )
                    }
                    burbujas.forEach { b ->
                        val r = radioDe(b.tamano)
                        Box(
                            modifier = Modifier
                                .offset(x = (b.x - r).dp, y = (b.y - r).dp)
                                .size((r * 2).dp)
                                .clip(CircleShape)
                                .background(COLORES_TAMANO[b.tamano] ?: Color.White),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = (jugadorX - ANCHO_JUGADOR / 2f).dp, y = Y_JUGADOR.dp)
                            .size(width = ANCHO_JUGADOR.dp, height = ALTO_JUGADOR.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (invulnerableHasta != 0L) Color(0xFFE0925C) else Color(0xFF8BBF6A)),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(30.dp), modifier = Modifier.padding(top = 10.dp)) {
                BotonMantenerArcade("⬅️") { dt -> jugadorX = (jugadorX - VELOCIDAD_JUGADOR_BOTON * dt).coerceIn(ANCHO_JUGADOR / 2f, ANCHO - ANCHO_JUGADOR / 2f) }
                BotonArcade("🔫") { disparar() }
                BotonMantenerArcade("➡️") { dt -> jugadorX = (jugadorX + VELOCIDAD_JUGADOR_BOTON * dt).coerceIn(ANCHO_JUGADOR / 2f, ANCHO - ANCHO_JUGADOR / 2f) }
            }
        }
    }
}
