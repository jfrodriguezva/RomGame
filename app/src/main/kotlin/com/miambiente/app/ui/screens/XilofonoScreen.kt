package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

private data class Barra(val nombre: String, val color: Color)

private val BARRAS = listOf(
    Barra("Do", Color(0xFFD9433A)),
    Barra("Re", Color(0xFFE08A3A)),
    Barra("Mi", Color(0xFFE0C23C)),
    Barra("Sol", Color(0xFF4C7A3A)),
    Barra("La", Color(0xFF3E7AA3)),
    Barra("Do", Color(0xFF7A4FA3)),
    Barra("Re", Color(0xFFE0669C)),
    Barra("Mi", Color(0xFFA97FC7)),
)

/**
 * Xilófono — cada barra suena una nota real (sintetizada en `SoundPlayer`,
 * escala pentatónica, nunca desafina) en vez del beep DTMF anterior. El
 * visual imita un xilófono real: barras cada vez más cortas de izquierda a
 * derecha (a menor longitud, tono más agudo — la misma física de un
 * instrumento real), con un círculo resonador y feedback al presionar.
 */
@Composable
fun XilofonoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("xilofono")!!

    GameShell(juego = juego, consigna = "Toca y escucha, sin reglas", onVolver = onVolver) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            BARRAS.forEachIndexed { i, barra ->
                // Cada barra siguiente es un poco más corta: más corta =
                // más aguda, la misma relación física que un xilófono real.
                val altoFraccion = 1f - i * 0.07f
                BarraXilofono(
                    barra = barra,
                    altoFraccion = altoFraccion,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onTocar = {
                        services.sound.tocarNota(i)
                        services.haptics.vibrar(Patron.TOQUE)
                    },
                )
            }
        }
    }
}

@Composable
private fun BarraXilofono(barra: Barra, altoFraccion: Float, modifier: Modifier, onTocar: () -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.35f),
        label = "tecla",
    )

    Box(modifier = modifier.fillMaxHeight(), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(altoFraccion.coerceIn(0.4f, 1f))
                .shadow(if (presionado) 1.dp else 6.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(if (presionado) barra.color.copy(alpha = 0.75f) else barra.color)
                .clickable(interactionSource = interaccion, indication = null) { onTocar() },
            contentAlignment = Alignment.Center,
        ) {
            // Los dos agujeros de resonancia de una barra real de xilófono,
            // no solo un rectángulo de color plano.
            Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)) {
                val radio = size.width * 0.14f
                drawCircle(Color.White.copy(alpha = 0.35f), radius = radio, center = Offset(size.width * 0.3f, size.height * 0.2f), style = Stroke(width = 3f))
                drawCircle(Color.White.copy(alpha = 0.35f), radius = radio, center = Offset(size.width * 0.7f, size.height * 0.2f), style = Stroke(width = 3f))
            }
            Text(
                barra.nombre,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }
    }
}
