package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.Categoria
import com.miambiente.app.model.FAMILIAS
import com.miambiente.app.model.FamiliaJuego
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta

@Composable
fun HomeScreen(onAbrirFamilia:(String)->Unit,onAjustes:()->Unit,nombre:String=""){
    var categoria by remember{mutableStateOf(Categoria.LOGICA)}
    val familias=FAMILIAS.filter{it.categoria==categoria}
    Column(Modifier.fillMaxSize().background(Papel).safeDrawingPadding()){
        Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
                Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF8BBF6A)),contentAlignment=Alignment.Center){Text("🎮",fontSize=23.sp)}
                Column{Text("Hola ${if(nombre.isBlank())"" else nombre} 👋",fontWeight=FontWeight.ExtraBold,color=Tinta,fontSize=22.sp);Text("${FAMILIAS.size} juegos · modos y niveles",color=TextoSuave,fontSize=12.sp)}
            }
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).clickable(onClick=onAjustes),contentAlignment=Alignment.Center){Text("⚙️")}
        }
        LazyRow(contentPadding=PaddingValues(horizontal=16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            items(Categoria.entries){cat->FilterChip(selected=categoria==cat,onClick={categoria=cat},label={Text("${cat.emoji} ${cat.titulo} (${FAMILIAS.count{it.categoria==cat}})")})}
        }
        LazyVerticalGrid(GridCells.Adaptive(170.dp),contentPadding=PaddingValues(16.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp),modifier=Modifier.fillMaxSize()){
            items(familias,key={it.id}){familia->TarjetaFamilia(familia){onAbrirFamilia(familia.id)}}
        }
    }
}

@Composable private fun TarjetaFamilia(familia:FamiliaJuego,onClick:()->Unit){
    Card(onClick=onClick,shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White),modifier=Modifier.height(150.dp)){
        Column(Modifier.fillMaxSize().padding(14.dp)){
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(familia.emoji,fontSize=30.sp);Text("${familia.modos.size} ${if(familia.modos.size==1)"juego" else "modos"}",fontSize=10.sp,color=TextoSuave)}
            Text(familia.titulo,fontWeight=FontWeight.ExtraBold,color=Tinta,fontSize=15.sp,maxLines=2,overflow=TextOverflow.Ellipsis,modifier=Modifier.padding(top=8.dp))
            Text(familia.descripcion,color=TextoSuave,fontSize=11.sp,maxLines=2,overflow=TextOverflow.Ellipsis,modifier=Modifier.padding(top=4.dp))
        }
    }
}
