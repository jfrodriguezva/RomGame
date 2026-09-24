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
    private var sortDestinations = emptyList<String>()
    private var sortItems = emptyList<SortItem>()
    private var sortIndex = 0
    private var draggingSort = false
    private var oddIndex = 0
    private var paused = false
    private var tutorial = false
    private var binomialGoal = emptyList<Int>()
    private var binomialCurrent = emptyList<Int>()
    private var puzzlePieces = emptyList<Int>()
    private var puzzleSlots = List<Int?>(4) { null }
    private var draggingPuzzle = -1

    override fun create() {
        shapes = ShapeRenderer()
        batch = SpriteBatch()
        font = GameTypography.create(25)
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val point = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                tap(point.x, point.y)
                return true
            }
            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                if (draggingPuzzle >= 0 && screen == Screen.PLAY) {
                    val point = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                    dropPuzzle(point.x, point.y)
                    return true
                }
                if (draggingSort && screen == Screen.PLAY) {
                    val point = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                    dropSort(point.x, point.y)
                    return true
                }
                if (!isSequenceMode() || dragging < 0 || screen != Screen.PLAY) return false
                val point = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                dropSequence(point.x)
                return true
            }
        }
    }

    private fun start(selected: Int = mode, showTutorial: Boolean = false) {
        mode = selected
        step = 0
        score = 0
        feedback = "Resuelve ${roundsForLevel(level)} retos"
        paused = false
        tutorial = showTutorial
        round = logicRound(family.id, mode, level, step)
        if (isSequenceMode()) {
            expectedSequence = if (family.id == "orden-secuencias") sequenceFor(mode, level) else cylinderSequence(level)
            sequence = expectedSequence.shuffled().let { if (it == expectedSequence && it.size > 1) it.reversed() else it }
        }
        if (isSortMode()) {
            if (isSensorySort()) {
                sortRound = sensorySort(mode)
                sortDestinations = listOf(sortRound!!.left, sortRound!!.right)
                sortItems = sortRound!!.items.shuffled().take((4 + level / 8).coerceAtMost(6))
            } else {
                val shape = shapeSort(mode)
                sortRound = null
                sortDestinations = shape.destinations
                sortItems = shape.items.shuffled().take((4 + level / 8).coerceAtMost(6))
            }
            sortIndex = 0
            draggingSort = false
            feedback = "Arrastra cada objeto a su grupo"
        }
        if (isDifferences()) {
            oddIndex = kotlin.random.Random.nextInt(differenceCellCount(level))
            feedback = "Encuentra el símbolo diferente"
        }
        if (isBinomial()) {
            binomialGoal = binomialTarget(level)
            binomialCurrent = List(4) { (binomialGoal[it] + 1 + it) % 4 }
            feedback = "Toca cada cuadro hasta copiar el patrón"
        }
        if (isPuzzle()) {
            puzzlePieces = (0..3).shuffled()
            puzzleSlots = List(4) { null }
            draggingPuzzle = -1
            feedback = "Arrastra cada pieza a su espacio"
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
                    if (x in left..left + 250f && y in bottom..bottom + 80f) start(index, true)
                }
            }
            Screen.PLAY -> {
                if (tutorial) { tutorial = false; return }
                if (paused) { if (y > 485f && x in 500f..640f) paused = false; return }
                if (y > 485f) {
                    when {
                        x < 115f -> screen = Screen.MODES
                        x < 230f -> { level = (level - 1).coerceAtLeast(1); start() }
                        x < 345f -> { level = (level + 1).coerceAtMost(20); start() }
                        x < 460f -> start()
                        x < 575f -> paused = true
                    }
                    return
                }
                if (isSequenceMode() && y in 190f..340f) {
                    dragging = sequenceIndexAt(x)
                    feedback = if (dragging >= 0) "Arrastra la pieza a su lugar" else feedback
                    return
                }
                if (isSortMode() && y in 235f..390f && x in 340f..620f) {
                    draggingSort = true
                    feedback = "Suelta sobre el grupo correcto"
                    return
                }
                if (isBinomial()) {
                    val cell = binomialIndexAt(x, y)
                    if (cell >= 0) {
                        binomialCurrent = binomialCurrent.toMutableList().also { it[cell] = (it[cell] + 1) % 4 }
                        if (binomialCurrent == binomialGoal) {
                            score = 1000 - level * 5
                            screen = Screen.RESULT
                            onComplete(family.id, level, score)
                        }
                    }
                    return
                }
                if (isPuzzle() && y in 90f..190f) {
                    val index = ((x - 260f) / 110f).toInt()
                    if (index in puzzlePieces.indices) draggingPuzzle = index
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
    private fun isShapeSort() = family.id == "formas-encajes" && mode in 2..3
    private fun isSortMode() = isSensorySort() || isShapeSort()
    private fun isSequenceMode() = family.id == "orden-secuencias" || (family.id == "formas-encajes" && mode == 4)
    private fun isBinomial() = family.id == "formas-encajes" && mode == 5
    private fun isPuzzle() = family.id == "formas-encajes" && mode == 6
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
        val destination = ((x - 90f) / (780f / sortDestinations.size)).toInt().coerceIn(sortDestinations.indices)
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

    private fun binomialIndexAt(x: Float, y: Float): Int {
        if (x !in 560f..760f || y !in 185f..385f) return -1
        val col = ((x - 560f) / 100f).toInt().coerceAtMost(1)
        val row = ((385f - y) / 100f).toInt().coerceAtMost(1)
        return row * 2 + col
    }

    private fun dropPuzzle(x: Float, y: Float) {
        val pieceListIndex = draggingPuzzle
        draggingPuzzle = -1
        if (pieceListIndex !in puzzlePieces.indices || x !in 330f..630f || y !in 220f..420f) return
        val col = ((x - 330f) / 150f).toInt().coerceAtMost(1)
        val row = ((420f - y) / 100f).toInt().coerceAtMost(1)
        val slot = row * 2 + col
        val piece = puzzlePieces[pieceListIndex]
        if (piece == slot) {
            puzzleSlots = puzzleSlots.toMutableList().also { it[slot] = piece }
            puzzlePieces = puzzlePieces.toMutableList().also { it.removeAt(pieceListIndex) }
            if (puzzlePieces.isEmpty()) {
                score = 1000 - level * 5
                screen = Screen.RESULT
                onComplete(family.id, level, score)
            } else feedback = "¡Pieza colocada!"
        } else feedback = "Esa pieza pertenece a otro espacio"
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
        compactButton(8f, "MODOS")
        compactButton(123f, "NIVEL -")
        compactButton(238f, "NIVEL +")
        compactButton(353f, "REINICIAR")
        compactButton(468f, if (paused) "SEGUIR" else "PAUSA")
        text("${family.modes[mode]} · Nivel $level", 770f, 516f, Color.WHITE, Align.center)
        text(round.prompt, 480f, 430f, Color(0xE0C23CFF.toInt()), Align.center)
        if (isSequenceMode()) renderSequence() else if (isSortMode()) renderSort() else if (isDifferences()) renderDifferences() else if (isBinomial()) renderBinomial() else if (isPuzzle()) renderPuzzle() else round.options.forEachIndexed { index, label ->
                val x = 170f + (index % 2) * 320f
                val y = 275f - (index / 2) * 130f
                box(x, y, 280f, 105f, Color(0x243E78FF.toInt()))
                if (family.id == "formas-encajes") geometryIcon(x + 48f, y + 52f, label, index)
                text(label, x + 140f, y + 63f, Color.WHITE, Align.center)
            }
        text(feedback, 480f, 70f, Color.LIGHT_GRAY, Align.center)
        if (tutorial) renderOverlay("CÓMO JUGAR", logicTutorial(family.id), "TOCA PARA EMPEZAR")
        if (paused) renderOverlay("PAUSA", "Tu partida está guardada en pantalla.", "TOCA SEGUIR")
    }

    private fun renderSort() {
        val width = 780f / sortDestinations.size
        sortDestinations.forEachIndexed { index, label ->
            val x = 90f + index * width
            box(x + 5f, 105f, width - 10f, 125f, if (index % 2 == 0) Color(0x6B4FA3FF.toInt()) else Color(0x3154B5FF.toInt()))
            text(label, x + width / 2f, 178f, Color.WHITE, Align.center)
        }
        if (sortIndex in sortItems.indices) {
            box(340f, 270f, 280f, 115f, if (draggingSort) Color(0xE0C23CFF.toInt()) else Color(0x4C9A5FFF.toInt()))
            text(sortItems[sortIndex].label, 480f, 338f, Color.WHITE, Align.center)
            if (isShapeSort()) geometryIcon(395f, 325f, sortItems[sortIndex].label, sortItems[sortIndex].destination)
        }
    }

    private fun renderBinomial() {
        text("PATRÓN", 320f, 405f, Color.LIGHT_GRAY, Align.center)
        text("TU CUBO", 660f, 405f, Color.LIGHT_GRAY, Align.center)
        renderColorGrid(220f, 185f, binomialGoal)
        renderColorGrid(560f, 185f, binomialCurrent)
    }

    private fun renderColorGrid(left: Float, bottom: Float, colors: List<Int>) {
        val palette = listOf(Color(0xD9433AFF.toInt()), Color(0x3E7AA3FF.toInt()), Color(0xE0C23CFF.toInt()), Color(0x4C7A3AFF.toInt()))
        colors.forEachIndexed { index, color ->
            val x = left + (index % 2) * 100f
            val y = bottom + (1 - index / 2) * 100f
            box(x + 4f, y + 4f, 92f, 92f, palette[color])
        }
    }

    private fun renderPuzzle() {
        repeat(4) { slot ->
            val x = 330f + (slot % 2) * 150f
            val y = 320f - (slot / 2) * 100f
            box(x + 5f, y + 5f, 140f, 90f, if (puzzleSlots[slot] != null) Color(0x4C9A5FFF.toInt()) else Color(0x243E78FF.toInt()))
            text(if (puzzleSlots[slot] != null) puzzleLabel(slot) else "${slot + 1}", x + 75f, y + 58f, Color.WHITE, Align.center)
        }
        puzzlePieces.forEachIndexed { index, piece ->
            val x = 260f + index * 110f
            box(x, 90f, 95f, 80f, if (index == draggingPuzzle) Color(0xE0C23CFF.toInt()) else Color(0x6B4FA3FF.toInt()))
            text(puzzleLabel(piece), x + 48f, 140f, Color.WHITE, Align.center)
        }
    }

    private fun puzzleLabel(piece: Int) = listOf("Sol", "Nube", "Árbol", "Casa")[piece]

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
            val height = if (family.id == "orden-secuencias" && mode == 0) 45f + label.toInt() * 7f else if (family.id == "formas-encajes") 45f + label.toInt() * 7f else 110f
            box(x + 5f, 200f, width - 10f, height, if (index == dragging) Color(0xE0C23CFF.toInt()) else Color(0x6B4FA3FF.toInt()))
            text(label, x + width / 2f, 265f, Color.WHITE, Align.center)
        }
        text("Orden correcto de izquierda a derecha", 480f, 150f, Color.LIGHT_GRAY, Align.center)
    }

    private fun renderResult() {
        text("NIVEL SUPERADO", 480f, 410f, Color.WHITE, Align.center)
        val earned = starsForScore(score)
        text("Puntuación: $score   Estrellas: $earned/3", 480f, 330f, Color(0xE0C23CFF.toInt()), Align.center)
        box(230f, 80f, 220f, 75f, Color(0x3D507AFF.toInt()))
        box(510f, 80f, 220f, 75f, Color(0x4C9A5FFF.toInt()))
        text("MODOS", 340f, 128f, Color.WHITE, Align.center)
        text("SIGUIENTE", 620f, 128f, Color.WHITE, Align.center)
    }

    private fun button(x: Float, label: String) {
        box(x, 488f, 120f, 40f, Color(0x3D507AFF.toInt()))
        text(label, x + 60f, 516f, Color.WHITE, Align.center)
    }
    private fun compactButton(x:Float,label:String){box(x,488f,107f,40f,Color(0x3D507AFF.toInt()));text(label,x+53f,516f,Color.WHITE,Align.center)}
    private fun renderOverlay(title:String,body:String,action:String){box(150f,145f,660f,250f,Color(0x111A2CFA.toInt()));text(title,480f,345f,Color(0xE0C23CFF.toInt()),Align.center);text(body,480f,275f,Color.WHITE,Align.center);text(action,480f,205f,Color.LIGHT_GRAY,Align.center)}

    private fun box(x: Float, y: Float, width: Float, height: Float, color: Color) {
        shapes.begin(ShapeRenderer.ShapeType.Filled)
        shapes.color = color
        shapes.roundedRect(x, y, width, height)
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
