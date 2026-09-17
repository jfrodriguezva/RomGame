package com.miambiente.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.GameProgress
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.CATALOGO
import com.miambiente.app.model.GameDef
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.theme.Area
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta
import com.miambiente.app.theme.coloresDe

private val IDS_DESTACADOS = listOf("pizarra", "xilofono", "collage", "colorear")

/**
 * Menú principal, equivalente nativo de app/page.tsx: acceso rápido a las
 * herramientas libres + selector de área + cuadrícula de materiales,
 * filtrados por edad mínima.
 */
@Composable
fun HomeScreen(edad: Int, onAbrirJuego: (String) -> Unit, onCambiarEdad: () -> Unit, onAjustes: () -> Unit) {
    var areaActiva by remember { mutableStateOf<Area?>(Area.entries.first()) }
    val disponibles = CATALOGO.filter { edad >= it.edadMinima }
    val destacados = IDS_DESTACADOS.mapNotNull { buscarJuego(it) }.filter { edad >= it.edadMinima }

    Column(modifier = Modifier.fillMaxSize().background(Papel).safeDrawingPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // `weight(1f)` + una sola línea con "…": en un celular angosto,
            // antes el título largo podía empujar los botones de la derecha
            // (edad, ajustes) fuera de la pantalla en vez de acortarse él.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false),
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF8BBF6A)),
                    contentAlignment = Alignment.Center,
                ) { Text("🧩", fontSize = 20.sp) }
                Column {
                    Text(
                        "RominaGame",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Tinta,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "${disponibles.size} materiales para $edad años",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSuave,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onCambiarEdad) {
                    Text("$edad años ✏️")
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onAjustes() },
                    contentAlignment = Alignment.Center,
                ) { Text("⚙️", fontSize = 18.sp) }
            }
        }

        if (destacados.isNotEmpty()) {
            // La pizarra y compañía son herramientas de uso libre y frecuente
            // (sin niveles, sin "terminar") — por eso viven aparte, siempre a
            // la mano arriba de todo, sin que el filtro de área las esconda.
            Text(
                "✨ Acceso rápido",
                style = MaterialTheme.typography.labelLarge,
                color = TextoSuave,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 6.dp),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 14.dp),
            ) {
                items(destacados) { juego -> TarjetaDestacada(juego, onClick = { onAbrirJuego(juego.id) }) }
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(Area.entries.toList()) { area ->
                val cantidad = disponibles.count { it.area == area }
                FilterChip(
                    selected = areaActiva == area,
                    onClick = { areaActiva = area },
                    label = { Text("${area.emoji} ${area.label} ($cantidad)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = coloresDe(area).fondo,
                        selectedLabelColor = coloresDe(area).texto,
                    ),
                )
            }
        }

        val lista = disponibles.filter { areaActiva == null || it.area == areaActiva }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(lista, key = { it.id }) { juego -> TarjetaJuego(juego, onClick = { onAbrirJuego(juego.id) }) }
        }
    }
}

@Composable
private fun TarjetaDestacada(juego: GameDef, onClick: () -> Unit) {
    val colores = coloresDe(juego.area)
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()

    Card(
        onClick = onClick,
        interactionSource = interaccion,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colores.acento),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.height(72.dp).scale(if (presionado) 0.96f else 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(juego.emoji, fontSize = 26.sp)
            Text(juego.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
        }
    }
}

@Composable
private fun TarjetaJuego(juego: GameDef, onClick: () -> Unit) {
    val colores = coloresDe(juego.area)
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()

    // Insignia de estrellas: progreso real guardado, no decorativo. Solo
    // los ~10-12 materiales visibles a la vez se componen (LazyVerticalGrid
    // es perezoso), así que esto no dispara 98 suscripciones simultáneas.
    val services = LocalServices.current
    val progreso by services.progress.progresoDe(juego.id).collectAsState(initial = GameProgress())

    Card(
        onClick = onClick,
        interactionSource = interaccion,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colores.fondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
        modifier = Modifier
            .height(128.dp)
            .animateContentSize()
            .scale(if (presionado) 0.95f else 1f),
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier.size(34.dp).clip(CircleShape).background(colores.acento.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center,
                ) { Text(juego.emoji, fontSize = 17.sp) }
                if (!juego.libre && progreso.estrellas > 0) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0C23C).copy(alpha = 0.85f)),
                    ) {
                        Text(
                            "⭐ ${progreso.estrellas}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3F342C),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        )
                    }
                }
            }
            Text(
                juego.title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colores.texto,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                juego.description,
                fontSize = 10.sp,
                color = TextoSuave,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
