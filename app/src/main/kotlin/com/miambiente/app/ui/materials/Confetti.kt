package com.miambiente.app.ui.materials

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val PIEZAS = listOf("🎉", "⭐", "✨", "🎈", "🌟", "🎊")

@Composable
private fun Particula(semilla: Int) {
    val random = remember(semilla) { Random(semilla) }
    val xInicial = remember(semilla) { random.nextFloat() }
    val emoji = remember(semilla) { PIEZAS[random.nextInt(PIEZAS.size)] }
    val giroFinal = remember(semilla) { random.nextFloat() * 360f }
    val duracion = remember(semilla) { 900 + random.nextInt(500) }
    val progreso = remember(semilla) { Animatable(0f) }

    LaunchedEffect(semilla) {
        progreso.animateTo(1f, animationSpec = tween(duracion, easing = LinearEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = xInicial * size.width
                translationY = progreso.value * size.height
                rotationZ = giroFinal * progreso.value
            }
            .alpha((1f - progreso.value).coerceIn(0f, 1f)),
    ) { Text(emoji, fontSize = 26.sp) }
}

/**
 * Celebración al completar un nivel — equivalente nativo de
 * components/ConfettiOverlay.tsx. Cada partícula es su propia animación
 * independiente (Animatable), no una sola transición compartida, así se
 * ven caer en momentos ligeramente distintos como confeti real.
 */
@Composable
fun ConfettiOverlay(activo: Boolean) {
    if (!activo) return
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(18) { i -> Particula(semilla = i) }
    }
}
