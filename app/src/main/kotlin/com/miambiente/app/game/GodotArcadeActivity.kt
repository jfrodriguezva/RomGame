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
        installGameChrome()
    }

    override fun getCommandLine(): MutableList<String> {
        val gameId = intent.getStringExtra(EXTRA_GAME_ID) ?: "tetris"
        return arcadeCommandLine(super.getCommandLine(), gameId).toMutableList()
    }

    override fun getHostPlugins(godot: Godot): Set<GodotPlugin> =
        setOf(RomGameProgressPlugin(godot, applicationContext, persistenceScope))

    /**
     * La implementación base usa ProcessPhoenix.forceQuit y mata también
     * MainActivity. Un Arcade debe cerrar solo su pantalla y regresar al
     * catálogo, nunca terminar todo RomGame.
     */
    override fun onGodotForceQuit(instance: Godot) {
        runOnUiThread {
            if (!isFinishing) finish()
        }
    }

    override fun onDestroy() {
        persistenceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_GAME_ID = "gameId"
    }
}

/**
 * Fuerza OpenGL en Android. Vulkan cerraba el proceso completo en
 * dispositivos sin un controlador Vulkan estable (incluido el emulador).
 */
internal fun arcadeCommandLine(base: List<String>, gameId: String): List<String> =
    base + listOf(
        "--rendering-method", "gl_compatibility",
        "--rendering-driver", "opengl3",
        "--", "--game-id=$gameId",
    )
