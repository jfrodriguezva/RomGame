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
    private var expectedSequence = emptyList<String>()
    private var sequence = emptyList<String>()
    private var dragging = -1
    private var sortRound: SortRound? = null
    private var sortItems = emptyList<SortItem>()
    private var sortIndex = 0
    private var draggingSort = false
    private var oddIndex = 0

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
            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (draggingSort && screen == Screen.PLAY) {
                    val point = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                    dropSort(point.x, point.y)
                    return true
                }
                if (family.id != "orden-secuencias" || dragging < 0 || screen != Screen.PLAY) return false
                val point = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                dropSequence(point.x)
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
        if (family.id == "orden-secuencias") {
            expectedSequence = sequenceFor(mode, level)
            sequence = expectedSequence.shuffled().let { if (it == expectedSequence && it.size > 1) it.reversed() else it }
        }
        if (isSensorySort()) {
            sortRound = sensorySort(mode)
            sortItems = sortRound!!.items.shuffled().take((4 + level / 8).coerceAtMost(6))
            sortIndex = 0
            draggingSort = false
            feedback = "Arrastra cada objeto a su grupo"
        }
        if (isDifferences()) {
            oddIndex = kotlin.random.Random.nextInt(differenceCellCount(level))
            feedback = "Encuentra el símbolo diferente"
        }
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
                if (family.id == "orden-secuencias" && y in 190f..315f) {
                    dragging = sequenceIndexAt(x)
                    feedback = if (dragging >= 0) "Arrastra la pieza a su lugar" else feedback
                    return
                }
                if (isSensorySort() && y in 235f..390f && x in 340f..620f) {
                    draggingSort = true
                    feedback = "Suelta sobre el grupo correcto"
                    return
                }
                if (isDifferences()) {
                    val selected = differenceIndexAt(x, y)
                    if (selected == oddIndex) {
                        score = 1000 - level * 5
                        screen = Screen.RESULT
                        onComplete(family.id, level, score)
                    } else if (selected >= 0) feedback = "Mira con atención: ese no cambia"
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

    private fun isSensorySort() = family.id == "percepcion" && mode in 1..5
    private fun isDifferences() = family.id == "percepcion" && mode == 8

    private fun differenceIndexAt(x: Float, y: Float): Int {
        val count = differenceCellCount(level)
        val cols = if (count <= 9) 3 else 5
        val rows = (count + cols - 1) / cols
        val width = 120f
        val height = 95f
        val left = (960f - cols * width) / 2f
        val bottom = 105f + (285f - rows * height) / 2f
        if (x !in left..left + cols * width || y !in bottom..bottom + rows * height) return -1
        val col = ((x - left) / width).toInt().coerceAtMost(cols - 1)
        val row = ((y - bottom) / height).toInt().coerceAtMost(rows - 1)
        return (row * cols + col).takeIf { it < count } ?: -1
    }

    private fun dropSort(x: Float, y: Float) {
        draggingSort = false
        if (y !in 105f..235f || sortIndex !in sortItems.indices) {
            feedback = "Lleva el objeto hasta una canasta"
            return
        }
        val destination = if (x < 480f) 0 else 1
        if (sortItems[sortIndex].destination == destination) {
            score += 100
            sortIndex++
            if (sortIndex >= sortItems.size) {
                screen = Screen.RESULT
                onComplete(family.id, level, score)
            } else feedback = "¡Correcto! Faltan ${sortItems.size - sortIndex}"
        } else {
            score = (score - 10).coerceAtLeast(0)
            feedback = "Ese grupo no corresponde"
        }
    }

    private fun sequenceIndexAt(x: Float): Int {
        if (sequence.isEmpty()) return -1
        val width = 780f / sequence.size
        return ((x - 90f) / width).toInt().takeIf { it in sequence.indices } ?: -1
    }

    private fun dropSequence(x: Float) {
        val target = sequenceIndexAt(x)
        val source = dragging
        dragging = -1
        if (source !in sequence.indices || target !in sequence.indices) return
        val changed = sequence.toMutableList()
        val item = changed.removeAt(source)
        changed.add(target, item)
        sequence = changed
        if (sequence == expectedSequence) {
            score = 1000 - level * 5
            feedback = "¡Secuencia completa!"
            screen = Screen.RESULT
            onComplete(family.id, level, score)
        } else feedback = "Sigue ordenando"
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
        if (family.id == "orden-secuencias") renderSequence() else if (isSensorySort()) renderSensorySort() else if (isDifferences()) renderDifferences() else round.options.forEachIndexed { index, label ->
                val x = 170f + (index % 2) * 320f
                val y = 275f - (index / 2) * 130f
                box(x, y, 280f, 105f, Color(0x243E78FF.toInt()))
                if (family.id == "formas-encajes") geometryIcon(x + 48f, y + 52f, label, index)
                text(label, x + 140f, y + 63f, Color.WHITE, Align.center)
            }
        text(feedback, 480f, 70f, Color.LIGHT_GRAY, Align.center)
    }

    private fun renderSensorySort() {
        val definition = sortRound ?: return
        box(90f, 105f, 350f, 125f, Color(0x6B4FA3FF.toInt()))
        box(520f, 105f, 350f, 125f, Color(0x3154B5FF.toInt()))
        text(definition.left, 265f, 178f, Color.WHITE, Align.center)
        text(definition.right, 695f, 178f, Color.WHITE, Align.center)
        if (sortIndex in sortItems.indices) {
            box(340f, 270f, 280f, 115f, if (draggingSort) Color(0xE0C23CFF.toInt()) else Color(0x4C9A5FFF.toInt()))
            text(sortItems[sortIndex].label, 480f, 338f, Color.WHITE, Align.center)
        }
    }

    private fun renderDifferences() {
        val count = differenceCellCount(level)
        val cols = if (count <= 9) 3 else 5
        val rows = (count + cols - 1) / cols
        val width = 120f
        val height = 95f
        val left = (960f - cols * width) / 2f
        val bottom = 105f + (285f - rows * height) / 2f
        repeat(count) { index ->
            val x = left + (index % cols) * width
            val y = bottom + (index / cols) * height
            box(x + 5f, y + 5f, width - 10f, height - 10f, Color(0x243E78FF.toInt()))
            shapes.begin(ShapeRenderer.ShapeType.Filled)
            shapes.color = if (index == oddIndex) Color(0xE0C23CFF.toInt()) else Color(0xE6E8F0FF.toInt())
            if (index == oddIndex) shapes.triangle(x + 60f, y + 70f, x + 30f, y + 25f, x + 90f, y + 25f)
            else shapes.circle(x + 60f, y + 48f, 27f, 32)
            shapes.end()
        }
    }

    private fun geometryIcon(x: Float, y: Float, label: String, index: Int) {
        shapes.begin(ShapeRenderer.ShapeType.Filled)
        shapes.color = Color(0xF3DBE3FF.toInt())
        when {
            label.contains("Círculo", true) || label.contains("Esfera", true) -> shapes.circle(x, y, 25f, 32)
            label.contains("Triángulo", true) || label.contains("Pirámide", true) -> shapes.triangle(x, y + 28f, x - 27f, y - 24f, x + 27f, y - 24f)
            index % 2 == 0 -> shapes.rect(x - 25f, y - 25f, 50f, 50f)
            else -> shapes.rect(x - 30f, y - 20f, 60f, 40f)
        }
        shapes.end()
    }

    private fun renderSequence() {
        val width = 780f / sequence.size
        sequence.forEachIndexed { index, label ->
            val x = 90f + index * width
            val height = if (mode == 0) 45f + label.toInt() * 7f else 110f
            box(x + 5f, 200f, width - 10f, height, if (index == dragging) Color(0xE0C23CFF.toInt()) else Color(0x6B4FA3FF.toInt()))
            text(label, x + width / 2f, 265f, Color.WHITE, Align.center)
        }
        text("Orden correcto de izquierda a derecha", 480f, 150f, Color.LIGHT_GRAY, Align.center)
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
