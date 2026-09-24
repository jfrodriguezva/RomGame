package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.GameProgress
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.GameDef
import com.miambiente.app.model.buscarMaterialConsolidado
import com.miambiente.app.model.juegosDe
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta

@Composable
fun MaterialConsolidadoScreen(
    materialId: String,
    onAbrirModo: (String) -> Unit,
    onVolver: () -> Unit,
) {
    val material = buscarMaterialConsolidado(materialId) ?: return
    val juegos = juegosDe(material)
    Column(Modifier.fillMaxSize().background(Papel).safeDrawingPadding()) {
        Card(
            onClick = onVolver,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.padding(start = 10.dp, top = 4.dp),
        ) { Text("← Volver", Modifier.padding(10.dp), color = Color(0xFF7A6234), fontWeight = FontWeight.Bold) }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(material.emoji, fontSize = 38.sp)
            Column {
                Text(material.titulo, color = Tinta, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Text(material.descripcion, color = TextoSuave, fontSize = 13.sp)
                Text("Elige un modo · cada uno conserva su progreso", color = Color(0xFF4C7A3A), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(180.dp),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(juegos, key = { it.id }) { juego ->
                TarjetaModo(juego) { onAbrirModo(juego.id) }
            }
        }
    }
}

@Composable
private fun TarjetaModo(juego: GameDef, onClick: () -> Unit) {
    val progreso by LocalServices.current.progress.progresoDe(juego.id).collectAsState(initial = GameProgress())
    val tipo = when {
        juego.libre -> "Modo libre"
        juego.area.name == "COMPANIA" -> "Partida completa"
        else -> "100 niveles progresivos"
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(juego.emoji, fontSize = 27.sp)
                if (progreso.estrellas > 0) Text("⭐ ${progreso.estrellas}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text(juego.title, color = Tinta, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(juego.description, color = TextoSuave, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(tipo, color = Color(0xFF4C7A3A), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
        }
    }
}
