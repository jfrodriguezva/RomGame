package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TopoLogicTest {

    @Test
    fun `el avance de la ronda no pasa de 20`() {
        assertEquals(0, avanceRondaTopo(DURACION_RONDA_S))
        assertEquals(10, avanceRondaTopo(DURACION_RONDA_S - 10))
        assertEquals(20, avanceRondaTopo(0))
    }

    @Test
    fun `la duracion arriba del topo baja con el avance pero no menos de 400ms`() {
        assertTrue(duracionTopoArriba(10) < duracionTopoArriba(0))
        assertEquals(400L, duracionTopoArriba(20))
    }

    @Test
    fun `la pausa entre apariciones baja con el avance pero no menos de 180ms`() {
        assertTrue(pausaEntreTopos(10) < pausaEntreTopos(0))
        assertEquals(300L, pausaEntreTopos(20))
        assertEquals(180L, pausaEntreTopos(50))
    }

    @Test
    fun `siguienteHoyoTopo nunca repite el hoyo actual`() {
        val muestras = listOf(0f, 0.12f, 0.33f, 0.5f, 0.67f, 0.88f, 0.999f)
        muestras.forEach { azar -> assertTrue(siguienteHoyoTopo(actual = 4, azar = azar) != 4) }
    }

    @Test
    fun `siguienteHoyoTopo cubre el primer y el ultimo hoyo segun el azar`() {
        assertEquals(0, siguienteHoyoTopo(actual = null, azar = 0f))
        assertEquals(HOYOS - 1, siguienteHoyoTopo(actual = null, azar = 0.999f))
    }

    @Test
    fun `siguienteHoyoTopo distribuye parejo entre los candidatos, sin sesgo de modulo`() {
        // Antes, mapear un indice 0..HOYOS-1 con modulo sobre 8 candidatos
        // hacia un hoyo casi el doble de veces que los demas. Con `azar`
        // repartido parejo en [0,1) cada candidato debe salir ~1000 de 8000.
        val conteo = mutableMapOf<Int, Int>()
        for (i in 0 until 8000) {
            val hoyo = siguienteHoyoTopo(actual = 4, azar = i / 8000f)
            conteo[hoyo] = (conteo[hoyo] ?: 0) + 1
        }
        assertEquals(8, conteo.size)
        assertTrue(conteo.values.all { it in 950..1050 })
    }

    @Test
    fun `gana la ronda con 10 puntos o mas`() {
        assertFalse(ganoRondaTopo(9))
        assertTrue(ganoRondaTopo(10))
        assertTrue(ganoRondaTopo(15))
    }
}
