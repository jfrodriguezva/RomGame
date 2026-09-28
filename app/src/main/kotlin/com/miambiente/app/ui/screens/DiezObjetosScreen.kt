package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.CasillaEmoji
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BANCO_OBJETOS = listOf(
    "🍎", "🚗", "⚽", "🎈", "🐱", "🌸", "📚", "🧦", "🔑", "🎁",
    "🦋", "🍩", "🎯", "🧸", "🚀", "⏰", "🎨", "🐟", "🌙", "🍪",
)
private val RELLENO = listOf("🌿", "⬜", "🔷", "✨", "◻️", "🔹")
private const val CELDAS = 42

internal data class EscenaObjetos(val celdas: List<String>, val posiciones: Map<Int, String>, val objetivos: List<String>)

internal fun nuevaEscena(): EscenaObjetos {
    val objetivos = BANCO_OBJETOS.shuffled().take(10)
    val posiciones = (0 until CELDAS).shuffled().take(10).zip(objetivos).toMap()
    val celdas = (0 until CELDAS).map { posiciones[it] ?: RELLENO.random() }
    return EscenaObjetos(celdas, posiciones, objetivos)
}

/** Encuentra los objetos — una sola escena con diez objetos distintos escondidos entre relleno. */
@Composable
fun DiezObjetosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("diez-objetos")!!

    var escena by remember { mutableStateOf(nuevaEscena()) }
    var encontrados by remember { mutableStateOf(setOf<Int>()) }

    fun tocar(i: Int) {
        if (i !in escena.posiciones || i in encontrados) return
        services.sound.tocar(Efecto.CORRECT)
        encontrados = encontrados + i
        if (encontrados.size == escena.objetivos.size) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch { delay(1200); escena = nuevaEscena(); encontrados = emptySet() }
        }
    }

    GameShell(
        juego = juego,
        consigna = "Busca los diez objetos de la lista en la escena",
        nota = "Encontrados ${encontrados.size}/10",
        onVolver = onVolver,
    ) {
        Column(Modifier.fillMaxWidth()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(escena.objetivos) { emoji ->
                    val yaEncontrado = escena.posiciones.any { (pos, e) -> e == emoji && pos in encontrados }
                    Text(if (yaEncontrado) "✅" else emoji, fontSize = 24.sp)
                }
            }
            LazyVerticalGrid(columns = GridCells.Fixed(6), contentPadding = PaddingValues(12.dp)) {
                items(escena.celdas.size) { i ->
                    CasillaEmoji(
                        emoji = if (i in encontrados) "✅" else escena.celdas[i],
                        modifier = Modifier.padding(4.dp),
                        tamanoFuente = 22.sp,
                        acertado = i in encontrados,
                        habilitado = i !in encontrados,
                        onClick = { tocar(i) },
                    )
                }
            }
        }
    }
}
