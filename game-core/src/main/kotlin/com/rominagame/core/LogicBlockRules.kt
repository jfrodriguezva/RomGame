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
    val (prompt, correct, distractors) = when (familyId) {
        "percepcion" -> listOf(
            Triple("Toca el color rojo", "Rojo", listOf("Azul", "Verde", "Amarillo")),
            Triple("¿Qué objeto es áspero?", "Piedra", listOf("Espejo", "Hielo", "Vidrio")),
            Triple("¿Qué usamos cuando hace frío?", "Abrigo", listOf("Abanico", "Hielo", "Sombrilla")),
            Triple("¿Qué pesa más?", "Roca", listOf("Pluma", "Hoja", "Globo")),
            Triple("¿Cuál suele ser dulce?", "Fresa", listOf("Sal", "Limón", "Aceituna")),
            Triple("¿Cuál huele bien?", "Flor", listOf("Basura", "Humo", "Calcetín")),
            Triple("¿Con qué escuchamos?", "Oídos", listOf("Ojos", "Nariz", "Manos")),
            Triple("Elige la sombra del árbol", "Árbol", listOf("Casa", "Auto", "Pelota")),
            Triple("Encuentra el elemento diferente", "Triángulo", listOf("Círculo", "Círculo", "Círculo")),
        )[mode.coerceIn(family.modes.indices)]
        "formas-encajes" -> listOf(
            Triple("Encuentra el círculo", "Círculo", listOf("Triángulo", "Cuadrado", "Rectángulo")),
            Triple("¿Cuál es un cuerpo redondo?", "Esfera", listOf("Cubo", "Pirámide", "Prisma")),
            Triple("¿Qué figura tiene tres lados?", "Triángulo", listOf("Círculo", "Cuadrado", "Pentágono")),
            Triple("¿Qué pieza encaja en el hueco redondo?", "Círculo", listOf("Estrella", "Cuadrado", "Triángulo")),
            Triple("Selecciona el cilindro más alto", "Alto", listOf("Bajo", "Ancho", "Plano")),
            Triple("Completa el patrón rojo-azul", "Rojo", listOf("Verde", "Morado", "Naranja")),
            Triple("Elige la pieza que completa la imagen", "Pieza 3", listOf("Pieza 1", "Pieza 2", "Pieza 4")),
        )[mode.coerceIn(family.modes.indices)]
        else -> Triple("Ordena $name de izquierda a derecha", "Primero", listOf("Segundo", "Tercero", "Último"))
    }
    val original = listOf(correct) + distractors
    val shift = (level + step + mode) % original.size
    val choices = original.drop(shift) + original.take(shift)
    return LogicRound(prompt, choices, choices.indexOf(correct))
}

fun roundsForLevel(level: Int): Int = (3 + (level - 1) / 4).coerceAtMost(7)

fun sequenceFor(mode: Int, level: Int): List<String> {
    val full = when (mode) {
        0 -> listOf("10", "9", "8", "7", "6", "5", "4", "3", "2", "1")
        1 -> listOf("Ancha", "Media", "Delgada")
        2 -> listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        3 -> listOf("Primavera", "Verano", "Otoño", "Invierno")
        4 -> listOf("Huevo", "Oruga", "Crisálida", "Mariposa")
        else -> listOf("Sol", "Nube", "Lluvia", "Río")
    }
    val requested = (3 + level / 4).coerceAtMost(full.size)
    return full.take(requested)
}
