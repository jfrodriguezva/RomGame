package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArcadeNuevosLogicTest {
    @Test
    fun moverValido_noPermiteAtravesarParedNiSaltarDeFila() {
        val libres = setOf(4, 5, 6)

        assertEquals(4, moverValido(4, 1, libres, cols = 5))
        assertEquals(5, moverValido(5, -1, libres, cols = 5))
        assertEquals(6, moverValido(5, 1, libres, cols = 5))
    }

    @Test
    fun capturarTerritorio_revelaSoloElLadoSinGuardian() {
        val cols = 5
        val total = 25
        val borde = (0 until total).filter {
            it / cols == 0 || it / cols == 4 || it % cols == 0 || it % cols == 4
        }.toSet()
        val trazo = setOf(7, 12, 17)

        val resultado = capturarTerritorio(
            seguras = borde,
            trazo = trazo,
            guardianes = setOf(11),
            cols = cols,
            total = total,
        )

        assertTrue(trazo.all { it in resultado })
        assertTrue(setOf(8, 13, 18).all { it in resultado })
        assertFalse(setOf(6, 11, 16).any { it in resultado })
    }
}
