package com.miambiente.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val COLOR_PERLA = Color(0xFFE0B92E)
private const val MAXIMO_POR_CATEGORIA = 9

/**
 * El banco dorado — antes eran tres contadores con un número dentro de
 * un círculo (nada que ver con el material real). Se rehizo para que de
 * verdad se "tome" cada pieza del banco: perlas sueltas (unidades),
 * barras de 10 perlas (decenas) y cuadrados de 100 perlas (centenas),
 * las tres dibujadas con las perlas reales del material Montessori en
 * vez de un dígito. Tocar una pieza del banco la agrega a la mesa de
 * trabajo; tocar una pieza ya puesta en la mesa la regresa al banco —
 * mismo modelo de toque (sin arrastre) que el resto de la app.
 */
@Composable
fun BancoDoradoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("banco-dorado")!!

    var objetivo by remember { mutableStateOf((1..299).random()) }
    var centenas by remember(objetivo) { mutableStateOf(0) }
    var decenas by remember(objetivo) { mutableStateOf(0) }
    var unidades by remember(objetivo) { mutableStateOf(0) }

    val cObjetivo = objetivo / 100
    val dObjetivo = (objetivo % 100) / 10
    val uObjetivo = objetivo % 10

    fun revisar() {
        if (centenas == cObjetivo && decenas == dObjetivo && unidades == uObjetivo) {
            services.sound.tocar(Efecto.WIN)
            scope.launch { services.progress.completarNivel(juego.id, 1) }
            scope.launch { delay(1200); objetivo = (1..299).random() }
        }
    }

    fun tomarCentena() { if (centenas < MAXIMO_POR_CATEGORIA) { centenas++; services.sound.tocar(Efecto.CLICK); revisar() } }
    fun tomarDecena() { if (decenas < MAXIMO_POR_CATEGORIA) { decenas++; services.sound.tocar(Efecto.CLICK); revisar() } }
    fun tomarUnidad() { if (unidades < MAXIMO_POR_CATEGORIA) { unidades++; services.sound.tocar(Efecto.CLICK); revisar() } }
    fun soltarCentena() { if (centenas > 0) { centenas--; services.sound.tocar(Efecto.CLICK) } }
    fun soltarDecena() { if (decenas > 0) { decenas--; services.sound.tocar(Efecto.CLICK) } }
    fun soltarUnidad() { if (unidades > 0) { unidades--; services.sound.tocar(Efecto.CLICK) } }

    GameShell(juego = juego, consigna = "Toma perlas del banco hasta componer $objetivo", onVolver = onVolver) {
        Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$centenas$decenas$unidades", fontSize = 40.sp, fontWeight = FontWeight.Bold)

            Text(
                "Mesa de trabajo — toca una pieza para regresarla al banco",
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 12.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                repeat(centenas) { i -> PiezaMesa(key = "c$i", onClick = ::soltarCentena) { CuadradoCentena() } }
                repeat(decenas) { i -> PiezaMesa(key = "d$i", onClick = ::soltarDecena) { BarraDecena() } }
                repeat(unidades) { i -> PiezaMesa(key = "u$i", onClick = ::soltarUnidad) { Perla(16.dp) } }
            }

            Text("Banco — toca para tomar una pieza", fontSize = 11.sp, modifier = Modifier.padding(top = 20.dp))
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                PiezaBanco("Centena", ::tomarCentena) { CuadradoCentena(34.dp) }
                PiezaBanco("Decena", ::tomarDecena) { BarraDecena(60.dp, 7.dp) }
                PiezaBanco("Unidad", ::tomarUnidad) { Perla(20.dp) }
            }
        }
    }
}

@Composable
private fun PiezaMesa(key: String, onClick: () -> Unit, contenido: @Composable () -> Unit) {
    androidx.compose.runtime.key(key) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { onClick() }
                .padding(3.dp),
        ) { contenido() }
    }
}

@Composable
private fun PiezaBanco(nombre: String, onClick: () -> Unit, contenido: @Composable () -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .scale(if (presionado) 0.92f else 1f)
                .shadow(3.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF6DD))
                .clickable(interactionSource = interaccion, indication = null) { onClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) { contenido() }
        Text(nombre, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
    }
}

/** Una perla dorada suelta — la unidad del material real. */
@Composable
private fun Perla(lado: Dp) {
    Box(Modifier.size(lado).clip(CircleShape).background(COLOR_PERLA).shadow(0.5.dp, CircleShape))
}

/** Una barra de 10 perlas — la decena, dibujada con Canvas para que dé igual cuántas haya en pantalla. */
@Composable
private fun BarraDecena(ancho: Dp = 84.dp, alto: Dp = 10.dp) {
    Canvas(Modifier.size(width = ancho, height = alto)) {
        val radio = size.height / 2.3f
        val paso = size.width / 10f
        repeat(10) { i -> drawCircle(color = COLOR_PERLA, radius = radio, center = Offset(paso * i + paso / 2f, size.height / 2f)) }
    }
}

/** Un cuadrado de 10x10 = 100 perlas — la centena, mismo dibujo compacto vía Canvas. */
@Composable
private fun CuadradoCentena(lado: Dp = 54.dp) {
    Canvas(Modifier.size(lado)) {
        val radio = size.width / 24f
        val paso = size.width / 10f
        for (fila in 0 until 10) {
            for (col in 0 until 10) {
                drawCircle(color = COLOR_PERLA, radius = radio, center = Offset(paso * col + paso / 2f, paso * fila + paso / 2f))
            }
        }
    }
}
