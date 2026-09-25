package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PangLogicTest {

    @Test
    fun `una burbuja de tamano 1 no se divide mas`() {
        val chica = BurbujaState(id = 0, x = 10f, y = 10f, velX = 5f, velY = 0f, tamano = 1)
        assertTrue(dividirBurbuja(chica, siguienteId = 100).isEmpty())
    }

    @Test
    fun `una burbuja de tamano 2 o 3 se divide en dos con velocidades horizontales opuestas`() {
        val grande = BurbujaState(id = 0, x = 10f, y = 10f, velX = 0f, velY = 0f, tamano = 3)
        val hijas = dividirBurbuja(grande, siguienteId = 100)
        assertEquals(2, hijas.size)
        assertTrue(hijas.all { it.tamano == 2 })
        assertEquals(100, hijas[0].id)
        assertEquals(101, hijas[1].id)
        assertTrue(hijas[0].velX > 0f && hijas[1].velX < 0f)
    }

    @Test
    fun `el radio crece con el tamano`() {
        assertTrue(radioDe(1) < radioDe(2))
        assertTrue(radioDe(2) < radioDe(3))
    }

    @Test
    fun `burbujasParaNivel agrega mas burbujas en niveles mayores hasta un tope`() {
        assertTrue(burbujasParaNivel(5).size >= burbujasParaNivel(1).size)
        assertEquals(6, burbujasParaNivel(20).size)
    }

    @Test
    fun `burbujasParaNivel sube la velocidad base con el nivel`() {
        val rapidezNivel1 = burbujasParaNivel(1).first().velX
        val rapidezNivel5 = burbujasParaNivel(5).first().velX
        assertTrue(kotlin.math.abs(rapidezNivel5) > kotlin.math.abs(rapidezNivel1))
    }

    @Test
    fun `circuloChocaRect detecta el arpon como rectangulo delgado`() {
        // Arpón: rectángulo angosto (3f de ancho) desde y=50 hasta y=400.
        assertTrue(circuloChocaRect(bx = 50f, by = 200f, r = 20f, rx = 48.5f, ry = 50f, rw = 3f, rh = 350f))
        assertFalse(circuloChocaRect(bx = 100f, by = 200f, r = 20f, rx = 48.5f, ry = 50f, rw = 3f, rh = 350f))
    }

    @Test
    fun `deberiaCaerPowerUpPang es determinista dado un azar fijo`() {
        assertTrue(deberiaCaerPowerUpPang(0.1f))
        assertFalse(deberiaCaerPowerUpPang(0.9f))
    }
}
