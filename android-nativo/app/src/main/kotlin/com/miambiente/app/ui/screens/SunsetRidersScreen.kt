package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonArcade
import com.miambiente.app.ui.materials.MarcadorArcade
import com.miambiente.app.ui.materials.MarcoArcade
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val ANCHO = 300f
private const val ALTO = 420f
private const val SUELO_Y = ALTO - 50f
private const val GRAVEDAD = 500f
private const val IMPULSO_SALTO = 330f
private const val JUGADOR_X = 55f
private const val VEL_BALA_JUGADOR = 260f
private const val VEL_BALA_ENEMIGA = 150f
private const val VIDAS_INICIALES = 3
private const val OBJETIVO_BANDIDOS = 12
private const val INVULNERABILIDAD_NANOS = 900_000_000L

internal enum class EstadoJugador { DE_PIE, AGACHADO, SALTANDO }
internal enum class TipoDisparo { ALTO, BAJO }

internal const val GOLPES_JEFE = 3

internal data class Bandido(
    val id: Int, val x: Float, val velX: Float, val proximoDisparoEn: Long,
    val vidas: Int = 1, val esJefe: Boolean = false,
)
internal data class BalaVaqueros(val id: Int, val x: Float, val tipo: TipoDisparo?, val deEnemigo: Boolean)

/** El último bandido de la ronda (justo antes de llegar al objetivo) es el
 * jefe — cierre real de la ronda, como el jefe de fin de nivel del arcade
 * original, en vez de terminar de golpe al derrotar a uno más. */
internal fun esRondaDeJefe(derrotados: Int, objetivo: Int = OBJETIVO_BANDIDOS): Boolean = derrotados == objetivo - 1

internal fun vidasParaBandido(esJefe: Boolean): Int = if (esJefe) GOLPES_JEFE else 1

/** Un tiro alto se esquiva agachado; uno bajo (a ras de piso) se esquiva saltando —
 * de pie no esquiva ninguno, igual que en el arcade real. */
internal fun sobreviveDisparo(estado: EstadoJugador, tipo: TipoDisparo): Boolean = when (tipo) {
    TipoDisparo.ALTO -> estado == EstadoJugador.AGACHADO
    TipoDisparo.BAJO -> estado == EstadoJugador.SALTANDO
}

internal fun velocidadBanditoParaDistancia(distancia: Int): Float = (80f + distancia * 1.5f).coerceAtMost(220f)

internal fun intervaloSpawnParaDistancia(distancia: Int): Long = (1500L - distancia * 6L).coerceAtLeast(600L)

/**
 * Vaqueros del ocaso — copia de la modalidad Sunset Riders: correr y
 * disparar de lado. El mundo avanza solo (la distancia sube con el
 * tiempo), los bandidos entran caminando desde la derecha y disparan
 * tiros altos o bajos, y sobrevivir depende de agacharse o saltar según
 * el tiro que venga — no basta con disparar rápido.
 */
@Composable
fun VaquerosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("vaqueros")!!

    var agachado by remember { mutableStateOf(false) }
    var alturaSalto by remember { mutableStateOf(0f) }
    var velocidadSalto by remember { mutableStateOf(0f) }
    var bandidos by remember { mutableStateOf(listOf<Bandido>()) }
    var balas by remember { mutableStateOf(listOf<BalaVaqueros>()) }
    var siguienteId by remember { mutableStateOf(0) }
    var derrotados by remember { mutableStateOf(0) }
    var vidas by remember { mutableStateOf(VIDAS_INICIALES) }
    var invulnerableHasta by remember { mutableStateOf(0L) }
    var terminado by remember { mutableStateOf(false) }
    var gano by remember { mutableStateOf(false) }

    val estado = when {
        alturaSalto > 0f -> EstadoJugador.SALTANDO
        agachado -> EstadoJugador.AGACHADO
        else -> EstadoJugador.DE_PIE
    }

    fun reiniciar() {
        agachado = false
        alturaSalto = 0f
        velocidadSalto = 0f
        bandidos = emptyList()
        balas = emptyList()
        derrotados = 0
        vidas = VIDAS_INICIALES
        invulnerableHasta = 0L
        terminado = false
        gano = false
    }

    fun saltar() {
        if (!terminado && alturaSalto == 0f) {
            val (h, v) = iniciarSalto(IMPULSO_SALTO)
            alturaSalto = h
            velocidadSalto = v
            services.sound.tocar(Efecto.CLICK)
        }
    }

    fun disparar() {
        if (terminado) return
        balas = balas + BalaVaqueros(siguienteId++, JUGADOR_X + 12f, null, deEnemigo = false)
        services.sound.tocar(Efecto.CLICK)
    }

    LaunchedEffect(terminado) {
        if (terminado) return@LaunchedEffect
        var anterior = withFrameNanos { it }
        var proximoBandidoEn = 900L
        var tiempoTranscurridoMs = 0L
        var distancia = 0
        while (true) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.032f)
            anterior = ahora
            if (terminado) break

            tiempoTranscurridoMs += (dt * 1000).toLong()
            distancia = (tiempoTranscurridoMs / 100L).toInt()

            if (invulnerableHasta != 0L && ahora > invulnerableHasta) invulnerableHasta = 0L

            if (alturaSalto > 0f) {
                val (h, v) = avanzarSalto(alturaSalto, velocidadSalto, GRAVEDAD, dt)
                alturaSalto = h
                velocidadSalto = v
            }

            // Mientras el jefe esté vivo no entran bandidos comunes — la
            // ronda se concentra en él, igual que en el arcade original.
            if (tiempoTranscurridoMs >= proximoBandidoEn && bandidos.none { it.esJefe }) {
                proximoBandidoEn = tiempoTranscurridoMs + intervaloSpawnParaDistancia(distancia)
                val esJefe = esRondaDeJefe(derrotados)
                bandidos = bandidos + Bandido(
                    id = siguienteId++,
                    x = ANCHO + 20f,
                    velX = -velocidadBanditoParaDistancia(distancia) * (if (esJefe) 0.6f else 1f),
                    proximoDisparoEn = tiempoTranscurridoMs + 700L + Random.nextLong(600L),
                    vidas = vidasParaBandido(esJefe),
                    esJefe = esJefe,
                )
            }

            bandidos = bandidos.mapNotNull { b ->
                val nx = b.x + b.velX * dt
                if (nx < JUGADOR_X - 30f) return@mapNotNull null // se pasó sin que lo mataran
                var nuevo = b.copy(x = nx)
                if (nx in (JUGADOR_X + 10f)..(ANCHO) && tiempoTranscurridoMs >= b.proximoDisparoEn) {
                    val tipo = if (Random.nextBoolean()) TipoDisparo.ALTO else TipoDisparo.BAJO
                    balas = balas + BalaVaqueros(siguienteId++, nx, tipo, deEnemigo = true)
                    nuevo = nuevo.copy(proximoDisparoEn = tiempoTranscurridoMs + 900L + Random.nextLong(700L))
                }
                nuevo
            }

            val bandidosRestantes = bandidos.toMutableList()
            val balasRestantes = mutableListOf<BalaVaqueros>()
            for (bala in balas) {
                if (bala.deEnemigo) {
                    val nx = bala.x - VEL_BALA_ENEMIGA * dt
                    if (nx <= JUGADOR_X + 6f && nx >= JUGADOR_X - 6f) {
                        // OJO: se recalcula aquí adentro (no se reusa el `estado` de
                        // arriba) porque ese `val` queda fijo en el valor que tenía
                        // cuando arrancó esta corrutina — leer `agachado`/`alturaSalto`
                        // directo sí siempre da el valor real más reciente.
                        val estadoActual = when {
                            alturaSalto > 0f -> EstadoJugador.SALTANDO
                            agachado -> EstadoJugador.AGACHADO
                            else -> EstadoJugador.DE_PIE
                        }
                        if (invulnerableHasta == 0L && bala.tipo != null && !sobreviveDisparo(estadoActual, bala.tipo)) {
                            vidas -= 1
                            invulnerableHasta = ahora + INVULNERABILIDAD_NANOS
                            services.sound.tocar(Efecto.WRONG)
                        }
                        continue // la bala se consume al llegar al jugador, la esquive o no
                    }
                    if (nx > JUGADOR_X - 6f) balasRestantes.add(bala.copy(x = nx))
                } else {
                    val nx = bala.x + VEL_BALA_JUGADOR * dt
                    val golpeado = bandidosRestantes.firstOrNull { circuloChocaRect(nx, SUELO_Y - 15f, 4f, it.x - 10f, SUELO_Y - 30f, 20f, 30f) }
                    if (golpeado != null) {
                        val vidasRestantes = golpeado.vidas - 1
                        if (vidasRestantes <= 0) {
                            bandidosRestantes.remove(golpeado)
                            // Al jefe se le cuenta como el resto de bandidos
                            // que faltaban de una vez: derrotarlo cierra la
                            // ronda, no suma de a uno como un bandido más.
                            derrotados += if (golpeado.esJefe) (OBJETIVO_BANDIDOS - derrotados) else 1
                            services.sound.tocar(Efecto.CORRECT)
                        } else {
                            bandidosRestantes[bandidosRestantes.indexOf(golpeado)] = golpeado.copy(vidas = vidasRestantes)
                            services.sound.tocar(Efecto.CLICK)
                        }
                    } else if (nx < ANCHO) {
                        balasRestantes.add(bala.copy(x = nx))
                    }
                }
            }
            bandidos = bandidosRestantes
            balas = balasRestantes

            if (vidas <= 0) {
                terminado = true
                services.sound.tocar(Efecto.WRONG)
                break
            }
            if (derrotados >= OBJETIVO_BANDIDOS) {
                terminado = true
                gano = true
                services.sound.tocar(Efecto.WIN)
                scope.launch { services.progress.completarNivel(juego.id, 1) }
                break
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            terminado && gano -> "¡Pueblo a salvo! $OBJETIVO_BANDIDOS bandidos atrapados"
            terminado -> "Un bandido te derribó — bandidos atrapados: $derrotados"
            bandidos.any { it.esJefe } -> "¡El jefe de la banda! Resiste $GOLPES_JEFE disparos"
            else -> "Bandidos $derrotados/$OBJETIVO_BANDIDOS · Vidas $vidas"
        },
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Volver a intentar") } },
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            MarcadorArcade("Bandidos $derrotados/$OBJETIVO_BANDIDOS · Vidas $vidas")
            MarcoArcade(colorFondo = Color(0xFFF5D99B), modifier = Modifier.padding(top = 8.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = ANCHO.dp, height = ALTO.dp),
            ) {
                // Suelo
                Box(
                    modifier = Modifier
                        .offset(y = SUELO_Y.dp)
                        .size(width = ANCHO.dp, height = 6.dp)
                        .background(Color(0xFF70452D)),
                )
                bandidos.forEach { b ->
                    val tam = if (b.esJefe) 46f else 30f
                    Box(
                        modifier = Modifier.offset(x = (b.x - tam / 2f).dp, y = (SUELO_Y - tam).dp).size(width = tam.dp, height = tam.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text(if (b.esJefe) "🤠👑" else "🤠", fontSize = if (b.esJefe) 26.sp else 22.sp) }
                }
                balas.forEach { bala ->
                    val y = if (bala.deEnemigo) {
                        when (bala.tipo) { TipoDisparo.ALTO -> SUELO_Y - 26f; TipoDisparo.BAJO -> SUELO_Y - 4f; null -> SUELO_Y - 15f }
                    } else {
                        SUELO_Y - 15f
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = bala.x.dp, y = y.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (bala.deEnemigo) Color(0xFFD9433A) else Color(0xFF3F342C)),
                    )
                }
                // Jugador: agachado se ve más bajo y ancho, saltando sube del suelo.
                val altoJugador = if (estado == EstadoJugador.AGACHADO) 22f else 34f
                Box(
                    modifier = Modifier
                        .offset(x = (JUGADOR_X - 15f).dp, y = (SUELO_Y - altoJugador - alturaSalto).dp)
                        .size(width = 30.dp, height = altoJugador.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (invulnerableHasta != 0L) Color(0xFFE0925C) else Color(0xFF6FBF73)),
                    contentAlignment = Alignment.Center,
                ) { Text(if (estado == EstadoJugador.SALTANDO) "🤸" else "🤠", fontSize = 18.sp) }
            }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(top = 10.dp)) {
                BotonVaqueros("🦆", presionado = agachado) { agachado = it }
                BotonArcade("🤸") { saltar() }
                BotonArcade("🔫") { disparar() }
            }
        }
    }
}

/** El único botón que no es un `BotonArcade` normal: "agacharte" es un
 * interruptor con estado propio (se queda agachado hasta que se vuelve a
 * tocar), no un toque único — por eso muestra si está activo o no,
 * mismo tamaño y forma que el resto para no romper la homologación visual. */
@Composable
private fun BotonVaqueros(texto: String, presionado: Boolean, onCambio: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(if (presionado) Color(0xFF8A5A2B) else Color.White)
            .clickable { onCambio(!presionado) },
        contentAlignment = Alignment.Center,
    ) { Text(texto, fontSize = 18.sp) }
}
