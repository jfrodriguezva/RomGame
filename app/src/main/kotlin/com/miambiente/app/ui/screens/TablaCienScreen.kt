package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** La tabla del cien — coloca del 1 al 100 en orden (secuencia numérica y estructura de la decena). */
@Composable
fun TablaCienScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("tabla-cien")!!

    var siguiente by remember { mutableStateOf(1) }

    fun tocar(n: Int) {
        if (n == siguiente) {
            services.sound.tocar(Efecto.CORRECT)
            siguiente++
            if (siguiente > 100) {
                services.sound.tocar(Efecto.WIN)
                scope.launch { services.progress.completarNivel(juego.id, 1) }
                scope.launch { delay(1500); siguiente = 1 }
            }
        } else {
            services.sound.tocar(Efecto.WRONG)
        }
    }

    GameShell(
        juego = juego,
        consigna = if (siguiente > 100) "¡Completaste la tabla del cien!" else "Toca el número $siguiente",
        onVolver = onVolver,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(10), contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)) {
            items(100) { i ->
                val n = i + 1
                val decena = (i / 10) % 2 == 0
                val esSiguiente = n == siguiente
                Box(
                    modifier = Modifier
                        .padding(1.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when {
                                n < siguiente -> Color(0xFF8BBF6A)
                                decena -> Color(0xFFF7F3EA)
                                else -> Color.White
                            },
                        )
                        .then(if (esSiguiente) Modifier.border(2.dp, Color(0xFFE0A93C), RoundedCornerShape(3.dp)) else Modifier)
                        .clickable { tocar(n) },
                    contentAlignment = Alignment.Center,
                ) { Text("$n", fontSize = 9.sp, modifier = Modifier.padding(3.dp)) }
            }
        }
    }
}
