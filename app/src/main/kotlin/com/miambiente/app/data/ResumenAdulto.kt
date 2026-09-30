package com.miambiente.app.data

/**
 * Lo que la vista del adulto destaca de la última semana. Separado de la
 * pantalla para poder probar las reglas sin emulador.
 */
data class ResumenSemana(
    /** Días distintos (de los últimos 7) en que se abrió algún modo. */
    val diasActivos: Int,
    /** Modos abiertos en la semana, del más al menos practicado. */
    val modosSemana: List<String>,
    /** Modos donde se equivoca mucho: conviene bajar de nivel o jugarlos juntos. */
    val cuesta: List<String>,
    /** Un modo para proponerle la próxima vez. */
    val sugerencia: String?,
)

/** Respuestas mínimas antes de juzgar que un modo le está costando. */
internal const val RESPUESTAS_MINIMAS = 8

/** Proporción de intentos fallidos a partir de la cual un modo "cuesta". */
internal const val PROPORCION_ERRORES = 0.4

/**
 * @param orden los ids en el orden del catálogo (desempata).
 * @param materialDe a qué material consolidado pertenece cada modo.
 * @param hoy día actual, en días desde la época.
 */
fun resumirSemana(
    progreso: Map<String, GameProgress>,
    orden: List<String>,
    materialDe: (String) -> String,
    hoy: Long,
): ResumenSemana {
    val semana = (hoy - 6)..hoy
    fun diasEnSemana(id: String) = progreso[id]?.dias.orEmpty().count { it in semana }

    val diasActivos = progreso.values.flatMap { it.dias }.filter { it in semana }.toSet().size

    val modosSemana = orden
        .filter { diasEnSemana(it) > 0 }
        .sortedByDescending { diasEnSemana(it) }

    val cuesta = orden
        .mapNotNull { id ->
            val p = progreso[id] ?: return@mapNotNull null
            val total = p.aciertos + p.errores
            if (total < RESPUESTAS_MINIMAS) return@mapNotNull null
            val proporcion = p.errores.toDouble() / total
            if (proporcion >= PROPORCION_ERRORES) id to proporcion else null
        }
        .sortedByDescending { it.second }
        .take(3)
        .map { it.first }

    // Sugerencia: del material menos practicado, el modo que menos se ha
    // abierto y que no se jugó esta semana. Así el resumen empuja a variar.
    val vecesPorMaterial = orden.groupBy(materialDe)
        .mapValues { (_, ids) -> ids.sumOf { progreso[it]?.vecesJugado ?: 0 } }
    val sugerencia = orden
        .filter { diasEnSemana(it) == 0 }
        .minWithOrNull(
            compareBy<String> { vecesPorMaterial[materialDe(it)] ?: 0 }
                .thenBy { progreso[it]?.vecesJugado ?: 0 }
                .thenBy { orden.indexOf(it) },
        )

    return ResumenSemana(diasActivos, modosSemana, cuesta, sugerencia)
}
