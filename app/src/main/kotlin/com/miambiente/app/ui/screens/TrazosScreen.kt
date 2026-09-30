package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.miambiente.app.model.Punto
import com.miambiente.app.model.avanceTrazo
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.dificultadTrazo
import com.miambiente.app.model.guiaTrazo
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState

/**
 * Trazos previos — sigue la guía punteada con el dedo. La forma cambia con
 * el nivel (línea, zigzag, ola, arcos, espiral) y cada vez hay que pasar
 * más cerca de la guía (`dificultadTrazo`). El trazo cuenta solo si
 * recorre la guía en orden (`avanceTrazo`); antes bastaba con llegar al
 * lado derecho de la pantalla por cualquier camino.
 */
@Composable
fun TrazosScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("trazos")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val d = remember(estado.nivel) { dificultadTrazo(estado.nivel) }
    val guia = remember(estado.nivel) { guiaTrazo(d.forma) }

    var lienzo by remember { mutableStateOf(IntSize.Zero) }
    var trazo by remember(estado.nivel) { mutableStateOf(listOf<Offset>()) }
    var avance by remember(estado.nivel) { mutableStateOf(0) }

    // Coordenadas normalizadas sobre un cuadrado centrado: la guía mantiene
    // su forma en pantallas anchas o altas.
    fun lado() = minOf(lienzo.width, lienzo.height).toFloat().coerceAtLeast(1f)
    fun origen() = Offset((lienzo.width - lado()) / 2f, (lienzo.height - lado()) / 2f)
    fun aPantalla(p: Punto) = origen() + Offset(p.x * lado(), p.y * lado())
    fun aNormal(o: Offset) = (o - origen()).let { Punto(it.x / lado(), it.y / lado()) }

    fun evaluar() {
        if (estado.logrado) return
        avance = avanceTrazo(guia, trazo.map(::aNormal), d.tolerancia)
        if (avance == guia.size) estado.completar()
    }

    GameShell(
        juego = juego,
        consigna = if (estado.logrado) "¡Bien trazado!" else "Sigue ${d.forma.nombre} desde el punto verde",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else {
            { Button(onClick = { trazo = emptyList(); avance = 0 }) { Text("Borrar") } }
        },
    ) {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { lienzo = it }
                    .pointerInput(estado.nivel) {
                        detectDragGestures(
                            onDragStart = { inicio -> trazo = listOf(inicio); avance = 0 },
                            onDrag = { change, _ ->
                                change.consume()
                                trazo = trazo + change.position
                                evaluar()
                            },
                        )
                    },
            ) {
                val puntos = guia.map(::aPantalla)
                for (i in 0 until puntos.size - 1) {
                    drawLine(
                        color = if (i < avance - 1) Color(0xFF8BBF6A) else Color(0xFFA39A8C),
                        start = puntos[i],
                        end = puntos[i + 1],
                        strokeWidth = 6f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 14f)),
                    )
                }
                // Marca de inicio y meta.
                drawCircle(Color(0xFF8BBF6A), radius = 20f, center = puntos.first())
                drawCircle(Color(0xFFE0C23C), radius = 24f, center = puntos.last(), style = Stroke(width = 6f))
                for (i in 0 until trazo.size - 1) {
                    drawLine(Color(0xFF3E7AA3), trazo[i], trazo[i + 1], strokeWidth = 14f, cap = StrokeCap.Round)
                }
            }
        }
    }
}
