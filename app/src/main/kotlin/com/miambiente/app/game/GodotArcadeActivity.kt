package com.miambiente.app.game

import android.os.Bundle
import com.miambiente.app.data.ProgressStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.godotengine.godot.Godot
import org.godotengine.godot.GodotActivity
import org.godotengine.godot.plugin.GodotPlugin

class GodotArcadeActivity : GodotActivity() {
    private val persistenceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameId = intent.getStringExtra(EXTRA_GAME_ID) ?: "tetris"
        persistenceScope.launch { ProgressStore(applicationContext).registrarJugada(gameId) }
    }

    override fun getCommandLine(): MutableList<String> {
        val gameId = intent.getStringExtra(EXTRA_GAME_ID) ?: "tetris"
        return super.getCommandLine().toMutableList().apply {
            add("--")
            add("--game-id=$gameId")
        }
    }

    override fun getHostPlugins(godot: Godot): Set<GodotPlugin> =
        setOf(RomGameProgressPlugin(godot, applicationContext, persistenceScope))

    override fun onDestroy() {
        persistenceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_GAME_ID = "gameId"
    }
}
