package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private fun vecinos4(indice: Int, columnas: Int, total: Int): List<Int> {
    val fila = indice / columnas
    val columna = indice % columnas
    return buildList {
        if (fila > 0) add(indice - columnas)
        if (indice + columnas < total) add(indice + columnas)
        if (columna > 0) add(indice - 1)
        if (columna < columnas - 1) add(indice + 1)
    }
}

/** Arcade de territorio original: descubre un mosaico avanzando desde el borde. */
@Composable
fun MosaicoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("mosaico")!!
    val columnas = 8
    val total = 64
    val guardianes = remember { setOf(18, 21, 34, 45, 53) }
    var reveladas by remember { mutableStateOf((0 until total).filter { it / columnas in setOf(0, 7) || it % columnas in setOf(0, 7) }.toSet()) }
    var vidas by remember { mutableIntStateOf(3) }
    var terminado by remember { mutableStateOf(false) }
    val objetivo = total - guardianes.size

    fun reiniciar() {
        reveladas = (0 until total).filter { it / columnas in setOf(0, 7) || it % columnas in setOf(0, 7) }.toSet()
        vidas = 3
        terminado = false
    }

    fun revelar(i: Int) {
        if (terminado || i in reveladas || reveladas.none { i in vecinos4(it, columnas, total) }) return
        if (i in guardianes) {
            services.sound.tocar(Efecto.WRONG)
            services.haptics.vibrar(Patron.ERROR)
            vidas--
            if (vidas == 0) terminado = true
            return
        }
        services.sound.tocar(Efecto.CLICK)
        reveladas = reveladas + i
        if (reveladas.count { it !in guardianes } >= objetivo) {
            terminado = true
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (vidas == 0) "Los guardianes te encontraron" else "Descubierto ${reveladas.size}/$objetivo · Vidas $vidas",
        onVolver = onVolver,
        acciones = if (terminado) ({ Button(onClick = ::reiniciar) { Text("Nuevo mosaico") } }) else null,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnas),
            modifier = Modifier.fillMaxSize().padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            items(total) { i ->
                val visible = i in reveladas
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (visible) Color(0xFF82C8A0) else Color(0xFF26384A))
                        .clickable { revelar(i) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (visible) Text(listOf("🌿", "🌼", "🦋", "🍓")[(i + i / columnas) % 4], fontSize = 17.sp)
                }
            }
        }
    }
}

/** Galería de puntería del oeste, sin personajes ni recursos de franquicias. */
@Composable
fun VaquerosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("vaqueros")!!
    var objetivo by remember { mutableIntStateOf(-1) }
    var puntos by remember { mutableIntStateOf(0) }
    var tiempo by remember { mutableIntStateOf(30) }
    var ronda by remember { mutableIntStateOf(0) }
    var jugando by remember { mutableStateOf(false) }

    fun iniciar() { puntos = 0; tiempo = 30; objetivo = -1; ronda++; jugando = true }

    LaunchedEffect(ronda, jugando) {
        if (!jugando) return@LaunchedEffect
        while (jugando && tiempo > 0) {
            objetivo = (0 until 12).filter { it != objetivo }.random()
            delay((850L - puntos * 18L).coerceAtLeast(380L))
        }
    }
    LaunchedEffect(ronda, jugando) {
        if (!jugando) return@LaunchedEffect
        while (jugando && tiempo > 0) { delay(1_000); tiempo-- }
        if (jugando) {
            jugando = false; objetivo = -1; services.sound.tocar(Efecto.WIN)
            if (puntos >= 12) scope.launch { services.progress.completarNivel(juego.id, 1) }
        }
    }

    GameShell(
        juego = juego,
        consigna = if (jugando) "Tiempo ${tiempo}s · Bandidos $puntos" else "Atrapa 12 bandidos en 30 segundos",
        onVolver = onVolver,
        acciones = { Button(onClick = ::iniciar) { Text(if (jugando) "Reiniciar" else "Jugar") } },
    ) {
        Column(Modifier.fillMaxSize().background(Color(0xFFF5D99B)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(3) { fila ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    repeat(4) { col ->
                        val i = fila * 4 + col
                        Box(
                            Modifier.size(68.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF70452D)).clickable(enabled = jugando && objetivo == i) {
                                puntos++; objetivo = -1; services.sound.tocar(Efecto.CORRECT)
                            },
                            contentAlignment = Alignment.Center,
                        ) { Text(if (objetivo == i) "🤠" else "🪟", fontSize = 34.sp) }
                    }
                }
            }
        }
    }
}

private val MAPA_COMEPUNTOS = listOf(
    "#########", "#.......#", "#.###.#.#", "#.......#", "#.#.#.#.#",
    "#.......#", "#.###.#.#", "#.......#", "#########",
)

/** Laberinto come-puntos por turnos: cada movimiento también mueve a los guardianes. */
@Composable
fun ComepuntosScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("comepuntos")!!
    val cols = 9
    val libres = remember { MAPA_COMEPUNTOS.flatMapIndexed { f, s -> s.indices.filter { s[it] != '#' }.map { f * cols + it } }.toSet() }
    var jugador by remember { mutableIntStateOf(10) }
    var fantasmas by remember { mutableStateOf(listOf(70, 16)) }
    var puntos by remember { mutableStateOf(libres - jugador - fantasmas.toSet()) }
    var terminado by remember { mutableStateOf(false) }

    fun reiniciar() { jugador = 10; fantasmas = listOf(70, 16); puntos = libres - jugador - fantasmas.toSet(); terminado = false }
    fun mover(delta: Int) {
        if (terminado) return
        val destino = jugador + delta
        if (destino !in libres || (delta == 1 && jugador % cols == cols - 1) || (delta == -1 && jugador % cols == 0)) return
        jugador = destino
        puntos = puntos - destino
        fantasmas = fantasmas.map { fantasma ->
            vecinos4(fantasma, cols, 81).filter { it in libres }.minByOrNull { abs(it / cols - jugador / cols) + abs(it % cols - jugador % cols) } ?: fantasma
        }
        if (jugador in fantasmas) { terminado = true; services.sound.tocar(Efecto.WRONG) }
        else if (puntos.isEmpty()) { terminado = true; services.sound.tocar(Efecto.WIN); scope.launch { services.progress.completarNivel(juego.id, 1) } }
        else services.sound.tocar(Efecto.CLICK)
    }

    GameShell(
        juego = juego,
        consigna = if (terminado) if (puntos.isEmpty()) "¡Laberinto limpio!" else "Un fantasma te alcanzó" else "Puntos restantes: ${puntos.size}",
        onVolver = onVolver,
        acciones = if (terminado) ({ Button(onClick = ::reiniciar) { Text("Reintentar") } }) else null,
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            LazyVerticalGrid(columns = GridCells.Fixed(cols), modifier = Modifier.size(306.dp)) {
                items(81) { i ->
                    Box(Modifier.size(34.dp).background(if (i in libres) Color(0xFF11152E) else Color(0xFF3858B8)), contentAlignment = Alignment.Center) {
                        Text(when { i == jugador -> "🟡"; i in fantasmas -> "👻"; i in puntos -> "·"; else -> "" }, color = Color.White, fontSize = 17.sp)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 12.dp)) {
                Button(onClick = { mover(-cols) }) { Text("▲") }
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    Button(onClick = { mover(-1) }) { Text("◀") }
                    Button(onClick = { mover(cols) }) { Text("▼") }
                    Button(onClick = { mover(1) }) { Text("▶") }
                }
            }
        }
    }
}

/** Arcade de nieve: cada travieso necesita tres impactos antes de rodar como bola. */
@Composable
fun NieveScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("nieve")!!
    var golpes by remember { mutableStateOf(List(8) { 0 }) }
    val rescatados = golpes.count { it >= 3 }
    fun reiniciar() { golpes = List(8) { 0 } }

    GameShell(
        juego = juego,
        consigna = if (rescatados == 8) "¡Montaña rescatada!" else "Lanza nieve: $rescatados / 8",
        onVolver = onVolver,
        acciones = if (rescatados == 8) ({ Button(onClick = ::reiniciar) { Text("Otra ronda") } }) else null,
    ) {
        Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFDDF4FF), Color.White))).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            repeat(4) { fila ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    repeat(2) { col ->
                        val i = fila * 2 + col
                        Box(
                            Modifier.size(92.dp).clip(CircleShape).background(Color.White.copy(alpha = .8f)).clickable(enabled = golpes[i] < 3) {
                                val nuevo = golpes.toMutableList(); nuevo[i]++; golpes = nuevo
                                services.sound.tocar(Efecto.CLICK)
                                if (nuevo.all { it >= 3 }) { services.sound.tocar(Efecto.WIN); scope.launch { services.progress.completarNivel(juego.id, 1) } }
                            },
                            contentAlignment = Alignment.Center,
                        ) { Text(listOf("👾", "❄️", "⛄", "⚪")[golpes[i].coerceIn(0, 3)], fontSize = 44.sp) }
                    }
                }
            }
        }
    }
}

/** Shooter espacial por carriles con ciclos discretos para no saturar recomposición. */
@Composable
fun EscuadronEstelarScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("escuadron-estelar")!!
    var carril by remember { mutableIntStateOf(1) }
    var enemigo by remember { mutableIntStateOf(0) }
    var escudo by remember { mutableIntStateOf(3) }
    var puntos by remember { mutableIntStateOf(0) }
    var jugando by remember { mutableStateOf(false) }
    var ronda by remember { mutableIntStateOf(0) }
    fun iniciar() { carril = 1; enemigo = 0; escudo = 3; puntos = 0; jugando = true; ronda++ }

    LaunchedEffect(ronda, jugando) {
        if (!jugando) return@LaunchedEffect
        while (jugando && escudo > 0 && puntos < 15) {
            enemigo = (0..2).random()
            delay((1_150L - puntos * 35L).coerceAtLeast(550L))
            if (jugando && enemigo == carril) escudo--
        }
        if (jugando) {
            jugando = false
            if (puntos >= 15) { services.sound.tocar(Efecto.WIN); scope.launch { services.progress.completarNivel(juego.id, 1) } }
            else services.sound.tocar(Efecto.WRONG)
        }
    }

    fun disparar() {
        if (!jugando) return
        if (carril == enemigo) { puntos++; enemigo = -1; services.sound.tocar(Efecto.CORRECT) }
        else services.sound.tocar(Efecto.CLICK)
    }

    GameShell(juego, if (jugando) "Naves $puntos/15 · Escudo $escudo" else "Pilota y dispara", onVolver = onVolver, acciones = { Button(onClick = ::iniciar) { Text(if (jugando) "Reiniciar" else "Despegar") } }) {
        Column(Modifier.fillMaxSize().background(Color(0xFF10152E)).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { Text(if (enemigo == it) "🛸" else "✨", fontSize = 42.sp) } }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { i -> Box(Modifier.size(82.dp).clip(RoundedCornerShape(18.dp)).background(if (carril == i) Color(0xFF425CC7) else Color(0xFF252C52)).clickable { carril = i }, contentAlignment = Alignment.Center) { Text(if (carril == i) "🚀" else "", fontSize = 46.sp) } } }
            Button(onClick = ::disparar, enabled = jugando) { Text("DISPARAR") }
        }
    }
}

/** Carrera por carriles: los obstáculos avanzan por pasos estables, sin bucle por cuadro. */
@Composable
fun GranPremioScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("gran-premio")!!
    var carril by remember { mutableIntStateOf(1) }
    var obstaculo by remember { mutableIntStateOf(0) }
    var fila by remember { mutableIntStateOf(0) }
    var distancia by remember { mutableIntStateOf(0) }
    var jugando by remember { mutableStateOf(false) }
    var ronda by remember { mutableIntStateOf(0) }
    fun iniciar() { carril = 1; obstaculo = 0; fila = 0; distancia = 0; jugando = true; ronda++ }

    LaunchedEffect(ronda, jugando) {
        if (!jugando) return@LaunchedEffect
        while (jugando && distancia < 30) {
            delay((480L - distancia * 7L).coerceAtLeast(260L))
            fila++
            if (fila >= 6) {
                if (carril == obstaculo) { jugando = false; services.sound.tocar(Efecto.WRONG); break }
                distancia++; fila = 0; obstaculo = (0..2).random()
            }
        }
        if (jugando && distancia >= 30) { jugando = false; services.sound.tocar(Efecto.WIN); scope.launch { services.progress.completarNivel(juego.id, 1) } }
    }

    GameShell(juego, if (jugando) "Meta: $distancia/30" else if (distancia >= 30) "¡Primer lugar!" else "Cambia de carril para esquivar", onVolver = onVolver, acciones = { Button(onClick = ::iniciar) { Text(if (jugando) "Reiniciar" else "Arrancar") } }) {
        Column(Modifier.fillMaxSize().background(Color(0xFF454545)).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            repeat(6) { f -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { c -> Box(Modifier.size(76.dp, 60.dp).background(if (c % 2 == 0) Color(0xFF505050) else Color(0xFF595959)), contentAlignment = Alignment.Center) { if (jugando && f == fila && c == obstaculo) Text("🚙", fontSize = 32.sp) } } } }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceEvenly) { repeat(3) { c -> Box(Modifier.size(76.dp, 58.dp).clip(RoundedCornerShape(12.dp)).background(if (c == carril) Color(0xFF7FA8D8) else Color(0xFFDDDDDD)).clickable { carril = c }, contentAlignment = Alignment.Center) { if (c == carril) Text("🏎️", fontSize = 36.sp) } } }
        }
    }
}
