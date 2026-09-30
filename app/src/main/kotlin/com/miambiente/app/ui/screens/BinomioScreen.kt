package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.dificultadBinomio
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState
import kotlin.random.Random

private val COLORES = listOf(Color(0xFFD9433A), Color(0xFF3E7AA3), Color(0xFFE0C23C), Color(0xFF4C7A3A))

/**
 * El cubo del binomio — arma el patrón de colores (base sensorial del
 * álgebra). La cuadrícula (2×2 a 3×3) y los colores en juego salen del
 * nivel (`dificultadBinomio`).
 */
@Composable
fun BinomioScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("binomio")!!
    val estado = rememberMaterialState(juego)
    val coloresArea = coloresDe(juego.area)
    val d = remember(estado.nivel) { dificultadBinomio(estado.nivel) }
    val paleta = COLORES.take(d.colores)
    val total = d.lado * d.lado

    val objetivo = remember(estado.nivel) { val rnd = Random(estado.nivel); List(total) { paleta.random(rnd) } }
    // Arranca distinto del objetivo para que siempre haya algo que hacer.
    var actual by remember(estado.nivel) {
        val rnd = Random(estado.nivel + 1)
        mutableStateOf(List(total) { i -> paleta.filter { it != objetivo[i] }.random(rnd) })
    }

    fun tocar(i: Int) {
        if (estado.logrado) return
        val siguienteColor = paleta[(paleta.indexOf(actual[i]) + 1) % paleta.size]
        actual = actual.toMutableList().also { it[i] = siguienteColor }
        services.sound.tocar(Efecto.CLICK)
        if (actual == objetivo) estado.completar()
    }

    GameShell(
        juego = juego,
        consigna = if (estado.logrado) "¡Igual que el patrón!" else "Toca cada cuadro hasta igualar el patrón",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(coloresArea, onClick = estado::siguiente) }
        } else null,
    ) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Patrón a copiar", modifier = Modifier.padding(bottom = 8.dp))
            Cuadricula(objetivo, d.lado, onTocar = null)
            Text("Tu cubo", modifier = Modifier.padding(top = 32.dp, bottom = 8.dp))
            Cuadricula(actual, d.lado, onTocar = ::tocar)
        }
    }
}

@Composable
private fun Cuadricula(colores: List<Color>, lado: Int, onTocar: ((Int) -> Unit)?) {
    Column(
        modifier = Modifier.shadow(4.dp, RoundedCornerShape(10.dp)).clip(RoundedCornerShape(10.dp)).background(Color(0xFFEFE7DA)).padding(4.dp),
    ) {
        for (fila in 0 until lado) {
            Row {
                for (col in 0 until lado) {
                    val i = fila * lado + col
                    val interaccion = remember { MutableInteractionSource() }
                    val presionado by interaccion.collectIsPressedAsState()
                    Box(
                        Modifier
                            .size(56.dp)
                            .padding(2.dp)
                            .scale(if (presionado) 0.92f else 1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colores[i])
                            .then(
                                if (onTocar != null) {
                                    Modifier.clickable(interactionSource = interaccion, indication = null) { onTocar(i) }
                                } else {
                                    Modifier
                                },
                            ),
                    )
                }
            }
        }
    }
}
