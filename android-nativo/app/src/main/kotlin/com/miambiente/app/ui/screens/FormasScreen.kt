package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.launch

private data class Figura(val id: String, val nombre: String)

private val FIGURAS = listOf(
    Figura("circulo", "Círculo"),
    Figura("cuadrado", "Cuadrado"),
    Figura("triangulo", "Triángulo"),
)

/**
 * Gabinete de figuras — nomenclatura por nombre, patrón MaterialQuiz.
 * Con solo un material de este patrón todavía, se deja la lógica local en
 * vez de extraer un composable genérico: en la versión web, los 4
 * componentes compartidos nacieron de ver el patrón repetirse muchas
 * veces, no al revés. Cuando haya un segundo material Quiz nativo, ahí
 * corresponde extraerlo.
 */
@Composable
fun FormasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("formas")!!

    var objetivo by remember { mutableStateOf(FIGURAS.random()) }
    var nota by remember { mutableStateOf<String?>(null) }
    var logrado by remember { mutableStateOf(0) }

    fun elegir(figura: Figura) {
        if (figura.id == objetivo.id) {
            services.sound.tocar(Efecto.CORRECT)
            services.haptics.vibrar(Patron.ACIERTO)
            nota = "¡Muy bien!"
            logrado++
            scope.launch { services.progress.completarNivel(juego.id, logrado) }
            objetivo = FIGURAS.filter { it.id != figura.id }.random()
        } else {
            services.sound.tocar(Efecto.WRONG)
            services.haptics.vibrar(Patron.ERROR)
            nota = "Esa todavía no. Busca otra"
        }
    }

    GameShell(
        juego = juego,
        consigna = "¿Qué figura es?",
        nota = nota,
        onVolver = onVolver,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FormaVisual(objetivo.id, tamano = 120.dp)

            Row(
                modifier = Modifier.padding(top = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FIGURAS.forEach { f ->
                    Button(
                        onClick = { elegir(f) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF2F5C82),
                        ),
                    ) { Text(f.nombre) }
                }
            }
        }
    }
}

@Composable
private fun FormaVisual(id: String, tamano: Dp) {
    when (id) {
        "circulo" -> Box(modifier = Modifier.size(tamano).clip(CircleShape).background(Color(0xFFF3DBE3)))
        "cuadrado" -> Box(modifier = Modifier.size(tamano).clip(RoundedCornerShape(4.dp)).background(Color(0xFFF3DBE3)))
        else -> androidx.compose.foundation.Canvas(modifier = Modifier.size(tamano)) {
            val ancho = size.width
            val alto = size.height
            val camino = Path().apply {
                moveTo(ancho / 2, 0f)
                lineTo(ancho, alto)
                lineTo(0f, alto)
                close()
            }
            drawPath(camino, color = Color(0xFFF3DBE3))
        }
    }
}
