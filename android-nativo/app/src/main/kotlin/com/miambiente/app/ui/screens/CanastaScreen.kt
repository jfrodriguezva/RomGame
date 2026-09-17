package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

private data class Estrella(val id: Int, val x: Float, var y: Float, val velocidad: Float)

private const val GRAVEDAD = 260f // dp/s²

/**
 * Atrapa las estrellas — caída con gravedad real y velocidad inicial
 * distinta por estrella (más variedad que caer todas a la misma
 * velocidad constante), integrada por cuadro con `withFrameNanos`.
 */
@Composable
fun CanastaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("canasta")!!

    var canastaX by remember { mutableStateOf(150f) }
    var estrellas by remember { mutableStateOf(listOf<Estrella>()) }
    var siguienteId by remember { mutableStateOf(0) }
    var atrapadas by remember { mutableStateOf(0) }
    val meta = 10

    LaunchedEffect(atrapadas) {
        while (atrapadas < meta) {
            kotlinx.coroutines.delay(700)
            estrellas = estrellas + Estrella(siguienteId, (0..300).random().toFloat(), 0f, (40..90).random().toFloat())
            siguienteId++
        }
    }

    LaunchedEffect(Unit) {
        var anterior = withFrameNanos { it }
        while (true) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.05f)
            anterior = ahora
            estrellas = estrellas.map { it.also { e -> e.y += (e.velocidad + GRAVEDAD * dt) * dt } }.filter { e ->
                if (e.y > 550f && e.y < 610f && kotlin.math.abs(e.x - canastaX) < 50f) {
                    services.sound.tocar(Efecto.CORRECT)
                    atrapadas++
                    if (atrapadas == meta) {
                        services.sound.tocar(Efecto.WIN)
                        scope.launch { services.progress.completarNivel(juego.id, 1) }
                    }
                    false
                } else e.y < 620f
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (atrapadas >= meta) "¡Las atrapaste todas!" else "Atrapa las estrellas: $atrapadas / $meta",
        onVolver = onVolver,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFE4F0FA), Color(0xFFEFF8EA))))
                .pointerInput(Unit) {
                    detectDragGestures { change, _ -> change.consume(); canastaX = change.position.x.coerceIn(0f, 340f) }
                },
        ) {
            estrellas.forEach { e -> Box(Modifier.offset(x = e.x.dp, y = e.y.dp)) { Text("⭐", fontSize = 24.sp) } }
            Box(Modifier.offset(x = canastaX.dp, y = 560.dp)) { Text("🧺", fontSize = 40.sp) }
        }
    }
}
