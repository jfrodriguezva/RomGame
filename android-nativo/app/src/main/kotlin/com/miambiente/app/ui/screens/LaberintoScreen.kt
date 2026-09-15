package com.miambiente.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

private const val COLS = 5
// 1 = camino abierto, 0 = pared. Fila 0 es la entrada, última fila la salida.
private val MAPA = listOf(
    1, 1, 0, 0, 0,
    0, 1, 1, 1, 0,
    0, 0, 0, 1, 0,
    0, 1, 1, 1, 0,
    0, 1, 0, 0, 0,
    0, 1, 1, 1, 1,
)

/** Laberinto — encuentra la salida moviéndote solo por el camino abierto. */
@Composable
fun LaberintoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("laberinto")!!
    val filas = MAPA.size / COLS
    val entrada = (0 until COLS).first { MAPA[it] == 1 }
    val salida = (filas - 1) * COLS + (0 until COLS).first { MAPA[(filas - 1) * COLS + it] == 1 }

    var posicion by remember { mutableStateOf(entrada) }
    var completo by remember { mutableStateOf(false) }

    fun sonAdyacentes(a: Int, b: Int): Boolean {
        val (fa, ca) = a / COLS to a % COLS
        val (fb, cb) = b / COLS to b % COLS
        return (fa == fb && kotlin.math.abs(ca - cb) == 1) || (ca == cb && kotlin.math.abs(fa - fb) == 1)
    }

    fun tocar(i: Int) {
        if (completo || MAPA[i] == 0 || !sonAdyacentes(posicion, i)) {
            if (MAPA[i] == 0) services.sound.tocar(Efecto.WRONG)
            return
        }
        services.sound.tocar(Efecto.CLICK)
        posicion = i
        if (posicion == salida) {
            completo = true
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Encontraste la salida!" else "Toca las casillas abiertas para avanzar",
        onVolver = onVolver,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(COLS), contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp)) {
            items(MAPA.size) { i ->
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(56.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                MAPA[i] == 0 -> Color(0xFF3F342C)
                                i == salida -> Color(0xFF8BBF6A)
                                else -> Color.White
                            },
                        )
                        .clickable { tocar(i) },
                    contentAlignment = Alignment.Center,
                ) { if (i == posicion) Text("🐭", fontSize = 22.sp) else if (i == salida) Text("🧀", fontSize = 18.sp) }
            }
        }
    }
}
