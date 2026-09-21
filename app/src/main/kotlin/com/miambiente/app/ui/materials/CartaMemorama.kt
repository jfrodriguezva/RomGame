package com.miambiente.app.ui.materials

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Carta con volteo real en 3D (`rotationY`), no un cambio instantáneo de
 * color: antes se "revelaba" de golpe, sin ninguna sensación de dar vuelta
 * la carta — el gesto físico central de un juego de memoria. Compartida
 * por Memorama y Memoria por turnos (antes cada una tenía su propia
 * versión desactualizada de esta misma idea).
 */
@Composable
fun CartaMemorama(
    emoji: String,
    visible: Boolean,
    encontrada: Boolean,
    colorDorso: Color = Color(0xFFA97FC7),
    // Memorama ahora usa cartas más chicas para caber muchas más — el
    // texto fijo de antes (28sp/24sp) se veía desbordado en una carta de
    // ~44dp, así que el tamaño de letra también se puede achicar.
    tamanoEmoji: TextUnit = 28.sp,
    tamanoDorso: TextUnit = 24.sp,
    onClick: () -> Unit,
) {
    val angulo by animateFloatAsState(if (visible) 180f else 0f, label = "volteo")
    val densidad = LocalDensity.current.density
    val forma = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(if (encontrada) 0.dp else 3.dp, forma)
            .graphicsLayer {
                rotationY = angulo
                cameraDistance = 12f * densidad
            }
            .clip(forma)
            .background(if (angulo <= 90f) colorDorso else Color.White)
            .then(if (encontrada) Modifier.border(2.dp, Color(0xFF4C7A3A), forma) else Modifier)
            .clickable(enabled = !encontrada) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (angulo > 90f) {
            // La cara con el dibujo se dibuja "espejada" en Y para que, al
            // pasar los 90°, se vea derecha y no invertida.
            Text(emoji, fontSize = tamanoEmoji, modifier = Modifier.padding(4.dp).graphicsLayer { rotationY = 180f })
        } else {
            Text("❓", fontSize = tamanoDorso, color = Color.White, modifier = Modifier.padding(4.dp))
        }
    }
}
