package com.miambiente.app.ui.screens

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SolitarioAranaLogicTest {

    private fun carta(v: Int) = CartaSol(0, v)

    @Test
    fun `en la arana una secuencia solo necesita bajar de 1 en 1, sin importar el color`() {
        val col = (13 downTo 10).map { carta(it) to true }
        assertTrue(secuenciaValidaArana(col, 0))
    }

    @Test
    fun `cualquier carta se puede colocar en una columna vacia en la arana`() {
        assertTrue(puedeColocarEnColumnaArana(carta(5), emptyList()))
    }

    @Test
    fun `una columna con las 13 cartas en orden K a As al final es un juego completo`() {
        val col = (13 downTo 1).map { carta(it) to true }
        assertTrue(juegoCompletoEnCola(col))
    }

    @Test
    fun `una columna con menos de 13 cartas nunca es un juego completo`() {
        val col = (13 downTo 5).map { carta(it) to true }
        assertFalse(juegoCompletoEnCola(col))
    }

    @Test
    fun `una columna de 13 cartas que no empieza en rey no es un juego completo`() {
        val col = (12 downTo 1).map { carta(it) to true } + (carta(1) to true)
        assertFalse(juegoCompletoEnCola(col))
    }
}
