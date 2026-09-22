package com.rominagame.core

fun starsForScore(score: Int): Int = when {
    score >= 850 -> 3
    score >= 600 -> 2
    score > 0 -> 1
    else -> 0
}

fun logicTutorial(familyId: String, mode: MemoryMode? = null): String = when (familyId) {
    "percepcion" -> "Observa el objeto y clasifícalo o elige la respuesta correcta."
    "formas-encajes" -> "Reconoce la figura y usa arrastre cuando aparezcan piezas."
    "orden-secuencias" -> "Arrastra cada elemento hasta formar el orden correcto."
    "memoria-observacion" -> when (mode) {
        MemoryMode.PAIRS -> "Destapa dos cartas y recuerda dónde está cada pareja."
        MemoryMode.MISSING -> "Memoriza la serie y descubre qué elemento desapareció."
        MemoryMode.TURNS -> "Forma más parejas que la computadora."
        MemoryMode.SEARCH -> "Encuentra todos los símbolos objetivo entre los distractores."
        null -> "Elige una modalidad de memoria."
    }
    else -> "Completa el objetivo del nivel."
}

fun languageTutorial(familyId: String, mode: Int): String = if (familyId == "palabras-sonidos") {
    listOf(
        "Escucha la consigna y distingue vocales de consonantes.",
        "Toca las letras en orden para completar el abecedario.",
        "Busca la letra con la que comienza la palabra.",
        "Compara los sonidos finales y encuentra la rima.",
        "Separa la palabra en golpes de voz y cuenta sus sílabas.",
        "Relaciona cada letra minúscula con su mayúscula.",
        "Desliza el dedo siguiendo la forma de la letra.",
        "Traza la guía completa sin levantar el dedo demasiado pronto.",
    )[mode]
} else {
    listOf(
        "Toca las letras en el orden correcto para formar la palabra.",
        "Arrastra la palabra al grupo singular o plural.",
        "Arrastra cada palabra al artículo que le corresponde.",
        "Relaciona la palabra en español con su equivalente en inglés.",
    )[mode]
}

fun curriculumTutorial(familyId: String, mode: Int): String {
    val selected = curriculumFamily(familyId).modes[mode]
    return when (selected.mechanic) {
        CurriculumMechanic.COUNT -> "Toca cada elemento una sola vez y comprueba la cantidad."
        CurriculumMechanic.SORT -> "Arrastra ${selected.title.lowercase()} al grupo que le corresponde."
        CurriculumMechanic.ORDER -> "Arrastra los elementos hasta formar la secuencia correcta."
        CurriculumMechanic.QUIZ -> "Observa ${selected.title.lowercase()} y selecciona la respuesta correcta."
    }
}
