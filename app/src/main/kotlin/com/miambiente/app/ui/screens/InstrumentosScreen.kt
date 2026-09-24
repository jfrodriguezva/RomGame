package com.miambiente.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.InstrumentoSonoro
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

private data class InstrumentoVisual(val sonido: InstrumentoSonoro, val nombre: String, val emoji: String)

private val INSTRUMENTOS_TOCABLES = listOf(
    InstrumentoVisual(InstrumentoSonoro.XILOFONO, "Xilófono", "🎼"),
    InstrumentoVisual(InstrumentoSonoro.PIANO, "Piano", "🎹"),
    InstrumentoVisual(InstrumentoSonoro.GUITARRA, "Guitarra", "🎸"),
    InstrumentoVisual(InstrumentoSonoro.FLAUTA, "Flauta", "🪈"),
    InstrumentoVisual(InstrumentoSonoro.TROMPETA, "Trompeta", "🎺"),
    InstrumentoVisual(InstrumentoSonoro.ACORDEON, "Acordeón", "🪗"),
    InstrumentoVisual(InstrumentoSonoro.ARPA, "Arpa", "🎶"),
)
private val NOTAS = listOf("Do", "Re", "Mi", "Fa", "Sol", "La", "Si")
private val COLORES = listOf(0xFFD9433A, 0xFFE08A3A, 0xFFE0C23C, 0xFF7DB44B, 0xFF4C7A3A, 0xFF3E7AA3, 0xFF7A4FA3)

@Composable
fun InstrumentosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    var elegido by remember { mutableStateOf(INSTRUMENTOS_TOCABLES.first()) }
    GameShell(
        juego = buscarJuego("instrumentos")!!,
        consigna = "Elige un instrumento y toca Do, Re, Mi, Fa, Sol, La y Si",
        onVolver = onVolver,
    ) {
        Column(Modifier.fillMaxSize()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(INSTRUMENTOS_TOCABLES) { instrumento ->
                    FilterChip(
                        selected = elegido == instrumento,
                        onClick = { elegido = instrumento },
                        label = { Text("${instrumento.emoji} ${instrumento.nombre}") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE6A7)),
                    )
                }
            }
            Text(
                "${elegido.emoji} ${elegido.nombre}",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
            )
            Row(
                Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                NOTAS.forEachIndexed { indice, nota ->
                    TeclaInstrumento(nota, Color(COLORES[indice]), Modifier.weight(1f).fillMaxHeight()) {
                        services.sound.tocarInstrumento(elegido.sonido, indice)
                        services.haptics.vibrar(Patron.TOQUE)
                    }
                }
            }
        }
    }
}

@Composable
private fun TeclaInstrumento(nota: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val pulsada by source.collectIsPressedAsState()
    val escala by animateFloatAsState(if (pulsada) .94f else 1f, label = "tecla-instrumento")
    Box(
        modifier = modifier
            .scale(escala)
            .shadow(if (pulsada) 1.dp else 5.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(if (pulsada) color.copy(alpha = .7f) else color)
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.size(18.dp).align(Alignment.TopCenter).padding(top = 4.dp)
                .clip(RoundedCornerShape(9.dp)).background(Color.White.copy(alpha = .4f)),
        )
        Text(nota, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
    }
}
