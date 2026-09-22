package com.miambiente.app.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.miambiente.app.data.ProgressStore
import com.rominagame.core.LogicBlockGame
import com.rominagame.core.LanguageGame
import com.rominagame.core.CurriculumGame
import com.rominagame.core.FinalBlockGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LogicBlockGdxActivity : AndroidApplication() {
    private val persistenceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val familyId = intent.getStringExtra(EXTRA_FAMILY_ID) ?: run { finish(); return }
        val progress = ProgressStore(applicationContext)
        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
            useAccelerometer = false
            useCompass = false
        }
        val complete: (String, Int, Int) -> Unit = { id, level, score ->
            persistenceScope.launch {
                progress.completarNivel(id, level)
                progress.registrarJugada(id)
                setResult(RESULT_OK, intent.putExtra("level", level).putExtra("score", score))
            }
        }
        val game = when (familyId) {
            "palabras-sonidos", "construye-palabras" -> LanguageGame(familyId, complete)
            "numeros-cantidades", "clasifica-mundo", "naturaleza-planeta", "personas-comunidad", "vida-practica" -> CurriculumGame(familyId, complete)
            "taller-creativo", "coordinacion-reflejos", "laberintos" -> FinalBlockGame(familyId, complete)
            else -> LogicBlockGame(familyId, complete)
        }
        initialize(game, config)
    }

    override fun onDestroy() {
        persistenceScope.cancel()
        super.onDestroy()
    }

    companion object { const val EXTRA_FAMILY_ID = "familyId" }
}
