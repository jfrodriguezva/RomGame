package com.miambiente.app.ui.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Una casilla especial: si un token cae ahí, salta a `destino` (adelante o atrás). */
data class CasillaEspecial(val posicion: Int, val destino: Int, val emoji: String, val mensaje: String)

/**
 * Juego de mesa de recorrido con dado — puerto genérico del patrón
 * compartido por la oca y serpientes y escaleras. Carrera real de dos
 * jugadores por turnos (tú contra la computadora, cada quien tira su
 * propio dado y avanza su propia ficha) en vez de un solo jugador
 * avanzando solo contra el tablero — así sí hay "esperar el turno y
 * aceptar el resultado", el objetivo pedagógico real de este material.
 */
@Composable
fun MaterialTablero(
    juego: GameDef,
    casillas: Int,
    especiales: List<CasillaEspecial>,
    colorFicha: Color,
    onVolver: () -> Unit,
) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val colores = coloresDe(juego.area)
    val colorCpu = Color(0xFFA39A8C)

    var posJugador by remember { mutableStateOf(0) }
    var posCpu by remember { mutableStateOf(0) }
    var dado by remember { mutableStateOf<Int?>(null) }
    var turno by remember { mutableStateOf("jugador") }
    var mensaje by remember { mutableStateOf("Tira el dado para empezar") }
    var tirando by remember { mutableStateOf(false) }
    var ganador by remember { mutableStateOf<String?>(null) }

    fun mover(esJugador: Boolean, valor: Int) {
        val posActual = if (esJugador) posJugador else posCpu
        var destino = (posActual + valor).coerceAtMost(casillas)
        if (esJugador) posJugador = destino else posCpu = destino
        val especial = especiales.find { it.posicion == destino }
        if (especial != null) {
            services.haptics.vibrar(if (especial.destino > destino) Patron.ACIERTO else Patron.ERROR)
            if (esJugador) posJugador = especial.destino else posCpu = especial.destino
        }
    }

    fun reiniciar() {
        posJugador = 0; posCpu = 0; dado = null
        turno = "jugador"; ganador = null
        mensaje = "Tira el dado para empezar"
    }

    fun tirar() {
        if (tirando || ganador != null || turno != "jugador") return
        tirando = true
        scope.launch {
            val valor = (1..6).random()
            dado = valor
            services.sound.tocar(Efecto.CLICK)
            delay(400)
            mover(esJugador = true, valor)
            if (posJugador >= casillas) {
                ganador = "jugador"
                mensaje = "¡Llegaste primero! 🎉"
                services.sound.tocar(Efecto.WIN)
                services.haptics.vibrar(Patron.LOGRO)
                scope.launch { services.progress.completarNivel(juego.id, 1) }
            } else {
                especiales.find { it.posicion == posJugador }?.let { mensaje = it.mensaje }
                turno = "cpu"
                mensaje = if (especiales.none { it.posicion == posJugador }) "Turno de la computadora" else mensaje
            }
            tirando = false
        }
    }

    LaunchedEffect(turno, ganador) {
        if (turno != "cpu" || ganador != null) return@LaunchedEffect
        delay(700)
        val valor = (1..6).random()
        dado = valor
        services.sound.tocar(Efecto.CLICK)
        delay(400)
        mover(esJugador = false, valor)
        if (posCpu >= casillas) {
            ganador = "cpu"
            mensaje = "Ganó la computadora, ¡otra vez!"
            services.sound.tocar(Efecto.WRONG)
        } else {
            turno = "jugador"
            mensaje = "Tu turno"
        }
    }

    GameShell(
        juego = juego,
        consigna = mensaje,
        celebrar = ganador == "jugador",
        onVolver = onVolver,
        acciones = {
            if (ganador != null) {
                Button(onClick = ::reiniciar) { Text("Jugar de nuevo") }
            } else if (turno == "jugador") {
                Button(onClick = ::tirar) { Text(if (dado == null) "Tirar dado 🎲" else "Dado: $dado — tirar de nuevo") }
            }
        },
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 4.dp)) {
                Text("Tú", fontSize = 11.sp, color = colorFicha, fontWeight = FontWeight.Bold)
                Text("Computadora", fontSize = 11.sp, color = colorCpu, fontWeight = FontWeight.Bold)
            }
            LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.fillMaxSize()) {
                items(casillas + 1) { i ->
                    val especial = especiales.find { it.posicion == i }
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .background(if (i == casillas) colores.acento else Color.White, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                            Text("$i", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            if (especial != null) Text(especial.emoji, fontSize = 12.sp)
                            Row {
                                if (posJugador == i) Box(Modifier.size(12.dp).clip(CircleShape).background(colorFicha))
                                if (posCpu == i) Box(Modifier.size(12.dp).clip(CircleShape).background(colorCpu))
                            }
                        }
                    }
                }
            }
        }
    }
}
