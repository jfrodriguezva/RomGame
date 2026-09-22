package com.miambiente.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Services
import com.miambiente.app.game.LogicBlockGdxActivity
import com.miambiente.app.game.MemoriaGdxActivity
import com.miambiente.app.game.AlignmentGdxActivity
import com.miambiente.app.game.GodotArcadeActivity
import com.miambiente.app.theme.MiAmbienteTheme
import com.miambiente.app.ui.screens.*

object Ruta { const val INICIO="inicio";const val AJUSTES="ajustes";const val FAMILIA="familia/{id}" }

class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);enableEdgeToEdge();val services=Services(applicationContext);setContent{MiAmbienteTheme{CompositionLocalProvider(LocalServices provides services){
  val settings by produceState<com.miambiente.app.data.Settings?>(null,services){services.settings.settings.collect{value=it}};val actual=settings?:return@CompositionLocalProvider;val nav=rememberNavController();val volver={nav.popBackStack();Unit}
  NavHost(navController=nav,startDestination=Ruta.INICIO){
   composable(Ruta.INICIO){HomeScreen(onAbrirFamilia={nav.navigate("familia/$it")},onAjustes={nav.navigate(Ruta.AJUSTES)},nombre=actual.nombre)}
   composable(Ruta.FAMILIA){entry->val context=LocalContext.current;FamiliaScreen(entry.arguments?.getString("id").orEmpty(),onAbrirJuego={id->when{id in setOf("gato","conecta4","rps","oca","serpientes","dado","solitario","arana-cartas","loteria","bingo","damas","damas-chinas","ajedrez","domino","adivinaquien")->context.startActivity(Intent(context,AlignmentGdxActivity::class.java).putExtra(AlignmentGdxActivity.EXTRA_GAME_ID,id));id in setOf("tetris","snake","arkanoid","mosaico","vaqueros","comepuntos","nieve","escuadron-estelar","gran-premio","carreras")->context.startActivity(Intent(context,GodotArcadeActivity::class.java).putExtra(GodotArcadeActivity.EXTRA_GAME_ID,id));else->nav.navigate(id)}},onAbrirMotor={id->if(id=="memoria-observacion")context.startActivity(Intent(context,MemoriaGdxActivity::class.java))else context.startActivity(Intent(context,LogicBlockGdxActivity::class.java).putExtra(LogicBlockGdxActivity.EXTRA_FAMILY_ID,id))},onVolver=volver)}
   composable(Ruta.AJUSTES){AjustesScreen(volver)}
  }
 }}}}
}
