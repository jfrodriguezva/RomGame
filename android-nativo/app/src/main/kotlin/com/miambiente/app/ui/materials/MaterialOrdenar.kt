package com.miambiente.app.ui.materials

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell

/**
 * Seriación por TOQUES, no por arrastre — modelo rehecho por completo.
 *
 * Bug real reportado repetidas veces ("Vida práctica, sigue fallando al
 * arrastrar y colocar"): el modelo anterior (arrastrar con el dedo,
 * `PiezaArrastrable`/`ZonaSoltar`) ya se le habían corregido dos causas
 * de fondo reales (el estado se reciclaba por posición en la lista en
 * vez de por pieza, y el punto de suelta exigía casi el centro exacto) y
 * seguía sintiéndose poco confiable — arrastrar con precisión es
 * genuinamente difícil para una mano de niño chico, sin importar cuánto
 * se afine la tolerancia. Rehecho con un modelo distinto de raíz: tocar
 * una pieza del canasto la "toma" (se ve seleccionada, con aro y
 * sombra), y tocar una casilla intenta soltarla ahí. Sin gesto de
 * arrastre, sin coordenadas de dedo en movimiento, sin nada que pueda
 * fallar a medio camino — un toque siempre se registra bien.
 *
 * `posicion` va de 1 a n.
 */
@Composable
fun MaterialOrdenar(
    juego: GameDef,
    n: Int,
    render: @Composable (posicion: Int, tamano: Dp) -> Unit,
    tamanoPara: (posicion: Int) -> Dp,
    consigna: String,
    onVolver: () -> Unit,
) {
    val services = LocalServices.current
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    var colocadas by remember(estado.nivel) { mutableStateOf(setOf<Int>()) }
    var enCanasto by remember(estado.nivel) { mutableStateOf((1..n).shuffled()) }
    var seleccionada by remember(estado.nivel) { mutableStateOf<Int?>(null) }

    val completo = colocadas.size == n

    fun tocarPieza(pieza: Int) {
        services.sound.tocar(Efecto.CLICK)
        seleccionada = if (seleccionada == pieza) null else pieza
    }

    fun tocarRanura(posicion: Int) {
        val pieza = seleccionada ?: return
        if (posicion == pieza) {
            estado.acierto("¡Ahí va!")
            colocadas = colocadas + pieza
            enCanasto = enCanasto - pieza
            seleccionada = null
            if (colocadas.size == n) estado.completar()
        } else {
            estado.intento("Ahí no va. Mira otra vez")
            seleccionada = null
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Completaste la serie!" else if (seleccionada != null) "Ahora toca dónde va" else consigna,
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // horizontalScroll en las dos filas: con n grande (barras
            // numéricas llega a 10, sistema solar a 8) las ranuras o las
            // piezas del canasto se salían de la pantalla y quedaban
            // inalcanzables — mismo bug de fondo que el de la pizarra.
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                for (i in 0 until n) {
                    val posicion = i + 1
                    val ladoRanura = tamanoPara(posicion)
                    val llena = posicion in colocadas
                    Box(
                        modifier = Modifier
                            .size(ladoRanura)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colores.fondo)
                            .then(
                                if (llena) Modifier else Modifier.border(2.dp, colores.acento.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
                            )
                            .then(if (!llena) Modifier.clickable { tocarRanura(posicion) } else Modifier),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Aparece con un pop de escala, no de golpe: sin
                        // esto la pieza "se perdía" del canasto y una
                        // aparecía de la nada en la casilla, sin ninguna
                        // sensación de haber llegado ahí.
                        val escalaLlegada by animateFloatAsState(if (llena) 1f else 0f, label = "llegadaPieza")
                        if (llena) Box(Modifier.scale(escalaLlegada)) { render(posicion, ladoRanura) }
                    }
                }
            }

            Text("Canasto", color = colores.texto, modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                // `key(pieza)`: ata el estado (acá solo visual, pero es el
                // mismo hábito que evitó el bug de reciclaje por posición
                // del modelo de arrastre anterior.
                enCanasto.forEach { pieza ->
                    key(pieza) {
                        val ladoPieza = tamanoPara(pieza)
                        val estaSeleccionada = seleccionada == pieza
                        val escala by animateFloatAsState(if (estaSeleccionada) 1.12f else 1f, label = "escalaSeleccion")
                        Box(
                            modifier = Modifier
                                .size(ladoPieza)
                                .scale(escala)
                                .shadow(if (estaSeleccionada) 8.dp else 3.dp, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .then(
                                    if (estaSeleccionada) Modifier.border(3.dp, Color(0xFFE0C23C), RoundedCornerShape(10.dp)) else Modifier,
                                )
                                .clickable { tocarPieza(pieza) },
                            contentAlignment = Alignment.Center,
                        ) { render(pieza, ladoPieza) }
                    }
                }
            }
        }
    }
}
