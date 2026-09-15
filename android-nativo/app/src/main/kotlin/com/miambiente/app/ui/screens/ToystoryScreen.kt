package com.miambiente.app.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    val meta = 10

    LaunchedEffect(aciertos) {
        if (aciertos < meta) {
            delay(if (aciertos == 0) 0 else 100)
            posicion = (20..300).random().toFloat() to (100..500).random().toFloat()
        }
    }

    fun tocar() {
        services.sound.tocar(Efecto.CORRECT)
        aciertos++
        posicion = (20..300).random().toFloat() to (100..500).random().toFloat()
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
            Box(
                Modifier
                    .offset(x = posicion.first.dp, y = posicion.second.dp)
                    .pointerInput(posicion) { detectTapGestures { tocar() } },
            ) { Text("🤠", fontSize = 48.sp) }
        }
    }
}
