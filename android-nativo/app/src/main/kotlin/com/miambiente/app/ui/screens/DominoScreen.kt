package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val FIGURAS = listOf("🐶", "🐱", "🐰", "🦋", "🌸", "⭐")
private data class Ficha(val id: Int, val a: String, val b: String)

/** Dominó de imágenes — encaja tu ficha con el dibujo, no con el número. */
@Composable
fun DominoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("domino")!!

    fun nuevoMazo(): List<Ficha> {
        val cadena = FIGURAS.shuffled().take(5)
        return (0 until 4).map { i -> Ficha(i, cadena[i], cadena[i + 1]) }.shuffled()
    }

    var mano by remember { mutableStateOf(nuevoMazo()) }
    var extremo by remember { mutableStateOf(mano.first().a) }
    var colocadas by remember { mutableStateOf(0) }

    fun tocar(ficha: Ficha) {
        if (ficha.a == extremo) {
            services.sound.tocar(Efecto.CORRECT)
            extremo = ficha.b
        } else if (ficha.b == extremo) {
            services.sound.tocar(Efecto.CORRECT)
            extremo = ficha.a
        } else {
            services.sound.tocar(Efecto.WRONG)
            return
        }
        mano = mano.filter { it.id != ficha.id }
        colocadas++
        if (mano.isEmpty()) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch {
                delay(1200)
                val nuevo = nuevoMazo()
                mano = nuevo
                extremo = nuevo.first().a
                colocadas = 0
            }
        }
    }

    GameShell(juego = juego, consigna = "Encaja una ficha con: $extremo", onVolver = onVolver) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(extremo, fontSize = 56.sp)
            Row(modifier = Modifier.padding(top = 32.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                mano.forEach { ficha ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .clickable { tocar(ficha) }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(ficha.a, fontSize = 24.sp)
                        Text("|")
                        Text(ficha.b, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
