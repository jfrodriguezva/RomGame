package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class Personaje(val emoji: String, val color: String, val animal: Boolean)

private val PERSONAJES = listOf(
    Personaje("🐶", "cafe", true), Personaje("🐱", "gris", true), Personaje("🐰", "blanco", true),
    Personaje("🐸", "verde", true), Personaje("🐷", "rosa", true), Personaje("🐨", "gris", true),
    Personaje("🦁", "cafe", true), Personaje("🐹", "cafe", true),
)

/** Adivina quién es — deducción por eliminación, filtrando por atributos. */
@Composable
fun AdivinaQuienScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("adivinaquien")!!

    var secreto by remember { mutableStateOf(PERSONAJES.random()) }
    var colorFiltro by remember(secreto) { mutableStateOf<String?>(null) }

    val visibles = PERSONAJES.filter { colorFiltro == null || it.color == colorFiltro }

    fun nuevaRonda() {
        secreto = PERSONAJES.random()
        colorFiltro = null
    }

    fun adivinar(p: Personaje) {
        if (p == secreto) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch { delay(1200); nuevaRonda() }
        } else {
            services.sound.tocar(Efecto.WRONG)
        }
    }

    GameShell(
        juego = juego,
        consigna = "Pregunta por color y luego toca quién es",
        onVolver = onVolver,
        acciones = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("cafe", "gris", "blanco", "verde", "rosa").forEach { color ->
                    Button(onClick = { colorFiltro = color }) { Text(color) }
                }
                Button(onClick = { colorFiltro = null }) { Text("Todos") }
            }
        },
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)) {
            items(visibles) { p ->
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .clickable { adivinar(p) },
                    contentAlignment = Alignment.Center,
                ) { Text(p.emoji, fontSize = 32.sp, modifier = Modifier.padding(12.dp)) }
            }
        }
    }
}
