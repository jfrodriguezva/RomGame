package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.miambiente.app.data.EstadoVoz
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Settings
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta
import kotlinx.coroutines.launch

/**
 * Ajustes reales de sonido/voz/vibración/música — antes se agregaron el
 * fondo musical y una voz más cálida sin ninguna forma de apagarlas desde
 * la pantalla; esta pantalla cierra ese hueco.
 */
@Composable
fun AjustesScreen(onVolver: () -> Unit, onVerProgreso: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val ajustes by services.settings.settings.collectAsState(initial = Settings())
    val estadoVoz by services.speech.estado.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(Papel)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onVolver) { Text("← Volver", color = Tinta, fontWeight = FontWeight.Bold) }
        }
        Text(
            "Ajustes",
            style = MaterialTheme.typography.headlineSmall,
            color = Tinta,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        )
        Text(
            "Se guardan solos, no hace falta confirmar nada.",
            color = TextoSuave,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        // El nombre del saludo del inicio. Se guarda solo, como todo aqui.
        OutlinedTextField(
            value = ajustes.nombre,
            onValueChange = { nuevo -> scope.launch { services.settings.setNombre(nuevo.take(20)) } },
            label = { Text("¿Cómo se llama?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        )

        // La edad solo decide desde qué nivel arranca un material que nunca
        // se ha jugado; los niveles anteriores siguen abiertos.
        Text("¿Cuántos años tiene?", fontWeight = FontWeight.Bold, color = Tinta, fontSize = 15.sp)
        Text(
            "Decide el nivel inicial de cada material nuevo. Siempre se puede volver a niveles anteriores.",
            color = TextoSuave,
            fontSize = 12.sp,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(top = 6.dp, bottom = 20.dp),
        ) {
            listOf(null, 2, 3, 4, 5, 6).forEach { edad ->
                FilterChip(
                    selected = ajustes.edad == edad,
                    onClick = { scope.launch { services.settings.setEdad(edad ?: 0) } },
                    label = { Text(if (edad == null) "Sin elegir" else if (edad == 6) "6+" else "$edad") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE6A7)),
                )
            }
        }

        FilaAjuste(
            emoji = "🔊",
            titulo = "Sonido",
            descripcion = "Los efectos al acertar, fallar o ganar",
            activo = ajustes.sonido,
            onCambiar = { scope.launch { services.settings.toggleSonido() } },
        )
        FilaAjuste(
            emoji = "🗣️",
            titulo = "Voz",
            descripcion = "Que se diga en voz alta la consigna de cada material",
            activo = ajustes.voz,
            onCambiar = { scope.launch { services.settings.toggleVoz() } },
        )
        if (ajustes.voz && (estadoVoz == EstadoVoz.SIN_ESPANOL || estadoVoz == EstadoVoz.SIN_MOTOR)) {
            Text(
                if (estadoVoz == EstadoVoz.SIN_MOTOR) {
                    "⚠️ Este dispositivo no tiene un motor de voz. Instala \"Servicios de voz de Google\" para escuchar las consignas."
                } else {
                    "⚠️ No hay una voz en español instalada. Descárgala en Ajustes del sistema › Salida de texto a voz."
                },
                color = Color(0xFF9A4B1C),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
            )
        }
        FilaAjuste(
            emoji = "🎵",
            titulo = "Música de fondo",
            descripcion = "Un acorde suave distinto por cada área",
            activo = ajustes.musica,
            onCambiar = { scope.launch { services.settings.toggleMusica() } },
        )
        FilaAjuste(
            emoji = "📳",
            titulo = "Vibración",
            descripcion = "Un pulso corto al tocar y al acertar",
            activo = ajustes.vibracion,
            onCambiar = { scope.launch { services.settings.toggleVibracion() } },
        )
        FilaAjuste(
            emoji = "🌙",
            titulo = "Modo calma",
            descripcion = "Voz más lenta, música más baja, sin confeti ni sonido de error",
            activo = ajustes.calma,
            onCambiar = { scope.launch { services.settings.toggleCalma() } },
        )

        Card(
            onClick = onVerProgreso,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4E9D7)),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("📊", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Ver progreso", fontWeight = FontWeight.Bold, color = Tinta, fontSize = 15.sp)
                    Text("Estrellas, niveles y materiales más usados", color = TextoSuave, fontSize = 12.sp)
                }
                Text("›", fontSize = 22.sp, color = TextoSuave)
            }
        }
    }
}

@Composable
private fun FilaAjuste(emoji: String, titulo: String, descripcion: String, activo: Boolean, onCambiar: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.Bold, color = Tinta, fontSize = 15.sp)
                Text(descripcion, color = TextoSuave, fontSize = 12.sp)
            }
            Switch(
                checked = activo,
                onCheckedChange = { onCambiar() },
                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF8BBF6A)),
            )
        }
    }
}
