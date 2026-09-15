package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val IMAGENES = listOf("🐶", "🐱", "🐰", "🦋", "🌸")
private data class Ficha(val id: Int, val a: Int, val b: Int)

/** Genera el set doble-4: todas las parejas a<=b entre las 5 imágenes (15 fichas). */
private fun setCompleto(): List<Ficha> {
    var id = 0
    val fichas = mutableListOf<Ficha>()
    for (a in IMAGENES.indices) for (b in a until IMAGENES.size) fichas.add(Ficha(id++, a, b))
    return fichas
}

private fun encaja(f: Ficha, extremo: Int) = f.a == extremo || f.b == extremo
private fun otroLado(f: Ficha, extremo: Int) = if (f.a == extremo) f.b else f.a

/**
 * Dominó de imágenes — reglas reales del dominó (no la versión simplificada
 * anterior de un solo extremo): cadena con dos extremos, mano repartida,
 * pozo para robar, y turnos alternos contra la computadora.
 */
@Composable
fun DominoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("domino")!!

    fun repartir(): Triple<List<Ficha>, List<Ficha>, List<Ficha>> {
        val barajado = setCompleto().shuffled()
        return Triple(barajado.take(5), barajado.subList(5, 10), barajado.subList(10, 15))
    }

    var manoJugador by remember { mutableStateOf(listOf<Ficha>()) }
    var manoCpu by remember { mutableStateOf(listOf<Ficha>()) }
    var pozo by remember { mutableStateOf(listOf<Ficha>()) }
    var cadena by remember { mutableStateOf(listOf<Ficha>()) }
    var izquierda by remember { mutableStateOf(-1) }
    var derecha by remember { mutableStateOf(-1) }
    var turno by remember { mutableStateOf("jugador") }
    var mensaje by remember { mutableStateOf("Toca una ficha para empezar") }
    var terminado by remember { mutableStateOf(false) }

    fun reiniciar() {
        val (j, c, p) = repartir()
        manoJugador = j; manoCpu = c; pozo = p
        cadena = emptyList(); izquierda = -1; derecha = -1
        turno = "jugador"; terminado = false
        mensaje = "Toca una ficha para empezar"
    }

    LaunchedEffect(Unit) { reiniciar() }

    fun colocar(f: Ficha, enIzquierda: Boolean) {
        if (cadena.isEmpty()) {
            cadena = listOf(f); izquierda = f.a; derecha = f.b
        } else if (enIzquierda) {
            cadena = listOf(f) + cadena; izquierda = otroLado(f, izquierda)
        } else {
            cadena = cadena + f; derecha = otroLado(f, derecha)
        }
    }

    fun terminarSiCorresponde(): Boolean {
        if (manoJugador.isEmpty()) {
            terminado = true; mensaje = "¡Ganaste! 🎉"
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            return true
        }
        if (manoCpu.isEmpty()) {
            terminado = true; mensaje = "Ganó la computadora, ¡otra vez!"
            services.sound.tocar(Efecto.WRONG)
            return true
        }
        val nadiePuede = pozo.isEmpty() &&
            manoJugador.none { encaja(it, izquierda) || encaja(it, derecha) } &&
            manoCpu.none { encaja(it, izquierda) || encaja(it, derecha) }
        if (nadiePuede) {
            terminado = true
            mensaje = if (manoJugador.size <= manoCpu.size) "¡Empate a tu favor!" else "Nadie puede más — ganó la computadora"
            return true
        }
        return false
    }

    fun jugarJugador(f: Ficha) {
        if (turno != "jugador" || terminado) return
        val enIzq = cadena.isEmpty() || f.a == izquierda || f.b == izquierda
        val enDer = cadena.isEmpty() || f.a == derecha || f.b == derecha
        if (!enIzq && !enDer) { services.sound.tocar(Efecto.WRONG); return }
        services.sound.tocar(Efecto.CORRECT)
        // Preferencia simple: si encaja a la derecha, ahí va; si no, a la izquierda.
        colocar(f, enIzquierda = !enDer)
        manoJugador = manoJugador.filter { it.id != f.id }
        if (!terminarSiCorresponde()) { turno = "cpu"; mensaje = "Turno de la computadora" }
    }

    LaunchedEffect(turno, terminado) {
        if (turno != "cpu" || terminado) return@LaunchedEffect
        delay(700)
        var jugable = manoCpu.firstOrNull { encaja(it, izquierda) || encaja(it, derecha) }
        while (jugable == null && pozo.isNotEmpty()) {
            manoCpu = manoCpu + pozo.first(); pozo = pozo.drop(1)
            jugable = manoCpu.firstOrNull { encaja(it, izquierda) || encaja(it, derecha) }
        }
        if (jugable != null) {
            val enDer = cadena.isEmpty() || jugable.a == derecha || jugable.b == derecha
            colocar(jugable, enIzquierda = !enDer)
            manoCpu = manoCpu.filter { it.id != jugable.id }
            services.sound.tocar(Efecto.CLICK)
        }
        if (!terminarSiCorresponde()) { turno = "jugador"; mensaje = "Tu turno" }
    }

    fun robar() {
        if (turno != "jugador" || terminado || pozo.isEmpty()) return
        manoJugador = manoJugador + pozo.first()
        pozo = pozo.drop(1)
        services.sound.tocar(Efecto.CLICK)
    }

    GameShell(
        juego = juego,
        consigna = mensaje,
        celebrar = terminado && manoJugador.isEmpty(),
        onVolver = onVolver,
        acciones = {
            if (terminado) {
                Button(onClick = ::reiniciar) { Text("Jugar de nuevo") }
            } else if (turno == "jugador" && manoJugador.none { encaja(it, izquierda) || encaja(it, derecha) } && cadena.isNotEmpty()) {
                Button(onClick = ::robar, enabled = pozo.isNotEmpty()) { Text("Robar del pozo (${pozo.size})") }
            }
        },
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Computadora: ${manoCpu.size} fichas", fontSize = 12.sp)
            LazyRow(modifier = Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(cadena) { f ->
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color.White).padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) { Text(IMAGENES[f.a], fontSize = 18.sp); Text("|"); Text(IMAGENES[f.b], fontSize = 18.sp) }
                }
            }
            Text("Tu mano", fontSize = 12.sp)
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                manoJugador.forEach { f ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .clickable { jugarJugador(f) }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) { Text(IMAGENES[f.a], fontSize = 22.sp); Text("|"); Text(IMAGENES[f.b], fontSize = 22.sp) }
                }
            }
        }
    }
}
