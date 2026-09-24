package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import kotlinx.coroutines.launch

private val PALABRAS = listOf("☀️" to "sol", "🍞" to "pan", "🌙" to "luna", "🐱" to "gato", "🌳" to "arbol")
private data class Letra(val indice: Int, val caracter: Char)

/** Alfabeto móvil — componer la palabra con letras sueltas, tocando en orden. */
@Composable
fun AlfabetoMovilScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("alfabeto-movil")!!

    var objetivo by remember { mutableStateOf(PALABRAS.random()) }
    var banco by remember(objetivo) {
        mutableStateOf(objetivo.second.mapIndexed { i, c -> Letra(i, c) }.shuffled())
    }
    var escrito by remember(objetivo) { mutableStateOf("") }

    fun tocar(letra: Letra) {
        val siguiente = objetivo.second[escrito.length]
        if (letra.caracter == siguiente) {
            services.sound.tocar(Efecto.CORRECT)
            escrito += letra.caracter
            banco = banco.filter { it.indice != letra.indice }
            if (escrito == objetivo.second) {
                services.sound.tocar(Efecto.WIN)
                scope.launch { services.progress.completarNivel(juego.id, 1) }
                scope.launch {
                    delay(1200)
                    objetivo = PALABRAS.filter { it.second != objetivo.second }.random()
                    escrito = ""
                }
            }
        } else {
            services.sound.tocar(Efecto.WRONG)
        }
    }

    GameShell(juego = juego, consigna = "Forma la palabra: ${objetivo.first}", onVolver = onVolver) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(objetivo.first, fontSize = 72.sp)
            Row(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                objetivo.second.indices.forEach { i ->
                    val llena = i < escrito.length
                    Box(
                        Modifier
                            .size(36.dp)
                            .shadow(if (llena) 2.dp else 0.dp, RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White)
                            .then(if (llena) Modifier else Modifier.border(2.dp, Color(0xFFA97FC7).copy(alpha = 0.35f), RoundedCornerShape(6.dp))),
                        contentAlignment = Alignment.Center,
                    ) { Text(if (llena) escrito[i].toString() else "", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                banco.forEach { letra ->
                    Box(
                        Modifier
                            .size(44.dp)
                            .shadow(3.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFA97FC7))
                            .clickable { tocar(letra) },
                        contentAlignment = Alignment.Center,
                    ) { Text(letra.caracter.toString(), fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
