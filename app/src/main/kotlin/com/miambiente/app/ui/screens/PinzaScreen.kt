package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.MaterialTransferir

/** Pinza de transferencia — patrón MaterialTransferir, con arrastre real. */
@Composable
fun PinzaScreen(onVolver: () -> Unit) {
    MaterialTransferir(
        juego = buscarJuego("pinza")!!,
        calcularObjetivo = { nivel -> phasedInt(nivel, listOf(3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 10)) },
        origenPara = { objetivo -> objetivo + 4 },
        render = { Text("🔴", fontSize = 28.sp) },
        consigna = "Mueve de uno en uno",
        onVolver = onVolver,
    )
}
