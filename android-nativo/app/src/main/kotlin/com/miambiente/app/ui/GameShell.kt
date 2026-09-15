package com.miambiente.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta
import com.miambiente.app.theme.coloresDe

/**
 * Marco común de todos los materiales — equivalente nativo de
 * components/GameShell.tsx. La consigna SIEMPRE se dice en voz alta aquí
 * (con TextToSpeech, que es parte del sistema): en la versión web hubo
 * que agregar esto a mano por componente (`hablarConsigna`); en nativo
 * es gratis para todos los materiales que usen GameShell.
 */
@Composable
fun GameShell(
    juego: GameDef,
    consigna: String? = null,
    nota: String? = null,
    onVolver: () -> Unit,
    acciones: (@Composable () -> Unit)? = null,
    contenido: @Composable () -> Unit,
) {
    val services = LocalServices.current
    val colores = coloresDe(juego.area)

    LaunchedEffect(consigna) {
        if (consigna != null) services.speech.hablar(consigna)
    }

    Column(modifier = Modifier.fillMaxSize().background(colores.fondo)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onVolver) { Text("← Volver", color = colores.texto) }
        }

        Text(
            "${juego.emoji} ${juego.title}",
            style = MaterialTheme.typography.titleLarge,
            color = colores.texto,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )

        if (consigna != null) {
            AnimatedContent(
                targetState = consigna,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "consigna",
            ) { texto ->
                Text(
                    texto,
                    fontWeight = FontWeight.Bold,
                    color = TextoSuave,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) { contenido() }

        if (nota != null) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(
                    nota,
                    color = Papel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(Tinta.copy(alpha = 0.85f), shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }

        if (acciones != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.Center,
            ) { acciones() }
        }
    }
}
