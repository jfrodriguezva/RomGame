package com.miambiente.app.ui.materials

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch

/**
 * Arrastre real con el dedo (pointerInput + detectDragGestures), extraído
 * de torre rosa y ¿vivo o no vivo? para que cualquier material nuevo que
 * necesite "tomar y soltar" lo reutilice en vez de copiar el mecanismo.
 * Es la mejora nativa insignia sobre la versión web (MaterialOrdenar y
 * MaterialClasificar son por toques allá, por arrastre real aquí).
 *
 * `descripcion` es opcional para no romper las llamadas ya existentes,
 * pero cuando se da, TalkBack puede al menos anunciar QUÉ es cada pieza
 * u objetivo — el gesto de arrastrar en sí no tiene una alternativa
 * accesible todavía (eso necesitaría acciones de accesibilidad
 * personalizadas: "tomar" y luego "soltar aquí" con doble toque, no solo
 * una descripción). Documentado como limitación real, no resuelta del
 * todo, en el README.
 */
/**
 * `resaltado` es el segundo pedazo del feedback de arrastre real: antes
 * una zona de destino se veía exactamente igual estuvieras arrastrando
 * una pieza sobre ella o no — solo se sabía si encajaba DESPUÉS de
 * soltar. Ahora, mientras el dedo pasa por encima, se ve un aro de "acá
 * puedes soltar" en tiempo real, como el resto de las apps con
 * arrastre-y-suelta modernas.
 */
@Composable
fun ZonaSoltar(
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    resaltado: Boolean = false,
    formaResaltado: Shape = RoundedCornerShape(16.dp),
    onPosicion: (Rect) -> Unit,
    contenido: @Composable () -> Unit,
) {
    val alfa by animateFloatAsState(if (resaltado) 1f else 0f, label = "resaltadoZona")
    Box(
        modifier = modifier
            .onGloballyPositioned { c -> onPosicion(Rect(c.positionInRoot(), c.size.toSize())) }
            .then(if (descripcion != null) Modifier.semantics { contentDescription = descripcion } else Modifier),
    ) {
        contenido()
        if (alfa > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFF4C7A3A).copy(alpha = 0.16f * alfa), formaResaltado)
                    .border(3.dp, Color(0xFF4C7A3A).copy(alpha = alfa), formaResaltado),
            )
        }
    }
}

@Composable
fun PiezaArrastrable(
    tamano: Dp,
    clave: Any,
    descripcion: String? = null,
    onArrastrar: ((centroRoot: Offset?) -> Unit)? = null,
    onSoltar: (centroRoot: Offset) -> Unit,
    contenido: @Composable () -> Unit,
) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var origenRoot by remember { mutableStateOf(Offset.Zero) }
    var arrastrando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val tamanoPx = with(density) { tamano.toPx() }

    // Feedback táctil real al tomar la pieza: antes no había ninguna
    // diferencia visual entre "quieta" y "en la mano", y al soltar en un
    // sitio inválido volvía de golpe sin animación — ahora crece un poco
    // mientras se arrastra y vuelve con un rebote suave si no encaja.
    val escala by animateFloatAsState(if (arrastrando) 1.15f else 1f, label = "escalaArrastre")

    Box(
        modifier = Modifier
            .size(tamano)
            .then(if (descripcion != null) Modifier.semantics { contentDescription = descripcion } else Modifier)
            .onGloballyPositioned { origenRoot = it.positionInRoot() }
            .graphicsLayer {
                translationX = offset.value.x
                translationY = offset.value.y
                scaleX = escala
                scaleY = escala
                shadowElevation = if (arrastrando) 12f else 0f
            }
            .pointerInput(clave) {
                detectDragGestures(
                    onDragStart = { arrastrando = true },
                    onDragEnd = {
                        arrastrando = false
                        val centro = origenRoot + offset.value + Offset(tamanoPx / 2, tamanoPx / 2)
                        onSoltar(centro)
                        onArrastrar?.invoke(null)
                        scope.launch { offset.animateTo(Offset.Zero, spring(dampingRatio = 0.6f, stiffness = 300f)) }
                    },
                    onDragCancel = {
                        arrastrando = false
                        onArrastrar?.invoke(null)
                        scope.launch { offset.animateTo(Offset.Zero, spring(dampingRatio = 0.6f, stiffness = 300f)) }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val nuevoOffset = offset.value + dragAmount
                        scope.launch { offset.snapTo(nuevoOffset) }
                        onArrastrar?.invoke(origenRoot + nuevoOffset + Offset(tamanoPx / 2, tamanoPx / 2))
                    },
                )
            },
    ) { contenido() }
}
