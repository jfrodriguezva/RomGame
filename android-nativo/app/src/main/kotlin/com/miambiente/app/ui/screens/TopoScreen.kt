package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.MarcadorArcade
import com.miambiente.app.ui.materials.MarcoArcade
import kotlinx.coroutines.delay

internal const val HOYOS = 9
internal const val DURACION_RONDA_S = 30

/** Cuánto ha avanzado la ronda, en "unidades de dificultad" — tope en 20 para que la velocidad no siga subiendo pasado ese punto. */
internal fun avanceRondaTopo(tiempoRestante: Int): Int =
    (DURACION_RONDA_S - tiempoRestante).coerceAtMost(20)

/** Cuánto tiempo queda arriba el topo: baja con el avance, sin pasar de 400ms para que siga siendo alcanzable. */
internal fun duracionTopoArriba(avance: Int): Long =
    (900L - avance * 25L).coerceAtLeast(400L)

/** Pausa entre apariciones: baja con el avance, sin pasar de 180ms. */
internal fun pausaEntreTopos(avance: Int): Long =
    (500L - avance * 10L).coerceAtLeast(180L)

/** Elige el siguiente hoyo distinto al actual; `azar` (en [0,1)) es la fuente
 * de azar externa, mismo patrón que `deberiaCaerPowerUp` en Arkanoid — así
 * se puede probar sin depender de `Random` real. Se mapea multiplicando en
 * vez de con módulo sobre un índice ya generado: `indice.mod(candidatos.size)`
 * sesga un hoyo (el que corresponde al resto de dividir `hoyos` entre
 * `candidatos.size`) para que salga casi el doble de veces que los demás. */
internal fun siguienteHoyoTopo(actual: Int?, azar: Float, hoyos: Int = HOYOS): Int {
    val candidatos = (0 until hoyos).filter { it != actual }
    val indice = (azar * candidatos.size).toInt().coerceIn(0, candidatos.size - 1)
    return candidatos[indice]
}

internal fun ganoRondaTopo(puntaje: Int): Boolean = puntaje >= 10

/**
 * Atrapa al topo (whack-a-mole) — arcade clásico, generado de cero.
 * Un topo aparece en un hoyo al azar (nunca dos veces seguidas en el
 * mismo) por un tiempo corto; tocarlo a tiempo suma un punto. Ronda de
 * 30 segundos con cuenta regresiva real, y la velocidad de aparición
 * sube un poco conforme avanza la ronda — pura reacción y ritmo, sin
 * letras ni números de por medio.
 */
@Composable
fun TopoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("topo")!!

    var activo by remember { mutableStateOf<Int?>(null) }
    var golpeado by remember { mutableStateOf<Int?>(null) }
    var puntaje by remember { mutableStateOf(0) }
    var tiempoRestante by remember { mutableStateOf(DURACION_RONDA_S) }
    var jugando by remember { mutableStateOf(false) }
    var rondaId by remember { mutableStateOf(0) }

    fun iniciar() {
        puntaje = 0
        tiempoRestante = DURACION_RONDA_S
        activo = null
        jugando = true
        rondaId++
    }

    fun golpear(hoyo: Int) {
        if (!jugando || activo != hoyo) return
        services.sound.tocar(Efecto.CORRECT)
        puntaje++
        golpeado = hoyo
        activo = null
    }

    // Aparición del topo: se detiene y reinicia con cada ronda nueva
    // (`rondaId`) para no arrastrar temporizadores de una ronda anterior.
    LaunchedEffect(rondaId, jugando) {
        if (!jugando) return@LaunchedEffect
        while (jugando) {
            val avance = avanceRondaTopo(tiempoRestante)
            val duracionArriba = duracionTopoArriba(avance)
            val pausa = pausaEntreTopos(avance)
            delay(pausa)
            if (!jugando) break
            val siguiente = siguienteHoyoTopo(activo, kotlin.random.Random.nextFloat())
            activo = siguiente
            delay(duracionArriba)
            if (activo == siguiente) activo = null
        }
    }

    LaunchedEffect(rondaId, jugando) {
        if (!jugando) return@LaunchedEffect
        while (tiempoRestante > 0 && jugando) {
            delay(1000)
            tiempoRestante -= 1
        }
        if (jugando) {
            jugando = false
            activo = null
            services.sound.tocar(Efecto.WIN)
            if (ganoRondaTopo(puntaje)) services.progress.completarNivel(juego.id, 1)
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            jugando -> "Tiempo: ${tiempoRestante}s · Puntos: $puntaje"
            tiempoRestante == 0 -> "¡Tiempo! Puntaje final: $puntaje"
            else -> "Toca «Jugar» para empezar"
        },
        onVolver = onVolver,
        acciones = { Button(onClick = ::iniciar) { Text(if (jugando) "Reiniciar" else "Jugar") } },
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            MarcadorArcade("Puntaje: $puntaje")
            MarcoArcade(colorFondo = Color(0xFF8BBF6A), modifier = Modifier.padding(top = 8.dp)) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    for (fila in 0 until 3) {
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            for (col in 0 until 3) {
                                val hoyo = fila * 3 + col
                                val arriba = activo == hoyo
                                val escala by animateFloatAsState(if (arriba) 1f else 0f, label = "topo")
                                Box(
                                    modifier = Modifier.size(76.dp).clip(CircleShape).background(Color(0xFF6B4A34)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFF3E2A1E)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (escala > 0.05f) {
                                            Text(
                                                "🐹",
                                                fontSize = 34.sp,
                                                modifier = Modifier
                                                    .scale(escala)
                                                    .clickable(enabled = jugando) { golpear(hoyo) },
                                            )
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
}
