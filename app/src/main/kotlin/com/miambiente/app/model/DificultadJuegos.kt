package com.miambiente.app.model

/**
 * Dificultad por nivel de los juegos con pantalla propia. Antes estos modos
 * anunciaban "100 niveles progresivos" pero jugaban siempre igual y
 * guardaban siempre el nivel 1. Cada función recibe el nivel (1 a 100) y
 * devuelve los parámetros del juego; las pruebas verifican que la
 * dificultad crece y que los valores quedan dentro de lo que la pantalla
 * puede dibujar.
 */

data class DificultadCanasta(val meta: Int, val pasoMs: Long)

fun dificultadCanasta(nivel: Int) = DificultadCanasta(
    meta = phasedInt(nivel, listOf(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)),
    pasoMs = phasedInt(nivel, listOf(650, 600, 560, 520, 480, 440, 400, 370, 340, 310, 280)).toLong(),
)

data class DificultadBurbujas(val meta: Int, val aparicionMs: Long, val velocidadMin: Int, val velocidadMax: Int)

fun dificultadBurbujas(nivel: Int) = DificultadBurbujas(
    meta = phasedInt(nivel, listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 18, 20)),
    aparicionMs = phasedInt(nivel, listOf(800, 740, 680, 620, 570, 520, 480, 440, 400, 370, 340)).toLong(),
    velocidadMin = phasedInt(nivel, listOf(40, 45, 50, 55, 60, 65, 70, 80, 90, 100, 110)),
    velocidadMax = phasedInt(nivel, listOf(90, 100, 110, 120, 130, 140, 150, 165, 180, 200, 220)),
)

data class DificultadGlobo(val meta: Int, val gravedad: Float)

fun dificultadGlobo(nivel: Int) = DificultadGlobo(
    meta = phasedInt(nivel, listOf(5, 7, 9, 11, 13, 15, 18, 21, 24, 27, 30)),
    gravedad = phased(nivel, listOf(150.0, 165.0, 180.0, 195.0, 210.0, 225.0, 240.0, 255.0, 270.0, 285.0, 300.0)).toFloat(),
)

data class DificultadReflejo(val rondas: Int, val umbralMs: Long, val esperaMinMs: Long, val esperaMaxMs: Long)

fun dificultadReflejo(nivel: Int) = DificultadReflejo(
    rondas = phasedInt(nivel, listOf(3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8)),
    umbralMs = phasedInt(nivel, listOf(1500, 1300, 1150, 1000, 900, 800, 720, 650, 580, 520, 460)).toLong(),
    esperaMinMs = 1000,
    esperaMaxMs = phasedInt(nivel, listOf(2000, 2200, 2400, 2600, 2800, 3000, 3200, 3400, 3600, 3800, 4000)).toLong(),
)

data class DificultadVibra(val maxPulsos: Int, val aciertos: Int, val pausaMs: Long)

fun dificultadVibra(nivel: Int) = DificultadVibra(
    maxPulsos = phasedInt(nivel, listOf(2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7)),
    aciertos = phasedInt(nivel, listOf(3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6)),
    pausaMs = phasedInt(nivel, listOf(650, 600, 560, 520, 480, 450, 420, 390, 360, 330, 300)).toLong(),
)

/** Parejas del memorama: de 3 (seis cartas) hasta 24. */
fun parejasMemorama(nivel: Int) = phasedInt(nivel, listOf(3, 4, 5, 6, 8, 10, 12, 14, 16, 20, 24))

data class DificultadDiferencias(val lado: Int, val rondas: Int)

fun dificultadDiferencias(nivel: Int) = DificultadDiferencias(
    lado = phasedInt(nivel, listOf(2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5)),
    rondas = phasedInt(nivel, listOf(2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5)),
)

data class DificultadObjetos(val lado: Int, val objetivos: Int)

fun dificultadObjetos(nivel: Int) = DificultadObjetos(
    lado = phasedInt(nivel, listOf(3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6)),
    objetivos = phasedInt(nivel, listOf(2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7)),
)

/** Celdas por lado del laberinto: de 3×3 a 8×8. */
fun ladoLaberinto(nivel: Int) = phasedInt(nivel, listOf(3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8))

data class DificultadPatron(val longitud: Int, val notaMs: Long)

fun dificultadPatron(nivel: Int) = DificultadPatron(
    longitud = phasedInt(nivel, listOf(2, 2, 3, 3, 4, 4, 5, 5, 6, 7, 8)),
    notaMs = phasedInt(nivel, listOf(600, 570, 540, 510, 480, 450, 420, 390, 360, 330, 300)).toLong(),
)

data class DificultadBinomio(val lado: Int, val colores: Int)

fun dificultadBinomio(nivel: Int) = DificultadBinomio(
    lado = phasedInt(nivel, listOf(2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3)),
    colores = phasedInt(nivel, listOf(2, 3, 3, 4, 3, 3, 4, 4, 4, 4, 4)),
)

/** Número más grande que se pide componer en el banco dorado. */
fun maximoBancoDorado(nivel: Int) = phasedInt(nivel, listOf(9, 20, 50, 99, 150, 250, 400, 600, 800, 999, 999))

/** Hasta qué número se completa la tabla (en decenas completas). */
fun hastaTablaCien(nivel: Int) = phasedInt(nivel, listOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 100)) / 10 * 10

data class DificultadAlfabeto(val longitud: Int, val distractores: Int)

fun dificultadAlfabeto(nivel: Int) = DificultadAlfabeto(
    longitud = phasedInt(nivel, listOf(3, 3, 3, 4, 4, 4, 5, 5, 6, 6, 7)),
    distractores = phasedInt(nivel, listOf(0, 0, 1, 1, 2, 2, 2, 3, 3, 4, 4)),
)

/** Palabras del alfabeto móvil por número de letras, sin acentos ni ñ. */
val PALABRAS_ALFABETO: Map<Int, List<Pair<String, String>>> = mapOf(
    3 to listOf("☀️" to "sol", "🍞" to "pan", "🌊" to "mar", "🐻" to "oso", "🐟" to "pez", "🍇" to "uva"),
    4 to listOf("🌙" to "luna", "🐱" to "gato", "🏠" to "casa", "🦆" to "pato", "🐸" to "rana", "🐮" to "vaca", "☁️" to "nube"),
    5 to listOf("🐶" to "perro", "🍓" to "fresa", "🪑" to "silla", "🧀" to "queso", "🐷" to "cerdo", "🐯" to "tigre", "🐝" to "abeja", "🌳" to "arbol"),
    6 to listOf("🐰" to "conejo", "🍒" to "cereza", "⚽" to "pelota", "🍅" to "tomate", "🦒" to "jirafa"),
    7 to listOf("🍎" to "manzana", "🐢" to "tortuga", "🐔" to "gallina"),
)

/**
 * Palabra del nivel y letras del banco: las de la palabra más
 * `distractores` que no están en ella, mezcladas de forma estable.
 */
fun rondaAlfabeto(nivel: Int, semilla: Int = nivel): Pair<Pair<String, String>, List<Char>> {
    val d = dificultadAlfabeto(nivel)
    val rnd = kotlin.random.Random(semilla)
    val palabra = PALABRAS_ALFABETO.getValue(d.longitud).random(rnd)
    val extras = ('a'..'z').filter { it !in palabra.second }.shuffled(rnd).take(d.distractores)
    return palabra to (palabra.second.toList() + extras).shuffled(rnd)
}

enum class FormaTrazo(val nombre: String) { RECTA("la línea"), ZIGZAG("el zigzag"), OLA("la ola"), ARCOS("los arcos"), ESPIRAL("la espiral") }

data class DificultadTrazo(val forma: FormaTrazo, val tolerancia: Float)

fun dificultadTrazo(nivel: Int): DificultadTrazo {
    val formas = FormaTrazo.entries
    val indice = phasedInt(nivel, listOf(0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4)).coerceIn(0, formas.lastIndex)
    // Tolerancia en fracción del lado menor del lienzo: cada vez hay que
    // pasar más cerca de la guía.
    val tolerancia = phased(nivel, listOf(0.14, 0.13, 0.12, 0.11, 0.10, 0.09, 0.085, 0.08, 0.075, 0.07, 0.065)).toFloat()
    return DificultadTrazo(formas[indice], tolerancia)
}

/** Punto en coordenadas normalizadas (0..1) del lienzo. */
data class Punto(val x: Float, val y: Float)

/** Puntos de control de la guía, en orden, en coordenadas normalizadas. */
fun guiaTrazo(forma: FormaTrazo, puntos: Int = 24): List<Punto> = (0 until puntos).map { i ->
    val t = i / (puntos - 1f)
    when (forma) {
        FormaTrazo.RECTA -> Punto(0.08f + 0.84f * t, 0.5f)
        FormaTrazo.ZIGZAG -> {
            val tramo = t * 4f
            val subida = tramo - kotlin.math.floor(tramo)
            val y = if (tramo.toInt() % 2 == 0) 0.3f + 0.4f * subida else 0.7f - 0.4f * subida
            Punto(0.08f + 0.84f * t, y)
        }
        FormaTrazo.OLA -> Punto(0.08f + 0.84f * t, 0.5f + 0.2f * kotlin.math.sin(t * 4 * Math.PI).toFloat())
        FormaTrazo.ARCOS -> Punto(0.08f + 0.84f * t, 0.65f - 0.3f * kotlin.math.abs(kotlin.math.sin(t * 3 * Math.PI)).toFloat())
        FormaTrazo.ESPIRAL -> {
            val angulo = t * 4 * Math.PI
            val radio = 0.05f + 0.33f * t
            Punto(0.5f + radio * kotlin.math.cos(angulo).toFloat(), 0.5f + radio * kotlin.math.sin(angulo).toFloat())
        }
    }
}

/**
 * Cuántos puntos de la guía se recorrieron en orden: un punto cuenta solo
 * si el trazo pasa a menos de `tolerancia` de él y ya se alcanzó el
 * anterior. Así no basta con llegar al final "por atajo".
 */
fun avanceTrazo(guia: List<Punto>, trazo: List<Punto>, tolerancia: Float): Int {
    var siguiente = 0
    for (p in trazo) {
        if (siguiente >= guia.size) break
        val g = guia[siguiente]
        val dx = p.x - g.x
        val dy = p.y - g.y
        if (dx * dx + dy * dy <= tolerancia * tolerancia) siguiente++
    }
    return siguiente
}

data class DificultadCara(val partes: Int, val aciertos: Int)

fun dificultadCara(nivel: Int) = DificultadCara(
    partes = phasedInt(nivel, listOf(3, 3, 4, 4, 5, 5, 5, 5, 5, 5, 5)),
    aciertos = phasedInt(nivel, listOf(3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8)),
)
