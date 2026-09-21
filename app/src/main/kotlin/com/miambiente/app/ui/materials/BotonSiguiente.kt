package com.miambiente.app.ui.materials

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.miambiente.app.theme.AreaColores

/**
 * Antes de esto, al completar un nivel no había ninguna forma de avanzar
 * al siguiente — `MaterialState.siguiente()` existía pero nada lo
 * llamaba. Este botón, en el slot `acciones` de GameShell, es lo que
 * cierra ese hueco en los 4 patrones compartidos a la vez.
 */
@Composable
fun BotonSiguienteNivel(colores: AreaColores, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = colores.acentoOscuro),
    ) { Text("Siguiente nivel →") }
}
