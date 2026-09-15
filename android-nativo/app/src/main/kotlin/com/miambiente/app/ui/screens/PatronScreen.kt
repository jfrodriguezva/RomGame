package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CAMPANAS = listOf(Color(0xFFD9433A), Color(0xFFE0C23C), Color(0xFF3E7AA3), Color(0xFF4C7A3A))

/** Las campanas — memoria auditiva: repite la melodía (Simon dice, con tonos y vibración reales). */
@Composable
fun PatronScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("patron")!!

    var secuencia by remember { mutableStateOf(listOf((0..3).random())) }
    var mostrando by remember { mutableStateOf(true) }
    var indiceEsperado by remember { mutableStateOf(0) }
    var sonando by remember { mutableStateOf(-1) }

    fun reproducir() {
        mostrando = true
        indiceEsperado = 0
        scope.launch {
            delay(500)
            secuencia.forEach { nota ->
                sonando = nota
                services.sound.tocarNota(nota)
                delay(500)
                sonando = -1
                delay(200)
            }
            mostrando = false
        }
    }

    LaunchedEffect(Unit) { reproducir() }

    fun nuevaRonda(masLarga: Boolean) {
        secuencia = if (masLarga) secuencia + (0..3).random() else List(secuencia.size) { (0..3).random() }
        reproducir()
    }

    fun tocar(nota: Int) {
        if (mostrando) return
        services.sound.tocarNota(nota)
        if (nota == secuencia[indiceEsperado]) {
            indiceEsperado++
            if (indiceEsperado == secuencia.size) {
                scope.launch { services.progress.completarNivel(juego.id, secuencia.size) }
                scope.launch { delay(900); nuevaRonda(masLarga = true) }
            }
        } else {
            scope.launch { delay(900); nuevaRonda(masLarga = false) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (mostrando) "Escucha la melodía..." else "Ahora repítela (${secuencia.size} campanas)",
        onVolver = onVolver,
    ) {
        Row(Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
            CAMPANAS.forEachIndexed { i, color ->
                androidx.compose.foundation.layout.Box(
                    Modifier
                        .padding(10.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (sonando == i) Color.White else color)
                        .clickable { tocar(i) },
                )
            }
        }
    }
}
