package com.rominagame.core

enum class LanguageMechanic { QUIZ, ALPHABET, BUILD, SORT, TRACE }

data class LanguageMode(val title: String, val mechanic: LanguageMechanic)
data class LanguageFamily(val id: String, val title: String, val modes: List<LanguageMode>)
data class LanguageChallenge(val prompt: String, val options: List<String>, val answer: Int)

val languageFamilies = listOf(
    LanguageFamily("palabras-sonidos", "Palabras y sonidos", listOf(
        LanguageMode("Vocales", LanguageMechanic.QUIZ), LanguageMode("Abecedario", LanguageMechanic.ALPHABET),
        LanguageMode("Sonidos iniciales", LanguageMechanic.QUIZ), LanguageMode("Rimas", LanguageMechanic.QUIZ),
        LanguageMode("Sílabas", LanguageMechanic.QUIZ), LanguageMode("Mayúsculas", LanguageMechanic.QUIZ),
        LanguageMode("Letras de lija", LanguageMechanic.TRACE), LanguageMode("Trazos", LanguageMechanic.TRACE),
    )),
    LanguageFamily("construye-palabras", "Construye palabras", listOf(
        LanguageMode("Alfabeto móvil", LanguageMechanic.BUILD), LanguageMode("Singular y plural", LanguageMechanic.SORT),
        LanguageMode("El o la", LanguageMechanic.SORT), LanguageMode("Inglés inicial", LanguageMechanic.QUIZ),
    )),
)

fun languageFamily(id: String) = languageFamilies.first { it.id == id }

fun languageChallenge(familyId: String, mode: Int, level: Int): LanguageChallenge {
    val base = if (familyId == "palabras-sonidos") listOf(
        LanguageChallenge("Selecciona una vocal", listOf("A", "M", "P", "S"), 0),
        LanguageChallenge("Continúa el abecedario: A, B...", listOf("C", "D", "E", "F"), 0),
        LanguageChallenge("¿Con qué letra inicia MESA?", listOf("M", "P", "S", "L"), 0),
        LanguageChallenge("¿Qué palabra rima con GATO?", listOf("Pato", "Mesa", "Sol", "Luz"), 0),
        LanguageChallenge("¿Cuántas sílabas tiene PELOTA?", listOf("3", "1", "2", "4"), 0),
        LanguageChallenge("Elige la mayúscula de b", listOf("B", "D", "P", "R"), 0),
        LanguageChallenge("Repasa la letra", listOf("A", "E", "I", "O"), 0),
        LanguageChallenge("Sigue el trazo de izquierda a derecha", listOf("→", "←", "↑", "↓"), 0),
    )[mode] else listOf(
        LanguageChallenge("Construye la palabra SOL", listOf("S", "O", "L", "A"), 0),
        LanguageChallenge("Clasifica: GATOS", listOf("Plural", "Singular", "Verbo", "Color"), 0),
        LanguageChallenge("Completa: ___ casa", listOf("La", "El", "Los", "Un"), 0),
        LanguageChallenge("¿Cómo se dice SOL en inglés?", listOf("Sun", "Moon", "Star", "Sky"), 0),
    )[mode]
    val shift = level % base.options.size
    val options = base.options.drop(shift) + base.options.take(shift)
    return base.copy(options = options, answer = options.indexOf(base.options[base.answer]))
}

fun alphabetLength(level: Int) = (5 + (level - 1) / 2).coerceAtMost(14)
fun buildWord(level: Int) = listOf("SOL", "MAR", "CASA", "LUNA", "GATO")[(level - 1) % 5]
