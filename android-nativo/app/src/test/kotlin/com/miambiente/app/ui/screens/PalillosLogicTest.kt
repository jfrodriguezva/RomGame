package com.miambiente.app.ui.screens

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private fun varilla(id: Int, cx: Float, cy: Float, angulo: Float, largo: Float = 100f) =
    Varilla(id = id, cx = cx, cy = cy, angulo = angulo, largo = largo, color = Color.Black)

class PalillosLogicTest {

    @Test
    fun `segmentosSeCruzan detecta cruce en X`() {
        assertTrue(segmentosSeCruzan(Punto(0f, 0f), Punto(10f, 10f), Punto(0f, 10f), Punto(10f, 0f)))
    }

    @Test
    fun `segmentosSeCruzan detecta no-cruce cuando son paralelos y separados`() {
        assertFalse(segmentosSeCruzan(Punto(0f, 0f), Punto(10f, 0f), Punto(0f, 5f), Punto(10f, 5f)))
    }

    @Test
    fun `segmentosSeCruzan detecta el caso colineal superpuesto`() {
        assertTrue(segmentosSeCruzan(Punto(0f, 0f), Punto(10f, 0f), Punto(5f, 0f), Punto(15f, 0f)))
    }

    @Test
    fun `segmentosSeCruzan detecta el caso de solo tocarse en un extremo`() {
        assertTrue(segmentosSeCruzan(Punto(0f, 0f), Punto(10f, 0f), Punto(10f, 0f), Punto(10f, 10f)))
    }

    @Test
    fun `varillasQueLaCruzan cuenta correctamente cuantas varillas del monton intersectan una dada`() {
        val horizontal = varilla(0, cx = 50f, cy = 50f, angulo = 0f)
        val vertical = varilla(1, cx = 50f, cy = 50f, angulo = 90f)
        val lejana = varilla(2, cx = 500f, cy = 500f, angulo = 0f)

        val cruces = varillasQueLaCruzan(horizontal, listOf(vertical, lejana))
        assertEquals(1, cruces.size)
        assertEquals(1, cruces.first().id)
    }

    @Test
    fun `la IA elige la varilla con menos cruces`() {
        val a = varilla(0, cx = 50f, cy = 50f, angulo = 0f)
        val b = varilla(1, cx = 50f, cy = 50f, angulo = 90f)
        val libre = varilla(2, cx = 500f, cy = 500f, angulo = 0f)

        val elegida = elegirVarillaIA(listOf(a, b, libre))
        assertEquals(2, elegida?.id)
    }

    @Test
    fun `probabilidadExito baja cuando hay mas cruces`() {
        assertTrue(probabilidadExito(0) > probabilidadExito(1))
        assertTrue(probabilidadExito(1) > probabilidadExito(3))
        assertEquals(0.2f, probabilidadExito(50))
        assertEquals(0.95f, probabilidadExito(0))
    }

    @Test
    fun `fueraDelMonton solo es true cuando ambos extremos quedan bajo la linea`() {
        val yaSalio = varilla(0, cx = 50f, cy = 300f, angulo = 0f, largo = 20f)
        assertTrue(fueraDelMonton(yaSalio))

        val cruzandoLaLinea = varilla(1, cx = 50f, cy = 260f, angulo = 90f, largo = 100f)
        assertFalse(fueraDelMonton(cruzandoLaLinea))
    }

    @Test
    fun `montonInicial genera N_VARILLAS con ids unicos`() {
        val monton = montonInicial()
        assertEquals(N_VARILLAS, monton.size)
        assertEquals(N_VARILLAS, monton.map { it.id }.distinct().size)
        assertTrue(monton.all { it.duenio == null })
    }
}
