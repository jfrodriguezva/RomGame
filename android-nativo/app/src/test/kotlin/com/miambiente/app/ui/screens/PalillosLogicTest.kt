package com.miambiente.app.ui.screens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PalillosLogicTest {

    private fun palillo(id: Int, cx: Float, cy: Float, angulo: Float, longitud: Float, z: Int) =
        Palillo(id = id, cx = cx, cy = cy, angulo = angulo, longitud = longitud, valor = 5, color = Color.Black, z = z)

    @Test
    fun `generarPila arma 19 palillos con un solo negro (el Mikado) de mayor valor`() {
        val pila = generarPila()
        assertEquals(19, pila.size)
        assertEquals(19, pila.map { it.id }.distinct().size)
        assertEquals(19, pila.map { it.z }.distinct().size)
        val negros = pila.filter { it.valor == 30 }
        assertEquals(1, negros.size)
        assertTrue(pila.all { it.valor <= 30 })
    }

    @Test
    fun `dos segmentos horizontal y vertical que se cruzan en el centro si se detectan`() {
        val a1 = Offset(0f, 5f); val a2 = Offset(10f, 5f)
        val b1 = Offset(5f, 0f); val b2 = Offset(5f, 10f)
        assertTrue(seCruzan(a1, a2, b1, b2))
    }

    @Test
    fun `dos segmentos paralelos separados no se cruzan`() {
        val a1 = Offset(0f, 0f); val a2 = Offset(10f, 0f)
        val b1 = Offset(0f, 5f); val b2 = Offset(10f, 5f)
        assertFalse(seCruzan(a1, a2, b1, b2))
    }

    @Test
    fun `un palillo sin nada encima (z mas alto) esta libre`() {
        // Dos palillos que se cruzan geométricamente (mismo centro, ángulos
        // distintos): el de z mayor (más arriba en la pila) debe estar
        // libre, y el de z menor (tapado) no.
        val debajo = palillo(id = 1, cx = 0.5f, cy = 0.5f, angulo = 0f, longitud = 0.5f, z = 0)
        val arriba = palillo(id = 2, cx = 0.5f, cy = 0.5f, angulo = 90f, longitud = 0.5f, z = 1)
        val pila = listOf(debajo, arriba)
        assertTrue(esLibre(arriba, pila))
        assertFalse(esLibre(debajo, pila))
    }

    @Test
    fun `dos palillos que no se cruzan estan libres los dos`() {
        val uno = palillo(id = 1, cx = 0.2f, cy = 0.2f, angulo = 0f, longitud = 0.1f, z = 0)
        val dos = palillo(id = 2, cx = 0.8f, cy = 0.8f, angulo = 0f, longitud = 0.1f, z = 1)
        val pila = listOf(uno, dos)
        assertTrue(esLibre(uno, pila))
        assertTrue(esLibre(dos, pila))
    }

    @Test
    fun `la distancia de un punto a un segmento es cero si el punto esta sobre el segmento`() {
        val a = Offset(0f, 0f); val b = Offset(10f, 0f)
        assertEquals(0f, distanciaPuntoSegmento(Offset(5f, 0f), a, b), 0.001f)
    }

    @Test
    fun `la distancia de un punto a un segmento se mide perpendicular cuando cae dentro del rango`() {
        val a = Offset(0f, 0f); val b = Offset(10f, 0f)
        assertEquals(3f, distanciaPuntoSegmento(Offset(5f, 3f), a, b), 0.001f)
    }
}
