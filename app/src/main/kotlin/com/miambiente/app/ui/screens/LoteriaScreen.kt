package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.etapaDe
import com.miambiente.app.model.phasedInt
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal data class CartaLoteria(val nombre: String, val emoji: String)

/**
 * Mazo de la lotería mexicana, recortado a propósito.
 *
 * La baraja tradicional trae 54 cartas, pero varias no van en una app para
 * un niño de 2 a 6 años: La muerte, La calavera, El diablito, El borracho,
 * El soldado y Las jaras quedaron fuera. Las que quedan son las de siempre
 * —El gallo, La sirena, El sol, La luna— que son además las que un niño
 * puede nombrar sin ayuda.
 *
 * Los emojis son de un solo carácter, sin secuencias ZWJ: ya hubo un bug
 * real de iconos compuestos que se dibujaban partidos en este proyecto.
 */
internal val MAZO_LOTERIA = listOf(
    CartaLoteria("El gallo", "🐓"),
    CartaLoteria("La dama", "👩"),
    CartaLoteria("El paraguas", "☂️"),
    CartaLoteria("La sirena", "🧜"),
    CartaLoteria("La escalera", "🪜"),
    CartaLoteria("El barril", "🛢️"),
    CartaLoteria("El árbol", "🌳"),
    CartaLoteria("El melón", "🍈"),
    CartaLoteria("La pera", "🍐"),
    CartaLoteria("La bandera", "🚩"),
    CartaLoteria("La garza", "🦢"),
    CartaLoteria("El pájaro", "🐦"),
    CartaLoteria("La mano", "✋"),
    CartaLoteria("La bota", "👢"),
    CartaLoteria("La luna", "🌙"),
    CartaLoteria("El cotorro", "🦜"),
    CartaLoteria("El corazón", "❤️"),
    CartaLoteria("La sandía", "🍉"),
    CartaLoteria("El tambor", "🥁"),
    CartaLoteria("El camarón", "🦐"),
    CartaLoteria("La araña", "🕷️"),
    CartaLoteria("La estrella", "⭐"),
    CartaLoteria("El mundo", "🌍"),
    CartaLoteria("El nopal", "🌵"),
    CartaLoteria("La rosa", "🌹"),
    CartaLoteria("La campana", "🔔"),
    CartaLoteria("El cantarito", "🏺"),
    CartaLoteria("El venado", "🦌"),
    CartaLoteria("El sol", "☀️"),
    CartaLoteria("La corona", "👑"),
    CartaLoteria("La chalupa", "🛶"),
    CartaLoteria("El pino", "🌲"),
    CartaLoteria("El pescado", "🐟"),
    CartaLoteria("La palma", "🌴"),
    CartaLoteria("La maceta", "🪴"),
    CartaLoteria("La rana", "🐸"),
)

/**
 * Lado del cartón: 2x2 al empezar, 3x3 a media tabla, 4x4 (las 16 casillas
 * del cartón real) al final. Crece por etapas, no nivel a nivel, para que
 * haya repetición cómoda antes de cada salto.
 */
internal fun ladoParaNivel(nivel: Int): Int =
    phasedInt(nivel, listOf(2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4)).coerceIn(2, 4)

/**
 * Hasta la etapa 6 el gritón muestra la carta además de decirla. Después
 * solo se oye: hay que reconocerla por el nombre, que es el paso difícil
 * de la lotería real.
 */
internal fun mostrarCartaParaNivel(nivel: Int): Boolean = etapaDe(nivel) <= 6

/**
 * Desde la etapa 4 el gritón puede cantar cartas que NO están en el cartón,
 * como en el juego de verdad. Ahí aparece el botón "no lo tengo": decir que
 * no la tienes también es una respuesta correcta, y es más difícil que
 * buscar la que sí está.
 */
internal fun cantaCartasFueraParaNivel(nivel: Int): Boolean = etapaDe(nivel) >= 4

/** Cartón del nivel: tantas cartas distintas como casillas tenga. */
internal fun repartirCarton(nivel: Int): List<CartaLoteria> {
    val lado = ladoParaNivel(nivel)
    return MAZO_LOTERIA.shuffled().take(lado * lado)
}

/**
 * Lotería mexicana — el gritón canta y el niño marca con su frijolito.
 *
 * Pantalla propia y no `MaterialQuiz`: aquí no hay una pregunta con
 * opciones, hay un cartón que se va llenando y un mazo que canta, con la
 * posibilidad de que lo cantado no esté en tu cartón.
 */
@Composable
fun LoteriaScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("loteria")!!

    var nivel by remember { mutableStateOf(1) }
    var carton by remember { mutableStateOf(repartirCarton(1)) }
    var marcadas by remember { mutableStateOf(setOf<String>()) }
    var cantada by remember { mutableStateOf(carton.random()) }
    var nota by remember { mutableStateOf<String?>(null) }
    var celebrar by remember { mutableStateOf(false) }

    val lado = ladoParaNivel(nivel)
    val puedeCantarFuera = cantaCartasFueraParaNivel(nivel)

    LaunchedEffect(Unit) { services.progress.registrarJugada(juego.id) }

    fun cantar() {
        val faltantes = carton.filter { it.nombre !in marcadas }
        if (faltantes.isEmpty()) return
        // Una de cada tres veces canta una carta de fuera, cuando el nivel
        // ya lo permite: suficiente para que haya que estar atento, no
        // tanto como para volverse tedioso.
        val fuera = puedeCantarFuera && (0..2).random() == 0
        cantada = if (fuera) {
            MAZO_LOTERIA.filter { c -> carton.none { it.nombre == c.nombre } }.randomOrNull()
                ?: faltantes.random()
        } else {
            faltantes.random()
        }
    }

    fun siguienteNivel() {
        val proximo = (nivel + 1).coerceAtMost(100)
        nivel = proximo
        carton = repartirCarton(proximo)
        marcadas = emptySet()
        cantada = carton.random()
        celebrar = false
        nota = null
    }

    fun tocar(carta: CartaLoteria) {
        val objetivo = cantada
        if (carta.nombre != objetivo.nombre) {
            // Control del error: la carta no se marca y se invita a oír de
            // nuevo. No hay penalización ni se pierde el cartón.
            services.sound.tocar(Efecto.WRONG)
            nota = "Esa no es. Escucha otra vez"
            return
        }
        services.sound.tocar(Efecto.CORRECT)
        nota = null
        marcadas = marcadas + carta.nombre

        if (marcadas.size == carton.size) {
            services.sound.tocar(Efecto.WIN)
            celebrar = true
            nota = "¡Lotería! Cartón lleno"
            scope.launch { services.progress.completarNivel(juego.id, nivel) }
            scope.launch {
                delay(1800)
                siguienteNivel()
            }
        } else {
            cantar()
        }
    }

    fun noLoTengo() {
        val estaEnCarton = carton.any { it.nombre == cantada.nombre }
        if (estaEnCarton) {
            services.sound.tocar(Efecto.WRONG)
            nota = "Sí la tienes: búscala bien"
            return
        }
        services.sound.tocar(Efecto.STAR)
        nota = "Bien visto: esa no estaba"
        cantar()
    }

    GameShell(
        juego = juego,
        consigna = if (celebrar) "¡Lotería!" else "¡${cantada.nombre}!",
        nota = nota,
        celebrar = celebrar,
        onVolver = onVolver,
        acciones = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { services.speech.hablar(cantada.nombre) }) {
                    Text("🔊 Repetir")
                }
                if (puedeCantarFuera) {
                    OutlinedButton(onClick = { noLoTengo() }) { Text("No la tengo") }
                }
            }
        },
    ) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "Nivel $nivel  ·  cartón de ${lado * lado}  ·  ${marcadas.size} marcadas",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp),
            )

            // La carta cantada, como la sostiene el gritón. Desde la etapa 7
            // solo se dice el nombre: el recuadro queda tapado a propósito.
            Box(
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFE0C23C))
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (mostrarCartaParaNivel(nivel)) cantada.emoji else "🔊", fontSize = 40.sp)
                    Text(cantada.nombre, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(lado),
                modifier = Modifier
                    .fillMaxWidth(if (lado >= 4) 1f else 0.8f)
                    .shadow(5.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFFBF2))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(carton) { carta ->
                    val marcada = carta.nombre in marcadas
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (marcada) Color(0xFF8BBF6A) else Color.White)
                            .clickable(enabled = !marcada) { tocar(carta) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(carta.emoji, fontSize = if (lado >= 4) 24.sp else 34.sp)
                            Text(
                                carta.nombre,
                                fontSize = if (lado >= 4) 8.sp else 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                        // El frijolito con el que se marca el cartón de verdad.
                        if (marcada) Text("🫘", fontSize = if (lado >= 4) 22.sp else 30.sp)
                    }
                }
            }
        }
    }
}
