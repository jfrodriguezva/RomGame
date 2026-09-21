package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArkanoidLogicTest {

    @Test
    fun `el nivel 1 tiene 3 filas por 6 columnas de ladrillos`() {
        val ladrillos = ladrillosParaNivel(1)
        assertEquals(3 * 6, ladrillos.size)
        assertEquals(ladrillos.size, ladrillos.map { it.id }.distinct().size)
        assertTrue(ladrillos.all { it.vidas == 1 })
    }

    @Test
    fun `los niveles agregan filas hasta un tope`() {
        assertEquals(3, filasParaNivel(1))
        assertEquals(4, filasParaNivel(2))
        assertEquals(8, filasParaNivel(20))
    }

    @Test
    fun `desde el nivel 3 hay ladrillos reforzados de 2 vidas`() {
        val ladrillos = ladrillosParaNivel(3)
        assertTrue(ladrillos.any { it.vidas == 2 })
        assertTrue(ladrillos.any { it.vidas == 1 })
    }

    @Test
    fun `la velocidad sube con el nivel pero no baja`() {
        assertTrue(velocidadParaNivel(5) > velocidadParaNivel(1))
        assertTrue(velocidadParaNivel(50) >= velocidadParaNivel(20))
    }

    @Test
    fun `un circulo lejos del rectangulo no choca`() {
        assertFalse(circuloChocaRect(bx = 0f, by = 0f, r = 5f, rx = 100f, ry = 100f, rw = 40f, rh = 20f))
    }

    @Test
    fun `un circulo centrado dentro del rectangulo si choca`() {
        assertTrue(circuloChocaRect(bx = 120f, by = 110f, r = 5f, rx = 100f, ry = 100f, rw = 40f, rh = 20f))
    }

    @Test
    fun `un circulo justo tocando el borde del rectangulo si choca`() {
        // Rectángulo de (100,100) a (140,120); círculo de radio 5 centrado en (145,110):
        // el punto más cercano del rectángulo es (140,110), a distancia 5 — justo el radio.
        assertTrue(circuloChocaRect(bx = 145f, by = 110f, r = 5f, rx = 100f, ry = 100f, rw = 40f, rh = 20f))
    }

    @Test
    fun `un circulo a mas del radio de distancia no choca`() {
        assertFalse(circuloChocaRect(bx = 150f, by = 110f, r = 5f, rx = 100f, ry = 100f, rw = 40f, rh = 20f))
    }
}
