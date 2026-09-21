package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SolitarioLogicTest {

    @Test
    fun `la baraja nueva tiene 52 cartas unicas`() {
        val baraja = barajaNuevaSol()
        assertEquals(52, baraja.size)
        assertEquals(52, baraja.toSet().size)
    }

    @Test
    fun `corazones y diamantes son rojos, picas y treboles no`() {
        assertTrue(esRojoSol(1))
        assertTrue(esRojoSol(2))
        assertFalse(esRojoSol(0))
        assertFalse(esRojoSol(3))
    }

    @Test
    fun `una secuencia que alterna color y baja de 1 en 1 es valida`() {
        val col = listOf(
            CartaSol(0, 8) to true, // 8 picas (negro)
            CartaSol(1, 7) to true, // 7 corazones (rojo)
            CartaSol(3, 6) to true, // 6 treboles (negro)
        )
        assertTrue(secuenciaValidaSol(col, 0))
    }

    @Test
    fun `una secuencia con dos cartas del mismo color seguidas no es valida`() {
        val col = listOf(
            CartaSol(0, 8) to true, // negro
            CartaSol(3, 7) to true, // negro tambien — invalido
        )
        assertFalse(secuenciaValidaSol(col, 0))
    }

    @Test
    fun `una carta boca abajo no se puede seleccionar como inicio de secuencia`() {
        val col = listOf(CartaSol(0, 8) to false, CartaSol(1, 7) to true)
        assertFalse(secuenciaValidaSol(col, 0))
    }

    @Test
    fun `solo un rey puede colocarse en una columna vacia`() {
        assertTrue(puedeColocarEnColumnaSol(CartaSol(0, 13), emptyList()))
        assertFalse(puedeColocarEnColumnaSol(CartaSol(0, 5), emptyList()))
    }

    @Test
    fun `una carta se coloca sobre otra de color distinto y un valor mas`() {
        val destino = listOf(CartaSol(1, 8) to true) // 8 rojo
        assertTrue(puedeColocarEnColumnaSol(CartaSol(0, 7), destino)) // 7 negro, va arriba
        assertFalse(puedeColocarEnColumnaSol(CartaSol(1, 7), destino)) // mismo color, invalido
        assertFalse(puedeColocarEnColumnaSol(CartaSol(0, 6), destino)) // valor no consecutivo
    }

    @Test
    fun `la fundacion solo acepta la siguiente carta en orden, empezando en el As`() {
        assertTrue(puedeColocarEnFundacionSol(CartaSol(0, 1), 0))
        assertFalse(puedeColocarEnFundacionSol(CartaSol(0, 2), 0))
        assertTrue(puedeColocarEnFundacionSol(CartaSol(0, 5), 4))
    }
}
