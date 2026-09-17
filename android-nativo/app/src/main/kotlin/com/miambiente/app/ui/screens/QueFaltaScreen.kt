package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.CasillaEmoji
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val OBJETOS = listOf("🍎", "🚗", "⚽", "📚", "🎈", "🔑", "🐱", "🌸")
private const val MS_MEMORIZAR = 2200

/**
 * ¿Qué falta? — juego de Kim (memoria de trabajo). La bandeja ahora se ve
 * como una bandeja real (tarjeta con sombra) y el tiempo para memorizar
 * se ve correr en una barra, no solo se siente por el texto "Memoriza...".
 */
@Composable
fun QueFaltaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("que-falta")!!

    var bandeja by remember { mutableStateOf(OBJETOS.shuffled().take(4)) }
    var faltante by remember { mutableStateOf<String?>(null) }
    var mostrando by remember { mutableStateOf(true) }
    var progresoTiempo by remember { mutableFloatStateOf(1f) }

    fun nuevaRonda() {
        bandeja = OBJETOS.shuffled().take(4)
        faltante = null
        mostrando = true
        progresoTiempo = 1f
        scope.launch {
            delay(MS_MEMORIZAR.toLong())
            faltante = bandeja.random()
            mostrando = false
        }
    }

    LaunchedEffect(Unit) { nuevaRonda() }

    LaunchedEffect(mostrando) {
        if (!mostrando) return@LaunchedEffect
        val inicio = withFrameNanos { it }
        while (mostrando) {
            val ahora = withFrameNanos { it }
            val transcurridoMs = (ahora - inicio) / 1_000_000
            progresoTiempo = (1f - transcurridoMs.toFloat() / MS_MEMORIZAR).coerceIn(0f, 1f)
        }
    }

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
            Column(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF3E8D0))
                    .padding(20.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    (if (mostrando) bandeja else bandeja.filter { it != faltante }).forEach { emoji ->
                        Text(emoji, fontSize = 36.sp)
                    }
                }
                if (mostrando) {
                    LinearProgressIndicator(
                        progress = { progresoTiempo },
                        color = Color(0xFFE0A93C),
                        trackColor = Color(0xFFE0A93C).copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    )
                }
            }
            if (!mostrando) {
                Row(modifier = Modifier.padding(top = 32.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    bandeja.shuffled().forEach { opcion ->
                        CasillaEmoji(emoji = opcion, tamanoFuente = 28.sp, onClick = { elegir(opcion) })
                    }
                }
            }
        }
    }
}
