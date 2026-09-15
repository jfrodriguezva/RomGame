package com.miambiente.app.ui.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

private data class Burbuja(val id: Int, var x: Float, var y: Float)

/** Burbujas — truena las burbujas antes de que suban (coordinación ojo-mano). */
@Composable
fun BurbujasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("burbujas")!!

    var burbujas by remember { mutableStateOf(listOf<Burbuja>()) }
    var siguienteId by remember { mutableStateOf(0) }
    var reventadas by remember { mutableStateOf(0) }
    val meta = 15

    LaunchedEffect(Unit) {
        while (reventadas < meta) {
            delay(500)
            burbujas = burbujas + Burbuja(siguienteId, (20..320).random().toFloat(), 620f)
            siguienteId++
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            burbujas = burbujas.map { it.copy(y = it.y - 6f) }.filter { it.y > -40f }
        }
    }

    fun reventar(id: Int) {
        services.sound.tocar(Efecto.CORRECT)
        burbujas = burbujas.filter { it.id != id }
        reventadas++
        if (reventadas == meta) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (reventadas >= meta) "¡Truena todas!" else "Truena las burbujas: $reventadas / $meta",
        onVolver = onVolver,
    ) {
        Box(Modifier.fillMaxSize().background(Color(0xFFE8F2F5))) {
            burbujas.forEach { b ->
                Box(
                    modifier = Modifier
                        .offset(x = b.x.dp, y = b.y.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB8E0EC).copy(alpha = 0.8f))
                        .pointerInput(b.id) { detectTapGestures { reventar(b.id) } },
                )
            }
        }
    }
}
