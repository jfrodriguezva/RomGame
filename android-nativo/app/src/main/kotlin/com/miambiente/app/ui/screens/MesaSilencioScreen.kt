package com.miambiente.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay

/** El juego del silencio — respirar con un círculo que crece y se encoge, sin niveles ni puntaje. */
@Composable
fun MesaSilencioScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("mesa-silencio")!!
    val transicion = rememberInfiniteTransition(label = "respirar")
    val escala by transicion.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "escala",
    )
    var texto by remember { mutableStateOf("Inhala...") }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            texto = "Inhala..."
            services.haptics.vibrar(Patron.TOQUE)
            delay(4000)
            texto = "Exhala..."
            services.haptics.vibrar(Patron.TOQUE)
            delay(4000)
        }
    }

    GameShell(juego = juego, consigna = texto, onVolver = onVolver) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(200.dp)
                    .scale(escala)
                    .clip(CircleShape)
                    .background(Color(0xFF8BBF6A).copy(alpha = 0.5f)),
            )
        }
    }
}
