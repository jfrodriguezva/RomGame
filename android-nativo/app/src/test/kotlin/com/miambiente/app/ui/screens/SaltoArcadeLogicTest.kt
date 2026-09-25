package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaltoArcadeLogicTest {

    @Test
    fun `iniciarSalto arranca con altura positiva y la velocidad del impulso`() {
        val (altura, velocidad) = iniciarSalto(impulso = 330f)
        assertEquals(1f, altura)
        assertEquals(330f, velocidad)
    }

    @Test
    fun `avanzarSalto sube mientras la velocidad es positiva`() {
        val (altura, velocidad) = avanzarSalto(alturaSalto = 10f, velocidadSalto = 300f, gravedad = 500f, dt = 0.05f)
        assertTrue(altura > 10f)
        assertTrue(velocidad < 300f) // la gravedad ya empezó a frenarlo
    }

    @Test
    fun `avanzarSalto aterriza en 0 apenas la altura cruza el piso`() {
        val (altura, velocidad) = avanzarSalto(alturaSalto = 2f, velocidadSalto = -400f, gravedad = 500f, dt = 0.05f)
        assertEquals(0f, altura)
        assertEquals(0f, velocidad)
    }

    @Test
    fun `avanzarSalto no hace nada si ya esta en el piso`() {
        val (altura, velocidad) = avanzarSalto(alturaSalto = 0f, velocidadSalto = 0f, gravedad = 500f, dt = 0.05f)
        assertEquals(0f, altura)
        assertEquals(0f, velocidad)
    }
}
