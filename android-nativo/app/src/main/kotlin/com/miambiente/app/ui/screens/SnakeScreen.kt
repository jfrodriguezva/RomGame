package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

internal const val SNAKE_COLS = 13
internal const val SNAKE_FILAS = 13
private val CELDA_SNAKE = 22.dp

internal data class DireccionSnake(val dr: Int, val dc: Int)

internal val ARRIBA = DireccionSnake(-1, 0)
internal val ABAJO = DireccionSnake(1, 0)
internal val IZQUIERDA = DireccionSnake(0, -1)
internal val DERECHA = DireccionSnake(0, 1)

/** ¿`nueva` es exactamente lo opuesto de `actual`? Girar 180° en un solo tic no es válido — la víbora se comería a sí misma de inmediato. */
internal fun esOpuesta(actual: DireccionSnake, nueva: DireccionSnake) =
    actual.dr == -nueva.dr && actual.dc == -nueva.dc

/** Un paso de la víbora: nueva cabeza en la dirección dada; si no comió, se recorta la cola. */
internal fun avanzarSnake(vibora: List<Pair<Int, Int>>, dir: DireccionSnake, comio: Boolean): List<Pair<Int, Int>> {
    val (hr, hc) = vibora.first()
    val nuevaCabeza = (hr + dir.dr) to (hc + dir.dc)
    val nueva = listOf(nuevaCabeza) + vibora
    return if (comio) nueva else nueva.dropLast(1)
}

internal fun chocaConMuro(cabeza: Pair<Int, Int>) =
    cabeza.first !in 0 until SNAKE_FILAS || cabeza.second !in 0 until SNAKE_COLS

internal fun chocaConsigoMisma(vibora: List<Pair<Int, Int>>) =
    vibora.first() in vibora.drop(1)

/**
 * Snake (la víbora) — arcade clásico, generado de cero para esta app.
 * Cuadrícula real con controles de dirección en pantalla (no swipe: en
 * una app táctil para niños, un swipe ambiguo se puede leer como scroll
 * o como intento de dirección — cuatro botones de flecha siempre se
 * interpretan igual). No se puede girar 180° en un solo tic, la
 * velocidad sube con la comida, y choca con el muro o consigo misma.
 */
@Composable
fun SnakeScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("snake")!!

    fun viboraInicial() = listOf(6 to 6, 6 to 5, 6 to 4)

    fun comidaAleatoria(vibora: List<Pair<Int, Int>>): Pair<Int, Int> {
        while (true) {
            val c = (0 until SNAKE_FILAS).random() to (0 until SNAKE_COLS).random()
            if (c !in vibora) return c
        }
    }

    var vibora by remember { mutableStateOf(viboraInicial()) }
    var direccion by remember { mutableStateOf(DERECHA) }
    var siguienteDireccion by remember { mutableStateOf(DERECHA) }
    var comida by remember { mutableStateOf(comidaAleatoria(viboraInicial())) }
    var puntaje by remember { mutableStateOf(0) }
    var terminado by remember { mutableStateOf(false) }

    fun reiniciar() {
        val inicial = viboraInicial()
        vibora = inicial
        direccion = DERECHA
        siguienteDireccion = DERECHA
        comida = comidaAleatoria(inicial)
        puntaje = 0
        terminado = false
    }

    fun girar(nueva: DireccionSnake) {
        if (terminado || esOpuesta(direccion, nueva)) return
        siguienteDireccion = nueva
    }

    LaunchedEffect(terminado) {
        if (terminado) return@LaunchedEffect
        while (true) {
            val velocidad = (220L - puntaje.coerceAtMost(15) * 8L).coerceAtLeast(100L)
            delay(velocidad)
            if (terminado) break
            direccion = siguienteDireccion
            val cabezaNueva = (vibora.first().first + direccion.dr) to (vibora.first().second + direccion.dc)
            if (chocaConMuro(cabezaNueva)) {
                terminado = true
                services.sound.tocar(Efecto.WRONG)
                break
            }
            val comio = cabezaNueva == comida
            val nuevaVibora = avanzarSnake(vibora, direccion, comio)
            if (chocaConsigoMisma(nuevaVibora)) {
                terminado = true
                services.sound.tocar(Efecto.WRONG)
                break
            }
            vibora = nuevaVibora
            if (comio) {
                services.sound.tocar(Efecto.CORRECT)
                puntaje++
                comida = comidaAleatoria(nuevaVibora)
            }
        }
    }

    var estrellaDada by remember { mutableStateOf(0) }
    LaunchedEffect(puntaje) {
        if (puntaje > 0 && puntaje % 5 == 0 && puntaje / 5 > estrellaDada) {
            estrellaDada = puntaje / 5
            services.progress.completarNivel(juego.id, 1)
        }
    }

    GameShell(
        juego = juego,
        consigna = if (terminado) "Juego terminado — Puntaje: $puntaje" else "Come y no choques",
        onVolver = onVolver,
        acciones = { if (terminado) Button(onClick = ::reiniciar) { Text("Jugar de nuevo") } },
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Puntaje: $puntaje", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState())
                    .size(width = CELDA_SNAKE * SNAKE_COLS, height = CELDA_SNAKE * SNAKE_FILAS)
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF223B24)),
            ) {
                Column {
                    for (f in 0 until SNAKE_FILAS) {
                        Row {
                            for (c in 0 until SNAKE_COLS) {
                                val celda = f to c
                                val esCabeza = celda == vibora.first()
                                val esCuerpo = !esCabeza && celda in vibora
                                val esComida = celda == comida
                                Box(
                                    modifier = Modifier.size(CELDA_SNAKE).padding(1.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    when {
                                        esComida -> Text("🍎", fontSize = 14.sp)
                                        esCabeza -> Box(Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp)).background(Color(0xFF8BBF6A)))
                                        esCuerpo -> Box(Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp)).background(Color(0xFF4C7A3A)))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 16.dp)) {
                BotonDireccion("⬆️") { girar(ARRIBA) }
                Row(horizontalArrangement = Arrangement.spacedBy(52.dp)) {
                    BotonDireccion("⬅️") { girar(IZQUIERDA) }
                    BotonDireccion("➡️") { girar(DERECHA) }
                }
                BotonDireccion("⬇️") { girar(ABAJO) }
            }
        }
    }
}

@Composable
private fun BotonDireccion(emoji: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(48.dp)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(emoji, fontSize = 20.sp) }
}
