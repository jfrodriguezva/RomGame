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
private fun letraMin(l: String) = Guia("lm-$l", "letra ${l.lowercase()}", l.lowercase(), "texto", l.lowercase())
private fun numero(n: Int) = Guia("n-$n", "Número $n", "$n", "texto", "$n")

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
    Guia("f-mariposa", "Mariposa", "🦋", "path", "M50 12 L50 88 M18 35 A14 18 0 1 1 17.9 35 Z M82 35 A14 18 0 1 1 82.1 35 Z M26 60 A10 14 0 1 1 25.9 60 Z M74 60 A10 14 0 1 1 74.1 60 Z"),
    Guia("f-flor", "Flor", "🌸", "path", "M50 13 A9 9 0 1 1 49.9 13 Z M50 49 A9 9 0 1 1 49.9 49 Z M32 31 A9 9 0 1 1 31.9 31 Z M68 31 A9 9 0 1 1 67.9 31 Z M50 34 A6 6 0 1 1 49.9 34 Z M50 49 L50 88"),
    Guia("f-nube", "Nube", "☁", "path", "M32 41 A14 14 0 1 1 31.9 41 Z M52 27 A18 18 0 1 1 51.9 27 Z M72 42 A13 13 0 1 1 71.9 42 Z M18 62 H86 Q90 62 90 66 Q90 70 86 70 H18 Q14 70 14 66 Q14 62 18 62 Z"),
    Guia("f-arbol", "Árbol", "🌳", "path", "M50 9 A26 26 0 1 1 49.9 9 Z M42 58 H58 V90 H42 Z"),
)

val GUIAS: List<Guia> = MINUSCULAS + VOCALES + CONSONANTES + NUMEROS + FORMAS

private val IDS_TRAZO_ABIERTO = setOf("f-ola", "f-zigzag", "f-espiral", "f-bucles")

/** Figuras cerradas que funcionan como dibujo para colorear (no solo trazo). */
val IMAGENES_COLOREAR: List<Guia> = FORMAS.filter { it.id !in IDS_TRAZO_ABIERTO }
