package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.dificultadReflejo
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Reflejo de color — material nuevo, exclusivo de la versión nativa: mide
 * el tiempo de reacción real en milisegundos con `System.nanoTime()`. Un
 * WebView no puede dar esta precisión de forma confiable (el bucle de
 * eventos de JavaScript y el render del navegador meten un retraso
 * variable que un `onClick` nativo no tiene).
 */
@Composable
fun ReflejoColorScreen(onVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("reflejo-color")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    // Cada nivel pide varias rondas por debajo de un tiempo que baja con el
    // nivel (`dificultadReflejo`). Antes cualquier toque bajo 600 ms daba
    // "el nivel 1", y nada cambiaba al seguir jugando.
    val d = remember(estado.nivel) { dificultadReflejo(estado.nivel) }

    var esperando by remember(estado.nivel) { mutableStateOf(true) }
    var listo by remember(estado.nivel) { mutableStateOf(false) }
    var inicioNs by remember(estado.nivel) { mutableStateOf(0L) }
    var ultimoMs by remember(estado.nivel) { mutableStateOf<Long?>(null) }
    var mejorMs by remember(estado.nivel) { mutableStateOf<Long?>(null) }
    var logradas by remember(estado.nivel) { mutableStateOf(0) }

    fun nuevaRonda() {
        esperando = true
        listo = false
        scope.launch {
            delay((d.esperaMinMs..d.esperaMaxMs).random())
            inicioNs = System.nanoTime()
            listo = true
            esperando = false
        }
    }

    LaunchedEffect(estado.nivel) { nuevaRonda() }

    fun tocar() {
        if (estado.logrado) return
        if (esperando) {
            estado.intento("¡Todavía no! Espera el verde")
            return
        }
        if (!listo) return
        val ms = (System.nanoTime() - inicioNs) / 1_000_000
        ultimoMs = ms
        if (mejorMs == null || ms < mejorMs!!) mejorMs = ms
        listo = false
        if (ms <= d.umbralMs) {
            logradas++
            estado.acierto("$ms ms")
            if (logradas >= d.rondas) { estado.completar(); return }
        } else {
            estado.intento("$ms ms: un poco más rápido")
        }
        scope.launch { delay(900); nuevaRonda() }
    }

    GameShell(
        juego = juego,
        consigna = when {
            estado.logrado -> "¡Qué reflejos!"
            esperando -> "Espera el color... ($logradas de ${d.rondas})"
            listo -> "¡Toca ahora!"
            else -> "Toca cuando cambie de color"
        },
        nota = estado.nota ?: ultimoMs?.let { "Tu tiempo: ${it} ms · Meta: ${d.umbralMs} ms" + (mejorMs?.let { m -> " · Mejor: ${m} ms" } ?: "") },
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(esperando, listo) { detectTapGestures { tocar() } },
            contentAlignment = Alignment.Center,
        ) {
            val escala by animateFloatAsState(if (listo) 1.12f else 1f, animationSpec = spring(dampingRatio = 0.4f), label = "reflejo")
            Box(
                Modifier
                    .size(160.dp)
                    .scale(escala)
                    .shadow(if (listo) 24.dp else 6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (listo) {
                                listOf(Color(0xFFA9D488), Color(0xFF8BBF6A), Color(0xFF6E9C57))
                            } else {
                                listOf(Color(0xFFE0685E), Color(0xFFD9433A), Color(0xFFB03329))
                            },
                        ),
                    ),
            )
        }
    }
}
