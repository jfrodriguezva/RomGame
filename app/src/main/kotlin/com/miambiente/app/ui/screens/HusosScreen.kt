package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.CURVA_HUSOS
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.MaterialTransferir

/** Los husos — patrón MaterialTransferir (corresponder cantidad exacta con un número). */
@Composable
fun HusosScreen(onVolver: () -> Unit) {
    MaterialTransferir(
        juego = buscarJuego("husos")!!,
        calcularObjetivo = { nivel -> phasedInt(nivel, CURVA_HUSOS).coerceIn(1, 9) },
        origenPara = { 9 },
        render = { Text("🥢", fontSize = 26.sp) },
        consigna = "Coloca la cantidad exacta de husos",
        onVolver = onVolver,
    )
}
