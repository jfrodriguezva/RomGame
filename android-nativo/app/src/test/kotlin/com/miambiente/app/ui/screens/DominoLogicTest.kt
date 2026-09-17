package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DominoLogicTest {

    @Test
    fun `el set doble-6 tiene exactamente 28 fichas`() {
        assertEquals(28, setCompleto().size)
    }

    @Test
    fun `no hay fichas repetidas ni con a mayor que b`() {
        val set = setCompleto()
        val pares = set.map { it.a to it.b }
        assertEquals("hay pares duplicados", pares.size, pares.toSet().size)
        set.forEach { assertTrue("${it.a},${it.b} debería tener a <= b", it.a <= it.b) }
    }

    @Test
    fun `estan todas las combinaciones posibles del 0 al 6`() {
        val esperadas = (0..6).flatMap { a -> (a..6).map { b -> a to b } }.toSet()
        val obtenidas = setCompleto().map { it.a to it.b }.toSet()
        assertEquals(esperadas, obtenidas)
    }

    @Test
    fun `encaja reconoce ambos lados de la ficha`() {
        val f = Ficha(0, 2, 4)
        assertTrue(encaja(f, 2))
        assertTrue(encaja(f, 4))
        assertFalse(encaja(f, 3))
    }

    @Test
    fun `otroLado da el lado contrario al extremo dado`() {
        val f = Ficha(0, 2, 4)
        assertEquals(4, otroLado(f, 2))
        assertEquals(2, otroLado(f, 4))
    }

    @Test
    fun `ficha doble (mula) da el mismo lado de cualquier forma`() {
        val mula = Ficha(0, 3, 3)
        assertEquals(3, otroLado(mula, 3))
    }
}
