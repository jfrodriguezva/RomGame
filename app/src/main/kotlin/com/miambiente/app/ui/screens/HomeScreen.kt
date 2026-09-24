package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.CategoriaMaterial
import com.miambiente.app.model.MATERIALES_CONSOLIDADOS
import com.miambiente.app.model.MaterialConsolidado
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.juegosDe
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta

private val ACCESOS_RAPIDOS = listOf("pizarra", "xilofono")

@Composable
fun HomeScreen(
    onAbrirJuego: (String) -> Unit,
    onAjustes: () -> Unit,
    nombre: String = "",
) {
    var categoria by remember { mutableStateOf(CategoriaMaterial.LOGICA) }
    val materiales = MATERIALES_CONSOLIDADOS.filter { it.categoria == categoria }
    val modosDisponibles = MATERIALES_CONSOLIDADOS.sumOf { it.modos.size }

    Column(Modifier.fillMaxSize().background(Papel).safeDrawingPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false),
            ) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF8BBF6A)),
                    contentAlignment = Alignment.Center,
                ) { Text("🧩", fontSize = 20.sp) }
                Column {
                    Text("Hola 👋", color = TextoSuave, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        nombre.ifBlank { "RominaGame" },
                        style = MaterialTheme.typography.headlineSmall,
                        color = Tinta,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "${MATERIALES_CONSOLIDADOS.size} materiales · $modosDisponibles modos",
                        color = TextoSuave,
                        fontSize = 11.sp,
                    )
                }
            }
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).clickable(onClick = onAjustes),
                contentAlignment = Alignment.Center,
            ) { Text("⚙️", fontSize = 19.sp) }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            items(CategoriaMaterial.entries) { item ->
                val cantidad = MATERIALES_CONSOLIDADOS.count { it.categoria == item }
                FilterChip(
                    selected = categoria == item,
                    onClick = { categoria = item },
                    label = { Text("${item.emoji} ${item.titulo} ($cantidad)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFE6A7),
                        selectedLabelColor = Color(0xFF6F4E20),
                    ),
                )
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Acceso rápido", fontWeight = FontWeight.Bold, color = TextoSuave, fontSize = 12.sp)
            ACCESOS_RAPIDOS.mapNotNull(::buscarJuego).forEach { juego ->
                Card(
                    onClick = { onAbrirJuego(juego.id) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCC79B)),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("${juego.emoji} ${juego.title}", Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Color(0xFF5C472A), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(190.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(materiales, key = { it.id }) { material ->
                TarjetaMaterial(material) { onAbrirJuego("material/${material.id}") }
            }
        }
    }
}

@Composable
private fun TarjetaMaterial(material: MaterialConsolidado, onClick: () -> Unit) {
    val juegos = juegosDe(material)
    val niveles = juegos.count { !it.libre && it.area.name != "COMPANIA" } * 100
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4E9D7)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().height(148.dp),
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(material.emoji, fontSize = 28.sp)
                Text("${material.modos.size} modos", color = Color(0xFF8A5A2B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text(material.titulo, fontWeight = FontWeight.ExtraBold, color = Tinta, fontSize = 15.sp, maxLines = 1)
            Text(material.descripcion, color = TextoSuave, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (niveles > 0) {
                Text("Hasta $niveles niveles combinados", color = Color(0xFF4C7A3A), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
