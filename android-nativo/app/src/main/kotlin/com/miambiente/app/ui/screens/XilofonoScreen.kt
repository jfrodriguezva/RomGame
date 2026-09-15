package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Haptics
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

private val BARRAS = listOf(
    Color(0xFFD9433A), Color(0xFFE08A3A), Color(0xFFE0C23C), Color(0xFF4C7A3A),
    Color(0xFF3E7AA3), Color(0xFF7A4FA3), Color(0xFFE0669C), Color(0xFFA97FC7),
)

/** Xilófono — explorar sonido libre, cada barra es un tono real distinto (ToneGenerator). */
@Composable
fun XilofonoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("xilofono")!!

    GameShell(juego = juego, consigna = "Toca y escucha, sin reglas", onVolver = onVolver) {
        Row(Modifier.fillMaxSize().padding(24.dp)) {
            BARRAS.forEachIndexed { i, color ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color)
                        .clickable {
                            services.sound.tocarNota(i)
                            services.haptics.vibrar(Patron.TOQUE)
                        },
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                ) {}
            }
        }
    }
}
