package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.model.maximoBancoDorado
import com.miambiente.app.theme.coloresDe
import com.miambiente.app.ui.GameShell
import com.miambiente.app.ui.materials.BotonSiguienteNivel
import com.miambiente.app.ui.materials.rememberMaterialState
import kotlin.random.Random

/**
 * El banco dorado — compón el número con centenas, decenas y unidades
 * (sistema decimal a la vista). Los números crecen con el nivel: primero
 * solo unidades, luego decenas y al final centenas (`maximoBancoDorado`).
 */
@Composable
fun BancoDoradoScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val juego = buscarJuego("banco-dorado")!!
    val estado = rememberMaterialState(juego)
    val colores = coloresDe(juego.area)

    val maximo = remember(estado.nivel) { maximoBancoDorado(estado.nivel) }
    val objetivo = remember(estado.nivel) { (1..maximo).random(Random(estado.nivel)) }
    var centenas by remember(estado.nivel) { mutableStateOf(0) }
    var decenas by remember(estado.nivel) { mutableStateOf(0) }
    var unidades by remember(estado.nivel) { mutableStateOf(0) }

    val cObjetivo = objetivo / 100
    val dObjetivo = (objetivo % 100) / 10
    val uObjetivo = objetivo % 10

    fun revisar() {
        if (estado.logrado) return
        services.sound.tocar(Efecto.CLICK)
        if (centenas == cObjetivo && decenas == dObjetivo && unidades == uObjetivo) estado.completar()
    }

    GameShell(
        juego = juego,
        consigna = if (estado.logrado) "¡Formaste el $objetivo!" else "Compón el número $objetivo",
        nota = estado.nota,
        celebrar = estado.logrado,
        onVolver = onVolver,
        selectorNivel = estado.selector,
        acciones = if (estado.logrado) {
            { BotonSiguienteNivel(colores, onClick = estado::siguiente) }
        } else null,
    ) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                when {
                    maximo >= 100 -> "$centenas$decenas$unidades"
                    maximo >= 10 -> "$decenas$unidades"
                    else -> "$unidades"
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(modifier = Modifier.padding(top = 24.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp)) {
                // Solo aparecen las posiciones que el nivel necesita.
                if (maximo >= 100) Contador("Centenas", centenas, 9, Color(0xFFE0C23C)) { centenas = it; revisar() }
                if (maximo >= 10) Contador("Decenas", decenas, 9, Color(0xFF3E7AA3)) { decenas = it; revisar() }
                Contador("Unidades", unidades, 9, Color(0xFFD9433A)) { unidades = it; revisar() }
            }
        }
    }
}

@Composable
private fun Contador(nombre: String, valor: Int, maximo: Int, color: Color, onCambiar: (Int) -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(nombre, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        androidx.compose.foundation.layout.Box(
            Modifier
                .size(56.dp)
                .scale(if (presionado) 0.9f else 1f)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(color)
                .clickable(interactionSource = interaccion, indication = null) { onCambiar((valor + 1) % (maximo + 1)) },
            contentAlignment = Alignment.Center,
        ) { Text("$valor", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold) }
    }
}
