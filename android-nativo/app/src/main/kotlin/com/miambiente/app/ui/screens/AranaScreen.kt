package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

private const val FILAS = 4
private const val COLUMNAS = 4
// 280dp en vez de un tamaño más grande: en un celular angosto (320-360dp
// de ancho) tiene que caber completo sin recortarse, con margen a los lados.
private val TAMANO_TABLERO = 280.dp

/**
 * La araña pintora — descubre la imagen escondida.
 *
 * Bug real reportado ("el juego de la araña bien hecho que descubre la
 * imagen"): antes cada una de las 16 casillas, al destaparse, mostraba el
 * MISMO emoji de araña por separado — no había ninguna "imagen" que
 * descubrir, solo 16 arañitas sueltas repetidas. Ahora hay una sola araña
 * grande dibujada de fondo, del tamaño de todo el tablero, y las 16
 * casillas son una cuadrícula que la TAPA; al tocarlas van desapareciendo
 * y dejan ver el pedazo de la araña grande que había debajo — recién al
 * destapar todas se ve la imagen completa y reconocible.
 */
@Composable
fun AranaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("arana")!!
    val total = FILAS * COLUMNAS

    var reveladas by remember { mutableStateOf(setOf<Int>()) }

    fun tocar(i: Int) {
        if (i in reveladas) return
        services.sound.tocar(Efecto.CLICK)
        reveladas = reveladas + i
        if (reveladas.size == total) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (reveladas.size == total) "¡Descubriste la imagen!" else "Toca cada casilla para descubrir la imagen",
        onVolver = onVolver,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(TAMANO_TABLERO)) {
                // La imagen completa de fondo: una sola araña grande, no
                // repetida, para que sí exista una imagen real detrás.
                Text(
                    "🕷️",
                    fontSize = 190.sp,
                    modifier = Modifier.align(Alignment.Center),
                )
                Column(modifier = Modifier.matchParentSize()) {
                    for (fila in 0 until FILAS) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0 until COLUMNAS) {
                                val i = fila * COLUMNAS + col
                                val revelada = i in reveladas
                                val alfa by animateFloatAsState(if (revelada) 0f else 1f, label = "tapaArana")
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(1.dp, Color.White.copy(alpha = 0.5f))
                                        .background(Color(0xFF7A4FA3).copy(alpha = alfa))
                                        .clickable(enabled = !revelada) { tocar(i) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
