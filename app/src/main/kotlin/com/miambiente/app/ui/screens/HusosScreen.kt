package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.MaterialTransferir

/** Los husos — patrón MaterialTransferir (corresponder cantidad exacta con un número). */
@Composable
fun HusosScreen(onVolver: () -> Unit) {
    val objetivo = phasedInt(1, listOf(1, 2, 3, 3, 4, 5, 6, 7, 8, 9, 9)).coerceIn(1, 9)
    MaterialTransferir(
        juego = buscarJuego("husos")!!,
        objetivo = objetivo,
        origenTotal = 9,
        render = { Text("🥢", fontSize = 26.sp) },
        consigna = "Coloca la cantidad exacta de husos",
        onVolver = onVolver,
    )
}
