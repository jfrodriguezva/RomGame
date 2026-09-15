package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/**
 * Reflejo de color — material nuevo, exclusivo de la versión nativa: mide
 * el tiempo de reacción real en milisegundos con `System.nanoTime()`. Un
 * WebView no puede dar esta precisión de forma confiable (el bucle de
 * eventos de JavaScript y el render del navegador meten un retraso
 * variable que un `onClick` nativo no tiene).
 */
@Composable
fun ReflejoColorScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("reflejo-color")!!

    var esperando by remember { mutableStateOf(true) }
    var listo by remember { mutableStateOf(false) }
    var inicioNs by remember { mutableStateOf(0L) }
    var ultimoMs by remember { mutableStateOf<Long?>(null) }
    var mejorMs by remember { mutableStateOf<Long?>(null) }

    fun nuevaRonda() {
        esperando = true
        listo = false
        scope.launch {
            delay((1200..3000).random().toLong())
            inicioNs = System.nanoTime()
            listo = true
            esperando = false
        }
    }

    LaunchedEffect(Unit) { nuevaRonda() }

    fun tocar() {
        if (esperando) {
            services.sound.tocar(Efecto.WRONG)
            return
        }
        if (!listo) return
        val ms = (System.nanoTime() - inicioNs) / 1_000_000
        ultimoMs = ms
        if (mejorMs == null || ms < mejorMs!!) mejorMs = ms
        services.sound.tocar(Efecto.CORRECT)
        if (ms < 600) scope.launch { services.progress.completarNivel(juego.id, 1) }
        listo = false
        scope.launch { delay(900); nuevaRonda() }
    }

    GameShell(
        juego = juego,
        consigna = when {
            esperando -> "Espera el color..."
            listo -> "¡Toca ahora!"
            else -> "Toca cuando cambie de color"
        },
        nota = ultimoMs?.let { "Tu tiempo: ${it} ms" + (mejorMs?.let { m -> " · Mejor: ${m} ms" } ?: "") },
        onVolver = onVolver,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(esperando, listo) { detectTapGestures { tocar() } },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(if (listo) Color(0xFF8BBF6A) else Color(0xFFD9433A)),
            )
        }
    }
}
