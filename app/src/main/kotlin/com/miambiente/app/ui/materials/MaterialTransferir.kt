package com.miambiente.app.ui.materials

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell

/**
 * Transferencia por cantidad exacta, por arrastre real — puerto del
 * patrón MaterialTransferir (pinza de transferencia, los husos). Tomar
 * uno de más (pasarse del objetivo) sigue siendo el error, igual que en
 * la web; el control del error reinicia la bandeja en vez de "perder".
 */
@Composable
fun MaterialTransferir(
    juego: GameDef,
    objetivo: Int,
    origenTotal: Int,
    render: @Composable () -> Unit,
    consigna: String,
    onVolver: () -> Unit,
) {
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val total = origenTotal.coerceAtLeast(objetivo)

    var enOrigen by remember(estado.nivel) { mutableStateOf(total) }
    var enDestino by remember(estado.nivel) { mutableStateOf(0) }
    var destinoRect by remember(estado.nivel) { mutableStateOf<Rect?>(null) }
    var rectArrastre by remember(estado.nivel) { mutableStateOf<Rect?>(null) }

    // Mismo bug de fondo que en MaterialOrdenar/MaterialClasificar
    // ("sigue fallando al arrastrar y colocar"): solapamiento de
    // rectángulos en vez de exigir el punto central exacto.
    fun soltar(rectPieza: Rect) {
        val rect = destinoRect ?: return
        if (!rect.overlaps(rectPieza)) return
        val nuevoDestino = enDestino + 1
        if (nuevoDestino > objetivo) {
            estado.intento("Te pasaste del objetivo. Vuelve a empezar")
            enOrigen = total
            enDestino = 0
        } else {
            estado.acierto("¡Uno más!")
            enOrigen -= 1
            enDestino = nuevoDestino
            if (nuevoDestino == objetivo) estado.completar()
        }
    }

    GameShell(
        juego = juego,
        consigna = if (estado.logrado) "¡Objetivo exacto!" else "$consigna (van $enDestino de $objetivo)",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(50)).background(colores.acento.copy(alpha = 0.16f)).padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text("Origen: $enOrigen · Van $enDestino de $objetivo", color = colores.texto, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            LinearProgressIndicator(
                progress = { (enDestino.toFloat() / objetivo).coerceIn(0f, 1f) },
                color = colores.acento,
                trackColor = colores.acento.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )

            Text("Origen", color = colores.texto, modifier = Modifier.padding(top = 12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(enOrigen) { i ->
                    PiezaArrastrable(
                        tamano = 48.dp,
                        clave = "origen-${estado.nivel}-$i-$enOrigen",
                        onArrastrar = { rect -> rectArrastre = rect },
                        onSoltar = { rect -> soltar(rect) },
                    ) { render() }
                }
            }

            Text("Destino", color = colores.texto, modifier = Modifier.padding(top = 12.dp))
            ZonaSoltar(
                modifier = Modifier.fillMaxWidth().height(90.dp).padding(top = 4.dp),
                resaltado = rectArrastre != null && destinoRect?.overlaps(rectArrastre!!) == true,
                formaResaltado = RoundedCornerShape(20.dp),
                onPosicion = { rect -> destinoRect = rect },
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().shadow(3.dp, RoundedCornerShape(20.dp)).clip(RoundedCornerShape(20.dp)).background(colores.fondo),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        // Cada pieza que llega aparece con un pop de escala
                        // (key(i) + Animatable propio): antes se perdía la
                        // silueta de la pieza arrastrada y una nueva
                        // aparecía de golpe en el destino, sin transición.
                        for (i in 0 until enDestino) {
                            key(i) {
                                val escala = remember { androidx.compose.animation.core.Animatable(0f) }
                                LaunchedEffect(Unit) { escala.animateTo(1f, spring(dampingRatio = 0.55f)) }
                                Box(Modifier.scale(escala.value)) { render() }
                            }
                        }
                    }
                }
            }
        }
    }
}
