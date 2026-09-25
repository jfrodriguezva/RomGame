package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EscuadronEstelarLogicTest {

    @Test
    fun `el intervalo entre enemigos baja con los puntos pero no menos de 550ms`() {
        assertTrue(intervaloEnemigoEscuadron(10) < intervaloEnemigoEscuadron(0))
        assertEquals(550L, intervaloEnemigoEscuadron(100))
    }

    @Test
    fun `impactaEscudo solo si el enemigo esta en el carril del jugador`() {
        assertTrue(impactaEscudo(carril = 1, enemigo = 1))
        assertFalse(impactaEscudo(carril = 1, enemigo = 2))
    }

    @Test
    fun `disparoAcierta solo si el carril coincide con el enemigo`() {
        assertTrue(disparoAcierta(carril = 0, enemigo = 0))
        assertFalse(disparoAcierta(carril = 0, enemigo = 1))
    }

    @Test
    fun `gana con 15 puntos o mas`() {
        assertFalse(ganoEscuadron(14))
        assertTrue(ganoEscuadron(15))
    }
}
