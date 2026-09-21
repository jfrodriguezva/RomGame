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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Settings
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.EspacioL
import com.miambiente.app.theme.EspacioM
import com.miambiente.app.theme.EspacioS
import com.miambiente.app.theme.EspacioXS
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.materials.ConfettiOverlay

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
    celebrar: Boolean = false,
    onVolver: () -> Unit,
    acciones: (@Composable () -> Unit)? = null,
    contenido: @Composable () -> Unit,
) {
    val services = LocalServices.current
    val colores = coloresDe(juego.area)
    val ajustes by services.settings.settings.collectAsState(initial = Settings())

    LaunchedEffect(consigna, ajustes.voz) {
        if (consigna != null && ajustes.voz) services.speech.hablar(consigna)
    }

    // Fondo musical tierno por área: entra al abrir el material y se
    // detiene al volver al Home, para que el silencio de la pantalla
    // principal siga siendo silencio.
    DisposableEffect(juego.area) {
        services.musica.sonarPara(juego.area)
        onDispose { services.musica.detener() }
    }

    // `enableEdgeToEdge()` (MainActivity) dibuja detrás de las barras del
    // sistema a propósito — pero sin `safeDrawingPadding()` el contenido
    // real (botones, zonas de soltar) queda debajo de la barra de
    // navegación en vez de solo el fondo. Encontrado probando en un
    // emulador real: una pieza soltada terminaba oculta tras la barra.
    Column(modifier = Modifier.fillMaxSize().background(colores.fondo).safeDrawingPadding()) {
        // Encabezado con una sombra sutil: separa visualmente la barra de
        // navegación del contenido de juego, un detalle de profundidad que
        // antes era plano de punta a punta.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(colores.fondo, shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
                .padding(bottom = EspacioXS),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = EspacioS, vertical = EspacioS),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onVolver) { Text("← Volver", color = colores.texto, fontWeight = FontWeight.Bold) }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(colores.acento.copy(alpha = 0.18f))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) { Text(juego.area.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colores.texto) }
            }

            Text(
                "${juego.emoji} ${juego.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colores.texto,
                modifier = Modifier.fillMaxWidth().padding(horizontal = EspacioM),
            )
        }

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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = EspacioL, vertical = EspacioS),
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            contenido()
            ConfettiOverlay(activo = celebrar)
        }

        if (nota != null) {
            Box(modifier = Modifier.fillMaxWidth().padding(EspacioM), contentAlignment = Alignment.Center) {
                Text(
                    nota,
                    color = Papel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(Tinta.copy(alpha = 0.85f), shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                        .padding(horizontal = EspacioM, vertical = EspacioS),
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
