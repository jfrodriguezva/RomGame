package com.miambiente.app.game

import org.godotengine.godot.GodotActivity

class GodotArcadeActivity : GodotActivity() {
    override fun getCommandLine(): MutableList<String> {
        val gameId = intent.getStringExtra(EXTRA_GAME_ID) ?: "tetris"
        return mutableListOf("--", "--game-id=$gameId")
    }

    companion object {
        const val EXTRA_GAME_ID = "gameId"
    }
}
