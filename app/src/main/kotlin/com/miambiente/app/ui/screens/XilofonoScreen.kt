package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.InstrumentoSonoro
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

private data class Barra(val nombre: String, val color: Color)
private data class OpcionInstrumento(val sonido: InstrumentoSonoro, val nombre: String, val emoji: String)

private val INSTRUMENTOS = listOf(
    OpcionInstrumento(InstrumentoSonoro.XILOFONO, "Xilófono", "🎼"),
    OpcionInstrumento(InstrumentoSonoro.PIANO, "Piano", "🎹"),
    OpcionInstrumento(InstrumentoSonoro.GUITARRA, "Guitarra", "🎸"),
    OpcionInstrumento(InstrumentoSonoro.FLAUTA, "Flauta", "🪈"),
    OpcionInstrumento(InstrumentoSonoro.TROMPETA, "Trompeta", "🎺"),
    OpcionInstrumento(InstrumentoSonoro.ACORDEON, "Acordeón", "🪗"),
    OpcionInstrumento(InstrumentoSonoro.ARPA, "Arpa", "🎶"),
)

private val BARRAS = listOf(
    Barra("Do", Color(0xFFD9433A)),
    Barra("Re", Color(0xFFE08A3A)),
    Barra("Mi", Color(0xFFE0C23C)),
    Barra("Fa", Color(0xFF7DB44B)),
    Barra("Sol", Color(0xFF4C7A3A)),
    Barra("La", Color(0xFF3E7AA3)),
    Barra("Si", Color(0xFF7A4FA3)),
)

/**
 * Xilófono — cada barra suena una nota real (sintetizada en `SoundPlayer`,
 * escala pentatónica, nunca desafina) en vez del beep DTMF anterior. El
 * visual imita un xilófono real: barras cada vez más cortas de izquierda a
 * derecha (a menor longitud, tono más agudo — la misma física de un
 * instrumento real), con un círculo resonador y feedback al presionar.
 */
@Composable
fun XilofonoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("xilofono")!!
    var instrumento by remember { mutableStateOf(INSTRUMENTOS.first()) }

    GameShell(juego = juego, consigna = "Elige un instrumento y toca Do, Re, Mi, Fa, Sol, La y Si", onVolver = onVolver) {
        Column(Modifier.fillMaxSize()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            ) {
                items(INSTRUMENTOS) { opcion ->
                    FilterChip(
                        selected = instrumento == opcion,
                        onClick = { instrumento = opcion },
                        label = { Text("${opcion.emoji} ${opcion.nombre}") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE6A7)),
                    )
                }
            }
            VistaInstrumento(
                instrumento = instrumento.sonido,
                onTocar = { indice ->
                    services.sound.tocarInstrumento(instrumento.sonido, indice)
                    services.haptics.vibrar(Patron.TOQUE)
                },
            )
        }
    }
}

@Composable
private fun VistaInstrumento(instrumento: InstrumentoSonoro, onTocar: (Int) -> Unit) {
    when (instrumento) {
        InstrumentoSonoro.XILOFONO -> Row(
            Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            BARRAS.forEachIndexed { i, barra ->
                BarraXilofono(barra, 1f - i * 0.07f, Modifier.weight(1f).fillMaxHeight()) { onTocar(i) }
            }
        }
        InstrumentoSonoro.PIANO -> PianoVisual(onTocar)
        else -> InstrumentoIlustrado(instrumento, onTocar)
    }
}

@Composable
private fun PianoVisual(onTocar: (Int) -> Unit) {
    Row(
        Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 14.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(Color(0xFF24211F))
            .padding(8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(3.dp),
    ) {
        BARRAS.forEachIndexed { indice, barra ->
            val source = remember { MutableInteractionSource() }
            val pulsada by source.collectIsPressedAsState()
            Box(
                Modifier.weight(1f).fillMaxHeight()
                    .clip(RoundedCornerShape(bottomStart = 9.dp, bottomEnd = 9.dp))
                    .background(if (pulsada) Color(0xFFFFE6A7) else Color.White)
                    .border(1.dp, Color(0xFFB8B0A8), RoundedCornerShape(bottomStart = 9.dp, bottomEnd = 9.dp))
                    .clickable(interactionSource = source, indication = null) { onTocar(indice) },
                contentAlignment = Alignment.BottomCenter,
            ) {
                if (indice in setOf(0, 1, 3, 4, 5)) {
                    Box(Modifier.fillMaxWidth(.48f).fillMaxHeight(.55f).align(Alignment.TopEnd).background(Color(0xFF211F20)))
                }
                Text(barra.nombre, color = Color(0xFF332F2C), fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 18.dp))
            }
        }
    }
}

@Composable
private fun InstrumentoIlustrado(instrumento: InstrumentoSonoro, onTocar: (Int) -> Unit) {
    val fondo = when (instrumento) {
        InstrumentoSonoro.GUITARRA -> Color(0xFFFFE0B2)
        InstrumentoSonoro.FLAUTA -> Color(0xFFE9EEF1)
        InstrumentoSonoro.TROMPETA -> Color(0xFFFFF0B5)
        InstrumentoSonoro.ACORDEON -> Color(0xFFF4D6D6)
        InstrumentoSonoro.ARPA -> Color(0xFFFFE7C2)
        else -> Color.White
    }
    Box(
        Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 14.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp)).background(fondo),
    ) {
        Canvas(Modifier.fillMaxSize().padding(28.dp)) {
            when (instrumento) {
                InstrumentoSonoro.GUITARRA -> {
                    drawCircle(Color(0xFFB96F2B), size.minDimension * .34f, Offset(size.width * .3f, size.height * .52f))
                    drawCircle(Color(0xFF7C421D), size.minDimension * .11f, Offset(size.width * .3f, size.height * .52f))
                    drawRoundRect(Color(0xFF8D5524), Offset(size.width * .3f, size.height * .42f), androidx.compose.ui.geometry.Size(size.width * .62f, size.height * .2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f))
                    repeat(6) { n -> drawLine(Color(0xFFFFE6C0), Offset(size.width * .28f, size.height * (.45f + n * .025f)), Offset(size.width * .94f, size.height * (.45f + n * .025f)), 2f) }
                }
                InstrumentoSonoro.FLAUTA -> {
                    drawLine(Color(0xFF9CAAB2), Offset(size.width * .08f, size.height * .48f), Offset(size.width * .92f, size.height * .48f), size.height * .18f)
                    drawCircle(Color(0xFF667780), size.height * .12f, Offset(size.width * .1f, size.height * .48f), style = Stroke(8f))
                    repeat(7) { n -> drawCircle(Color(0xFF42525A), 10f, Offset(size.width * (.28f + n * .075f), size.height * .48f)) }
                }
                InstrumentoSonoro.TROMPETA -> {
                    drawCircle(Color(0xFFD69E20), size.height * .25f, Offset(size.width * .82f, size.height * .5f), style = Stroke(size.height * .12f))
                    drawLine(Color(0xFFE2AF2E), Offset(size.width * .15f, size.height * .5f), Offset(size.width * .78f, size.height * .5f), size.height * .13f)
                    repeat(3) { n -> drawRoundRect(Color(0xFF8F6A18), Offset(size.width * (.4f + n * .1f), size.height * .25f), androidx.compose.ui.geometry.Size(size.width * .045f, size.height * .3f), androidx.compose.ui.geometry.CornerRadius(9f)) }
                }
                InstrumentoSonoro.ACORDEON -> {
                    drawRoundRect(Color(0xFFB73535), Offset(size.width * .08f, size.height * .2f), androidx.compose.ui.geometry.Size(size.width * .22f, size.height * .6f), androidx.compose.ui.geometry.CornerRadius(24f))
                    drawRoundRect(Color(0xFFB73535), Offset(size.width * .7f, size.height * .2f), androidx.compose.ui.geometry.Size(size.width * .22f, size.height * .6f), androidx.compose.ui.geometry.CornerRadius(24f))
                    repeat(9) { n -> drawLine(if (n % 2 == 0) Color(0xFF6D3030) else Color(0xFFE9B8A2), Offset(size.width * (.31f + n * .043f), size.height * .25f), Offset(size.width * (.31f + n * .043f), size.height * .75f), 7f) }
                }
                InstrumentoSonoro.ARPA -> {
                    val marco = Path().apply { moveTo(size.width * .18f, size.height * .82f); lineTo(size.width * .38f, size.height * .12f); lineTo(size.width * .84f, size.height * .82f); close() }
                    drawPath(marco, Color(0xFFC38A32), style = Stroke(size.height * .06f))
                    repeat(7) { n ->
                        val x = size.width * (.36f + n * .065f)
                        drawLine(BARRAS[n].color, Offset(x, size.height * (.18f + n * .02f)), Offset(x, size.height * .78f), 5f)
                    }
                }
                else -> Unit
            }
        }
        Row(
            Modifier.fillMaxWidth().fillMaxHeight(.28f).align(Alignment.BottomCenter).padding(10.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(7.dp),
        ) {
            BARRAS.forEachIndexed { indice, barra ->
                val source = remember { MutableInteractionSource() }
                val pulsada by source.collectIsPressedAsState()
                Box(
                    Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(12.dp))
                        .background(if (pulsada) barra.color else barra.color.copy(alpha = .82f))
                        .clickable(interactionSource = source, indication = null) { onTocar(indice) },
                    contentAlignment = Alignment.Center,
                ) { Text(barra.nombre, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp) }
            }
        }
    }
}

@Composable
private fun BarraXilofono(barra: Barra, altoFraccion: Float, modifier: Modifier, onTocar: () -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.35f),
        label = "tecla",
    )

    Box(modifier = modifier.fillMaxHeight(), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(altoFraccion.coerceIn(0.4f, 1f))
                .shadow(if (presionado) 1.dp else 6.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(if (presionado) barra.color.copy(alpha = 0.75f) else barra.color)
                .clickable(interactionSource = interaccion, indication = null) { onTocar() },
            contentAlignment = Alignment.Center,
        ) {
            // Los dos agujeros de resonancia de una barra real de xilófono,
            // no solo un rectángulo de color plano.
            Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)) {
                val radio = size.width * 0.14f
                drawCircle(Color.White.copy(alpha = 0.35f), radius = radio, center = Offset(size.width * 0.3f, size.height * 0.2f), style = Stroke(width = 3f))
                drawCircle(Color.White.copy(alpha = 0.35f), radius = radio, center = Offset(size.width * 0.7f, size.height * 0.2f), style = Stroke(width = 3f))
            }
            Text(
                barra.nombre,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }
    }
}
