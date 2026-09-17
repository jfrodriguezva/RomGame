package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
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
fun AjustesScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val ajustes by services.settings.settings.collectAsState(initial = Settings())

    Column(Modifier.fillMaxSize().background(Papel).safeDrawingPadding().padding(20.dp)) {
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
