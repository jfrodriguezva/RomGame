package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.PiezaArrastrable
import com.miambiente.app.ui.materials.ZonaSoltar
import com.miambiente.app.ui.materials.rememberMaterialState

private val PIEZAS = listOf("🌞" to 0, "☁️" to 1, "🌳" to 2, "🏠" to 3)

/** Rompecabezas — arrastra cada pieza a su lugar exacto (relación parte-todo). */
@Composable
fun RompecabezasScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("rompecabezas")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    var colocadas by remember(estado.nivel) { mutableStateOf(setOf<Int>()) }
    val piezasRevueltas = remember(estado.nivel) { PIEZAS.shuffled() }
    val ranuraRects = remember(estado.nivel) { mutableStateMapOf<Int, Rect>() }
    val completo = colocadas.size == PIEZAS.size

    fun soltar(posicionCorrecta: Int, puntoRoot: androidx.compose.ui.geometry.Offset) {
        val ranura = ranuraRects.entries.find { (_, rect) -> rect.contains(puntoRoot) }?.key
        if (ranura == posicionCorrecta) {
            estado.acierto("¡Ahí va!")
            colocadas = colocadas + posicionCorrecta
            if (colocadas.size == PIEZAS.size) estado.completar()
        } else if (ranura != null) {
            estado.intento()
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Armaste la imagen!" else "Arrastra cada pieza a su lugar",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        acciones = if (estado.logrado) {
            { com.miambiente.app.ui.materials.BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                for (fila in 0..1) {
                    Column {
                        for (col in 0..1) {
                            val posicion = fila * 2 + col
                            ZonaSoltar(
                                modifier = Modifier.size(70.dp),
                                onPosicion = { rect -> ranuraRects[posicion] = rect },
                            ) {
                                Box(
                                    Modifier.size(70.dp).clip(RoundedCornerShape(6.dp)).background(colores.fondo),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (posicion in colocadas) {
                                        Text(PIEZAS.first { it.second == posicion }.first, fontSize = 28.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.padding(top = 32.dp)) {
                piezasRevueltas.filter { it.second !in colocadas }.forEach { (emoji, posicion) ->
                    PiezaArrastrable(
                        tamano = 56.dp,
                        clave = posicion,
                        onSoltar = { punto -> soltar(posicion, punto) },
                    ) { Text(emoji, fontSize = 26.sp) }
                }
            }
        }
    }
}
