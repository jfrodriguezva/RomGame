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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Caída por pasos estables, sin listas mutables ni bucle de estado por frame. */
@Composable fun CanastaScreen(onVolver: () -> Unit) {
    val services=LocalServices.current; val scope=rememberCoroutineScope(); val juego=buscarJuego("canasta")!!
    var canasta by remember{mutableIntStateOf(1)}; var estrellaCarril by remember{mutableIntStateOf(0)}; var estrellaFila by remember{mutableIntStateOf(0)}
    var atrapadas by remember{mutableIntStateOf(0)}; var perdidas by remember{mutableIntStateOf(0)}; var jugando by remember{mutableStateOf(false)}; var ronda by remember{mutableIntStateOf(0)}
    val meta=10
    fun iniciar(){canasta=1;atrapadas=0;perdidas=0;estrellaFila=0;estrellaCarril=(0..2).random();jugando=true;ronda++}
    LaunchedEffect(ronda,jugando){
        if(!jugando)return@LaunchedEffect
        while(jugando&&atrapadas<meta){delay((520L-atrapadas*18L).coerceAtLeast(300L));estrellaFila++;if(estrellaFila>=6){if(estrellaCarril==canasta){atrapadas++;services.sound.tocar(Efecto.CORRECT)}else perdidas++;estrellaFila=0;estrellaCarril=(0..2).random()}}
        if(jugando&&atrapadas>=meta){jugando=false;services.sound.tocar(Efecto.WIN);scope.launch{services.progress.completarNivel(juego.id,1)}}
    }
    GameShell(juego,if(atrapadas>=meta)"¡Las atrapaste!" else "Estrellas $atrapadas/$meta · Pasaron $perdidas",onVolver=onVolver,acciones={Button(onClick=::iniciar){Text(if(jugando)"Reiniciar" else "Jugar")}}){
        Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFE4F0FA),Color(0xFFEFF8EA)))).padding(10.dp)){
            repeat(6){f->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){repeat(3){c->Box(Modifier.size(82.dp,58.dp),contentAlignment=Alignment.Center){if(jugando&&f==estrellaFila&&c==estrellaCarril)Text("⭐",fontSize=30.sp)}}}}
            Row(Modifier.fillMaxWidth().padding(top=8.dp),horizontalArrangement=Arrangement.SpaceEvenly){repeat(3){c->Box(Modifier.size(82.dp,62.dp).clip(RoundedCornerShape(14.dp)).background(if(canasta==c)Color.White else Color.White.copy(alpha=.35f)).clickable(enabled=jugando){canasta=c},contentAlignment=Alignment.Center){if(canasta==c)Text("🧺",fontSize=40.sp)}}}
        }
    }
}
