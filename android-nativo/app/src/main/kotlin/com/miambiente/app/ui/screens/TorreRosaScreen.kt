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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

private const val N_CUBOS = 5

/**
 * Torre rosa: arrastrar cada cubo a su lugar en la fila, del más grande al
 * más chico. Primer material nativo con arrastre real (pointerInput +
 * detectDragGestures) — en la versión web, MaterialOrdenar es por toques
 * (tomar del canasto, en orden); aquí el niño de verdad "toma" la pieza y
 * la suelta donde va, más fiel al material Montessori real.
 */
@Composable
fun TorreRosaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("torre-rosa")!!

    // 1 = cubo más grande. La ranura `i` (0-based) espera el cubo (i+1).
    var colocados by remember { mutableStateOf(setOf<Int>()) }
    var enCanasto by remember { mutableStateOf((1..N_CUBOS).shuffled()) }
    var nota by remember { mutableStateOf<String?>(null) }
    val ranuraRects = remember { mutableStateMapOf<Int, Rect>() }

    val completo = colocados.size == N_CUBOS

    fun soltarEn(cubo: Int, puntoRoot: Offset) {
        val ranuraObjetivo = ranuraRects.entries.find { (_, rect) -> rect.contains(puntoRoot) }?.key
        if (ranuraObjetivo == cubo - 1) {
            services.sound.tocar(Efecto.CORRECT)
            services.haptics.vibrar(Patron.ACIERTO)
            nota = "¡Ahí va!"
            colocados = colocados + cubo
            enCanasto = enCanasto - cubo
            if (colocados.size == N_CUBOS) {
                scope.launch { services.progress.completarNivel(juego.id, 1) }
            }
        } else if (ranuraObjetivo != null) {
            services.sound.tocar(Efecto.WRONG)
            services.haptics.vibrar(Patron.ERROR)
            nota = "Ahí no va. Mira el tamaño"
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Completaste la torre!" else "Arrastra del más grande al más chico",
        nota = nota,
        onVolver = onVolver,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Fila de ranuras: el tamaño de la ranura ya sugiere qué cubo va ahí.
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                for (i in 0 until N_CUBOS) {
                    val ladoRanura = (24 + (N_CUBOS - i) * 12).dp
                    Box(
                        modifier = Modifier
                            .size(ladoRanura)
                            .onGloballyPositioned { c ->
                                ranuraRects[i] = Rect(c.positionInRoot(), c.size.toSize())
                            }
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE9DFE8)),
                        contentAlignment = Alignment.Center,
                    ) {
                        val cuboAqui = i + 1
                        if (cuboAqui in colocados) {
                            CuboRosa(tamano = ladoRanura, numero = cuboAqui, arrastrable = false, onSoltar = {})
                        }
                    }
                }
            }

            Text("Canasto", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF8A5A2B))
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                enCanasto.sorted().forEach { cubo ->
                    val lado = (24 + (N_CUBOS - (cubo - 1)) * 10).dp
                    CuboRosa(
                        tamano = lado,
                        numero = cubo,
                        arrastrable = true,
                        onSoltar = { puntoRoot -> soltarEn(cubo, puntoRoot) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CuboRosa(
    tamano: Dp,
    numero: Int,
    arrastrable: Boolean,
    onSoltar: (Offset) -> Unit,
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
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE0669C))
            .then(
                if (arrastrable) {
                    Modifier.pointerInput(numero) {
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
                    }
                } else Modifier,
            ),
    )
}
