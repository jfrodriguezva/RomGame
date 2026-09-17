package com.miambiente.app.data

/**
 * Guías punteadas de la pizarra — puerto directo de data/guias.ts. Son el
 * equivalente digital de los resaques metálicos y las letras de lija: una
 * figura para repasar con el dedo. `contenido` de tipo "path" es sintaxis
 * SVG estándar (mismo formato que un vector drawable de Android), así que
 * se porta literal: se parsea con androidx.core.graphics.PathParser en vez
 * de reinterpretar cada figura a mano.
 */
data class Guia(val id: String, val nombre: String, val icono: String, val tipo: String, val contenido: String)

private fun letra(l: String) = Guia("l-$l", "Letra $l", l, "texto", l)
private fun numero(n: Int) = Guia("n-$n", "Número $n", "$n", "texto", "$n")

// Bug real reportado: "la letra 'a' que sean como las escribe un niño, no
// como una computadora". Antes TODAS las minúsculas se dibujaban con
// `Typeface.DEFAULT_BOLD` trazado — una tipografía de computadora, con
// proporciones geométricas perfectas, nada parecido a un trazo de lápiz de
// una sola línea. Las vocales (las más practicadas) ahora son un trazo
// real de "bolita y palito" como se enseña a escribir la a, la o y la e en
// preescolar, con `path` en vez de `texto` — el mismo mecanismo que ya
// usan las FORMAS de abajo. El resto de las minúsculas (consonantes) se
// quedan con texto por ahora — extenderlas a las 22 restantes es la misma
// técnica, pero una a la vez para que cada una se vea bien de verdad.
private val TRAZOS_MINUSCULA: Map<String, String> = mapOf(
    "a" to "M 60 62 C 60 74 48 78 38 76 C 26 74 20 66 20 55 C 20 42 28 32 42 30 C 50 29 58 33 60 40 L 60 78",
    "o" to "M 50 32 A 22 23 0 1 1 49.9 32 Z",
    "e" to "M 62 50 L 24 50 C 24 38 34 30 46 30 C 58 30 66 38 66 48 C 66 56 58 60 48 58",
)

private fun letraMin(l: String): Guia {
    val min = l.lowercase()
    val trazo = TRAZOS_MINUSCULA[min]
    return if (trazo != null) {
        Guia("lm-$l", "letra $min", min, "path", trazo)
    } else {
        Guia("lm-$l", "letra $min", min, "texto", min)
    }
}

private val LETRAS = listOf(
    "A", "E", "I", "O", "U", "M", "P", "S", "L", "T", "D", "N", "F", "B", "C",
    "R", "G", "V", "J", "Ñ", "H", "K", "Q", "W", "X", "Y", "Z",
)

val VOCALES: List<Guia> = listOf("A", "E", "I", "O", "U").map(::letra)
val CONSONANTES: List<Guia> = listOf(
    "M", "P", "S", "L", "T", "D", "N", "F", "B", "C", "R", "G", "V", "J", "Ñ", "H", "K", "Q", "W", "X", "Y", "Z",
).map(::letra)
val MINUSCULAS: List<Guia> = LETRAS.map(::letraMin)
val NUMEROS: List<Guia> = (1..9).map(::numero) + numero(0)

val FORMAS: List<Guia> = listOf(
    Guia("f-circulo", "Círculo", "○", "path", "M50 12 A38 38 0 1 1 49.9 12 Z"),
    Guia("f-cuadrado", "Cuadrado", "□", "path", "M15 15 H85 V85 H15 Z"),
    Guia("f-triangulo", "Triángulo", "△", "path", "M50 12 L88 84 H12 Z"),
    Guia("f-rombo", "Rombo", "◇", "path", "M50 10 L88 50 L50 90 L12 50 Z"),
    Guia("f-estrella", "Estrella", "☆", "path", "M50 8 L61 38 H93 L67 57 L77 88 L50 69 L23 88 L33 57 L7 38 H39 Z"),
    Guia("f-corazon", "Corazón", "♡", "path", "M50 86 C10 58 12 24 34 20 C43 18 49 25 50 32 C51 25 57 18 66 20 C88 24 90 58 50 86 Z"),
    Guia("f-ola", "Ondas", "〰", "path", "M8 50 Q23 20 38 50 T68 50 T98 50"),
    Guia("f-zigzag", "Zigzag", "⟋", "path", "M8 70 L26 30 L44 70 L62 30 L80 70 L94 40"),
    Guia("f-espiral", "Espiral", "🌀", "path", "M50 50 m0 0 a4 4 0 1 1 6 4 a10 10 0 1 1 -14 6 a18 18 0 1 1 26 -12 a28 28 0 1 1 -40 -8 a38 38 0 1 1 58 22"),
    Guia("f-bucles", "Bucles", "🪢", "path", "M10 60 c6 -22 18 -22 24 0 c6 22 18 22 24 0 c6 -22 18 -22 24 0 c4 14 8 16 8 16"),
    Guia("f-casa", "Casa", "🏠", "path", "M20 88 V44 L50 18 L80 44 V88 Z M40 88 V62 H60 V88"),
    Guia("f-sol", "Sol", "☀", "path", "M50 28 A22 22 0 1 1 49.9 28 Z M50 4 V14 M50 86 V96 M4 50 H14 M86 50 H96 M18 18 L25 25 M75 75 L82 82 M82 18 L75 25 M25 75 L18 82"),
    Guia("f-pez", "Pez", "🐟", "path", "M25 50 A30 18 0 1 1 24.9 50 Z M25 50 L8 35 L8 65 Z M75 42 A3 3 0 1 1 74.9 42 Z"),
    // Bug real reportado: "mariposa no se parece" — antes eran 4 óvalos
    // sueltos sin forma de ala. Ahora cada ala es una gota con curvas
    // reales (como un ala de verdad) y el cuerpo es un huso, no una línea.
    Guia(
        "f-mariposa",
        "Mariposa",
        "🦋",
        "path",
        "M42 15 L35 5 M58 15 L65 5 " +
            "M50 18 C46 18 44 24 44 34 C44 55 46 75 50 85 C54 75 56 55 56 34 C56 24 54 18 50 18 Z " +
            "M48 28 C30 18 10 22 8 40 C6 55 20 60 34 52 C42 47 47 38 48 28 Z " +
            "M52 28 C70 18 90 22 92 40 C94 55 80 60 66 52 C58 47 53 38 52 28 Z " +
            "M47 50 C34 46 20 50 18 62 C16 72 26 78 36 72 C43 68 47 58 47 50 Z " +
            "M53 50 C66 46 80 50 82 62 C84 72 74 78 64 72 C57 68 53 58 53 50 Z",
    ),
    Guia("f-flor", "Flor", "🌸", "path", "M50 13 A9 9 0 1 1 49.9 13 Z M50 49 A9 9 0 1 1 49.9 49 Z M32 31 A9 9 0 1 1 31.9 31 Z M68 31 A9 9 0 1 1 67.9 31 Z M50 34 A6 6 0 1 1 49.9 34 Z M50 49 L50 88"),
    // Bug real reportado: "ni nubes" — antes eran 3 círculos flotando
    // arriba de una barra separada, con un hueco visible entre ambos.
    // Ahora es un solo contorno cerrado y continuo, sin huecos.
    Guia(
        "f-nube",
        "Nube",
        "☁",
        "path",
        "M20 65 A12 12 0 1 1 34 45 A16 16 0 1 1 58 38 A14 14 0 1 1 82 55 A10 10 0 1 1 88 68 " +
            "Q80 72 70 68 Q60 74 50 68 Q40 74 30 68 Q22 70 20 65 Z",
    ),
    Guia("f-arbol", "Árbol", "🌳", "path", "M50 9 A26 26 0 1 1 49.9 9 Z M42 58 H58 V90 H42 Z"),
)

val GUIAS: List<Guia> = MINUSCULAS + VOCALES + CONSONANTES + NUMEROS + FORMAS

private val IDS_TRAZO_ABIERTO = setOf("f-ola", "f-zigzag", "f-espiral", "f-bucles")

/** Figuras cerradas que funcionan como dibujo para colorear (no solo trazo). */
val IMAGENES_COLOREAR: List<Guia> = FORMAS.filter { it.id !in IDS_TRAZO_ABIERTO }
