package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.CATALOGO
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.Area
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta
import com.miambiente.app.theme.coloresDe

/**
 * Menú principal, equivalente nativo de app/page.tsx: selector de área +
 * cuadrícula de materiales, filtrados por edad mínima.
 */
@Composable
fun HomeScreen(edad: Int, onAbrirJuego: (String) -> Unit, onCambiarEdad: () -> Unit) {
    var areaActiva by remember { mutableStateOf<Area?>(Area.entries.first()) }
    val disponibles = CATALOGO.filter { edad >= it.edadMinima }

    Column(modifier = Modifier.fillMaxSize().background(Papel)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Mi ambiente", style = MaterialTheme.typography.headlineMedium, color = Tinta)
                Text(
                    "${disponibles.size} materiales para $edad años",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSuave,
                )
            }
            OutlinedButton(onClick = onCambiarEdad) {
                Text("$edad años ✏️")
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
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(lista) { juego -> TarjetaJuego(juego, onClick = { onAbrirJuego(juego.id) }) }
        }
    }
}

@Composable
private fun TarjetaJuego(juego: GameDef, onClick: () -> Unit) {
    val colores = coloresDe(juego.area)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colores.fondo),
        modifier = Modifier.aspectRatio(1f),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(juego.emoji, fontSize = 32.sp)
            Text(
                juego.title,
                fontWeight = FontWeight.ExtraBold,
                color = colores.texto,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(juego.description, fontSize = 12.sp, color = TextoSuave)
        }
    }
}
