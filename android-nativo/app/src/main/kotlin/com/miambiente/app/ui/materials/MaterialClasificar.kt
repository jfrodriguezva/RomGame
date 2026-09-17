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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.GameDef
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import kotlin.random.Random

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
    cantidadPorRonda: Int,
    canastas: List<DefCanasta>,
    render: @Composable (T) -> Unit,
    consigna: String,
    onVolver: () -> Unit,
) {
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    var pendientes by remember(estado.nivel) {
        mutableStateOf(pool.shuffled(Random(estado.nivel)).take(cantidadPorRonda.coerceAtMost(pool.size)))
    }
    var acertados by remember(estado.nivel) { mutableStateOf(0) }
    val canastaRects = remember(estado.nivel) { mutableStateMapOf<String, Rect>() }

    val completo = pendientes.isEmpty()

    fun soltar(item: ItemClasificar<T>, puntoRoot: androidx.compose.ui.geometry.Offset) {
        val canastaId = canastaRects.entries.find { (_, rect) -> rect.contains(puntoRoot) }?.key ?: return
        if (canastaId == item.categoria) {
            estado.acierto("¡Correcto!")
            pendientes = pendientes.filter { it.id != item.id }
            acertados++
            if (pendientes.isEmpty()) estado.completar()
        } else {
            estado.intento("Esa no va ahí. Mira otra vez")
        }
    }

    GameShell(
        juego = juego,
        consigna = if (completo) "¡Clasificaste todo!" else consigna,
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
                Text("Quedan: ${pendientes.size} · Acertados: $acertados", color = colores.texto, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // horizontalScroll: cantidadPorRonda varía por material (hasta
            // 8 en algunos), y sin esto las piezas de más quedaban fuera de
            // pantalla en vez de solo apretadas.
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 16.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
            ) {
                pendientes.forEach { item ->
                    PiezaArrastrable(
                        tamano = 64.dp,
                        clave = item.id,
                        onSoltar = { punto -> soltar(item, punto) },
                    ) { render(item.valor) }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                canastas.forEach { canasta ->
                    ZonaSoltar(
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        onPosicion = { rect -> canastaRects[canasta.id] = rect },
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
