package com.miambiente.app.ui.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

/** Una casilla especial: si el token cae ahí, salta a `destino` (adelante o atrás). */
data class CasillaEspecial(val posicion: Int, val destino: Int, val emoji: String, val mensaje: String)

/**
 * Juego de mesa de recorrido con dado — puerto genérico del patrón
 * compartido por la oca y serpientes y escaleras: tirar el dado, avanzar,
 * y algunas casillas mandan a otra parte del tablero. Un solo jugador
 * contra el tablero (llegar a la meta), no hay turnos contra otro jugador.
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

    var posicion by remember { mutableStateOf(0) }
    var dado by remember { mutableStateOf<Int?>(null) }
    var mensaje by remember { mutableStateOf("Tira el dado para empezar") }
    var tirando by remember { mutableStateOf(false) }
    var gano by remember { mutableStateOf(false) }

    fun tirar() {
        if (tirando || gano) return
        tirando = true
        scope.launch {
            val valor = (1..6).random()
            dado = valor
            services.sound.tocar(Efecto.CLICK)
            delay(300)
            var destino = (posicion + valor).coerceAtMost(casillas)
            posicion = destino
            val especial = especiales.find { it.posicion == destino }
            if (especial != null) {
                delay(400)
                mensaje = especial.mensaje
                services.haptics.vibrar(if (especial.destino > destino) Patron.ACIERTO else Patron.ERROR)
                delay(500)
                posicion = especial.destino
            }
            if (posicion >= casillas) {
                gano = true
                mensaje = "¡Llegaste a la meta!"
                services.sound.tocar(Efecto.WIN)
                services.haptics.vibrar(Patron.LOGRO)
                scope.launch { services.progress.completarNivel(juego.id, 1) }
            } else if (especial == null) {
                mensaje = "Tira otra vez"
            }
            tirando = false
        }
    }

    fun reiniciar() {
        posicion = 0
        gano = false
        mensaje = "Tira el dado para empezar"
    }

    GameShell(
        juego = juego,
        consigna = mensaje,
        celebrar = gano,
        onVolver = onVolver,
        acciones = {
            if (gano) {
                Button(onClick = ::reiniciar) { Text("Jugar de nuevo") }
            } else {
                Button(onClick = ::tirar) { Text(if (dado == null) "Tirar dado 🎲" else "Dado: ${dado} — tirar de nuevo") }
            }
        },
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.fillMaxSize()) {
                items(casillas + 1) { i ->
                    val especial = especiales.find { it.posicion == i }
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .background(if (i == casillas) colores.acento else Color.White, RoundedCornerShape(6.dp))
                            .clickable { },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                            Text("$i", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            if (especial != null) Text(especial.emoji, fontSize = 12.sp)
                            if (posicion == i) Box(Modifier.size(14.dp).clip(CircleShape).background(colorFicha))
                        }
                    }
                }
            }
        }
    }
}
