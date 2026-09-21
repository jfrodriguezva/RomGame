package com.miambiente.app.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.rominagame.core.MemoriaGame

class MemoriaGdxActivity:AndroidApplication(){
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);val config=AndroidApplicationConfiguration().apply{useImmersiveMode=true;useAccelerometer=false;useCompass=false};initialize(MemoriaGame{moves->runOnUiThread{setResult(RESULT_OK,intent.putExtra("moves",moves));finish()}},config)}
}
