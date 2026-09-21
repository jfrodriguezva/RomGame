package com.miambiente.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.DefCanasta
import com.miambiente.app.ui.materials.ItemClasificar
import com.miambiente.app.ui.materials.MaterialClasificar

private val POOL = listOf(
    ItemClasificar("🐰", "conejo", "herbivoro"), ItemClasificar("🐄", "vaca", "herbivoro"),
    ItemClasificar("🦓", "cebra", "herbivoro"),
    ItemClasificar("🦁", "leon", "carnivoro"), ItemClasificar("🐺", "lobo", "carnivoro"),
    ItemClasificar("🐊", "cocodrilo", "carnivoro"),
    ItemClasificar("🐻", "oso", "omnivoro"), ItemClasificar("🐷", "cerdo", "omnivoro"),
)

/** ¿Qué come? — patrón MaterialClasificar (herbívoro / carnívoro / omnívoro). */
@Composable
fun DietaAnimalScreen(onVolver: () -> Unit) {
    MaterialClasificar(
        juego = buscarJuego("dieta-animal")!!,
        pool = POOL,
        cantidadPorRonda = 6,
        canastas = listOf(
            DefCanasta("herbivoro", "Herbívoro", Color(0xFFE9F0E4)),
            DefCanasta("carnivoro", "Carnívoro", Color(0xFFFBE9E7)),
            DefCanasta("omnivoro", "Omnívoro", Color(0xFFF3ECF8)),
        ),
        render = { emoji -> Text(emoji, fontSize = 32.sp) },
        consigna = "Arrastra cada animal según lo que come",
        onVolver = onVolver,
    )
}
