package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.DefCanasta
import com.miambiente.app.ui.materials.ItemClasificar
import com.miambiente.app.ui.materials.MaterialClasificar

private val POOL = listOf(
    ItemClasificar(true, "e1", "entera"), ItemClasificar(true, "e2", "entera"),
    ItemClasificar(true, "e3", "entera"),
    ItemClasificar(false, "m1", "mitad"), ItemClasificar(false, "m2", "mitad"),
    ItemClasificar(false, "m3", "mitad"),
)

/** Mitades y enteros — patrón MaterialClasificar (primer contacto con la fracción). */
@Composable
fun MitadesScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("mitades")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("entera", "Entera", Color(0xFFFBE9E7)),
            DefCanasta("mitad", "Mitad", Color(0xFFEAF1F8)),
        ),
        render = { entera -> FiguraFraccion(entera) },
        consigna = "¿Está entera o a la mitad?",
        onVolver = onVolver,
    )
}

@Composable
private fun FiguraFraccion(entera: Boolean) {
    val descripcion = if (entera) "Figura entera, círculo completo" else "Figura a la mitad, medio círculo"
    Canvas(Modifier.size(44.dp).semantics { contentDescription = descripcion }) {
        if (entera) {
            drawArc(color = Color(0xFFE08A3A), startAngle = 0f, sweepAngle = 360f, useCenter = true)
        } else {
            drawArc(color = Color(0xFFE08A3A), startAngle = 0f, sweepAngle = 180f, useCenter = true)
        }
    }
}
