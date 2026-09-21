package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarFamilia
import com.miambiente.app.model.juegosDe
import com.miambiente.app.model.Motor
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta

@Composable fun FamiliaScreen(id:String,onAbrirJuego:(String)->Unit,onAbrirMotor:(String)->Unit,onVolver:()->Unit){
    val familia=buscarFamilia(id)?:return
    val juegos=juegosDe(familia)
    Column(Modifier.fillMaxSize().background(Papel).safeDrawingPadding().padding(16.dp)){
        TextButton(onClick=onVolver){Text("← Volver")}
        Text("${familia.emoji} ${familia.titulo}",fontWeight=FontWeight.ExtraBold,color=Tinta,fontSize=26.sp)
        Text(familia.descripcion,color=TextoSuave,modifier=Modifier.padding(vertical=6.dp))
        if(familia.motor==Motor.LIBGDX){Button(onClick={onAbrirMotor(familia.id)},modifier=Modifier.padding(vertical=8.dp)){Text("🎮 Jugar versión enriquecida")}}
        Text("Elige un modo",fontWeight=FontWeight.Bold,color=Tinta,modifier=Modifier.padding(top=8.dp,bottom=10.dp))
        LazyVerticalGrid(GridCells.Adaptive(170.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            items(juegos,key={it.id}){juego->Card(onClick={onAbrirJuego(juego.id)},shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color.White),modifier=Modifier.height(120.dp)){Column(Modifier.padding(14.dp)){Text("${juego.emoji}  ${juego.title}",fontWeight=FontWeight.Bold,color=Tinta);Text(juego.description,color=TextoSuave,fontSize=11.sp,modifier=Modifier.padding(top=8.dp))}}}
        }
    }
}
