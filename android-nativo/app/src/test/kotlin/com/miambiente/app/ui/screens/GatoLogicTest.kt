package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GatoLogicTest {

    private fun vacio() = List<String?>(9) { null }

    @Test
    fun `tablero vacio no tiene ganador`() {
        assertNull(ganador(vacio()))
    }

    @Test
    fun `fila superior completa gana`() {
        val t = vacio().toMutableList()
        t[0] = "X"; t[1] = "X"; t[2] = "X"
        assertEquals("X", ganador(t))
    }

    @Test
    fun `columna gana`() {
        val t = vacio().toMutableList()
        t[0] = "O"; t[3] = "O"; t[6] = "O"
        assertEquals("O", ganador(t))
    }

    @Test
    fun `diagonal gana`() {
        val t = vacio().toMutableList()
        t[0] = "X"; t[4] = "X"; t[8] = "X"
        assertEquals("X", ganador(t))
    }

    @Test
    fun `dos en linea no ganan`() {
        val t = vacio().toMutableList()
        t[0] = "X"; t[1] = "X"
        assertNull(ganador(t))
    }

    @Test
    fun `casillas vacias en medio de la linea no cuentan`() {
        val t = vacio().toMutableList()
        t[0] = "X"; t[2] = "X"
        assertNull(ganador(t))
    }

    @Test
    fun `la CPU toma la jugada que la hace ganar en vez de jugar al azar`() {
        // O ya tiene dos en la fila superior; puede ganar en la casilla 2.
        val t = vacio().toMutableList()
        t[0] = "O"; t[1] = "O"
        t[3] = "X"; t[4] = "X"
        assertEquals(2, mejorJugadaCpu(t))
    }

    @Test
    fun `la CPU bloquea al jugador en vez de dejarlo ganar`() {
        // X tiene dos en la fila superior (amenaza en la casilla 2) y la CPU
        // no tiene ninguna jugada propia para ganar ya — debe bloquear.
        val t = vacio().toMutableList()
        t[0] = "X"; t[1] = "X"
        t[3] = "O"
        assertEquals(2, mejorJugadaCpu(t))
    }

    @Test
    fun `la CPU con juego perfecto nunca pierde desde un tablero vacio`() {
        var t = vacio().toMutableList<String?>()
        var turnoJugador = true
        while (ganador(t) == null && t.any { it == null }) {
            if (turnoJugador) {
                val libre = t.indices.first { t[it] == null }
                t[libre] = "X"
            } else {
                val jugada = mejorJugadaCpu(t)
                t[jugada] = "O"
            }
            turnoJugador = !turnoJugador
        }
        assertTrue("la CPU con minimax real no debería perder", ganador(t) != "X")
    }
}
