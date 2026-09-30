package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.Resultado
import com.miambiente.app.model.SerieOrdenar
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.escenaRompecabezas
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.PiezaArrastrable
import com.miambiente.app.ui.materials.ZonaSoltar
import com.miambiente.app.ui.materials.rememberMaterialState

/**
 * Rompecabezas — arrastra cada pieza a su lugar exacto (relación parte-todo).
 * Crece con el nivel (2×2, 3×3, 4×4) y muestra arriba el modelo terminado:
 * antes era siempre la misma cuadrícula de 4 y sin modelo, así que no había
 * forma de saber dónde iba cada pieza más que probando.
 */
@Composable
fun RompecabezasScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("rompecabezas")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    val escena = remember(estado.nivel) { escenaRompecabezas(estado.nivel) }
    val lado = escena.lado
    val total = lado * lado
    // Pieza k (1..total) va en la casilla k: misma regla que la seriación.
    var armado by remember(estado.nivel) { mutableStateOf(SerieOrdenar.nueva(total)) }
    val ranuraRects = remember(estado.nivel) { mutableStateMapOf<Int, Rect>() }
    var rectArrastre by remember(estado.nivel) { mutableStateOf<Rect?>(null) }

    fun colocar(pieza: Int, ranura: Int?) {
        if (ranura == null) return
        val (nuevo, resultado) = armado.tocarPieza(pieza).tocarLugar(ranura)
        armado = nuevo
        when (resultado) {
            Resultado.ACIERTO -> estado.acierto("¡Ahí va!")
            Resultado.COMPLETO -> { estado.acierto("¡Ahí va!"); estado.completar() }
            Resultado.ERROR -> estado.intento()
            else -> Unit
        }
    }

    fun soltar(pieza: Int, rectPieza: Rect) {
        colocar(pieza, ranuraRects.entries.find { (_, rect) -> rect.overlaps(rectPieza) }?.key)
    }

    GameShell(
        juego = juego,
        consigna = if (armado.completa) "¡Armaste la imagen!" else "Arrastra cada pieza a su lugar, como en el modelo",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { com.miambiente.app.ui.materials.BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Modelo: la imagen terminada, en chico.
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colores.fondo.copy(alpha = 0.5f))
                    .padding(4.dp)
                    .semantics { contentDescription = "Modelo del rompecabezas terminado" },
            ) {
                for (fila in 0 until lado) {
                    Row { for (col in 0 until lado) Box(Modifier.size(26.dp), contentAlignment = Alignment.Center) { Text(escena.piezas[fila * lado + col], fontSize = 16.sp) } }
                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .shadow(4.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(colores.fondo.copy(alpha = 0.3f))
                    .padding(2.dp),
            ) {
                for (fila in 0 until lado) {
                    Row {
                        for (col in 0 until lado) {
                            val casilla = fila * lado + col + 1
                            val llena = casilla in armado.colocadas
                            val rect = ranuraRects[casilla]
                            ZonaSoltar(
                                modifier = Modifier.size(64.dp),
                                resaltado = rectArrastre != null && rect?.overlaps(rectArrastre!!) == true,
                                formaResaltado = RoundedCornerShape(6.dp),
                                onPosicion = { r -> ranuraRects[casilla] = r },
                            ) {
                                Box(
                                    Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colores.fondo)
                                        .then(if (llena) Modifier else Modifier.border(2.dp, colores.acento.copy(alpha = 0.3f), RoundedCornerShape(6.dp))),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (llena) Text(escena.piezas[casilla - 1], fontSize = 28.sp)
                                }
                            }
                        }
                    }
                }
            }
            // key(pieza): mismo bug de "acomodar" encontrado en
            // MaterialOrdenar/MaterialClasificar — sin él, al colocar una
            // pieza el resto se recorría un lugar y heredaba el arrastre a
            // medias de la pieza anterior en esa posición.
            Row(
                modifier = Modifier.padding(top = 20.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                armado.enCanasto.forEach { pieza ->
                    key(pieza) {
                        PiezaArrastrable(
                            tamano = 56.dp,
                            clave = pieza,
                            onArrastrar = { rect -> rectArrastre = rect },
                            onSoltar = { rect -> soltar(pieza, rect) },
                            accionesAccesibles = (1..total).filterNot { it in armado.colocadas }.map { ranura ->
                                CustomAccessibilityAction("Poner en la casilla $ranura") { colocar(pieza, ranura); true }
                            },
                        ) { Text(escena.piezas[pieza - 1], fontSize = 26.sp) }
                    }
                }
            }
        }
    }
}
