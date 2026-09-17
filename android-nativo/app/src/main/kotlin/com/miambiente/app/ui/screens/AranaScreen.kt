package com.miambiente.app.ui.screens

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

/** La araña pintora — descubre la imagen escondida, recorriendo todo el tablero. */
@Composable
fun AranaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("arana")!!
    val imagen = "🕷️"

    var reveladas by remember { mutableStateOf(setOf<Int>()) }

    fun tocar(i: Int) {
        if (i in reveladas) return
        services.sound.tocar(Efecto.CLICK)
        reveladas = reveladas + i
        if (reveladas.size == 16) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (reveladas.size == 16) "¡Descubriste la imagen!" else "Toca cada casilla para descubrir la imagen",
        onVolver = onVolver,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp)) {
            items(16) { i ->
                val revelada = i in reveladas
                val escala by animateFloatAsState(if (revelada) 1f else 0f, animationSpec = spring(dampingRatio = 0.5f), label = "revela")
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(64.dp)
                        .shadow(if (revelada) 1.dp else 3.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (revelada) Color(0xFFF3ECF8) else Color(0xFF7A4FA3))
                        .clickable(enabled = !revelada) { tocar(i) },
                    contentAlignment = Alignment.Center,
                ) { if (revelada) Text(imagen, fontSize = 20.sp, modifier = Modifier.scale(escala)) }
            }
        }
    }
}
