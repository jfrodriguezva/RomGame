package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.dificultadCanasta
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState
import kotlinx.coroutines.delay

/**
 * Atrapa las estrellas — caída por pasos estables. La meta y la velocidad
 * salen del nivel (`dificultadCanasta`).
 */
@Composable fun CanastaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("canasta")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val d = remember(estado.nivel) { dificultadCanasta(estado.nivel) }

    var canasta by remember(estado.nivel) { mutableIntStateOf(1) }
    var estrellaCarril by remember(estado.nivel) { mutableIntStateOf(0) }
    var estrellaFila by remember(estado.nivel) { mutableIntStateOf(0) }
    var atrapadas by remember(estado.nivel) { mutableIntStateOf(0) }
    var perdidas by remember(estado.nivel) { mutableIntStateOf(0) }
    var jugando by remember(estado.nivel) { mutableStateOf(false) }
    var ronda by remember { mutableIntStateOf(0) }

    fun iniciar() { canasta = 1; atrapadas = 0; perdidas = 0; estrellaFila = 0; estrellaCarril = (0..2).random(); jugando = true; ronda++ }

    LaunchedEffect(ronda, jugando, estado.nivel) {
        if (!jugando) return@LaunchedEffect
        while (jugando && atrapadas < d.meta) {
            // Se acelera un poco dentro de la ronda, sin bajar de 250 ms.
            delay((d.pasoMs - atrapadas * 12L).coerceAtLeast(250L))
            estrellaFila++
            if (estrellaFila >= 6) {
                if (estrellaCarril == canasta) { atrapadas++; services.sound.tocar(Efecto.CORRECT) } else perdidas++
                estrellaFila = 0
                estrellaCarril = (0..2).random()
            }
        }
        if (jugando && atrapadas >= d.meta) { jugando = false; estado.completar() }
    }

    GameShell(
        juego,
        if (atrapadas >= d.meta) "¡Las atrapaste!" else "Estrellas $atrapadas/${d.meta} · Pasaron $perdidas",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else {
            { Button(onClick = ::iniciar) { Text(if (jugando) "Reiniciar" else "Jugar") } }
        },
    ) {
        Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFE4F0FA), Color(0xFFEFF8EA)))).padding(10.dp)) {
            repeat(6) { f -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { c -> Box(Modifier.size(82.dp, 58.dp), contentAlignment = Alignment.Center) { if (jugando && f == estrellaFila && c == estrellaCarril) Text("⭐", fontSize = 30.sp) } } } }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                repeat(3) { c ->
                    Box(
                        Modifier.size(82.dp, 62.dp).clip(RoundedCornerShape(14.dp))
                            .background(if (canasta == c) Color.White else Color.White.copy(alpha = .35f))
                            .clickable(enabled = jugando) { canasta = c }
                            .semantics { contentDescription = "Carril ${c + 1}" },
                        contentAlignment = Alignment.Center,
                    ) { if (canasta == c) Text("🧺", fontSize = 40.sp) }
                }
            }
        }
    }
}
