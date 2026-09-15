package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val OBJETOS = listOf("🍎", "🚗", "⚽", "📚", "🎈", "🔑", "🐱", "🌸")

/** ¿Qué falta? — material independiente (juego de Kim: memoria de trabajo). */
@Composable
fun QueFaltaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("que-falta")!!

    var bandeja by remember { mutableStateOf(OBJETOS.shuffled().take(4)) }
    var faltante by remember { mutableStateOf<String?>(null) }
    var mostrando by remember { mutableStateOf(true) }

    fun nuevaRonda() {
        bandeja = OBJETOS.shuffled().take(4)
        faltante = null
        mostrando = true
        scope.launch {
            delay(2200)
            faltante = bandeja.random()
            mostrando = false
        }
    }

    LaunchedEffect(Unit) { nuevaRonda() }

    fun elegir(opcion: String) {
        if (mostrando) return
        if (opcion == faltante) {
            services.sound.tocar(Efecto.CORRECT)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch { delay(1200); nuevaRonda() }
        } else {
            services.sound.tocar(Efecto.WRONG)
        }
    }

    GameShell(
        juego = juego,
        consigna = if (mostrando) "Memoriza la bandeja..." else "¿Qué desapareció?",
        onVolver = onVolver,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                (if (mostrando) bandeja else bandeja.filter { it != faltante }).forEach { emoji ->
                    Text(emoji, fontSize = 36.sp)
                }
            }
            if (!mostrando) {
                Row(modifier = Modifier.padding(top = 32.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    bandeja.shuffled().forEach { opcion ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .clickable { elegir(opcion) }
                                .padding(14.dp),
                        ) { Text(opcion, fontSize = 28.sp) }
                    }
                }
            }
        }
    }
}
