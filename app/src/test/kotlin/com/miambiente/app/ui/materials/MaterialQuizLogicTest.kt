package com.miambiente.app.ui.materials

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Lección de tres periodos y generación de opciones del quiz. */
class MaterialQuizLogicTest {
    private val items = (1..8).map { ItemQuiz(it, "id$it", "Nombre $it") }

    @Test
    fun `el periodo avanza con la etapa y nunca pasa de 3`() {
        assertEquals(1, periodoPara(1))
        assertEquals(1, periodoPara(10))
        assertEquals(2, periodoPara(11))
        assertEquals(2, periodoPara(20))
        assertEquals(3, periodoPara(21))
        assertEquals(3, periodoPara(100))
    }

    @Test
    fun `las opciones incluyen siempre al objetivo, sin repetir`() {
        repeat(50) {
            val objetivo = items.random()
            val opciones = generarOpciones(items, objetivo, 4)
            assertEquals(4, opciones.size)
            assertTrue(objetivo in opciones)
            assertEquals(4, opciones.map { it.id }.toSet().size)
        }
    }

    @Test
    fun `pedir mas opciones que items no inventa elementos`() {
        val opciones = generarOpciones(items, items.first(), 20)
        assertEquals(items.size, opciones.size)
    }
}
