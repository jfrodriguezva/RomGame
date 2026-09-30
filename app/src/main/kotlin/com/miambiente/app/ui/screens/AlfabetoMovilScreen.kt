package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.rondaAlfabeto
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState

private data class Letra(val indice: Int, val caracter: Char)

/**
 * Alfabeto móvil — componer la palabra con letras sueltas, tocando en
 * orden. Con el nivel las palabras son más largas (3 a 7 letras) y el banco
 * trae letras de más que no van (`rondaAlfabeto`).
 */
@Composable
fun AlfabetoMovilScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("alfabeto-movil")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    val ronda = remember(estado.nivel) { rondaAlfabeto(estado.nivel) }
    val objetivo = ronda.first
    var banco by remember(estado.nivel) { mutableStateOf(ronda.second.mapIndexed { i, c -> Letra(i, c) }) }
    var escrito by remember(estado.nivel) { mutableStateOf("") }

    fun tocar(letra: Letra) {
        if (estado.logrado) return
        val siguiente = objetivo.second[escrito.length]
        if (letra.caracter == siguiente) {
            escrito += letra.caracter
            banco = banco.filter { it.indice != letra.indice }
            estado.acierto("¡${letra.caracter}!")
            if (escrito == objetivo.second) estado.completar()
        } else {
            estado.intento("Esa no. Escucha: ${objetivo.second}")
        }
    }

    GameShell(
        juego = juego,
        consigna = if (estado.logrado) "¡Escribiste ${objetivo.second}!" else "Forma la palabra: ${objetivo.first}",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
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
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                banco.forEach { letra ->
                    Box(
                        Modifier
                            .size(48.dp)
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
