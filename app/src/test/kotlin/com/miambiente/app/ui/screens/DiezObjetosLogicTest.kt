package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiezObjetosLogicTest {
    @Test
    fun `nuevaEscena coloca diez objetivos distintos en celdas distintas`() {
        val escena = nuevaEscena()

        assertEquals(10, escena.objetivos.toSet().size)
        assertEquals(10, escena.posiciones.size)
        assertEquals(escena.objetivos.toSet(), escena.posiciones.values.toSet())
        assertTrue(escena.posiciones.keys.all { it in escena.celdas.indices })
    }

    @Test
    fun `cada celda objetivo coincide con la escena visible`() {
        val escena = nuevaEscena()

        escena.posiciones.forEach { (indice, emoji) ->
            assertEquals(emoji, escena.celdas[indice])
        }
    }
}
