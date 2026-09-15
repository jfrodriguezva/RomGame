package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class Obstaculo(val id: Int, val x: Float, var y: Float)

/** Carreras — esquiva y llega a la meta (atención sostenida y respuesta veloz). */
@Composable
fun CarrerasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("carreras")!!

    var carroX by remember { mutableStateOf(160f) }
    var obstaculos by remember { mutableStateOf(listOf<Obstaculo>()) }
    var siguienteId by remember { mutableStateOf(0) }
    var segundos by remember { mutableStateOf(0) }
    var chocado by remember { mutableStateOf(false) }
    val meta = 15

    fun reiniciar() {
        carroX = 160f
        obstaculos = emptyList()
        segundos = 0
        chocado = false
    }

    LaunchedEffect(chocado) {
        while (!chocado && segundos < meta) {
            delay(1000)
            segundos++
        }
        if (segundos >= meta) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    LaunchedEffect(chocado) {
        while (!chocado) {
            delay(800)
            obstaculos = obstaculos + Obstaculo(siguienteId, (20..300).random().toFloat(), 0f)
            siguienteId++
        }
    }

    LaunchedEffect(chocado) {
        while (!chocado) {
            delay(50)
            obstaculos = obstaculos.map { it.also { o -> o.y += 14f } }
            val choque = obstaculos.any { it.y in 480f..560f && kotlin.math.abs(it.x - carroX) < 40f }
            if (choque) {
                services.sound.tocar(Efecto.WRONG)
                services.haptics.vibrar(Patron.ERROR)
                chocado = true
            }
            obstaculos = obstaculos.filter { it.y < 620f }
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            chocado -> "¡Chocaste! Toca para reintentar"
            segundos >= meta -> "¡Llegaste a la meta!"
            else -> "Esquiva los obstáculos: $segundos / $meta s"
        },
        onVolver = onVolver,
        acciones = if (chocado || segundos >= meta) {
            { Button(onClick = ::reiniciar) { Text("Reintentar") } }
        } else null,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F2F5))
                .pointerInput(chocado) {
                    detectDragGestures { change, _ -> change.consume(); carroX = change.position.x.coerceIn(0f, 320f) }
                },
        ) {
            obstaculos.forEach { o -> Box(Modifier.offset(x = o.x.dp, y = o.y.dp)) { Text("🚧", fontSize = 24.sp) } }
            Box(Modifier.offset(x = carroX.dp, y = 520.dp)) { Text("🏎️", fontSize = 36.sp) }
        }
    }
}
