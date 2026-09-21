package com.miambiente.app.ui.materials

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * Ficha de tablero con posición animada — puerto compartido para Damas,
 * Damas chinas y Ajedrez.
 *
 * Bug real reportado ("al mover se pierde la silueta"): antes cada ficha
 * se dibujaba DENTRO de la celda de su posición actual en la cuadrícula
 * (`Row`/`Column`), así que al moverse la ficha desaparecía de la celda
 * vieja y una ficha "nueva" aparecía de golpe en la celda nueva — sin
 * ninguna animación, se perdía por completo el rastro visual de que era
 * la MISMA pieza moviéndose. Ahora las fichas se dibujan en una capa
 * aparte, superpuesta sobre la cuadrícula de fondo, cada una en su
 * posición absoluta animada (`animateDpAsState`) y con `key(id)` — un
 * identificador estable que NO cambia al moverse (a diferencia de
 * fila/col) — así Compose sabe que sigue siendo la misma ficha y anima
 * el desplazamiento en vez de recrearla de la nada en el lugar nuevo.
 */
@Composable
fun AnimatedPieza(id: Int, fila: Int, col: Int, tamanoCelda: Dp, contenido: @Composable () -> Unit) {
    key(id) {
        val x by animateDpAsState(tamanoCelda * col, label = "fichaX")
        val y by animateDpAsState(tamanoCelda * fila, label = "fichaY")
        Box(
            modifier = Modifier.offset(x = x, y = y).size(tamanoCelda),
        ) { contenido() }
    }
}
