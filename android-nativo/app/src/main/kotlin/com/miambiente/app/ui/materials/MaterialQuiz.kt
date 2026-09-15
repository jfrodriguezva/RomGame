package com.miambiente.app.ui.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.GameDef
import com.miambiente.app.model.etapaDe
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell

/** Un elemento de nomenclatura: lo que se ve + su nombre + su id único. */
data class ItemQuiz<T>(val valor: T, val id: String, val nombre: String)

private const val RONDA = 5

private fun periodoPara(nivel: Int): Int = when {
    etapaDe(nivel) <= 1 -> 1
    etapaDe(nivel) <= 2 -> 2
    else -> 3
}

/**
 * Lección de tres periodos de Montessori, completa — puerto real del
 * patrón MaterialQuiz de la web (components/MaterialQuiz.tsx), no una
 * simplificación:
 * - Periodo 1 ("esto es..."): el material PRESENTA, no examina. Se
 *   muestra el objetivo con su nombre a la vista; el niño solo confirma.
 * - Periodo 2 ("muéstrame..."): varios objetos a la vista, SIN nombre
 *   escrito; hay que tocar el correcto — es reconocer, no nombrar.
 * - Periodo 3 ("¿qué es esto?"): un solo objetivo a la vista, se elige
 *   su nombre entre varios — es evocar de memoria, el más difícil.
 * El periodo avanza solo según la etapa del nivel (`etapaDe`, 10 etapas
 * de 10 niveles); no es una opción manual.
 */
@Composable
fun <T> MaterialQuiz(
    juego: GameDef,
    items: List<ItemQuiz<T>>,
    curvaOpciones: (nivel: Int) -> Int,
    render: @Composable (T, grande: Boolean) -> Unit,
    pregunta: String = "¿Qué es esto?",
    onVolver: () -> Unit,
) {
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val periodo = periodoPara(estado.nivel)

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

    fun acertar() {
        estado.acierto()
        aciertosRonda++
        siguienteRonda()
    }

    val consigna = when {
        estado.logrado -> "¡Nivel completo!"
        periodo == 1 -> "Esto es: ${objetivo.nombre}"
        periodo == 2 -> "Toca: ${objetivo.nombre}"
        else -> pregunta
    }

    GameShell(
        juego = juego,
        consigna = consigna,
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (periodo) {
                1 -> {
                    render(objetivo.valor, true)
                    Text(
                        objetivo.nombre,
                        color = colores.texto,
                        modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                    )
                    Button(onClick = ::acertar) { Text("¡Ya sé! Siguiente") }
                }
                2 -> {
                    Text(
                        "ronda: $aciertosRonda / $RONDA",
                        modifier = Modifier.padding(bottom = 24.dp),
                        color = colores.texto,
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (opciones <= 4) 2 else 3),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(elecciones) { item ->
                            val esObjetivo = item.id == objetivo.id
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .clickable { if (esObjetivo) acertar() else estado.intento() }
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) { render(item.valor, false) }
                        }
                    }
                }
                else -> {
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
                                onClick = { if (item.id == objetivo.id) acertar() else estado.intento() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = colores.texto),
                            ) { Text(item.nombre) }
                        }
                    }
                }
            }
        }
    }
}

private fun <T> generarOpciones(items: List<ItemQuiz<T>>, objetivo: ItemQuiz<T>, n: Int): List<ItemQuiz<T>> {
    val distractores = items.filter { it.id != objetivo.id }.shuffled().take(n - 1)
    return (distractores + objetivo).shuffled()
}
