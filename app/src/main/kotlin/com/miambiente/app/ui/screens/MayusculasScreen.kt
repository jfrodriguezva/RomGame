package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.DefCanasta
import com.miambiente.app.ui.materials.ItemClasificar
import com.miambiente.app.ui.materials.MaterialClasificar

private val POOL = listOf(
    ItemClasificar("A", "A", "mayuscula"), ItemClasificar("B", "B", "mayuscula"),
    ItemClasificar("C", "C", "mayuscula"), ItemClasificar("D", "D", "mayuscula"),
    ItemClasificar("a", "a", "minuscula"), ItemClasificar("b", "b", "minuscula"),
    ItemClasificar("c", "c", "minuscula"), ItemClasificar("d", "d", "minuscula"),
)

/** Mayúsculas y minúsculas — patrón MaterialClasificar. */
@Composable
fun MayusculasScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("mayusculas")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("mayuscula", "Mayúscula", Color(0xFFEAF1F8)),
            DefCanasta("minuscula", "minúscula", Color(0xFFF3ECF8)),
        ),
        render = { letra -> Text(letra, fontSize = 32.sp, fontWeight = FontWeight.Bold) },
        consigna = "Clasifica según cómo se ve la letra",
        onVolver = onVolver,
    )
}
