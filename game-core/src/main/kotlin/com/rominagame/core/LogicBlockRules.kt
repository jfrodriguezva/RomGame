package com.rominagame.core

data class LogicFamily(
    val id: String,
    val title: String,
    val modes: List<String>,
)

data class LogicRound(
    val prompt: String,
    val options: List<String>,
    val answer: Int,
)

val logicFamilies = listOf(
    LogicFamily("percepcion", "Percepción sensorial", listOf("Colores", "Texturas", "Temperatura", "Peso", "Sabores", "Olores", "Sentidos", "Sombras", "Diferencias")),
    LogicFamily("formas-encajes", "Formas y encajes", listOf("Formas", "Sólidos", "Lados", "Orificios", "Cilindros", "Binomio", "Rompecabezas")),
    LogicFamily("orden-secuencias", "Orden y secuencias", listOf("Torre rosa", "Escalera marrón", "Días", "Estaciones", "Ciclo de vida", "Ciclo del agua")),
)

fun logicFamily(id: String): LogicFamily = logicFamilies.first { it.id == id }

fun logicRound(familyId: String, mode: Int, level: Int, step: Int): LogicRound {
    val family = logicFamily(familyId)
    val name = family.modes[mode.coerceIn(family.modes.indices)]
    val correct = (level + step + mode) % 4
    val choices = when (familyId) {
        "percepcion" -> listOf("Suave", "Áspero", "Frío", "Caliente")
        "formas-encajes" -> listOf("Círculo", "Triángulo", "Cuadrado", "Rectángulo")
        else -> listOf("Primero", "Segundo", "Tercero", "Último")
    }
    val prompt = when (familyId) {
        "percepcion" -> "$name: identifica la opción ${correct + 1}"
        "formas-encajes" -> "$name: encuentra la pieza indicada"
        else -> "$name: selecciona el paso correcto"
    }
    return LogicRound(prompt, choices, correct)
}

fun roundsForLevel(level: Int): Int = (3 + (level - 1) / 4).coerceAtMost(7)
