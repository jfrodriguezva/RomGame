package com.miambiente.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

private data class Ser(val emoji: String, val vivo: Boolean)

private val POOL = listOf(
    Ser("🐶", true), Ser("🌳", true), Ser("🦋", true), Ser("🐟", true),
    Ser("🪨", false), Ser("☁️", false), Ser("🚗", false), Ser("⭐", false),
)

/**
 * ¿Vivo o no vivo? — clasificación en canastas, patrón MaterialClasificar,
 * también con arrastre real: se toma el objeto y se suelta en la canasta
 * correcta, en vez de tocar la canasta con el objeto pendiente fijo como
 * en la versión web.
 */
@Composable
fun SeresVivosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("seres-vivos")!!

    var pendientes by remember { mutableStateOf(POOL.shuffled().take(6)) }
    var nota by remember { mutableStateOf<String?>(null) }
    var acertados by remember { mutableStateOf(0) }
    val canastaRects = remember { mutableStateMapOf<Boolean, Rect>() }

    val completo = pendientes.isEmpty()

    fun soltar(ser: Ser, puntoRoot: Offset) {
        val canasta = canastaRects.entries.find { (_, rect) -> rect.contains(puntoRoot) }?.key ?: return
        if (canasta == ser.vivo) {
            services.sound.tocar(Efecto.CORRECT)
            services.haptics.vibrar(Patron.ACIERTO)
            nota = "¡Correcto!"
            pendientes = pendientes.filter { it != ser }
            acertados++
            if (pendientes.isEmpty()) {
                scope.launch { services.progress.completarNivel(juego.id, 1) }
            }
        } else {
            services.sound.tocar(Efecto.WRONG)
            services.haptics.vibrar(Patron.ERROR)
            nota = "Esa no va ahí. Mira otra vez"
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Clasificaste todo!" else "Arrastra cada uno a su canasta",
        nota = nota,
        onVolver = onVolver,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("Quedan: ${pendientes.size} · Acertados: $acertados", color = Color(0xFF2E5C4C))

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                pendientes.forEach { ser ->
                    ObjetoArrastrable(emoji = ser.emoji, onSoltar = { punto -> soltar(ser, punto) })
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Canasta(
                    titulo = "Vivo",
                    color = Color(0xFFEAF3EF),
                    modifier = Modifier.weight(1f).fillMaxSize()
                        .onGloballyPositioned { c -> canastaRects[true] = Rect(c.positionInRoot(), c.size.toSize()) },
                )
                Canasta(
                    titulo = "No vivo",
                    color = Color(0xFFF3ECF8),
                    modifier = Modifier.weight(1f).fillMaxSize()
                        .onGloballyPositioned { c -> canastaRects[false] = Rect(c.positionInRoot(), c.size.toSize()) },
                )
            }
        }
    }
}

@Composable
private fun Canasta(titulo: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(20.dp)).background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(titulo, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

@Composable
private fun ObjetoArrastrable(emoji: String, onSoltar: (Offset) -> Unit) {
    val tamano: Dp = 64.dp
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var origenRoot by remember { mutableStateOf(Offset.Zero) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val tamanoPx = with(density) { tamano.toPx() }

    Box(
        modifier = Modifier
            .size(tamano)
            .onGloballyPositioned { origenRoot = it.positionInRoot() }
            .graphicsLayer {
                translationX = offset.value.x
                translationY = offset.value.y
            }
            .pointerInput(emoji) {
                detectDragGestures(
                    onDragEnd = {
                        val centro = origenRoot + offset.value + Offset(tamanoPx / 2, tamanoPx / 2)
                        onSoltar(centro)
                        scope.launch { offset.snapTo(Offset.Zero) }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offset.snapTo(offset.value + dragAmount) }
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = 36.sp)
    }
}
