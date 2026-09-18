package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class Parte(val id: String, val nombre: String, val dx: Int, val dy: Int, val tamano: Int)

private val PARTES = listOf(
    Parte("ojo-izq", "el ojo", -45, -25, 26),
    Parte("ojo-der", "el ojo", 45, -25, 26),
    Parte("nariz", "la nariz", 0, 10, 22),
    Parte("boca", "la boca", 0, 55, 60),
)

/**
 * Toca la cara — material independiente: tocar directo sobre el dibujo,
 * no elegir de una lista.
 *
 * Bug real reportado ("toca la cara no hace nada"): "el ojo" son en
 * realidad DOS partes distintas (ojo-izq y ojo-der) con el mismo nombre
 * mostrado. Antes `tocar(id)` comparaba por `id` exacto contra el
 * objetivo elegido al azar — así que tocar el ojo "equivocado" (el otro,
 * no el que salió sorteado) contaba como error, sin ningún aviso visual
 * de que pasó algo. Como además la consigna ("Toca el ojo") no distingue
 * cuál de los dos, y al acertar el objetivo podía volver a caer en el
 * OTRO ojo (mismo texto en pantalla, nada visible cambió), la experiencia
 * completa se sentía como "no hace nada" aunque técnicamente sí
 * respondía. Ahora se compara por NOMBRE (tocar cualquiera de los dos
 * ojos cuenta si el objetivo es "el ojo"), y un toque equivocado sí
 * muestra un aviso real en vez de fallar en silencio.
 */
@Composable
fun CaraScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("cara")!!

    var objetivo by remember { mutableStateOf(PARTES.random()) }
    var error by remember { mutableStateOf<String?>(null) }
    var acertada by remember { mutableStateOf<String?>(null) }

    fun tocar(parte: Parte) {
        if (parte.nombre == objetivo.nombre) {
            services.sound.tocar(Efecto.CORRECT)
            services.haptics.vibrar(Patron.ACIERTO)
            error = null
            acertada = parte.id
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch {
                delay(700)
                objetivo = PARTES.filter { it.nombre != objetivo.nombre }.random()
                acertada = null
            }
        } else {
            services.sound.tocar(Efecto.WRONG)
            error = "Ese no es. Intenta otra vez"
            scope.launch { delay(900); error = null }
        }
    }

    GameShell(juego = juego, consigna = "Toca ${objetivo.nombre}", nota = error, onVolver = onVolver) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                // Orejas y pelo: antes la cara era un círculo liso, ahora
                // se lee como una cara de verdad de un vistazo.
                Box(Modifier.offset(x = (-104).dp).size(38.dp).clip(CircleShape).background(Color(0xFFF3DBE3)).shadow(1.dp, CircleShape))
                Box(Modifier.offset(x = 104.dp).size(38.dp).clip(CircleShape).background(Color(0xFFF3DBE3)).shadow(1.dp, CircleShape))
                Box(Modifier.size(220.dp).shadow(4.dp, CircleShape).clip(CircleShape).background(Color(0xFFF3DBE3))) {
                    Box(Modifier.fillMaxSize()) {
                        Box(Modifier.offset(y = (-92).dp).size(width = 210.dp, height = 70.dp).align(Alignment.TopCenter).clip(CircleShape).background(Color(0xFF6B4A34)))
                    }
                    PARTES.forEach { parte ->
                        // clickable (no pointerInput+detectTapGestures) para que
                        // TalkBack pueda enfocar y activar cada parte con doble
                        // toque — un gesto crudo no aparece en el árbol de
                        // accesibilidad aunque se le agregue contentDescription.
                        if (parte.id == "boca") {
                            Canvas(
                                modifier = Modifier
                                    .size(parte.tamano.dp)
                                    .align(Alignment.Center)
                                    .offset(x = parte.dx.dp, y = parte.dy.dp)
                                    .semantics { contentDescription = parte.nombre }
                                    .clickable { tocar(parte) },
                            ) {
                                drawArc(
                                    color = Color(0xFFA23B3B),
                                    startAngle = 20f,
                                    sweepAngle = 140f,
                                    useCenter = false,
                                    size = Size(size.width, size.height * 1.4f),
                                    topLeft = androidx.compose.ui.geometry.Offset(0f, -size.height * 0.3f),
                                    style = Stroke(width = size.height * 0.22f, cap = StrokeCap.Round),
                                )
                            }
                        } else {
                            val pulso by animateFloatAsState(if (acertada == parte.id) 1.3f else 1f, label = "pulsoParte")
                            Box(
                                modifier = Modifier
                                    .size(parte.tamano.dp)
                                    .align(Alignment.Center)
                                    .offset(x = parte.dx.dp, y = parte.dy.dp)
                                    .graphicsLayer { scaleX = pulso; scaleY = pulso }
                                    .clip(CircleShape)
                                    .background(Color(0xFF3F342C))
                                    .semantics { contentDescription = parte.nombre }
                                    .clickable { tocar(parte) },
                            )
                        }
                    }
                }
            }
        }
    }
}
