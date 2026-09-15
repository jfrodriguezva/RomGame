package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import kotlinx.coroutines.launch

private data class Estrella(val id: Int, val x: Float, var y: Float)

/** Atrapa las estrellas — mueve la canasta para anticipar la trayectoria. */
@Composable
fun CanastaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("canasta")!!

    var canastaX by remember { mutableStateOf(150f) }
    var estrellas by remember { mutableStateOf(listOf<Estrella>()) }
    var siguienteId by remember { mutableStateOf(0) }
    var atrapadas by remember { mutableStateOf(0) }
    val meta = 10

    LaunchedEffect(Unit) {
        while (atrapadas < meta) {
            kotlinx.coroutines.delay(700)
            estrellas = estrellas + Estrella(siguienteId, (0..300).random().toFloat(), 0f)
            siguienteId++
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(50)
            estrellas = estrellas.map { it.also { e -> e.y += 10f } }.filter { e ->
                if (e.y > 560f && e.y < 610f && kotlin.math.abs(e.x - canastaX) < 50f) {
                    services.sound.tocar(Efecto.CORRECT)
                    atrapadas++
                    if (atrapadas == meta) {
                        services.sound.tocar(Efecto.WIN)
                        scope.launch { services.progress.completarNivel(juego.id, 1) }
                    }
                    false
                } else e.y < 620f
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (atrapadas >= meta) "¡Las atrapaste todas!" else "Atrapa las estrellas: $atrapadas / $meta",
        onVolver = onVolver,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, _ -> change.consume(); canastaX = change.position.x.coerceIn(0f, 340f) }
                },
        ) {
            estrellas.forEach { e -> Box(Modifier.offset(x = e.x.dp, y = e.y.dp)) { Text("⭐", fontSize = 24.sp) } }
            Box(Modifier.offset(x = canastaX.dp, y = 560.dp)) { Text("🧺", fontSize = 40.sp) }
        }
    }
}
