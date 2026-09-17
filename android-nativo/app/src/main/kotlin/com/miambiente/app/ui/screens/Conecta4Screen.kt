package com.miambiente.app.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val CONECTA4_COLS = 5
internal const val CONECTA4_FILAS = 4
private const val COLS = CONECTA4_COLS
private const val FILAS = CONECTA4_FILAS

internal fun hayGanador(tablero: List<String?>, ficha: String): Boolean {
    fun en(c: Int, f: Int) = if (c in 0 until COLS && f in 0 until FILAS) tablero[f * COLS + c] else null
    for (c in 0 until COLS) for (f in 0 until FILAS) {
        if (en(c, f) != ficha) continue
        val direcciones = listOf(1 to 0, 0 to 1, 1 to 1, 1 to -1)
        for ((dc, df) in direcciones) {
            if ((0 until 4).all { en(c + it * dc, f + it * df) == ficha }) return true
        }
    }
    return false
}

/**
 * Cuatro en línea — alinea cuatro fichas antes que la computadora.
 *
 * Bug real encontrado jugando en el emulador: `gan`/`empate` antes vivían
 * *dentro* del efecto que reinicia el tablero, y el efecto que mueve a la
 * computadora no los conocía en absoluto — solo miraba `turno`. Como
 * jugar() siempre pone `turno = "cpu"` (incluso en la jugada que gana),
 * la computadora alcanzaba a mover una vez más después de que el jugador
 * ya había ganado, y esa jugada extra reiniciaba el efecto de fin de
 * juego a medio camino. Ahora `gan`/`empate` se calculan una vez por
 * recomposición (como en Gato, que sí lo hacía bien) y ambos efectos los
 * respetan.
 */
@Composable
fun Conecta4Screen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("conecta4")!!

    var tablero by remember { mutableStateOf(List<String?>(COLS * FILAS) { null }) }
    var turno by remember { mutableStateOf("jugador") }
    var mensaje by remember { mutableStateOf("Tu turno") }

    val ganoJugador = hayGanador(tablero, "🔴")
    val ganoCpu = !ganoJugador && hayGanador(tablero, "🔵")
    val empate = !ganoJugador && !ganoCpu && tablero.none { it == null }
    val terminado = ganoJugador || ganoCpu || empate

    fun columnaLlena(c: Int) = (0 until FILAS).none { f -> tablero[f * COLS + c] == null }

    fun soltarEn(c: Int, ficha: String): List<String?>? {
        if (columnaLlena(c)) return null
        val fila = (FILAS - 1 downTo 0).first { f -> tablero[f * COLS + c] == null }
        return tablero.toMutableList().also { it[fila * COLS + c] = ficha }
    }

    fun reiniciar() {
        tablero = List(COLS * FILAS) { null }
        turno = "jugador"
        mensaje = "Tu turno"
    }

    fun jugar(c: Int) {
        if (turno != "jugador" || terminado) return
        val nuevo = soltarEn(c, "🔴") ?: return
        services.sound.tocar(Efecto.CLICK)
        tablero = nuevo
        turno = "cpu"
    }

    LaunchedEffect(ganoJugador, ganoCpu, empate) {
        if (ganoJugador) {
            services.sound.tocar(Efecto.WIN)
            mensaje = "¡Ganaste! 🎉"
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            delay(1800); reiniciar()
        } else if (ganoCpu) {
            services.sound.tocar(Efecto.WRONG)
            mensaje = "Ganó la computadora"
            delay(1800); reiniciar()
        } else if (empate) {
            mensaje = "¡Empate!"
            delay(1800); reiniciar()
        }
    }

    LaunchedEffect(turno, tablero) {
        if (turno != "cpu" || terminado) return@LaunchedEffect
        delay(500)
        val columnasLibres = (0 until COLS).filter { !columnaLlena(it) }
        if (columnasLibres.isEmpty()) return@LaunchedEffect
        val columnaGanadora = columnasLibres.firstOrNull { c -> soltarEn(c, "🔵")?.let { hayGanador(it, "🔵") } == true }
        val columnaBloqueo = columnasLibres.firstOrNull { c -> soltarEn(c, "🔴")?.let { hayGanador(it, "🔴") } == true }
        val elegida = columnaGanadora ?: columnaBloqueo ?: columnasLibres.random()
        tablero = soltarEn(elegida, "🔵") ?: tablero
        turno = "jugador"
    }

    GameShell(juego = juego, consigna = mensaje, onVolver = onVolver) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            // Tablero real con marco: antes las fichas flotaban sueltas
            // sobre el fondo, sin el marco azul clásico de Cuatro en línea.
            Column(
                modifier = Modifier
                    .shadow(6.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF2F5C82))
                    .padding(6.dp),
            ) {
                for (f in 0 until FILAS) {
                    Row {
                        for (c in 0 until COLS) {
                            val ficha = tablero[f * COLS + c]
                            val tamano by animateDpAsState(
                                if (ficha != null) 40.dp else 0.dp,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "ficha",
                            )
                            Box(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF224867))
                                    .clickable { jugar(c) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(
                                    Modifier
                                        .size(tamano)
                                        .shadow(if (tamano > 0.dp) 2.dp else 0.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(if (ficha == "🔴") Color(0xFFD9433A) else if (ficha == "🔵") Color(0xFF7AC0E0) else Color.Transparent),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
