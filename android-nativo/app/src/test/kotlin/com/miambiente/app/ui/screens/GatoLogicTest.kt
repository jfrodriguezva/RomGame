package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
}
