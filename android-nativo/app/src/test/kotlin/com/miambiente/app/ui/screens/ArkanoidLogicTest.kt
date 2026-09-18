package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArkanoidLogicTest {

    @Test
    fun `el tablero inicial tiene filas por columnas ladrillos`() {
        val ladrillos = ladrillosIniciales()
        assertEquals(FILAS_LADRILLOS_TEST * COLS_LADRILLOS_TEST, ladrillos.size)
        assertEquals(ladrillos.size, ladrillos.map { it.id }.distinct().size)
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

private const val FILAS_LADRILLOS_TEST = 5
private const val COLS_LADRILLOS_TEST = 6
