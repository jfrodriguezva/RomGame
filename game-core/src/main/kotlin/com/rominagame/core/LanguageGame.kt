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

class LanguageGame(familyId: String, private val onComplete: (String, Int, Int) -> Unit = { _, _, _ -> }) : ApplicationAdapter() {
    private enum class Screen { MODES, PLAY, RESULT }
    private val family = languageFamily(familyId)
    private val viewport = FitViewport(960f, 540f)
    private lateinit var shapes: ShapeRenderer; private lateinit var batch: SpriteBatch; private lateinit var font: BitmapFont
    private var screen = Screen.MODES; private var mode = 0; private var level = 1; private var score = 0
    private var challenge = languageChallenge(family.id, 0, 1); private var progress = 0; private var built = ""
    private var dragging = false; private var traceProgress = 0f; private var feedback = "Elige una modalidad"

    override fun create() {
        shapes = ShapeRenderer(); batch = SpriteBatch(); font = BitmapFont().apply { data.setScale(1.7f) }
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean { val p = point(x, y); tap(p.x, p.y); return true }
            override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
                if (mechanic() == LanguageMechanic.TRACE && screen == Screen.PLAY) { traceProgress += 1f; if (traceProgress >= 45f) finish(); return true }
                return false
            }
            override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
                if (!dragging) return false
                val p = point(x, y); dragging = false; dropSort(p.x, p.y); return true
            }
        }
    }
    private fun point(x: Int, y: Int) = viewport.unproject(Vector3(x.toFloat(), y.toFloat(), 0f))
    private fun mechanic() = family.modes[mode].mechanic
    private fun start(selected: Int = mode) { mode = selected; screen = Screen.PLAY; score = 0; progress = 0; built = ""; traceProgress = 0f; challenge = languageChallenge(family.id, mode, level); feedback = instruction() }
    private fun instruction() = when (mechanic()) { LanguageMechanic.ALPHABET -> "Toca las letras en orden"; LanguageMechanic.BUILD -> "Forma ${buildWord(level)}"; LanguageMechanic.SORT -> "Arrastra la palabra al grupo"; LanguageMechanic.TRACE -> "Desliza siguiendo la guía"; else -> "Elige la respuesta correcta" }
    private fun finish() { score = 1000 - level * 5; screen = Screen.RESULT; onComplete(family.id, level, score) }

    private fun tap(x: Float, y: Float) {
        when (screen) {
            Screen.MODES -> family.modes.indices.forEach { i -> val bx = 90f + (i % 3) * 270f; val by = 355f - (i / 3) * 105f; if (x in bx..bx + 235f && y in by..by + 75f) start(i) }
            Screen.RESULT -> if (y < 175f) { if (x < 480f) screen = Screen.MODES else { level = (level + 1).coerceAtMost(20); start() } }
            Screen.PLAY -> {
                if (y > 485f) { when { x < 145f -> screen = Screen.MODES; x < 300f -> { level = (level - 1).coerceAtLeast(1); start() }; x < 455f -> { level = (level + 1).coerceAtMost(20); start() } }; return }
                when (mechanic()) {
                    LanguageMechanic.QUIZ -> { val i = optionAt(x, y); if (i == challenge.answer) finish() else if (i >= 0) feedback = "Escucha y vuelve a intentar" }
                    LanguageMechanic.ALPHABET -> { val i = optionAt(x, y); val expected = ((progress % 26) + 65).toChar(); if (i >= 0 && displayedOptions()[i].first() == expected) { progress++; if (progress >= alphabetLength(level)) finish() else feedback = "Bien: sigue con ${((progress % 26) + 65).toChar()}" } else if (i >= 0) feedback = "Busca la siguiente letra" }
                    LanguageMechanic.BUILD -> { val i = optionAt(x, y); if (i >= 0) { val target = buildWord(level); val letter = target[(built.length).coerceAtMost(target.lastIndex)]; if (displayedOptions()[i].first() == letter) { built += letter; if (built == target) finish() } else feedback = "Esa letra no sigue" } }
                    LanguageMechanic.SORT -> if (x in 340f..620f && y in 250f..390f) dragging = true
                    LanguageMechanic.TRACE -> Unit
                }
            }
        }
    }
    private fun optionAt(x: Float, y: Float): Int { if (x !in 170f..790f || y !in 115f..375f) return -1; val col = ((x - 170f) / 320f).toInt(); val row = ((375f - y) / 130f).toInt(); return row * 2 + col }
    private fun dropSort(x: Float, y: Float) { if (y !in 105f..225f) return; val correctLeft = mode == 1; val droppedLeft = x < 480f; if (correctLeft == droppedLeft) finish() else feedback = "Prueba en el otro grupo" }

    override fun render() { Gdx.gl.glClearColor(.035f, .055f, .09f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); viewport.apply(); shapes.projectionMatrix = viewport.camera.combined; batch.projectionMatrix = viewport.camera.combined; when (screen) { Screen.MODES -> renderModes(); Screen.PLAY -> renderPlay(); Screen.RESULT -> renderResult() } }
    private fun renderModes() { text(family.title.uppercase(), 480f, 505f, Color.WHITE); text("Modalidades · 20 niveles", 480f, 465f, Color.LIGHT_GRAY); family.modes.forEachIndexed { i, m -> val x = 90f + (i % 3) * 270f; val y = 355f - (i / 3) * 105f; box(x, y, 235f, 75f, Color(0x3154B5FF.toInt())); text(m.title, x + 117f, y + 48f, Color.WHITE) } }
    private fun renderPlay() { button(18f, "MODOS"); button(160f, "NIVEL -"); button(305f, "NIVEL +"); text("${family.modes[mode].title} · Nivel $level", 700f, 516f, Color.WHITE); text(challenge.prompt, 480f, 430f, Color(0xE0C23CFF.toInt())); when (mechanic()) { LanguageMechanic.SORT -> renderSort(); LanguageMechanic.TRACE -> renderTrace(); else -> renderOptions() }; text(feedback, 480f, 70f, Color.LIGHT_GRAY) }
    private fun displayedOptions(): List<String> = if (mechanic() == LanguageMechanic.ALPHABET) List(4) { ((progress + it) % 26 + 65).toChar().toString() } else if (mechanic() == LanguageMechanic.BUILD) { val target = buildWord(level); listOf(target[built.length.coerceAtMost(target.lastIndex)].toString(), "A", "E", "O").distinct().let { it + List(4 - it.size) { "S" } } } else challenge.options
    private fun renderOptions() { displayedOptions().take(4).forEachIndexed { i, label -> val x = 170f + (i % 2) * 320f; val y = 275f - (i / 2) * 130f; box(x, y, 280f, 105f, Color(0x243E78FF.toInt())); text(label, x + 140f, y + 63f, Color.WHITE) }; if (mechanic() == LanguageMechanic.BUILD) text("Construido: $built", 480f, 100f, Color.WHITE) }
    private fun renderSort() { box(90f, 105f, 350f, 120f, Color(0x6B4FA3FF.toInt())); box(520f, 105f, 350f, 120f, Color(0x3154B5FF.toInt())); text(if (mode == 1) "PLURAL" else "EL", 265f, 175f, Color.WHITE); text(if (mode == 1) "SINGULAR" else "LA", 695f, 175f, Color.WHITE); box(340f, 270f, 280f, 110f, if (dragging) Color(0xE0C23CFF.toInt()) else Color(0x4C9A5FFF.toInt())); text(if (mode == 1) "GATOS" else "CASA", 480f, 335f, Color.WHITE) }
    private fun renderTrace() { shapes.begin(ShapeRenderer.ShapeType.Line); shapes.color = Color(0xE0C23CFF.toInt()); shapes.rect(250f, 150f, 460f, 220f); shapes.line(280f, 180f, 680f, 340f); shapes.end(); text("Progreso ${(traceProgress / 45f * 100).toInt()}%", 480f, 120f, Color.WHITE) }
    private fun renderResult() { text("NIVEL SUPERADO", 480f, 405f, Color.WHITE); text("Puntuación $score", 480f, 330f, Color(0xE0C23CFF.toInt())); box(230f, 80f, 220f, 75f, Color(0x3D507AFF.toInt())); box(510f, 80f, 220f, 75f, Color(0x4C9A5FFF.toInt())); text("MODOS", 340f, 128f, Color.WHITE); text("SIGUIENTE", 620f, 128f, Color.WHITE) }
    private fun button(x: Float, label: String) { box(x, 488f, 120f, 40f, Color(0x3D507AFF.toInt())); text(label, x + 60f, 516f, Color.WHITE) }
    private fun box(x: Float, y: Float, w: Float, h: Float, color: Color) { shapes.begin(ShapeRenderer.ShapeType.Filled); shapes.color = color; shapes.rect(x, y, w, h); shapes.end() }
    private fun text(value: String, x: Float, y: Float, color: Color) { batch.begin(); font.color = color; font.draw(batch, value, x - 220f, y, 440f, Align.center, false); batch.end() }
    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)
    override fun dispose() { shapes.dispose(); batch.dispose(); font.dispose() }
}
