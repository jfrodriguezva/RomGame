package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.CURVA_CONTAR
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.MaterialTransferir

/** Contar y tocar — patrón MaterialTransferir (correspondencia uno a uno). */
@Composable
fun ContarScreen(onVolver: () -> Unit) {
    MaterialTransferir(
        juego = buscarJuego("contar")!!,
        calcularObjetivo = { nivel -> phasedInt(nivel, CURVA_CONTAR).coerceIn(1, 9) },
        origenPara = { 9 },
        render = { Text("🔵", fontSize = 26.sp) },
        consigna = "Cuenta y transfiere la cantidad exacta",
        onVolver = onVolver,
    )
}
