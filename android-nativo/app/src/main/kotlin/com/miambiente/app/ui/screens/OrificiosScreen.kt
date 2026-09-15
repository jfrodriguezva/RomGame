package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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

private val POOL = listOf(
    ItemClasificar("circulo", "c1", "circulo"), ItemClasificar("circulo", "c2", "circulo"),
    ItemClasificar("cuadrado", "q1", "cuadrado"), ItemClasificar("cuadrado", "q2", "cuadrado"),
    ItemClasificar("triangulo", "t1", "triangulo"), ItemClasificar("triangulo", "t2", "triangulo"),
)

/** Encaja la figura — patrón MaterialClasificar (cada figura en su agujero exacto). */
@Composable
fun OrificiosScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("orificios")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("circulo", "◯", Color(0xFFEAF1F8)),
            DefCanasta("cuadrado", "▢", Color(0xFFF3ECF8)),
            DefCanasta("triangulo", "△", Color(0xFFFBE9E7)),
        ),
        render = { id -> FiguraOrificio(id) },
        consigna = "Arrastra cada figura a su agujero exacto",
        onVolver = onVolver,
    )
}

@Composable
private fun FiguraOrificio(id: String) {
    val color = Color(0xFFE0669C)
    when (id) {
        "circulo" -> Box(Modifier.size(40.dp).clip(CircleShape).background(color))
        "cuadrado" -> Box(Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)).background(color))
        else -> Canvas(Modifier.size(40.dp)) {
            drawPath(
                Path().apply { moveTo(size.width / 2, 0f); lineTo(size.width, size.height); lineTo(0f, size.height); close() },
                color,
            )
        }
    }
}
