package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeLogicTest {

    @Test
    fun `girar 180 grados en un tic se detecta como opuesta`() {
        assertTrue(esOpuesta(DERECHA, IZQUIERDA))
        assertTrue(esOpuesta(ARRIBA, ABAJO))
        assertFalse(esOpuesta(DERECHA, ARRIBA))
        assertFalse(esOpuesta(DERECHA, DERECHA))
    }

    @Test
    fun `avanzar sin comer mantiene el mismo largo`() {
        val vibora = listOf(5 to 5, 5 to 4, 5 to 3)
        val nueva = avanzarSnake(vibora, DERECHA, comio = false)
        assertEquals(3, nueva.size)
        assertEquals(5 to 6, nueva.first())
    }

    @Test
    fun `avanzar comiendo crece un segmento`() {
        val vibora = listOf(5 to 5, 5 to 4, 5 to 3)
        val nueva = avanzarSnake(vibora, DERECHA, comio = true)
        assertEquals(4, nueva.size)
        assertEquals(5 to 6, nueva.first())
        assertEquals(5 to 3, nueva.last())
    }

    @Test
    fun `choca con el muro fuera de los limites`() {
        assertTrue(chocaConMuro(-1 to 5))
        assertTrue(chocaConMuro(5 to SNAKE_COLS))
        assertFalse(chocaConMuro(5 to 5))
    }

    @Test
    fun `choca consigo misma cuando la cabeza pisa el cuerpo`() {
        val viboraChocada = listOf(5 to 5, 5 to 6, 5 to 7, 6 to 7, 6 to 6, 6 to 5, 5 to 5)
        assertTrue(chocaConsigoMisma(viboraChocada))
        val viboraLimpia = listOf(5 to 5, 5 to 4, 5 to 3)
        assertFalse(chocaConsigoMisma(viboraLimpia))
    }
}
