package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GranPremioLogicTest {

    @Test
    fun `el intervalo entre obstaculos baja con la distancia pero no menos de 260ms`() {
        assertTrue(intervaloObstaculoGranPremio(10) < intervaloObstaculoGranPremio(0))
        assertEquals(260L, intervaloObstaculoGranPremio(100))
    }

    @Test
    fun `chocaGranPremio solo si el carril coincide con el obstaculo`() {
        assertTrue(chocaGranPremio(carril = 2, obstaculo = 2))
        assertFalse(chocaGranPremio(carril = 2, obstaculo = 0))
    }

    @Test
    fun `gana la carrera al llegar a 30 de distancia`() {
        assertFalse(ganoGranPremio(29))
        assertTrue(ganoGranPremio(30))
    }
}
