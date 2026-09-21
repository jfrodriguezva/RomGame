package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.*
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Carrera estable: usa una grilla lógica y evita recomponer listas en cada frame. */
@Composable fun CarrerasScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("carreras")!!
    var carril by remember { mutableIntStateOf(1) }
    var obstaculoCarril by remember { mutableIntStateOf(0) }
    var obstaculoFila by remember { mutableIntStateOf(0) }
    var distancia by remember { mutableIntStateOf(0) }
    var jugando by remember { mutableStateOf(false) }
    var chocado by remember { mutableStateOf(false) }
    var ronda by remember { mutableIntStateOf(0) }
    val meta = 25
    fun iniciar() { carril=1; obstaculoCarril=(0..2).random(); obstaculoFila=0; distancia=0; chocado=false; jugando=true; ronda++ }
    LaunchedEffect(ronda, jugando) {
        if (!jugando) return@LaunchedEffect
        while (jugando && !chocado && distancia < meta) {
            delay((520L-distancia*9L).coerceAtLeast(280L)); obstaculoFila++
            if (obstaculoFila==6 && obstaculoCarril==carril) { chocado=true; jugando=false; services.sound.tocar(Efecto.WRONG); services.haptics.vibrar(Patron.ERROR) }
            else if (obstaculoFila>6) { distancia++; obstaculoFila=0; obstaculoCarril=(0..2).filter { it!=obstaculoCarril }.random() }
        }
        if (jugando && distancia>=meta) { jugando=false; services.sound.tocar(Efecto.WIN); scope.launch { services.progress.completarNivel(juego.id,1) } }
    }
    GameShell(juego, when { chocado->"¡Choque! Intenta otro carril"; distancia>=meta->"¡Llegaste a la meta!"; else->"Distancia: $distancia / $meta" }, onVolver=onVolver, acciones={ Button(onClick=::iniciar){ Text(if(jugando)"Reiniciar" else "Arrancar") } }) {
        Column(Modifier.fillMaxSize().background(Color(0xFF3F4347)).padding(10.dp), horizontalAlignment=Alignment.CenterHorizontally) {
            repeat(6){f-> Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){ repeat(3){c-> Box(Modifier.size(82.dp,58.dp).background(if(c==1)Color(0xFF4B4F53) else Color(0xFF45494D)),contentAlignment=Alignment.Center){ if(jugando&&obstaculoFila==f&&obstaculoCarril==c) Text("🚧",fontSize=30.sp) } } } }
            Row(Modifier.fillMaxWidth().padding(top=8.dp),horizontalArrangement=Arrangement.SpaceEvenly){ repeat(3){c-> Box(Modifier.size(82.dp,64.dp).clip(RoundedCornerShape(14.dp)).background(if(carril==c)Color(0xFF98C6E8) else Color(0xFFE1E1E1)).clickable(enabled=jugando){carril=c},contentAlignment=Alignment.Center){if(carril==c)Text("🏎️",fontSize=38.sp)} } }
        }
    }
}
