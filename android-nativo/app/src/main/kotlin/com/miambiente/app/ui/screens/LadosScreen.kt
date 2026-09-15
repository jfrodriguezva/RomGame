package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.DefCanasta
import com.miambiente.app.ui.materials.ItemClasificar
import com.miambiente.app.ui.materials.MaterialClasificar
import kotlin.math.cos
import kotlin.math.sin

private val POOL = listOf(
    ItemClasificar("triangulo", "t1", "3"), ItemClasificar("triangulo2", "t2", "3"),
    ItemClasificar("cuadrado", "c1", "4"), ItemClasificar("rectangulo", "c2", "4"),
    ItemClasificar("pentagono", "p1", "5"), ItemClasificar("hexagono", "h1", "6"),
)

/** ¿Cuántos lados tiene? — patrón MaterialClasificar (geometría). */
@Composable
fun LadosScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("lados")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("3", "3 lados", Color(0xFFFBE9E7)),
            DefCanasta("4", "4 lados", Color(0xFFEAF1F8)),
            DefCanasta("5", "5 lados", Color(0xFFF3ECF8)),
            DefCanasta("6", "6 lados", Color(0xFFE9F0E4)),
        ),
        render = { id -> FiguraLados(id) },
        consigna = "Arrastra cada figura según sus lados",
        onVolver = onVolver,
    )
}

@Composable
private fun FiguraLados(id: String) {
    val color = Color(0xFFA97FC7)
    when {
        id.startsWith("t") -> Canvas(Modifier.size(40.dp)) {
            drawPath(Path().apply { moveTo(size.width / 2, 0f); lineTo(size.width, size.height); lineTo(0f, size.height); close() }, color)
        }
        id.startsWith("c1") -> Box(Modifier.size(40.dp).clip(RoundedCornerShape(2.dp)).background(color))
        id.startsWith("c2") -> Box(Modifier.size(width = 48.dp, height = 30.dp).clip(RoundedCornerShape(2.dp)).background(color))
        else -> {
            val lados = if (id.startsWith("p")) 5 else 6
            Canvas(Modifier.size(40.dp)) {
                val radio = size.minDimension / 2
                val centro = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                val camino = Path()
                for (i in 0 until lados) {
                    val angulo = Math.toRadians((i * 360.0 / lados) - 90.0)
                    val punto = androidx.compose.ui.geometry.Offset(centro.x + (radio * cos(angulo)).toFloat(), centro.y + (radio * sin(angulo)).toFloat())
                    if (i == 0) camino.moveTo(punto.x, punto.y) else camino.lineTo(punto.x, punto.y)
                }
                camino.close()
                drawPath(camino, color)
            }
        }
    }
}
