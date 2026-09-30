package com.miambiente.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Settings
import com.miambiente.app.model.ETAPAS
import com.miambiente.app.model.GameDef
import com.miambiente.app.model.LEVEL_COUNT
import com.miambiente.app.model.STAGE_SIZE
import com.miambiente.app.model.etapaDe
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
/**
 * Lo que el marco necesita para mostrar el nivel actual y dejar elegir
 * otro. `desbloqueado` es una función para leerlo fresco al abrir el
 * selector (cambia al completar niveles).
 */
class SelectorNivel(
    val actual: Int,
    val desbloqueado: () -> Int,
    val onElegir: (Int) -> Unit,
)

@Composable
fun GameShell(
    juego: GameDef,
    consigna: String? = null,
    nota: String? = null,
    celebrar: Boolean = false,
    onVolver: () -> Unit,
    selectorNivel: SelectorNivel? = null,
    acciones: (@Composable () -> Unit)? = null,
    contenido: @Composable () -> Unit,
) {
    val services = LocalServices.current
    val colores = coloresDe(juego.area)
    val ajustes by services.settings.settings.collectAsState(initial = Settings())
    var eligiendoNivel by remember { mutableStateOf(false) }

    LaunchedEffect(consigna, ajustes.voz) {
        if (consigna != null && ajustes.voz) services.speech.hablar(consigna)
    }

    // Cuenta cada apertura del material para el resumen del adulto.
    LaunchedEffect(juego.id) { services.progress.registrarJugada(juego.id) }

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
                .padding(bottom = 4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onVolver) { Text("← Volver", color = colores.texto, fontWeight = FontWeight.Bold) }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (selectorNivel != null) {
                        val etapa = ETAPAS[etapaDe(selectorNivel.actual) - 1]
                        TextButton(
                            onClick = { eligiendoNivel = true },
                            modifier = Modifier.semantics { contentDescription = "Nivel ${selectorNivel.actual}. Toca para elegir otro nivel" },
                        ) {
                            Text("${etapa.emoji} Nivel ${selectorNivel.actual} ▾", color = colores.texto, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colores.acento.copy(alpha = 0.18f))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                    ) { Text(juego.area.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colores.texto) }
                }
            }

            Text(
                "${juego.emoji} ${juego.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colores.texto,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            contenido()
            // En modo calma no hay confeti: la celebración queda en la voz
            // y la estrella, sin estímulo visual extra.
            ConfettiOverlay(activo = celebrar && !ajustes.calma)
        }

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

    if (eligiendoNivel && selectorNivel != null) {
        DialogoNiveles(
            actual = selectorNivel.actual,
            desbloqueado = selectorNivel.desbloqueado(),
            acento = colores.acento,
            onElegir = { nivel ->
                eligiendoNivel = false
                selectorNivel.onElegir(nivel)
            },
            onCerrar = { eligiendoNivel = false },
        )
    }
}

/**
 * Los 100 niveles agrupados por etapa. Se puede volver a cualquier nivel ya
 * abierto; los siguientes se ven con candado para que se note el camino.
 */
@Composable
private fun DialogoNiveles(
    actual: Int,
    desbloqueado: Int,
    acento: Color,
    onElegir: (Int) -> Unit,
    onCerrar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } },
        title = { Text("Elige un nivel", fontWeight = FontWeight.ExtraBold) },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.heightIn(max = 420.dp),
            ) {
                ETAPAS.forEach { etapa ->
                    item(key = "etapa-${etapa.numero}", span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            "${etapa.emoji} Etapa ${etapa.numero} · ${etapa.nombre}",
                            fontWeight = FontWeight.Bold,
                            color = TextoSuave,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    val desde = (etapa.numero - 1) * STAGE_SIZE + 1
                    items((desde until desde + STAGE_SIZE).filter { it <= LEVEL_COUNT }, key = { it }) { nivel ->
                        CasillaNivel(nivel, abierto = nivel <= desbloqueado, esActual = nivel == actual, acento = acento) {
                            onElegir(nivel)
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun CasillaNivel(nivel: Int, abierto: Boolean, esActual: Boolean, acento: Color, onClick: () -> Unit) {
    val fondo = when {
        esActual -> acento
        abierto -> acento.copy(alpha = 0.18f)
        else -> Color(0xFFEDEDED)
    }
    val base = Modifier
        .clip(RoundedCornerShape(10.dp))
        .background(fondo)
        .semantics { contentDescription = if (abierto) "Nivel $nivel" else "Nivel $nivel, todavía cerrado" }
    Box(
        modifier = if (abierto) base.clickable(onClick = onClick) else base,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (abierto) "$nivel" else "🔒",
            fontWeight = FontWeight.Bold,
            color = if (esActual) Color.White else Tinta,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}
