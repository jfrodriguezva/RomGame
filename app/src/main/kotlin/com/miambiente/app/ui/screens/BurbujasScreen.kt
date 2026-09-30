package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.dificultadBurbujas
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState
import kotlinx.coroutines.delay
import kotlin.math.sin

private data class Burbuja(val id: Int, val xBase: Float, var x: Float, var y: Float, val velocidad: Float, val fase: Float)

/**
 * Burbujas — cada una sube a su propia velocidad y se mece de lado a
 * lado (seno del tiempo), integrado por cuadro real con `withFrameNanos`.
 * La meta, la frecuencia y la velocidad salen del nivel.
 */
@Composable
fun BurbujasScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("burbujas")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val d = remember(estado.nivel) { dificultadBurbujas(estado.nivel) }

    var burbujas by remember(estado.nivel) { mutableStateOf(listOf<Burbuja>()) }
    var siguienteId by remember(estado.nivel) { mutableStateOf(0) }
    var reventadas by remember(estado.nivel) { mutableStateOf(0) }

    LaunchedEffect(estado.nivel, reventadas >= d.meta) {
        while (reventadas < d.meta) {
            delay(d.aparicionMs)
            val xBase = (20..320).random().toFloat()
            burbujas = burbujas + Burbuja(siguienteId, xBase, xBase, 620f, (d.velocidadMin..d.velocidadMax).random().toFloat(), (0..628).random() / 100f)
            siguienteId++
        }
    }

    LaunchedEffect(estado.nivel) {
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
            }.filter { it.y > -60f }
        }
    }

    fun reventar(id: Int) {
        if (reventadas >= d.meta) return
        burbujas = burbujas.filter { it.id != id }
        reventadas++
        estado.acierto("¡Pop!")
        if (reventadas == d.meta) estado.completar()
    }

    GameShell(
        juego = juego,
        consigna = if (reventadas >= d.meta) "¡Las tronaste todas!" else "Truena las burbujas: $reventadas / ${d.meta}",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
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
                        // 52dp: por encima del mínimo de 48dp para un dedo chico.
                        .size(52.dp)
                        .clip(CircleShape)
                        // Degradado radial descentrado: da el brillo de una
                        // pompa de jabón real, no un círculo de color plano.
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.9f), Color(0xFFB8E0EC).copy(alpha = 0.55f), Color(0xFF8FC4D6).copy(alpha = 0.45f)),
                                center = Offset(32f, 28f),
                                radius = 60f,
                            ),
                        )
                        .pointerInput(b.id) { detectTapGestures { reventar(b.id) } },
                    contentAlignment = Alignment.TopStart,
                ) {
                    Box(
                        Modifier
                            .offset(x = 10.dp, y = 8.dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f)),
                    )
                }
            }
        }
    }
}
