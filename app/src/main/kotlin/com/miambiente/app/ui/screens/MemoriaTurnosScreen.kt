package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.CartaMemorama
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val EMOJIS = listOf("🐶", "🐱", "🐰", "🦋", "🌸", "⭐")

private data class CartaTurno(val id: Int, val emoji: String)

/** Memoria por turnos — material independiente (esperar el turno, memoria, aceptar el resultado). */
@Composable
fun MemoriaTurnosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("memoria-turnos")!!

    var cartas by remember { mutableStateOf((EMOJIS + EMOJIS).mapIndexed { i, e -> CartaTurno(i, e) }.shuffled()) }
    var volteadas by remember { mutableStateOf(setOf<Int>()) }
    var encontradas by remember { mutableStateOf(setOf<Int>()) }
    var turno by remember { mutableStateOf("jugador") }
    var puntosJugador by remember { mutableStateOf(0) }
    var puntosCpu by remember { mutableStateOf(0) }
    var bloqueado by remember { mutableStateOf(false) }

    fun reiniciar() {
        cartas = (EMOJIS + EMOJIS).mapIndexed { i, e -> CartaTurno(i, e) }.shuffled()
        volteadas = emptySet(); encontradas = emptySet()
        turno = "jugador"; puntosJugador = 0; puntosCpu = 0; bloqueado = false
    }

    fun jugar(a: Int, b: Int) {
        bloqueado = true
        scope.launch {
            volteadas = setOf(a, b)
            delay(700)
            if (cartas[a].emoji == cartas[b].emoji) {
                services.sound.tocar(Efecto.CORRECT)
                encontradas = encontradas + a + b
                if (turno == "jugador") puntosJugador++ else puntosCpu++
                if (encontradas.size == cartas.size) {
                    services.sound.tocar(Efecto.WIN)
                    if (puntosJugador >= puntosCpu) scope.launch { services.progress.completarNivel(juego.id, 1) }
                }
            } else {
                services.sound.tocar(Efecto.WRONG)
                turno = if (turno == "jugador") "cpu" else "jugador"
            }
            volteadas = emptySet()
            bloqueado = false
        }
    }

    fun tocarJugador(id: Int) {
        if (bloqueado || turno != "jugador" || id in encontradas || id in volteadas) return
        val previa = volteadas
        if (previa.isEmpty()) volteadas = setOf(id) else jugar(previa.first(), id)
    }

    LaunchedEffect(turno, bloqueado, encontradas.size) {
        if (turno == "cpu" && !bloqueado && encontradas.size < cartas.size) {
            delay(700)
            val disponibles = cartas.map { it.id }.filter { it !in encontradas }
            val a = disponibles.random()
            val b = disponibles.filter { it != a }.random()
            jugar(a, b)
        }
    }

    // Antes se quedaba en el mensaje final para siempre — los demás
    // juegos de turnos (Gato, Cuatro en línea) sí se reinician solos.
    LaunchedEffect(encontradas.size) {
        if (encontradas.size == cartas.size) {
            delay(2200)
            reiniciar()
        }
    }

    GameShell(
        juego = juego,
        consigna = if (encontradas.size == cartas.size) {
            if (puntosJugador >= puntosCpu) "¡Ganaste $puntosJugador a $puntosCpu!" else "Ganó la computadora $puntosCpu a $puntosJugador"
        } else if (turno == "jugador") "Tu turno" else "Turno de la computadora",
        onVolver = onVolver,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp)) {
            // Marcador con el turno resaltado: antes solo se sabía de
            // quién era el turno leyendo el texto de la consigna.
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
            ) {
                MarcadorJugador("Tú", puntosJugador, activo = turno == "jugador", color = Color(0xFF3E7AA3))
                Text(" · ", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                MarcadorJugador("Computadora", puntosCpu, activo = turno == "cpu", color = Color(0xFFA23B3B))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(cartas, key = { it.id }) { carta ->
                    val encontrada = carta.id in encontradas
                    val visible = carta.id in volteadas || encontrada
                    CartaMemorama(
                        emoji = carta.emoji,
                        visible = visible,
                        encontrada = encontrada,
                        colorDorso = Color(0xFFE0B586),
                        onClick = { tocarJugador(carta.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MarcadorJugador(nombre: String, puntos: Int, activo: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (activo) color.copy(alpha = 0.18f) else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text("$nombre: $puntos", color = color, fontWeight = if (activo) FontWeight.ExtraBold else FontWeight.Normal, fontSize = 13.sp)
    }
}
