package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val COLORES = listOf(Color(0xFFD9433A), Color(0xFF3E7AA3), Color(0xFFE0C23C), Color(0xFF4C7A3A))

/** El cubo del binomio — arma el patrón de colores (base sensorial del álgebra). */
@Composable
fun BinomioScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("binomio")!!

    var objetivo by remember { mutableStateOf(List(4) { COLORES.random() }) }
    var actual by remember(objetivo) { mutableStateOf(List(4) { COLORES.random() }) }

    fun tocar(i: Int) {
        val siguienteColor = COLORES[(COLORES.indexOf(actual[i]) + 1) % COLORES.size]
        actual = actual.toMutableList().also { it[i] = siguienteColor }
        services.sound.tocar(Efecto.CLICK)
        if (actual == objetivo) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch { delay(1200); objetivo = List(4) { COLORES.random() } }
        }
    }

    GameShell(juego = juego, consigna = "Toca cada cuadro hasta igualar el patrón", onVolver = onVolver) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Patrón a copiar", modifier = Modifier.padding(bottom = 8.dp))
            Cuadricula(objetivo, onTocar = null)
            Text("Tu cubo", modifier = Modifier.padding(top = 32.dp, bottom = 8.dp))
            Cuadricula(actual, onTocar = ::tocar)
        }
    }
}

@Composable
private fun Cuadricula(colores: List<Color>, onTocar: ((Int) -> Unit)?) {
    Column {
        for (fila in 0..1) {
            Row {
                for (col in 0..1) {
                    val i = fila * 2 + col
                    Box(
                        Modifier
                            .size(56.dp)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colores[i])
                            .then(if (onTocar != null) Modifier.clickable { onTocar(i) } else Modifier),
                    )
                }
            }
        }
    }
}
