package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.materials.ItemQuiz
import com.miambiente.app.ui.materials.MaterialQuiz
import kotlin.math.cos
import kotlin.math.sin

private val HORAS = (1..9).map { h -> ItemQuiz(h, "h$h", "$h en punto") }

/** ¿Qué hora es? — patrón MaterialQuiz, con un reloj analógico real dibujado con Canvas. */
@Composable
fun RelojScreen(onVolver: () -> Unit) {
    MaterialQuiz(
        juego = buscarJuego("reloj")!!,
        items = HORAS,
        curvaOpciones = { nivel -> phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 4, 4, 4, 4)) },
        render = { hora, grande -> RelojAnalogico(hora, tamano = if (grande) 140.dp else 72.dp) },
        pregunta = "¿Qué hora es?",
        onVolver = onVolver,
    )
}

@Composable
private fun RelojAnalogico(hora: Int, tamano: androidx.compose.ui.unit.Dp) {
    // La descripción sí dice la hora: un niño que ve el reloj también
    // "lee" la hora de un vistazo, así que TalkBack debe darle la misma
    // información — no sería un juego jugable de otro modo.
    Canvas(modifier = Modifier.size(tamano).semantics { contentDescription = "Reloj marcando las $hora en punto" }) {
        val radio = size.minDimension / 2
        val centro = Offset(size.width / 2, size.height / 2)
        drawCircle(color = Color(0xFF3F342C), radius = radio, center = centro, style = Stroke(width = radio * 0.05f))
        for (i in 0 until 12) {
            val angulo = Math.toRadians((i * 30 - 90).toDouble())
            val externo = Offset(centro.x + (radio * 0.9f * cos(angulo)).toFloat(), centro.y + (radio * 0.9f * sin(angulo)).toFloat())
            val interno = Offset(centro.x + (radio * 0.78f * cos(angulo)).toFloat(), centro.y + (radio * 0.78f * sin(angulo)).toFloat())
            drawLine(Color(0xFF3F342C), interno, externo, strokeWidth = radio * 0.04f, cap = StrokeCap.Round)
        }
        val anguloHora = Math.toRadians((hora * 30 - 90).toDouble())
        drawLine(
            color = Color(0xFF3F342C),
            start = centro,
            end = Offset(centro.x + (radio * 0.5f * cos(anguloHora)).toFloat(), centro.y + (radio * 0.5f * sin(anguloHora)).toFloat()),
            strokeWidth = radio * 0.08f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color(0xFF8A5A2B),
            start = centro,
            end = Offset(centro.x, centro.y - radio * 0.75f),
            strokeWidth = radio * 0.05f,
            cap = StrokeCap.Round,
        )
        drawCircle(color = Color(0xFF3F342C), radius = radio * 0.08f, center = centro)
    }
}
