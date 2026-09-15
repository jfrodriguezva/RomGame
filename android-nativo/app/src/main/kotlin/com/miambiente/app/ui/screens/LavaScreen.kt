package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** El piso es lava — no toques el suelo (reflejos y control del salto). */
@Composable
fun LavaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("lava")!!

    var lava by remember { mutableStateOf(-1) }
    var puntos by remember { mutableStateOf(0) }
    val meta = 12

    LaunchedEffect(puntos) {
        if (puntos < meta) {
            delay(900)
            lava = (0 until 9).random()
        }
    }

    fun tocar(i: Int) {
        if (i == lava) {
            services.sound.tocar(Efecto.WRONG)
        } else {
            services.sound.tocar(Efecto.CORRECT)
            puntos++
            lava = -1
            if (puntos == meta) {
                services.sound.tocar(Efecto.WIN)
                scope.launch { services.progress.completarNivel(juego.id, 1) }
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (puntos >= meta) "¡Sobreviviste!" else "Toca cualquier casilla menos la lava: $puntos / $meta",
        onVolver = onVolver,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(32.dp)) {
            items(9) { i ->
                Box(
                    Modifier
                        .padding(6.dp)
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (i == lava) Color(0xFFD9433A) else Color(0xFFE9F0E4))
                        .clickable { tocar(i) },
                )
            }
        }
    }
}
