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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.GameProgress
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.CATALOGO
import com.miambiente.app.model.CategoriaMaterial
import com.miambiente.app.model.MATERIALES_CONSOLIDADOS
import com.miambiente.app.model.juegosDe
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta

/**
 * Resumen para el adulto: todo lo que `ProgressStore` ya guardaba (estrellas,
 * niveles completados, veces abierto) y que antes no se veía en ninguna
 * parte. Agrupado por categoría y material, igual que el inicio.
 */
@Composable
fun ProgresoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val ids = remember { CATALOGO.map { it.id } }
    val resumen = remember(services) { services.progress.resumen(ids) }
    val progreso by resumen.collectAsState(initial = emptyMap())

    val estrellas = progreso.values.sumOf { it.estrellas }
    val niveles = progreso.values.sumOf { it.completados.size }
    val usados = progreso.values.count { it.vecesJugado > 0 }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Papel).safeDrawingPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            TextButton(onClick = onVolver) { Text("← Volver", color = Tinta, fontWeight = FontWeight.Bold) }
            Text(
                "Progreso",
                style = MaterialTheme.typography.headlineSmall,
                color = Tinta,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Cifra("⭐", "$estrellas", "estrellas", Modifier.weight(1f))
                Cifra("✅", "$niveles", "niveles", Modifier.weight(1f))
                Cifra("🧩", "$usados/${ids.size}", "modos usados", Modifier.weight(1f))
            }
        }
        CategoriaMaterial.entries.forEach { categoria ->
            item(key = categoria.name) {
                Text(
                    "${categoria.emoji} ${categoria.titulo}",
                    fontWeight = FontWeight.ExtraBold,
                    color = Tinta,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
            items(MATERIALES_CONSOLIDADOS.filter { it.categoria == categoria }, key = { it.id }) { material ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("${material.emoji} ${material.titulo}", fontWeight = FontWeight.Bold, color = Tinta, fontSize = 14.sp)
                        juegosDe(material).forEach { juego ->
                            FilaModo("${juego.emoji} ${juego.title}", progreso[juego.id] ?: GameProgress())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Cifra(emoji: String, valor: String, etiqueta: String, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4E9D7)),
        modifier = modifier,
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Text(valor, fontWeight = FontWeight.ExtraBold, color = Tinta, fontSize = 18.sp)
            Text(etiqueta, color = TextoSuave, fontSize = 11.sp)
        }
    }
}

@Composable
private fun FilaModo(titulo: String, p: GameProgress) {
    Row(
        Modifier.fillMaxWidth().padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            titulo,
            color = if (p.vecesJugado > 0) Tinta else TextoSuave,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            if (p.vecesJugado == 0) "sin abrir" else "⭐ ${p.estrellas} · ✅ ${p.completados.size} · ${p.vecesJugado}×",
            color = TextoSuave,
            fontSize = 11.sp,
        )
    }
}
