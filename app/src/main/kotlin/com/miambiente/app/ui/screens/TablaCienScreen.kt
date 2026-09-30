package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.hastaTablaCien
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState

/**
 * La tabla del cien — coloca los números en orden (secuencia numérica y
 * estructura de la decena). Cada nivel llega un poco más lejos, de decena
 * en decena, hasta el 100 (`hastaTablaCien`). Antes el primer intento ya
 * pedía los cien números seguidos.
 */
@Composable
fun TablaCienScreen(onVolver: () -> Unit) {
    val juego = buscarJuego("tabla-cien")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val hasta = remember(estado.nivel) { hastaTablaCien(estado.nivel) }

    var siguiente by remember(estado.nivel) { mutableStateOf(1) }

    fun tocar(n: Int) {
        if (estado.logrado || n < siguiente) return
        if (n == siguiente) {
            siguiente++
            estado.acierto("¡$n!")
            if (siguiente > hasta) estado.completar()
        } else {
            estado.intento("Busca el $siguiente")
        }
    }

    GameShell(
        juego = juego,
        consigna = if (siguiente > hasta) "¡Llegaste al $hasta!" else "Toca el número $siguiente",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(10), contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)) {
            items(hasta) { i ->
                val n = i + 1
                val decena = (i / 10) % 2 == 0
                val esSiguiente = n == siguiente
                Box(
                    modifier = Modifier
                        .padding(1.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when {
                                n < siguiente -> Color(0xFF8BBF6A)
                                decena -> Color(0xFFF7F3EA)
                                else -> Color.White
                            },
                        )
                        .then(if (esSiguiente) Modifier.border(2.dp, Color(0xFFE0A93C), RoundedCornerShape(3.dp)) else Modifier)
                        .clickable { tocar(n) },
                    contentAlignment = Alignment.Center,
                ) { Text("$n", fontSize = 9.sp, modifier = Modifier.padding(3.dp)) }
            }
        }
    }
}
