package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.dificultadGlobo
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState

private const val IMPULSO = -340f // dp/s al tocar

/**
 * El globo volador — física real (gravedad + impulso), no incrementos
 * fijos por tic: cada cuadro se mide el tiempo real transcurrido
 * (`withFrameNanos`, sincronizado con la pantalla) y se integra
 * velocidad y posición como un juego de verdad, no una animación por
 * pasos discretos.
 */
@Composable
fun GloboScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("globo")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)
    val d = remember(estado.nivel) { dificultadGlobo(estado.nivel) }

    var altura by remember(estado.nivel) { mutableStateOf(300f) }
    var velocidad by remember(estado.nivel) { mutableStateOf(0f) }
    var toques by remember(estado.nivel) { mutableStateOf(0) }
    // El globo espera quieto hasta el primer toque: antes empezaba a caer
    // al abrir la pantalla, antes de que el niño entendiera qué hacer.
    var empezado by remember(estado.nivel) { mutableStateOf(false) }
    var caido by remember(estado.nivel) { mutableStateOf(false) }
    var completo by remember(estado.nivel) { mutableStateOf(false) }

    fun reiniciar() {
        altura = 300f; velocidad = 0f; toques = 0; empezado = false; caido = false; completo = false
    }

    LaunchedEffect(empezado, caido, completo, estado.nivel) {
        if (!empezado) return@LaunchedEffect
        var anterior = withFrameNanos { it }
        while (!caido && !completo) {
            val ahora = withFrameNanos { it }
            val dt = ((ahora - anterior) / 1_000_000_000f).coerceAtMost(0.05f)
            anterior = ahora
            velocidad += d.gravedad * dt
            altura += velocidad * dt
            if (altura < 20f) { altura = 20f; velocidad = 0f }
            if (altura > 580f) {
                caido = true
                estado.intento("¡Se cayó! Intenta de nuevo")
            }
        }
    }

    GameShell(
        juego = juego,
        consigna = when {
            completo -> "¡Mantuviste el globo en el aire!"
            caido -> "¡Se cayó! Intenta de nuevo"
            !empezado -> "Toca el globo para empezar"
            else -> "Toca solamente el globo: $toques / ${d.meta}"
        },
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = when {
            estado.logrado -> ({ BotonSiguienteNivel(colores, onClick = estado::siguiente) })
            caido -> ({ Button(onClick = ::reiniciar) { Text("Reintentar") } })
            else -> null
        },
    ) {
        Box(
            Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFDCEEFA), Color(0xFFF3FAFF)))),
        ) {
            Box(
                Modifier.align(Alignment.TopCenter).offset { IntOffset(0, altura.dp.roundToPx()) }.clickable(enabled = !caido && !completo) {
                    services.sound.tocar(Efecto.CLICK)
                    empezado = true
                    velocidad = IMPULSO
                    toques++
                    if (toques >= d.meta) {
                        completo = true
                        estado.completar()
                    }
                },
            ) { Text("🎈", fontSize = 56.sp) }
        }
    }
}
