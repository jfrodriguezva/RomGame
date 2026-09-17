package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val EMOJIS = listOf("🐶", "🐱", "🐰", "🦋", "🌸", "⭐")

private data class Carta(val id: Int, val emoji: String)

/**
 * Carta con volteo real en 3D (`rotationY`), no un cambio instantáneo de
 * color: antes se "revelaba" de golpe, sin ninguna sensación de dar vuelta
 * la carta — el gesto físico central de un juego de memoria.
 */
@Composable
private fun CartaMemorama(emoji: String, visible: Boolean, encontrada: Boolean, onClick: () -> Unit) {
    val angulo by animateFloatAsState(if (visible) 180f else 0f, label = "volteo")
    val densidad = LocalDensity.current.density
    val forma = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(if (encontrada) 0.dp else 3.dp, forma)
            .graphicsLayer {
                rotationY = angulo
                cameraDistance = 12f * densidad
            }
            .clip(forma)
            .background(if (angulo <= 90f) Color(0xFFA97FC7) else Color.White)
            .then(if (encontrada) Modifier.border(2.dp, Color(0xFF4C7A3A), forma) else Modifier)
            .clickable(enabled = !encontrada) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (angulo > 90f) {
            // La cara con el dibujo se dibuja "espejada" en Y para que, al
            // pasar los 90°, se vea derecha y no invertida.
            Text(emoji, fontSize = 28.sp, modifier = Modifier.padding(10.dp).graphicsLayer { rotationY = 180f })
        } else {
            Text("❓", fontSize = 24.sp, color = Color.White, modifier = Modifier.padding(10.dp))
        }
    }
}

/** Juego de memoria — material independiente (memoria visual y concentración). */
@Composable
fun MemoramaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("memorama")!!

    var cartas by remember { mutableStateOf((EMOJIS + EMOJIS).mapIndexed { i, e -> Carta(i, e) }.shuffled()) }
    var volteadas by remember { mutableStateOf(setOf<Int>()) }
    var encontradas by remember { mutableStateOf(setOf<Int>()) }
    var bloqueado by remember { mutableStateOf(false) }

    fun reiniciar() {
        cartas = (EMOJIS + EMOJIS).mapIndexed { i, e -> Carta(i, e) }.shuffled()
        volteadas = emptySet()
        encontradas = emptySet()
    }

    fun tocar(id: Int) {
        if (bloqueado || id in encontradas || id in volteadas) return
        val nuevas = volteadas + id
        volteadas = nuevas
        if (nuevas.size == 2) {
            bloqueado = true
            scope.launch {
                delay(700)
                val (a, b) = nuevas.toList()
                if (cartas.first { it.id == a }.emoji == cartas.first { it.id == b }.emoji) {
                    services.sound.tocar(Efecto.CORRECT)
                    encontradas = encontradas + a + b
                    if (encontradas.size == cartas.size) {
                        services.sound.tocar(Efecto.WIN)
                        scope.launch { services.progress.completarNivel(juego.id, 1) }
                    }
                } else {
                    services.sound.tocar(Efecto.WRONG)
                }
                volteadas = emptySet()
                bloqueado = false
            }
        }
    }

    LaunchedEffect(encontradas) {
        if (encontradas.size == cartas.size) {
            delay(1600)
            reiniciar()
        }
    }

    GameShell(
        juego = juego,
        consigna = if (encontradas.size == cartas.size) "¡Encontraste todas las parejas!" else "Encuentra las parejas",
        onVolver = onVolver,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
        ) {
            items(cartas, key = { it.id }) { carta ->
                val encontrada = carta.id in encontradas
                val visible = carta.id in volteadas || encontrada
                CartaMemorama(emoji = carta.emoji, visible = visible, encontrada = encontrada, onClick = { tocar(carta.id) })
            }
        }
    }
}
