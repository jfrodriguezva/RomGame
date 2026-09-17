package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val RETOS = listOf(
    "Salta 3 veces", "Da 2 vueltas", "Toca tus pies", "Aplaude 5 veces",
    "Camina de puntitas", "Haz como un avión",
)
private val CARAS = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

/**
 * Dado de retos — antes el dado era un emoji fijo que nunca se movía, ni
 * siquiera al "tirarlo". Ahora gira de verdad (varias vueltas completas,
 * con desaceleración) y muestra caras de dado reales mientras rueda.
 */
@Composable
fun DadoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("dado")!!
    var reto by remember { mutableStateOf("Tira el dado para empezar") }
    var cara by remember { mutableStateOf(CARAS.first()) }
    var angulo by remember { mutableFloatStateOf(0f) }
    var girando by remember { mutableStateOf(false) }
    val anguloAnimado by animateFloatAsState(angulo, animationSpec = tween(700), label = "dado")

    fun tirar() {
        if (girando) return
        girando = true
        services.sound.tocar(Efecto.CLICK)
        angulo += 360f * (2..3).random() + (0..300).random()
        scope.launch {
            repeat(6) { cara = CARAS.random(); delay(90) }
            delay(200)
            reto = RETOS.random()
            girando = false
        }
    }

    GameShell(
        juego = juego,
        consigna = reto,
        onVolver = onVolver,
        acciones = {
            Button(onClick = ::tirar) { Text("Tirar dado 🎲") }
        },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(cara, fontSize = 110.sp, modifier = Modifier.graphicsLayer { rotationZ = anguloAnimado })
        }
    }
}
