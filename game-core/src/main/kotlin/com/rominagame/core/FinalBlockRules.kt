package com.rominagame.core

enum class FinalMechanic { DRAW, COLOR, COLLAGE, MUSIC, PATTERN, TARGET, BASKET, MAZE }
data class FinalMode(val title: String, val mechanic: FinalMechanic)
data class FinalFamily(val id: String, val title: String, val modes: List<FinalMode>)

val finalFamilies = listOf(
    FinalFamily("taller-creativo", "Taller creativo", listOf(
        FinalMode("Pizarra", FinalMechanic.DRAW), FinalMode("Colorear", FinalMechanic.COLOR),
        FinalMode("Collage", FinalMechanic.COLLAGE), FinalMode("Xilófono", FinalMechanic.MUSIC),
        FinalMode("Instrumentos", FinalMechanic.MUSIC), FinalMode("Patrones", FinalMechanic.PATTERN),
    )),
    FinalFamily("coordinacion-reflejos", "Coordinación y reflejos", listOf(
        FinalMode("Burbujas", FinalMechanic.TARGET), FinalMode("Canasta", FinalMechanic.BASKET),
        FinalMode("Globo", FinalMechanic.TARGET), FinalMode("Topo", FinalMechanic.TARGET),
        FinalMode("Vibra y adivina", FinalMechanic.PATTERN), FinalMode("Reflejo de color", FinalMechanic.TARGET),
    )),
    FinalFamily("laberintos", "Laberintos y recorridos", listOf(
        FinalMode("Laberinto", FinalMechanic.MAZE), FinalMode("Araña", FinalMechanic.MAZE),
    )),
)

fun finalFamily(id: String) = finalFamilies.first { it.id == id }
fun targetHits(level: Int) = (3 + level / 3).coerceAtMost(9)
fun mazeSize(level: Int) = (4 + level / 4).coerceAtMost(9)
fun musicNotes(level: Int) = (4 + level / 3).coerceAtMost(8)
fun targetContains(targetX: Float, targetY: Float, x: Float, y: Float, radius: Float = 55f): Boolean {
    val dx = x - targetX; val dy = y - targetY
    return dx * dx + dy * dy <= radius * radius
}

fun mazePath(size: Int): Set<Pair<Int, Int>> {
    val path = mutableSetOf<Pair<Int, Int>>()
    for (x in 0 until size) path += x to 0
    for (y in 0 until size) path += (size - 1) to y
    return path
}
