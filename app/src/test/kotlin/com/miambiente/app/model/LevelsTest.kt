package com.miambiente.app.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas internas de la curva de niveles (`phased`/`phasedInt`), el
 * mismo motor que usan los 98 materiales para escalar dificultad. Un
 * error aquí afecta silenciosamente a todo el catálogo a la vez, así que
 * vale la pena tenerlo cubierto aparte de cualquier pantalla.
 */
class LevelsTest {

    @Test
    fun `etapaDe nivel 1 es etapa 1`() {
        assertEquals(1, etapaDe(1))
    }

    @Test
    fun `etapaDe nivel 100 es etapa 10, la ultima`() {
        assertEquals(10, etapaDe(100))
    }

    @Test
    fun `etapaDe nunca se sale de 1 al 10`() {
        for (nivel in 1..100) {
            val etapa = etapaDe(nivel)
            assertTrue("nivel $nivel dio etapa $etapa", etapa in 1..10)
        }
    }

    @Test
    fun `phasedInt en el primer nivel de cada etapa toca la parada exacta`() {
        val paradas = listOf(2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6)
        // Nivel 1 (etapa 1, inicio) debe dar exactamente paradas[0].
        assertEquals(2, phasedInt(1, paradas))
    }

    @Test
    fun `phasedInt no decrece con paradas crecientes`() {
        val paradas = listOf(2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6)
        var anterior = phasedInt(1, paradas)
        for (nivel in 2..100) {
            val actual = phasedInt(nivel, paradas)
            assertTrue("nivel $nivel: $actual bajó de $anterior", actual >= anterior)
            anterior = actual
        }
    }

    @Test
    fun `estrellasPara siempre da entre 1 y 5`() {
        for (nivel in 1..100) {
            val estrellas = estrellasPara(nivel)
            assertTrue(estrellas in 1..5)
        }
    }

    @Test
    fun `estrellasPara nivel 1 da 1 estrella, nivel 100 da 5`() {
        assertEquals(1, estrellasPara(1))
        assertEquals(5, estrellasPara(100))
    }
}
