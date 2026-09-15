package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.MaterialOrdenar

private val PLANETAS = listOf(
    "Mercurio" to Color(0xFFA39A8C), "Venus" to Color(0xFFE0C23C), "Tierra" to Color(0xFF3E7AA3),
    "Marte" to Color(0xFFD9433A), "Júpiter" to Color(0xFFE08A3A), "Saturno" to Color(0xFFE0B586),
    "Urano" to Color(0xFF6FA6CC), "Neptuno" to Color(0xFF3E5C82),
)

/** El sistema solar — patrón MaterialOrdenar (secuencia fija desde el Sol). */
@Composable
fun SistemaSolarScreen(onVolver: () -> Unit) {
    MaterialOrdenar(
        juego = buscarJuego("sistema-solar")!!,
        n = PLANETAS.size,
        tamanoPara = { 64.dp },
        render = { posicion, _ ->
            val (nombre, color) = PLANETAS[posicion - 1]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(22.dp).clip(CircleShape).background(color))
                    Text(nombre, fontSize = 9.sp)
                }
            }
        },
        consigna = "Ordena los planetas desde el Sol",
        onVolver = onVolver,
    )
}
