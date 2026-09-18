package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    var rectArrastre by remember(estado.nivel) { mutableStateOf<Rect?>(null) }
    val completo = colocadas.size == PIEZAS.size

    // Mismo bug de fondo del arrastre que en MaterialOrdenar: solapamiento
    // de rectángulos en vez de exigir el punto central exacto.
    fun soltar(posicionCorrecta: Int, rectPieza: Rect) {
        val ranura = ranuraRects.entries.find { (_, rect) -> rect.overlaps(rectPieza) }?.key
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
            Row(
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(10.dp)).clip(RoundedCornerShape(10.dp)).background(colores.fondo.copy(alpha = 0.3f)).padding(2.dp),
            ) {
                for (fila in 0..1) {
                    Column {
                        for (col in 0..1) {
                            val posicion = fila * 2 + col
                            val llena = posicion in colocadas
                            val rect = ranuraRects[posicion]
                            ZonaSoltar(
                                modifier = Modifier.size(70.dp),
                                resaltado = rectArrastre != null && rect?.overlaps(rectArrastre!!) == true,
                                formaResaltado = RoundedCornerShape(6.dp),
                                onPosicion = { r -> ranuraRects[posicion] = r },
                            ) {
                                Box(
                                    Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colores.fondo)
                                        .then(if (llena) Modifier else Modifier.border(2.dp, colores.acento.copy(alpha = 0.3f), RoundedCornerShape(6.dp))),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (llena) Text(PIEZAS.first { it.second == posicion }.first, fontSize = 28.sp)
                                }
                            }
                        }
                    }
                }
            }
            // key(posicion): mismo bug de "acomodar" encontrado en
            // MaterialOrdenar/MaterialClasificar — sin él, al colocar una
            // pieza el resto se recorría un lugar y heredaba el arrastre a
            // medias de la pieza anterior en esa posición.
            Row(modifier = Modifier.padding(top = 32.dp)) {
                piezasRevueltas.filter { it.second !in colocadas }.forEach { (emoji, posicion) ->
                    key(posicion) {
                        PiezaArrastrable(
                            tamano = 56.dp,
                            clave = posicion,
                            onArrastrar = { rect -> rectArrastre = rect },
                            onSoltar = { rect -> soltar(posicion, rect) },
                        ) { Text(emoji, fontSize = 26.sp) }
                    }
                }
            }
        }
    }
}
