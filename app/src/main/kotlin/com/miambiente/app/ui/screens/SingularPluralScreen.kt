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
    ItemClasificar("gato", "gato", "singular"), ItemClasificar("flor", "flor", "singular"),
    ItemClasificar("árbol", "arbol", "singular"), ItemClasificar("casa", "casa", "singular"),
    ItemClasificar("gatos", "gatos", "plural"), ItemClasificar("flores", "flores", "plural"),
    ItemClasificar("árboles", "arboles", "plural"), ItemClasificar("casas", "casas", "plural"),
)

/** Singular y plural — patrón MaterialClasificar (la palabra junto a su cantidad). */
@Composable
fun SingularPluralScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("singular-plural")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("singular", "Singular", Color(0xFFEAF1F8)),
            DefCanasta("plural", "Plural", Color(0xFFF3ECF8)),
        ),
        render = { palabra -> Text(palabra, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        consigna = "Arrastra cada palabra a singular o plural",
        onVolver = onVolver,
    )
}
