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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val ANCHO = 300f
private const val ALTO = 420f
private const val SUELO_Y = ALTO - 60f
private const val RADIO_ENEMIGO = 16f
private const val VIDAS_INICIALES = 3
private const val INVULNERABILIDAD_NANOS = 900_000_000L
internal const val GOLPES_PARA_ATRAPAR = 3
internal const val VEL_RODADA = 230f
private const val VEL_PROYECTIL = 240f
private const val ALCANCE_PATADA = 30f
private const val VELOCIDAD_JUGADOR_BOTON = 200f

internal data class EnemigoNieve(
    val id: Int, val x: Float, val velX: Float,
    val golpes: Int = 0, val atrapado: Boolean = false, val rodando: Boolean = false,
)
internal data class ProyectilNieve(val id: Int, val x: Float, val velX: Float)

/** Al tercer golpe de nieve, el enemigo queda atrapado (quieto, ya no
 * patrulla) — recién ahí se lo puede patear. */
internal fun registrarGolpe(enemigo: EnemigoNieve): EnemigoNieve {
    val golpes = (enemigo.golpes + 1).coerceAtMost(GOLPES_PARA_ATRAPAR)
    val atrapado = golpes >= GOLPES_PARA_ATRAPAR
    return enemigo.copy(golpes = golpes, atrapado = atrapado, velX = if (atrapado) 0f else enemigo.velX)
}

/** Patear solo funciona sobre un enemigo ya atrapado y quieto — lo manda a
 * rodar en la dirección de la patada hasta chocar con otro o con la pared. */
internal fun patearEnemigo(enemigo: EnemigoNieve, direccion: Int): EnemigoNieve =
    if (enemigo.atrapado && !enemigo.rodando) enemigo.copy(rodando = true, velX = VEL_RODADA * direccion) else enemigo

/**
 * Rescate de nieve — copia de la modalidad Snow Bros: la nieve no mata al
 * toque, hace falta pegarle 3 veces a un enemigo para atraparlo dentro de
 * una bola quieta, y solo entonces se la puede patear para que ruede y
 * arrase con cualquier otro enemigo en su camino (matanza en cadena real,
 * no uno por uno). Simplificación declarada: una sola plataforma a nivel
 * de piso (sin saltar entre niveles), para concentrar el esfuerzo en el
 * ciclo real de atrapar-y-patear, que es lo que distingue a este arcade.
 */
@Composable
fun NieveScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("nieve")!!

    fun enemigosIniciales() = listOf(
        EnemigoNieve(0, ANCHO * 0.25f, 55f),
        EnemigoNieve(1, ANCHO * 0.5f, -65f),
        EnemigoNieve(2, ANCHO * 0.75f, 60f),
    )

    var jugadorX by remember { mutableStateOf(ANCHO / 2f) }
    var mirando by remember { mutableStateOf(1) }
    var enemigos by remember { mutableStateOf(enemigosIniciales()) }
    var proyectiles by remember { mutableStateOf(listOf<ProyectilNieve>()) }
    var siguienteId by remember { mutableStateOf(10) }
    var puntaje by remember { mutableStateOf(0) }
    var vidas by remember { mutableStateOf(VIDAS_INICIALES) }
    var invulnerableHasta by remember { mutableStateOf(0L) }
    var terminado by remember { mutableStateOf(false) }
    var gano by remember { mutableStateOf(false) }

    fun reiniciar() {
        jugadorX = ANCHO / 2f
        mirando = 1
        enemigos = enemigosIniciales()
        proyectiles = emptyList()
        puntaje = 0
        vidas = VIDAS_INICIALES
        invulnerableHasta = 0L
        terminado = false
        gano = false
    }

    fun disparar() {
        if (terminado) return
        proyectiles = proyectiles + ProyectilNieve(siguienteId++, jugadorX + mirando * 16f, VEL_PROYECTIL * mirando)
        services.sound.tocar(Efecto.CLICK)
    }

    LaunchedEffect(terminado) {
        if (terminado) return@LaunchedEffect
        var anterior = withFrameNanos { it }
        while (true) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.032f)
            anterior = ahora
            if (terminado) break

            if (invulnerableHasta != 0L && ahora > invulnerableHasta) invulnerableHasta = 0L

            // Patrulla, o rueda si ya la patearon; atrapada-y-quieta no se mueve.
            enemigos = enemigos.map { e ->
                when {
                    e.rodando -> e.copy(x = e.x + e.velX * dt)
                    e.atrapado -> e
                    else -> {
                        var x = e.x + e.velX * dt
                        var vx = e.velX
                        if (x < RADIO_ENEMIGO) { x = RADIO_ENEMIGO; vx = abs(vx) }
                        if (x > ANCHO - RADIO_ENEMIGO) { x = ANCHO - RADIO_ENEMIGO; vx = -abs(vx) }
                        e.copy(x = x, velX = vx)
                    }
                }
            }

            // Una bola rodando que llega a la pared se destruye sola (el
            // enemigo de adentro no escapa gratis solo porque ya no hay
            // nadie más a quien chocar).
            val salieronPorElBorde = enemigos.filter { it.rodando && (it.x <= RADIO_ENEMIGO || it.x >= ANCHO - RADIO_ENEMIGO) }
            if (salieronPorElBorde.isNotEmpty()) {
                puntaje += salieronPorElBorde.size * 20
                enemigos = enemigos - salieronPorElBorde.toSet()
                services.sound.tocar(Efecto.WIN)
            }

            // Matanza en cadena: cada bola rodando destruye a cualquier
            // enemigo (atrapado o no) que todavía no esté rodando.
            val rodando = enemigos.filter { it.rodando }
            val impactados = enemigos.filter { candidato ->
                !candidato.rodando && rodando.any { bola -> abs(bola.x - candidato.x) < RADIO_ENEMIGO * 1.6f }
            }
            if (impactados.isNotEmpty()) {
                puntaje += impactados.size * 15
                enemigos = enemigos - impactados.toSet()
                services.sound.tocar(Efecto.CORRECT)
            }

            // El jugador patea con solo acercarse a un enemigo ya atrapado y quieto.
            enemigos = enemigos.map { e ->
                if (e.atrapado && !e.rodando && abs(e.x - jugadorX) < RADIO_ENEMIGO + ALCANCE_PATADA) {
                    services.sound.tocar(Efecto.CLICK)
                    patearEnemigo(e, mirando)
                } else e
            }

            // Solo un enemigo todavía patrullando (sin atrapar) lastima al
            // jugador — uno ya atrapado (quieto o rodando) es inofensivo
            // para quien lo pateó, igual que la bola es "tuya" en el
            // arcade real una vez que la mandaste a rodar.
            val peligroso = enemigos.firstOrNull { !it.atrapado && abs(it.x - jugadorX) < RADIO_ENEMIGO + 12f }
            if (peligroso != null && invulnerableHasta == 0L) {
                vidas -= 1
                invulnerableHasta = ahora + INVULNERABILIDAD_NANOS
                services.sound.tocar(Efecto.WRONG)
            }

            proyectiles = proyectiles.mapNotNull { p ->
                val nx = p.x + p.velX * dt
                if (nx < 0f || nx > ANCHO) return@mapNotNull null
                val golpeado = enemigos.firstOrNull { !it.atrapado && abs(it.x - nx) < RADIO_ENEMIGO }
                if (golpeado != null) {
                    enemigos = enemigos.map { if (it.id == golpeado.id) registrarGolpe(it) else it }
                    services.sound.tocar(Efecto.CORRECT)
                    return@mapNotNull null
                }
                p.copy(x = nx)
            }

            if (vidas <= 0) {
                terminado = true
                services.sound.tocar(Efecto.WRONG)
                break
            }
            if (enemigos.isEmpty()) {
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
            terminado && gano -> "¡Montaña rescatada! Puntaje $puntaje"
            terminado -> "Te alcanzaron — puntaje $puntaje"
            else -> "Enemigos: ${enemigos.size} · Vidas $vidas"
        },
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Otra ronda") } },
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            MarcadorArcade("Enemigos: ${enemigos.size} · Vidas $vidas")
            MarcoArcade(colorFondo = Color(0xFFDDF4FF), modifier = Modifier.padding(top = 8.dp)) {
                Box(modifier = Modifier.align(Alignment.Center).size(width = ANCHO.dp, height = ALTO.dp)) {
                    Box(modifier = Modifier.offset(y = SUELO_Y.dp).size(width = ANCHO.dp, height = 4.dp).background(Color(0xFFB8E6FF)))
                    enemigos.forEach { e ->
                        val emoji = when { e.rodando -> "⚪"; e.atrapado -> "⛄"; else -> "👾" }
                        Box(
                            modifier = Modifier
                                .offset(x = (e.x - RADIO_ENEMIGO).dp, y = (SUELO_Y - RADIO_ENEMIGO * 2).dp)
                                .size((RADIO_ENEMIGO * 2).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center,
                        ) { Text(emoji, fontSize = 20.sp) }
                    }
                    proyectiles.forEach { p ->
                        Box(
                            modifier = Modifier
                                .offset(x = (p.x - 5f).dp, y = (SUELO_Y - RADIO_ENEMIGO).dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = (jugadorX - 15f).dp, y = (SUELO_Y - 30f).dp)
                            .size(width = 30.dp, height = 30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (invulnerableHasta != 0L) Color(0xFFE0925C) else Color(0xFF6FBF73)),
                        contentAlignment = Alignment.Center,
                    ) { Text(if (mirando > 0) "🙂" else "🙃", fontSize = 18.sp) }
                }
            }
            Text(
                "Acércate a un ⛄ para patearlo",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                BotonMantenerArcade("⬅️") { dt ->
                    mirando = -1
                    jugadorX = (jugadorX - VELOCIDAD_JUGADOR_BOTON * dt).coerceIn(RADIO_ENEMIGO, ANCHO - RADIO_ENEMIGO)
                }
                BotonArcade("❄️") { disparar() }
                BotonMantenerArcade("➡️") { dt ->
                    mirando = 1
                    jugadorX = (jugadorX + VELOCIDAD_JUGADOR_BOTON * dt).coerceIn(RADIO_ENEMIGO, ANCHO - RADIO_ENEMIGO)
                }
            }
        }
    }
}
