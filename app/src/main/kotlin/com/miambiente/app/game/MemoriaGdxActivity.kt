package com.miambiente.app.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.rominagame.core.MemoriaGame
import com.miambiente.app.data.ProgressStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MemoriaGdxActivity:AndroidApplication(){
    private val persistenceScope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        val progress=ProgressStore(applicationContext)
        val config=AndroidApplicationConfiguration().apply{useImmersiveMode=true;useAccelerometer=false;useCompass=false}
        initialize(MemoriaGame{mode,level,score->persistenceScope.launch{progress.completarNivel("memoria-observacion-$mode",level);progress.registrarJugada("memoria-observacion");setResult(RESULT_OK,intent.putExtra("mode",mode).putExtra("level",level).putExtra("score",score))}},config)
        installGameChrome()
    }
    override fun onDestroy(){persistenceScope.cancel();super.onDestroy()}
}
