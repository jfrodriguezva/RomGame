package com.miambiente.app.ui.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Una celda genérica de "toca este dibujo" con feedback real de presión
 * (escala + sombra) y un aro de acierto opcional — extraída de los
 * juegos de búsqueda visual (¿Qué falta?, ¿Qué es distinto?, Encuentra
 * los objetos) que antes eran una `Box` plana sin ninguna respuesta al
 * tocar, idéntica en los tres.
 */
@Composable
fun CasillaEmoji(
    emoji: String,
    modifier: Modifier = Modifier,
    tamanoFuente: TextUnit = 28.sp,
    acertado: Boolean = false,
    habilitado: Boolean = true,
    onClick: () -> Unit,
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    Box(
        modifier = modifier
            .scale(if (presionado) 0.92f else 1f)
            .shadow(if (acertado) 0.dp else 3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(if (acertado) Color(0xFFE9F0E4) else Color.White)
            .then(if (acertado) Modifier.border(2.dp, Color(0xFF4C7A3A), RoundedCornerShape(12.dp)) else Modifier)
            .clickable(interactionSource = interaccion, indication = null, enabled = habilitado) { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) { Text(emoji, fontSize = tamanoFuente) }
}
