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

data class SortItem(val label: String, val destination: Int)
data class SortRound(val left: String, val right: String, val items: List<SortItem>)
data class MultiSortRound(val destinations: List<String>, val items: List<SortItem>)

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
            Triple("Copia exactamente el patrón", "Rojo", listOf("Verde", "Morado", "Naranja")),
            Triple("Arrastra cada pieza a su lugar", "Pieza 3", listOf("Pieza 1", "Pieza 2", "Pieza 4")),
        )[mode.coerceIn(family.modes.indices)]
        else -> Triple("Ordena $name de izquierda a derecha", "Primero", listOf("Segundo", "Tercero", "Último"))
    }
    val original = listOf(correct) + distractors
    val shift = (level + step + mode) % original.size
    val choices = original.drop(shift) + original.take(shift)
    return LogicRound(prompt, choices, choices.indexOf(correct))
}

fun roundsForLevel(level: Int): Int = (3 + (level - 1) / 4).coerceAtMost(7)
fun differenceCellCount(level: Int): Int = (9 + (level - 1) / 3).coerceAtMost(15)

fun sensorySort(mode: Int): SortRound = when (mode) {
    1 -> SortRound("ÁSPERO", "LISO", listOf(SortItem("Piedra", 0), SortItem("Ladrillo", 0), SortItem("Bellota", 0), SortItem("Espejo", 1), SortItem("Hielo", 1), SortItem("Huevo", 1)))
    2 -> SortRound("CALIENTE", "FRÍO", listOf(SortItem("Fuego", 0), SortItem("Café", 0), SortItem("Sol", 0), SortItem("Nieve", 1), SortItem("Hielo", 1), SortItem("Helado", 1)))
    3 -> SortRound("PESADO", "LIGERO", listOf(SortItem("Pesa", 0), SortItem("Roca", 0), SortItem("Elefante", 0), SortItem("Pluma", 1), SortItem("Globo", 1), SortItem("Mariposa", 1)))
    4 -> SortRound("DULCE", "SALADO", listOf(SortItem("Caramelo", 0), SortItem("Paleta", 0), SortItem("Pastel", 0), SortItem("Pretzel", 1), SortItem("Papas", 1), SortItem("Sal", 1)))
    5 -> SortRound("HUELE BIEN", "HUELE MAL", listOf(SortItem("Flor", 0), SortItem("Pastel", 0), SortItem("Limón", 0), SortItem("Calcetín", 1), SortItem("Basura", 1), SortItem("Zorrillo", 1)))
    else -> error("Mode $mode is not a sensory classification")
}

fun shapeSort(mode: Int): MultiSortRound = when (mode) {
    2 -> MultiSortRound(
        listOf("3 LADOS", "4 LADOS", "5 LADOS", "6 LADOS"),
        listOf(SortItem("Triángulo", 0), SortItem("Triángulo 2", 0), SortItem("Cuadrado", 1), SortItem("Rectángulo", 1), SortItem("Pentágono", 2), SortItem("Hexágono", 3)),
    )
    3 -> MultiSortRound(
        listOf("CÍRCULO", "CUADRADO", "TRIÁNGULO"),
        listOf(SortItem("Círculo", 0), SortItem("Círculo 2", 0), SortItem("Cuadrado", 1), SortItem("Cuadrado 2", 1), SortItem("Triángulo", 2), SortItem("Triángulo 2", 2)),
    )
    else -> error("Mode $mode is not a shape classification")
}

fun cylinderSequence(level: Int): List<String> = (1..(3 + (level - 1) / 3).coerceAtMost(9)).map(Int::toString).reversed()

fun binomialTarget(level: Int): List<Int> = List(4) { index -> (level + index * 2) % 4 }

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
