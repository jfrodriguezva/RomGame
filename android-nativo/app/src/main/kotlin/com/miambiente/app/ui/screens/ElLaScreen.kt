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
    ItemClasificar("sol", "sol", "el"), ItemClasificar("perro", "perro", "el"),
    ItemClasificar("gato", "gato", "el"), ItemClasificar("árbol", "arbol", "el"),
    ItemClasificar("luna", "luna", "la"), ItemClasificar("casa", "casa", "la"),
    ItemClasificar("mesa", "mesa", "la"), ItemClasificar("flor", "flor", "la"),
)

/** El o la — patrón MaterialClasificar (género gramatical). */
@Composable
fun ElLaScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("el-la")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("el", "El", Color(0xFFEAF1F8)),
            DefCanasta("la", "La", Color(0xFFFBE9E7)),
        ),
        render = { palabra -> Text(palabra, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        consigna = "Arrastra cada palabra a \"el\" o \"la\"",
        onVolver = onVolver,
    )
}
