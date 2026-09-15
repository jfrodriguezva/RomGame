package com.miambiente.app.ui.materials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell

/** Un elemento de nomenclatura: lo que se ve + su nombre + su id único. */
data class ItemQuiz<T>(val valor: T, val id: String, val nombre: String)

private const val RONDA = 5

/**
 * Lección de tres periodos, simplificada al periodo de reconocer
 * ("¿Qué es esto?", elegir el nombre correcto entre varias opciones) —
 * puerto del patrón MaterialQuiz. La versión web sí implementa los tres
 * periodos completos (nombrar, reconocer, evocar); portar eso fiel es
 * trabajo pendiente, no una limitación de la plataforma.
 */
@Composable
fun <T> MaterialQuiz(
    juego: GameDef,
    items: List<ItemQuiz<T>>,
    curvaOpciones: (nivel: Int) -> Int,
    render: @Composable (T, grande: Boolean) -> Unit,
    onVolver: () -> Unit,
) {
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    var objetivo by remember(estado.nivel) { mutableStateOf(items.random()) }
    val opciones = curvaOpciones(estado.nivel).coerceIn(2, items.size)
    var elecciones by remember(estado.nivel, objetivo) {
        mutableStateOf(generarOpciones(items, objetivo, opciones))
    }
    var aciertosRonda by remember(estado.nivel) { mutableStateOf(0) }

    fun siguienteRonda() {
        if (aciertosRonda >= RONDA) {
            aciertosRonda = 0
            estado.completar()
        } else {
            objetivo = items.filter { it.id != objetivo.id }.random()
            elecciones = generarOpciones(items, objetivo, opciones)
        }
    }

    fun elegir(item: ItemQuiz<T>) {
        if (item.id == objetivo.id) {
            estado.acierto()
            aciertosRonda++
            siguienteRonda()
        } else {
            estado.intento()
        }
    }

    GameShell(
        juego = juego,
        consigna = if (estado.logrado) "¡Nivel completo!" else "¿Qué es esto?",
        nota = estado.nota,
        onVolver = onVolver,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            render(objetivo.valor, true)

            Text(
                "ronda: $aciertosRonda / $RONDA",
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                color = colores.texto,
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(if (opciones <= 4) 2 else 3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(elecciones) { item ->
                    Button(
                        onClick = { elegir(item) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = colores.texto),
                    ) { Text(item.nombre) }
                }
            }
        }
    }
}

private fun <T> generarOpciones(items: List<ItemQuiz<T>>, objetivo: ItemQuiz<T>, n: Int): List<ItemQuiz<T>> {
    val distractores = items.filter { it.id != objetivo.id }.shuffled().take(n - 1)
    return (distractores + objetivo).shuffled()
}
