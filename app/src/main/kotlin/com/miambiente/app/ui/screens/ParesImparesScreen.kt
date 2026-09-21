package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.DefCanasta
import com.miambiente.app.ui.materials.ItemClasificar
import com.miambiente.app.ui.materials.MaterialClasificar

private val POOL = (2..11).map { n -> ItemClasificar(n, "n$n", if (n % 2 == 0) "par" else "impar") }

/** Pares e impares — patrón MaterialClasificar (¿se reparte en parejas exactas?). */
@Composable
fun ParesImparesScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("pares-impares")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("par", "Par", Color(0xFFE9F0E4)),
            DefCanasta("impar", "Impar", Color(0xFFF3ECF8)),
        ),
        render = { n ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$n", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(n) { Text("●", fontSize = 8.sp) }
                }
            }
        },
        consigna = "Arrastra cada cantidad a par o impar",
        onVolver = onVolver,
    )
}
