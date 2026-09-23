package com.rominagame.core

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class DrawingTool(val label: String) {
    PENCIL("Lápiz"), CRAYON("Crayón"), MARKER("Marcador"), NEON("Neón"),
    SPRAY("Aerosol"), FILL("Relleno"), STAMP("Sello"), ERASER("Borrador"),
}

enum class StudioInstrument(val label: String) {
    XYLOPHONE("Xilófono"), PIANO("Piano"), GUITAR("Guitarra"), FLUTE("Flauta"),
    TRUMPET("Trompeta"), ACCORDION("Acordeón"), HARP("Arpa"),
}

data class StudioNote(val label: String, val frequency: Double)

val studioNotes = listOf(
    StudioNote("Do", 261.63), StudioNote("Re", 293.66), StudioNote("Mi", 329.63),
    StudioNote("Fa", 349.23), StudioNote("Sol", 392.00), StudioNote("La", 440.00),
    StudioNote("Si", 493.88),
)

val drawingColors = intArrayOf(
    0x2F2A26FF, 0xE04A3FFF.toInt(), 0xF08A3CFF.toInt(), 0xF5C542FF.toInt(),
    0x8BBF5AFF.toInt(), 0x3F9E6DFF, 0x3FA7C4FF, 0x3F6FB5FF,
    0x7A5EC4FF, 0xC25AA6FF.toInt(), 0xE58FA8FF.toInt(), 0x8B5E3CFF.toInt(),
    0xC9A87CFF.toInt(), 0x9AA5ADFF.toInt(), 0xFFFFFFFF.toInt(), 0x000000FF,
)

val drawingWidths = intArrayOf(4, 10, 22, 44)
val drawingSymmetries = intArrayOf(1, 2, 4, 6, 8)

fun symmetricPoints(x: Float, y: Float, width: Int, height: Int, symmetry: Int): List<Pair<Float, Float>> {
    val cx = width / 2f
    val cy = height / 2f
    val count = symmetry.coerceAtLeast(1)
    return buildList {
        repeat(count) { index ->
            val angle = 2.0 * PI * index / count
            val dx = x - cx
            val dy = y - cy
            add((cx + dx * cos(angle) - dy * sin(angle)).toFloat() to (cy + dx * sin(angle) + dy * cos(angle)).toFloat())
        }
    }
}
