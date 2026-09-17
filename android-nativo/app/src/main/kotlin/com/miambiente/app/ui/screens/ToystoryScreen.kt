package com.miambiente.app.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Aventura de juguetes — salta y explora: toca cada reto antes de que se vaya. */
@Composable
fun ToystoryScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("toystory")!!

    var posicion by remember { mutableStateOf(160f to 260f) }
    var aciertos by remember { mutableStateOf(0) }
    var rondaId by remember { mutableIntStateOf(0) }
    val meta = 10

    LaunchedEffect(aciertos) {
        if (aciertos < meta) {
            delay(if (aciertos == 0) 0 else 100)
            posicion = (20..300).random().toFloat() to (100..500).random().toFloat()
            rondaId++
        }
    }

    fun tocar() {
        services.sound.tocar(Efecto.CORRECT)
        aciertos++
        posicion = (20..300).random().toFloat() to (100..500).random().toFloat()
        rondaId++
        if (aciertos == meta) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (aciertos >= meta) "¡Terminaste la aventura!" else "Toca a Woody: $aciertos / $meta",
        onVolver = onVolver,
    ) {
        Box(Modifier.fillMaxSize()) {
            // Aparece con un pequeño rebote en vez de aparecer de golpe en
            // el nuevo lugar, y una sombra ovalada debajo le da la
            // sensación de estar "parado" en el suelo, no flotando.
            val animatable = remember(rondaId) { androidx.compose.animation.core.Animatable(0f) }
            LaunchedEffect(rondaId) { animatable.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
            val escala = animatable.value
            Box(Modifier.offset(x = posicion.first.dp, y = (posicion.second + 44).dp).size(width = 44.dp, height = 12.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.12f)))
            Box(
                Modifier
                    .offset(x = posicion.first.dp, y = posicion.second.dp)
                    .scale(escala)
                    .pointerInput(posicion) { detectTapGestures { tocar() } },
            ) { Text("🤠", fontSize = 48.sp) }
        }
    }
}
