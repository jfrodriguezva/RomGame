package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

// Mosaico (Gals Panic), Vaqueros (Sunset Riders), Comepuntos (Pac-Man) y
// Nieve (Snow Bros) tenían aquí una primera versión "inspirada" (blanco
// aleatorio, revelar casillas fijas, laberinto por turnos, aguantar 3
// toques) que el usuario pidió rehacer como copias reales de la mecánica
// de cada arcade — ahora viven en sus propios archivos
// (GalsPanicScreen.kt, SunsetRidersScreen.kt, PacManScreen.kt,
// SnowBrosScreen.kt) con la modalidad de juego genuina de cada uno. Este
// archivo se queda solo con los dos arcades que no se pidió rehacer.

/** Shooter espacial por carriles con ciclos discretos para no saturar recomposición. */
@Composable
fun EscuadronEstelarScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("escuadron-estelar")!!
    var carril by remember { mutableIntStateOf(1) }
    var enemigo by remember { mutableIntStateOf(0) }
    var escudo by remember { mutableIntStateOf(3) }
    var puntos by remember { mutableIntStateOf(0) }
    var jugando by remember { mutableStateOf(false) }
    var ronda by remember { mutableIntStateOf(0) }
    fun iniciar() { carril = 1; enemigo = 0; escudo = 3; puntos = 0; jugando = true; ronda++ }

    LaunchedEffect(ronda, jugando) {
        if (!jugando) return@LaunchedEffect
        while (jugando && escudo > 0 && puntos < 15) {
            enemigo = (0..2).random()
            delay((1_150L - puntos * 35L).coerceAtLeast(550L))
            if (jugando && enemigo == carril) escudo--
        }
        if (jugando) {
            jugando = false
            if (puntos >= 15) { services.sound.tocar(Efecto.WIN); scope.launch { services.progress.completarNivel(juego.id, 1) } }
            else services.sound.tocar(Efecto.WRONG)
        }
    }

    fun disparar() {
        if (!jugando) return
        if (carril == enemigo) { puntos++; enemigo = -1; services.sound.tocar(Efecto.CORRECT) }
        else services.sound.tocar(Efecto.CLICK)
    }

    GameShell(juego, if (jugando) "Naves $puntos/15 · Escudo $escudo" else "Pilota y dispara", onVolver = onVolver, acciones = { Button(onClick = ::iniciar) { Text(if (jugando) "Reiniciar" else "Despegar") } }) {
        Column(Modifier.fillMaxSize().background(Color(0xFF10152E)).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { Text(if (enemigo == it) "🛸" else "✨", fontSize = 42.sp) } }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { i -> Box(Modifier.size(82.dp).clip(RoundedCornerShape(18.dp)).background(if (carril == i) Color(0xFF425CC7) else Color(0xFF252C52)).clickable { carril = i }, contentAlignment = Alignment.Center) { Text(if (carril == i) "🚀" else "", fontSize = 46.sp) } } }
            Button(onClick = ::disparar, enabled = jugando) { Text("DISPARAR") }
        }
    }
}

/** Carrera por carriles: los obstáculos avanzan por pasos estables, sin bucle por cuadro. */
@Composable
fun GranPremioScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("gran-premio")!!
    var carril by remember { mutableIntStateOf(1) }
    var obstaculo by remember { mutableIntStateOf(0) }
    var fila by remember { mutableIntStateOf(0) }
    var distancia by remember { mutableIntStateOf(0) }
    var jugando by remember { mutableStateOf(false) }
    var ronda by remember { mutableIntStateOf(0) }
    fun iniciar() { carril = 1; obstaculo = 0; fila = 0; distancia = 0; jugando = true; ronda++ }

    LaunchedEffect(ronda, jugando) {
        if (!jugando) return@LaunchedEffect
        while (jugando && distancia < 30) {
            delay((480L - distancia * 7L).coerceAtLeast(260L))
            fila++
            if (fila >= 6) {
                if (carril == obstaculo) { jugando = false; services.sound.tocar(Efecto.WRONG); break }
                distancia++; fila = 0; obstaculo = (0..2).random()
            }
        }
        if (jugando && distancia >= 30) { jugando = false; services.sound.tocar(Efecto.WIN); scope.launch { services.progress.completarNivel(juego.id, 1) } }
    }

    GameShell(juego, if (jugando) "Meta: $distancia/30" else if (distancia >= 30) "¡Primer lugar!" else "Cambia de carril para esquivar", onVolver = onVolver, acciones = { Button(onClick = ::iniciar) { Text(if (jugando) "Reiniciar" else "Arrancar") } }) {
        Column(Modifier.fillMaxSize().background(Color(0xFF454545)).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            repeat(6) { f -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { c -> Box(Modifier.size(76.dp, 60.dp).background(if (c % 2 == 0) Color(0xFF505050) else Color(0xFF595959)), contentAlignment = Alignment.Center) { if (jugando && f == fila && c == obstaculo) Text("🚙", fontSize = 32.sp) } } } }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { c -> Box(Modifier.size(76.dp, 58.dp).clip(RoundedCornerShape(12.dp)).background(if (c == carril) Color(0xFF7FA8D8) else Color(0xFFDDDDDD)).clickable { carril = c }, contentAlignment = Alignment.Center) { if (c == carril) Text("🏎️", fontSize = 36.sp) } } }
        }
    }
}
