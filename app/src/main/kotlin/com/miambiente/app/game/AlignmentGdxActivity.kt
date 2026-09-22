package com.miambiente.app.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.miambiente.app.data.ProgressStore
import com.rominagame.core.AlignmentGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AlignmentGdxActivity:AndroidApplication(){
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);val id=intent.getStringExtra(EXTRA_GAME_ID)?:run{finish();return};val progress=ProgressStore(applicationContext);val config=AndroidApplicationConfiguration().apply{useImmersiveMode=true;useAccelerometer=false;useCompass=false};initialize(AlignmentGame(id){game,level,_->scope.launch{progress.completarNivel(game,level);progress.registrarJugada(game)}},config)}
    override fun onDestroy(){scope.cancel();super.onDestroy()}
    companion object{const val EXTRA_GAME_ID="gameId"}
}
