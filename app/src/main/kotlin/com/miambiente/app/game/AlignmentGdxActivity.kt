package com.miambiente.app.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.miambiente.app.data.ProgressStore
import com.rominagame.core.AlignmentGame
import com.rominagame.core.PathBoardGame
import com.rominagame.core.SolitaireGame
import com.rominagame.core.LotteryGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AlignmentGdxActivity:AndroidApplication(){
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);val id=intent.getStringExtra(EXTRA_GAME_ID)?:run{finish();return};val progress=ProgressStore(applicationContext);val config=AndroidApplicationConfiguration().apply{useImmersiveMode=true;useAccelerometer=false;useCompass=false};val complete:(String,Int,Int)->Unit={game,level,_->scope.launch{progress.completarNivel(game,level);progress.registrarJugada(game)}};val game=when{id in setOf("oca","serpientes","dado")->PathBoardGame(id,complete);id in setOf("solitario","arana-cartas")->SolitaireGame(id=="arana-cartas",complete);id in setOf("loteria","bingo")->LotteryGame(id=="bingo",complete);else->AlignmentGame(id,complete)};initialize(game,config)}
    override fun onDestroy(){scope.cancel();super.onDestroy()}
    companion object{const val EXTRA_GAME_ID="gameId"}
}
