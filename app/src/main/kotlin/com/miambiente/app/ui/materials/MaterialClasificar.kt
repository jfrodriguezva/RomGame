package com.miambiente.app.ui.materials

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.CURVA_CLASIFICAR
import com.miambiente.app.model.GameDef
import com.miambiente.app.model.Resultado
import com.miambiente.app.model.RondaClasificar
import com.miambiente.app.model.phasedInt
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell

data class ItemClasificar<T>(val valor: T, val id: String, val categoria: String)
data class DefCanasta(val id: String, val nombre: String, val color: Color)

/**
 * Clasificación en canastas por arrastre real — puerto del patrón
 * MaterialClasificar, el más reutilizado del repo en la versión web (más
 * de 20 materiales). Aquí se toma el objeto y se suelta en la canasta,
 * en vez de tocar la canasta con el objeto pendiente fijo.
 */
@Composable
fun <T> MaterialClasificar(
    juego: GameDef,
    pool: List<ItemClasificar<T>>,
    canastas: List<DefCanasta>,
    render: @Composable (T) -> Unit,
    consigna: String,
    onVolver: () -> Unit,
    // Objetos por ronda según el nivel; antes era un número fijo (6) y los
    // 100 niveles de cada material jugaban igual.
    cantidadPara: (nivel: Int) -> Int = { nivel -> phasedInt(nivel, CURVA_CLASIFICAR) },
) {
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    val (elegidos, rondaInicial) = remember(estado.nivel) {
        RondaClasificar.nueva(pool, cantidadPara(estado.nivel), estado.nivel) { it.id to it.categoria }
    }
    var ronda by remember(estado.nivel) { mutableStateOf(rondaInicial) }
    val pendientes = elegidos.filter { item -> ronda.pendientes.any { it.first == item.id } }
    val acertados = ronda.acertados
    val canastaRects = remember(estado.nivel) { mutableStateMapOf<String, Rect>() }
    var rectArrastre by remember(estado.nivel) { mutableStateOf<Rect?>(null) }

    val completo = ronda.completa

    // Mismo bug de fondo que en MaterialOrdenar ("sigue fallando al
    // arrastrar y colocar"): exigir que el punto central de la pieza
    // caiga dentro de la canasta es muy poco tolerante. Ahora basta con
    // que los rectángulos se solapen.
    fun clasificar(item: ItemClasificar<T>, canastaId: String) {
        val (nueva, resultado) = ronda.clasificar(item.id, canastaId)
        ronda = nueva
        when (resultado) {
            Resultado.ACIERTO -> estado.acierto("¡Correcto!")
            Resultado.COMPLETO -> { estado.acierto("¡Correcto!"); estado.completar() }
            Resultado.ERROR -> estado.intento("Esa no va ahí. Mira otra vez")
            else -> Unit
        }
    }

    fun soltar(item: ItemClasificar<T>, rectPieza: Rect) {
        val canastaId = canastaRects.entries.find { (_, rect) -> rect.overlaps(rectPieza) }?.key ?: return
        clasificar(item, canastaId)
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Clasificaste todo!" else consigna,
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(50)).background(colores.acento.copy(alpha = 0.16f)).padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text("Quedan: ${pendientes.size} · Acertados: $acertados", color = colores.texto, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // horizontalScroll: los objetos por ronda crecen con el nivel
            // (hasta 8), y sin esto las piezas de más quedaban fuera de
            // pantalla en vez de solo apretadas.
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 16.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
            ) {
                // `key(item.id)` — mismo bug de fondo que en MaterialOrdenar:
                // sin él, al retirar un objeto ya clasificado, Compose
                // reciclaba el estado de arrastre por POSICIÓN en vez de por
                // objeto, y el resto de las piezas heredaba estado ajeno.
                pendientes.forEach { item ->
                    key(item.id) {
                        PiezaArrastrable(
                            tamano = 64.dp,
                            clave = item.id,
                            onArrastrar = { rect -> rectArrastre = rect },
                            onSoltar = { rect -> soltar(item, rect) },
                            accionesAccesibles = canastas.map { canasta ->
                                CustomAccessibilityAction("Poner en ${canasta.nombre}") { clasificar(item, canasta.id); true }
                            },
                        ) { render(item.valor) }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                canastas.forEach { canasta ->
                    val rect = canastaRects[canasta.id]
                    ZonaSoltar(
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        resaltado = rectArrastre != null && rect?.overlaps(rectArrastre!!) == true,
                        formaResaltado = RoundedCornerShape(20.dp),
                        onPosicion = { r -> canastaRects[canasta.id] = r },
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .shadow(3.dp, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .background(canasta.color),
                            contentAlignment = Alignment.Center,
                        ) { Text(canasta.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }
                    }
                }
            }
        }
    }
}
