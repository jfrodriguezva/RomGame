package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiezDiferenciasLogicTest {
    @Test
    fun `nuevaRonda genera exactamente diez diferencias`() {
        val ronda = nuevaRonda()

        assertEquals(10, ronda.diferencias.size)
        assertEquals(ronda.a.size, ronda.b.size)
    }

    @Test
    fun `las celdas marcadas como diferencia realmente difieren y el resto coincide`() {
        val ronda = nuevaRonda()

        ronda.a.indices.forEach { i ->
            if (i in ronda.diferencias) {
                assertTrue(ronda.a[i] != ronda.b[i])
            } else {
                assertEquals(ronda.a[i], ronda.b[i])
            }
        }
    }
}
