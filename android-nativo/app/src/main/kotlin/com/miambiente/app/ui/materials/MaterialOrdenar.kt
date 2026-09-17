package com.miambiente.app.ui.materials

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell

/**
 * Seriación por arrastre real — puerto del patrón MaterialOrdenar, pero
 * con arrastre (`PiezaArrastrable`/`ZonaSoltar`) en vez de tocar del
 * canasto en orden como en la web. `posicion` va de 1 a n; con
 * `invertido = false` la posición 1 espera la pieza más grande/primera
 * (torre rosa, escalera marrón); con `invertido = true` la fila sale en
 * orden ascendente (ciclo de vida, secuencias — ver la nota equivalente
 * en data/levels/ciclo-vida.ts de la versión web).
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
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    var colocadas by remember(estado.nivel) { mutableStateOf(setOf<Int>()) }
    var enCanasto by remember(estado.nivel) { mutableStateOf((1..n).shuffled()) }
    val ranuraRects = remember(estado.nivel) { mutableStateMapOf<Int, Rect>() }
    var puntoArrastre by remember(estado.nivel) { mutableStateOf<androidx.compose.ui.geometry.Offset?>(null) }

    val completo = colocadas.size == n

    fun soltarEn(pieza: Int, puntoRoot: androidx.compose.ui.geometry.Offset) {
        val ranuraObjetivo = ranuraRects.entries.find { (_, rect) -> rect.contains(puntoRoot) }?.key
        if (ranuraObjetivo == pieza - 1) {
            estado.acierto("¡Ahí va!")
            colocadas = colocadas + pieza
            enCanasto = enCanasto - pieza
            if (colocadas.size == n) estado.completar()
        } else if (ranuraObjetivo != null) {
            estado.intento("Ahí no va. Mira otra vez")
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Completaste la serie!" else consigna,
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
                    val ladoRanura = tamanoPara(i + 1)
                    val rect = ranuraRects[i]
                    ZonaSoltar(
                        modifier = Modifier.size(ladoRanura),
                        resaltado = puntoArrastre != null && rect?.contains(puntoArrastre!!) == true,
                        formaResaltado = RoundedCornerShape(6.dp),
                        onPosicion = { r -> ranuraRects[i] = r },
                    ) {
                        val piezaAqui = i + 1
                        val llena = piezaAqui in colocadas
                        Box(
                            modifier = Modifier
                                .size(ladoRanura)
                                .clip(RoundedCornerShape(6.dp))
                                .background(colores.fondo)
                                .then(
                                    if (llena) Modifier else Modifier.border(2.dp, colores.acento.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (llena) render(piezaAqui, ladoRanura)
                        }
                    }
                }
            }

            Text("Canasto", color = colores.texto, modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                // Bug real: antes esto era `enCanasto.sorted()`, así que el
                // canasto SIEMPRE mostraba las piezas en el orden correcto
                // (1, 2, 3...) sin importar cómo se revolvió `enCanasto` al
                // iniciar el nivel — el ejercicio de seriación quedaba
                // resuelto de antemano, sin nada que pensar. Ahora se
                // respeta el orden revuelto real.
                enCanasto.forEach { pieza ->
                    val ladoPieza = tamanoPara(pieza)
                    PiezaArrastrable(
                        tamano = ladoPieza,
                        clave = pieza,
                        onArrastrar = { punto -> puntoArrastre = punto },
                        onSoltar = { punto -> soltarEn(pieza, punto) },
                    ) {
                        // Tarjeta compartida para toda pieza del canasto: antes
                        // cada material dibujaba su contenido "al aire", sin
                        // fondo ni sombra que lo distinguiera de la pantalla.
                        Box(
                            modifier = Modifier
                                .size(ladoPieza)
                                .shadow(3.dp, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center,
                        ) { render(pieza, ladoPieza) }
                    }
                }
            }
        }
    }
}
