package com.miambiente.app.model

/**
 * Reglas puras de los dos materiales base de seriación y transferencia,
 * separadas de las pantallas para poder probarlas sin emulador. Antes
 * vivían dentro de los composables y un error (todas las series en el nivel
 * 1) pasó sin que ninguna prueba lo notara.
 */

/** Qué pasó con un toque: la pantalla decide sonido, vibración y mensaje. */
enum class Resultado { NADA, SELECCION, ACIERTO, ERROR, COMPLETO }

/**
 * Serie de `n` piezas que se ordena tocando una pieza del canasto y luego
 * su lugar. `posicion` y pieza van de 1 a n; la pieza k va en el lugar k.
 */
data class SerieOrdenar(
    val n: Int,
    val enCanasto: List<Int>,
    val colocadas: Set<Int> = emptySet(),
    val seleccionada: Int? = null,
) {
    val completa: Boolean get() = colocadas.size == n

    /** Tomar una pieza, o soltarla si ya estaba tomada. */
    fun tocarPieza(pieza: Int): SerieOrdenar =
        if (pieza !in enCanasto) this else copy(seleccionada = if (seleccionada == pieza) null else pieza)

    /** Intentar dejar la pieza tomada en `posicion`. */
    fun tocarLugar(posicion: Int): Pair<SerieOrdenar, Resultado> {
        val pieza = seleccionada ?: return this to Resultado.NADA
        if (posicion != pieza) return copy(seleccionada = null) to Resultado.ERROR
        val nueva = copy(colocadas = colocadas + pieza, enCanasto = enCanasto - pieza, seleccionada = null)
        return nueva to if (nueva.completa) Resultado.COMPLETO else Resultado.ACIERTO
    }

    companion object {
        fun nueva(n: Int, mezclar: (List<Int>) -> List<Int> = { it.shuffled() }): SerieOrdenar {
            val tamano = n.coerceAtLeast(1)
            return SerieOrdenar(tamano, mezclar((1..tamano).toList()))
        }
    }
}

/**
 * Pasar de uno en uno hasta llegar exacto al `objetivo`. Pasarse es el
 * control del error: la bandeja vuelve a empezar, sin castigo.
 */
data class Transferencia(
    val objetivo: Int,
    val total: Int,
    val enDestino: Int = 0,
) {
    val enOrigen: Int get() = total - enDestino
    val completa: Boolean get() = enDestino == objetivo

    fun transferirUno(): Pair<Transferencia, Resultado> {
        if (completa) return this to Resultado.NADA
        val siguiente = enDestino + 1
        if (siguiente > objetivo) return copy(enDestino = 0) to Resultado.ERROR
        val nueva = copy(enDestino = siguiente)
        return nueva to if (nueva.completa) Resultado.COMPLETO else Resultado.ACIERTO
    }

    companion object {
        fun nueva(objetivo: Int, origen: Int): Transferencia {
            val meta = objetivo.coerceAtLeast(1)
            return Transferencia(meta, origen.coerceAtLeast(meta))
        }
    }
}

// Curvas de dificultad de los materiales de seriación y transferencia: con
// nombre propio para que las pantallas y las pruebas usen las mismas.
val CURVA_TORRE_ROSA = listOf(3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 10)
val CURVA_CILINDROS = listOf(3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 10)
val CURVA_ESCALERA_MARRON = listOf(3, 4, 4, 5, 5, 6, 7, 7, 7, 7, 7)
val CURVA_CONTAR = listOf(2, 3, 3, 4, 5, 6, 7, 8, 9, 9, 9)
val CURVA_HUSOS = listOf(1, 2, 3, 3, 4, 5, 6, 7, 8, 9, 9)
val CURVA_PINZA = listOf(3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 10)
