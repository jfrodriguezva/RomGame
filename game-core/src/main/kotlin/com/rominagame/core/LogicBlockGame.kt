package com.rominagame.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport

class LogicBlockGame(
    familyId: String,
    private val onComplete: (String, Int, Int) -> Unit = { _, _, _ -> },
) : ApplicationAdapter() {
    private enum class Screen { MODES, PLAY, RESULT }

    private val family = logicFamily(familyId)
    private val viewport = FitViewport(960f, 540f)
    private lateinit var shapes: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private var screen = Screen.MODES
    private var mode = 0
    private var level = 1
    private var step = 0
    private var score = 0
    private var feedback = "Elige una modalidad"
    private var round = logicRound(family.id, 0, 1, 0)

    override fun create() {
        shapes = ShapeRenderer()
        batch = SpriteBatch()
        font = BitmapFont().apply { data.setScale(1.65f) }
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val point = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                tap(point.x, point.y)
                return true
            }
        }
    }

    private fun start(selected: Int = mode) {
        mode = selected
        step = 0
        score = 0
        feedback = "Resuelve ${roundsForLevel(level)} retos"
        round = logicRound(family.id, mode, level, step)
        screen = Screen.PLAY
    }

    private fun tap(x: Float, y: Float) {
        when (screen) {
            Screen.MODES -> {
                val cols = 3
                family.modes.indices.forEach { index ->
                    val left = 75f + (index % cols) * 290f
                    val bottom = 355f - (index / cols) * 110f
                    if (x in left..left + 250f && y in bottom..bottom + 80f) start(index)
                }
            }
            Screen.PLAY -> {
                if (y > 485f) {
                    when {
                        x < 145f -> screen = Screen.MODES
                        x < 300f -> { level = (level - 1).coerceAtLeast(1); start() }
                        x < 455f -> { level = (level + 1).coerceAtMost(20); start() }
                    }
                    return
                }
                val option = if (y in 95f..355f) {
                    val col = ((x - 170f) / 320f).toInt()
                    val row = ((355f - y) / 130f).toInt()
                    row * 2 + col
                } else -1
                if (option !in round.options.indices) return
                if (option == round.answer) {
                    score += 100
                    step++
                    if (step >= roundsForLevel(level)) {
                        screen = Screen.RESULT
                        onComplete(family.id, level, score)
                    } else {
                        feedback = "¡Correcto! ${step}/${roundsForLevel(level)}"
                        round = logicRound(family.id, mode, level, step)
                    }
                } else {
                    score = (score - 10).coerceAtLeast(0)
                    feedback = "Observa e inténtalo otra vez"
                }
            }
            Screen.RESULT -> if (y < 180f) {
                if (x < 480f) screen = Screen.MODES else { level = (level + 1).coerceAtMost(20); start() }
            }
        }
    }

    override fun render() {
        Gdx.gl.glClearColor(.035f, .055f, .09f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply()
        shapes.projectionMatrix = viewport.camera.combined
        batch.projectionMatrix = viewport.camera.combined
        when (screen) {
            Screen.MODES -> renderModes()
            Screen.PLAY -> renderPlay()
            Screen.RESULT -> renderResult()
        }
    }

    private fun renderModes() {
        text(family.title.uppercase(), 480f, 505f, Color.WHITE, Align.center)
        text("Modalidades conservadas · 20 niveles", 480f, 465f, Color.LIGHT_GRAY, Align.center)
        family.modes.forEachIndexed { index, label ->
            val x = 75f + (index % 3) * 290f
            val y = 355f - (index / 3) * 110f
            box(x, y, 250f, 80f, Color(0x3154B5FF.toInt()))
            text(label, x + 125f, y + 50f, Color.WHITE, Align.center)
        }
    }

    private fun renderPlay() {
        button(18f, "MODOS")
        button(160f, "NIVEL -")
        button(305f, "NIVEL +")
        text("${family.modes[mode]} · Nivel $level", 690f, 516f, Color.WHITE, Align.center)
        text(round.prompt, 480f, 430f, Color(0xE0C23CFF.toInt()), Align.center)
        round.options.forEachIndexed { index, label ->
            val x = 170f + (index % 2) * 320f
            val y = 275f - (index / 2) * 130f
            box(x, y, 280f, 105f, Color(0x243E78FF.toInt()))
            text(label, x + 140f, y + 63f, Color.WHITE, Align.center)
        }
        text(feedback, 480f, 70f, Color.LIGHT_GRAY, Align.center)
    }

    private fun renderResult() {
        text("NIVEL SUPERADO", 480f, 410f, Color.WHITE, Align.center)
        text("Puntuación: $score", 480f, 330f, Color(0xE0C23CFF.toInt()), Align.center)
        box(230f, 80f, 220f, 75f, Color(0x3D507AFF.toInt()))
        box(510f, 80f, 220f, 75f, Color(0x4C9A5FFF.toInt()))
        text("MODOS", 340f, 128f, Color.WHITE, Align.center)
        text("SIGUIENTE", 620f, 128f, Color.WHITE, Align.center)
    }

    private fun button(x: Float, label: String) {
        box(x, 488f, 120f, 40f, Color(0x3D507AFF.toInt()))
        text(label, x + 60f, 516f, Color.WHITE, Align.center)
    }

    private fun box(x: Float, y: Float, width: Float, height: Float, color: Color) {
        shapes.begin(ShapeRenderer.ShapeType.Filled)
        shapes.color = color
        shapes.rect(x, y, width, height)
        shapes.end()
    }

    private fun text(value: String, x: Float, y: Float, color: Color, align: Int) {
        batch.begin()
        font.color = color
        font.draw(batch, value, x - 220f, y, 440f, align, false)
        batch.end()
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)
    override fun dispose() { shapes.dispose(); batch.dispose(); font.dispose() }
}
