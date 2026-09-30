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

/**
 * Ronda de clasificación: cada objeto pendiente va en la canasta de su
 * categoría. Equivocarse no lo saca de la ronda.
 */
data class RondaClasificar(
    /** Objetos pendientes como (id, categoría). */
    val pendientes: List<Pair<String, String>>,
    val acertados: Int = 0,
) {
    val completa: Boolean get() = pendientes.isEmpty()

    fun clasificar(id: String, canasta: String): Pair<RondaClasificar, Resultado> {
        val item = pendientes.find { it.first == id } ?: return this to Resultado.NADA
        if (item.second != canasta) return this to Resultado.ERROR
        val nueva = copy(pendientes = pendientes - item, acertados = acertados + 1)
        return nueva to if (nueva.completa) Resultado.COMPLETO else Resultado.ACIERTO
    }

    companion object {
        /**
         * Toma `cantidad` objetos del `pool` (sin pasar de su tamaño),
         * mezclados de forma estable según `semilla` (el nivel), para que
         * repetir un nivel dé la misma ronda.
         */
        fun <T> nueva(pool: List<T>, cantidad: Int, semilla: Int, clave: (T) -> Pair<String, String>): Pair<List<T>, RondaClasificar> {
            val elegidos = pool.shuffled(kotlin.random.Random(semilla)).take(cantidad.coerceIn(1, pool.size.coerceAtLeast(1)))
            return elegidos to RondaClasificar(elegidos.map(clave))
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

/** Un rompecabezas cuadrado: `piezas` en orden de lectura, todas distintas. */
data class EscenaRompecabezas(val lado: Int, val piezas: List<String>)

// Las piezas de cada escena son distintas entre sí: con dos iguales habría
// dos lugares "correctos" y el control del error dejaría de ser claro.
private val ESCENAS_ROMPECABEZAS = mapOf(
    2 to listOf("🌞", "☁️", "🌳", "🏠"),
    3 to listOf("🌞", "☁️", "🌈", "🌳", "🏠", "🌲", "🌷", "🐶", "🌻"),
    4 to listOf("🌙", "⭐", "☁️", "🌈", "🌲", "🏠", "🚗", "🌳", "🌷", "🐶", "🐱", "🌻", "🍄", "🐞", "🦋", "🐌"),
)

/** Lado de la cuadrícula según el nivel: 2×2 al empezar, 3×3 y luego 4×4. */
val CURVA_ROMPECABEZAS = listOf(2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4)

fun escenaRompecabezas(nivel: Int): EscenaRompecabezas {
    val lado = phasedInt(nivel, CURVA_ROMPECABEZAS).coerceIn(2, 4)
    return EscenaRompecabezas(lado, ESCENAS_ROMPECABEZAS.getValue(lado))
}

/**
 * Objetos por ronda en los materiales de clasificación. Antes eran siempre 6
 * en los 100 niveles; ahora arranca con 3 y llega a 8 (o al total del
 * material, si tiene menos).
 */
val CURVA_CLASIFICAR = listOf(3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8)
