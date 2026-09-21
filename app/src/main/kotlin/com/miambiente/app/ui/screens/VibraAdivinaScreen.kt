package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Vibra y adivina — material nuevo, exclusivo de la versión nativa: el
 * teléfono vibra un número de pulsos (1 a 5) y hay que sentirlos y contar,
 * sin ver nada en pantalla hasta después. La versión web no puede hacer
 * esto — no tiene acceso a un motor de vibración real, solo a
 * `navigator.vibrate` (cuando existe) sin el control fino de patrones que
 * sí tiene `VibrationEffect` nativo.
 */
@Composable
fun VibraAdivinaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("vibra-adivina")!!

    var objetivo by remember { mutableStateOf((1..5).random()) }
    var vibrando by remember { mutableStateOf(false) }
    var listo by remember { mutableStateOf(false) }

    fun vibrarPulsos() {
        vibrando = true
        listo = false
        scope.launch {
            repeat(objetivo) {
                services.haptics.vibrar(com.miambiente.app.data.Patron.ACIERTO)
                delay(450)
            }
            vibrando = false
            listo = true
        }
    }

    fun elegir(n: Int) {
        if (!listo) return
        if (n == objetivo) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            objetivo = (1..5).random()
            listo = false
        } else {
            services.sound.tocar(Efecto.WRONG)
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            vibrando -> "Siente los pulsos..."
            listo -> "¿Cuántos pulsos sentiste?"
            else -> "Toca \"Vibrar\" y cuenta con los ojos cerrados"
        },
        onVolver = onVolver,
        acciones = { Button(onClick = ::vibrarPulsos, enabled = !vibrando) { Text("📳 Vibrar") } },
    ) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.padding(top = 48.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                (1..5).forEach { n ->
                    val interaccion = remember { MutableInteractionSource() }
                    val presionado by interaccion.collectIsPressedAsState()
                    androidx.compose.foundation.layout.Box(
                        Modifier
                            .size(56.dp)
                            .scale(if (presionado) 0.9f else 1f)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFFA97FC7))
                            .clickable(interactionSource = interaccion, indication = null) { elegir(n) },
                        contentAlignment = Alignment.Center,
                    ) { Text("$n", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
