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
