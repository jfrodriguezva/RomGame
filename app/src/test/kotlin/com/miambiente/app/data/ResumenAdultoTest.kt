package com.miambiente.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Reglas del resumen semanal de la vista del adulto y del historial de días. */
class ResumenAdultoTest {
    private val hoy = 20_000L
    private val orden = listOf("a1", "a2", "b1", "b2")
    private val material = mapOf("a1" to "A", "a2" to "A", "b1" to "B", "b2" to "B")

    private fun resumir(progreso: Map<String, GameProgress>) =
        resumirSemana(progreso, orden, { material.getValue(it) }, hoy)

    @Test
    fun `sin actividad no hay dias ni modos de la semana`() {
        val r = resumir(emptyMap())
        assertEquals(0, r.diasActivos)
        assertTrue(r.modosSemana.isEmpty())
        assertTrue(r.cuesta.isEmpty())
    }

    @Test
    fun `cuenta dias distintos solo de los ultimos siete`() {
        val r = resumir(
            mapOf(
                "a1" to GameProgress(dias = setOf(hoy, hoy - 1, hoy - 10)),
                "b1" to GameProgress(dias = setOf(hoy, hoy - 6)),
            ),
        )
        assertEquals(3, r.diasActivos)
    }

    @Test
    fun `los modos de la semana van del mas practicado al menos`() {
        val r = resumir(
            mapOf(
                "a1" to GameProgress(dias = setOf(hoy)),
                "b2" to GameProgress(dias = setOf(hoy, hoy - 1, hoy - 2)),
                "a2" to GameProgress(dias = setOf(hoy - 8)),
            ),
        )
        assertEquals(listOf("b2", "a1"), r.modosSemana)
    }

    @Test
    fun `un modo cuesta con muchos errores y suficientes respuestas`() {
        val r = resumir(
            mapOf(
                "a1" to GameProgress(aciertos = 4, errores = 6),
                "a2" to GameProgress(aciertos = 1, errores = 3),
                "b1" to GameProgress(aciertos = 20, errores = 2),
            ),
        )
        assertEquals(listOf("a1"), r.cuesta)
    }

    @Test
    fun `la sugerencia viene del material menos practicado`() {
        val r = resumir(
            mapOf(
                "a1" to GameProgress(vecesJugado = 5, dias = setOf(hoy)),
                "a2" to GameProgress(vecesJugado = 1),
                "b1" to GameProgress(vecesJugado = 2),
            ),
        )
        assertEquals("b2", r.sugerencia)
    }

    @Test
    fun `no sugiere lo que ya jugo esta semana`() {
        val todos = orden.associateWith { GameProgress(dias = setOf(hoy)) }
        assertNull(resumir(todos).sugerencia)
    }

    @Test
    fun `el historial de dias descarta los viejos`() {
        val dias = agregarDia(setOf(hoy - DIAS_GUARDADOS, hoy - 5), hoy)
        assertEquals(setOf(hoy - 5, hoy), dias)
    }

    @Test
    fun `el historial de dias se guarda y se lee igual`() {
        val dias = setOf(hoy, hoy - 3)
        assertEquals(dias, leerDias(escribirDias(dias)))
        assertEquals(emptySet<Long>(), leerDias("x,,"))
    }
}
