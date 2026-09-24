package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private data class Burbuja(val id: Int, val xBase: Float, var x: Float, var y: Float, val velocidad: Float, val fase: Float)

/**
 * Burbujas — cada una sube a su propia velocidad y se mece de lado a
 * lado (seno del tiempo), integrado por cuadro real con `withFrameNanos`
 * en vez de subir todas parejo en pasos fijos.
 */
@Composable
fun BurbujasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("burbujas")!!

    var burbujas by remember { mutableStateOf(listOf<Burbuja>()) }
    var siguienteId by remember { mutableStateOf(0) }
    var reventadas by remember { mutableStateOf(0) }
    val meta = 15

    LaunchedEffect(reventadas) {
        while (reventadas < meta) {
            delay(500)
            val xBase = (20..320).random().toFloat()
            burbujas = burbujas + Burbuja(siguienteId, xBase, xBase, 620f, (60..140).random().toFloat(), (0..628).random() / 100f)
            siguienteId++
        }
    }

    LaunchedEffect(Unit) {
        var anterior = withFrameNanos { it }
        var tiempo = 0f
        while (true) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.05f)
            anterior = ahora
            tiempo += dt
            burbujas = burbujas.map {
                it.also { b ->
                    b.y -= b.velocidad * dt
                    b.x = b.xBase + sin(tiempo * 2f + b.fase) * 18f
                }
            }.filter { it.y > -40f }
        }
    }

    fun reventar(id: Int) {
        services.sound.tocar(Efecto.CORRECT)
        burbujas = burbujas.filter { it.id != id }
        reventadas++
        if (reventadas == meta) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (reventadas >= meta) "¡Truena todas!" else "Truena las burbujas: $reventadas / $meta",
        onVolver = onVolver,
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFFDCF0F5), Color(0xFFEFF8FA))),
            ),
        ) {
            burbujas.forEach { b ->
                Box(
                    modifier = Modifier
                        .offset(x = b.x.dp, y = b.y.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        // Degradado radial descentrado: da el brillo de una
                        // pompa de jabón real, no un círculo de color plano.
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.9f), Color(0xFFB8E0EC).copy(alpha = 0.55f), Color(0xFF8FC4D6).copy(alpha = 0.45f)),
                                center = Offset(25f, 22f),
                                radius = 46f,
                            ),
                        )
                        .pointerInput(b.id) { detectTapGestures { reventar(b.id) } },
                    contentAlignment = Alignment.TopStart,
                ) {
                    Box(
                        Modifier
                            .offset(x = 8.dp, y = 6.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f)),
                    )
                }
            }
        }
    }
}
