package com.miambiente.app.game

import android.content.Context
import com.miambiente.app.data.ProgressStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class RomGameProgressPlugin(
    godot: Godot,
    context: Context,
    private val scope: CoroutineScope,
) : GodotPlugin(godot) {
    private val progress = ProgressStore(context)

    override fun getPluginName(): String = "RomGameProgress"

    @UsedByGodot
    fun completeLevel(gameId: String, level: Int, score: Int) {
        scope.launch {
            progress.completarNivel(gameId, level)
            getActivity()?.setResult(
                android.app.Activity.RESULT_OK,
                getActivity()?.intent?.putExtra("level", level)?.putExtra("score", score),
            )
        }
    }
}
