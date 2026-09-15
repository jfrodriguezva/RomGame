package com.miambiente.app.ui.materials

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch

/**
 * Arrastre real con el dedo (pointerInput + detectDragGestures), extraído
 * de torre rosa y ¿vivo o no vivo? para que cualquier material nuevo que
 * necesite "tomar y soltar" lo reutilice en vez de copiar el mecanismo.
 * Es la mejora nativa insignia sobre la versión web (MaterialOrdenar y
 * MaterialClasificar son por toques allá, por arrastre real aquí).
 */
@Composable
fun ZonaSoltar(modifier: Modifier = Modifier, onPosicion: (Rect) -> Unit, contenido: @Composable () -> Unit) {
    Box(
        modifier = modifier.onGloballyPositioned { c ->
            onPosicion(Rect(c.positionInRoot(), c.size.toSize()))
        },
    ) { contenido() }
}

@Composable
fun PiezaArrastrable(
    tamano: Dp,
    clave: Any,
    onSoltar: (centroRoot: Offset) -> Unit,
    contenido: @Composable () -> Unit,
) {
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
            .pointerInput(clave) {
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
    ) { contenido() }
}
