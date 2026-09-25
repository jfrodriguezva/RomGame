package com.miambiente.app.ui.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Marco único para los 11 arcade del catálogo (Snake, Tetris, Arkanoid,
 * Pang, Topo, Mosaico, Vaqueros, Comepuntos, Nieve, Escuadrón estelar,
 * Gran premio): antes cada uno armaba su propio `Box` con un tamaño
 * distinto (de 200 a 420dp de alto) — se pidió homologar para que se
 * sientan una sola familia, no pantallas sueltas construidas por separado.
 */
private val ANCHO_ARCADE = 300.dp
private val ALTO_ARCADE = 420.dp

@Composable
fun MarcoArcade(colorFondo: Color, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .size(width = ANCHO_ARCADE, height = ALTO_ARCADE)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(colorFondo),
        content = content,
    )
}

/** Botón de un solo toque — mismo tamaño y estilo en los 11 arcade. */
@Composable
fun BotonArcade(texto: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(texto, fontSize = 18.sp) }
}

/**
 * Botón de "mantener presionado": mientras el dedo sigue encima, llama a
 * `onMientrasPresionado` una vez por cuadro con el `dt` real transcurrido
 * — reemplaza el arrastre (`detectDragGestures`) que usaban antes Arkanoid,
 * Pang y Nieve para mover la paleta/al jugador, con el mismo criterio ya
 * probado en Snake/Comepuntos de "un botón siempre se interpreta igual",
 * pero sin perder el movimiento continuo que esos tres sí necesitan.
 */
@Composable
fun BotonMantenerArcade(texto: String, onMientrasPresionado: (dt: Float) -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()

    LaunchedEffect(presionado) {
        if (!presionado) return@LaunchedEffect
        var anterior = withFrameNanos { it }
        while (true) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.05f)
            anterior = ahora
            onMientrasPresionado(dt)
        }
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(if (presionado) Color(0xFFE0C23C) else Color.White)
            .clickable(interactionSource = interaccion, indication = null, onClick = {}),
        contentAlignment = Alignment.Center,
    ) { Text(texto, fontSize = 22.sp) }
}

/** Cruz de 4 flechas — misma disposición ya usada en Snake: arriba sola,
 * izquierda/derecha en la misma fila, abajo sola. */
@Composable
fun PadDireccional(onArriba: () -> Unit, onAbajo: () -> Unit, onIzquierda: () -> Unit, onDerecha: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BotonArcade("⬆️", onArriba)
        Row(horizontalArrangement = Arrangement.spacedBy(52.dp)) {
            BotonArcade("⬅️", onIzquierda)
            BotonArcade("➡️", onDerecha)
        }
        BotonArcade("⬇️", onAbajo)
    }
}

/** Línea de marcador (nivel/puntaje/vidas) con el mismo tamaño y peso de
 * letra en los 11 arcade, en vez de que cada uno elija el suyo. */
@Composable
fun MarcadorArcade(texto: String) {
    Text(texto, fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
}
