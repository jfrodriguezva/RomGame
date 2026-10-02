package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TetrisLogicTest {

    @Test
    fun `la pieza O tiene 4 celdas en cualquier rotacion`() {
        val o = PIEZAS_TETRIS[1]
        for (rot in 0..3) assertEquals(4, celdasDeTetris(o, rot).size)
    }

    @Test
    fun `rotar 4 veces vuelve a la forma original`() {
        val t = PIEZAS_TETRIS[2]
        val original = celdasDeTetris(t, 0).toSet()
        val trasCuatroGiros = celdasDeTetris(t, 4).toSet()
        assertEquals(original, trasCuatroGiros)
    }

    @Test
    fun `la pieza I rotada es vertical`() {
        val i = PIEZAS_TETRIS[0]
        val vertical = celdasDeTetris(i, 1)
        // Las 4 celdas deben compartir la misma columna en la rotación vertical.
        assertEquals(1, vertical.map { it.second }.distinct().size)
    }

    @Test
    fun `no hay colision contra un tablero vacio dentro de los limites`() {
        val tablero = List(TETRIS_FILAS) { List(TETRIS_COLS) { null as androidx.compose.ui.graphics.Color? } }
        val estado = EstadoPiezaTetris(PIEZAS_TETRIS[1], 0, fila = 0, col = 0)
        assertFalse(colisionaTetris(estado, tablero))
    }

    @Test
    fun `hay colision al salirse por la derecha del tablero`() {
        val tablero = List(TETRIS_FILAS) { List(TETRIS_COLS) { null as androidx.compose.ui.graphics.Color? } }
        val estado = EstadoPiezaTetris(PIEZAS_TETRIS[1], 0, fila = 0, col = TETRIS_COLS - 1)
        assertTrue(colisionaTetris(estado, tablero))
    }

    @Test
    fun `hay colision contra una celda ya ocupada`() {
        val color = androidx.compose.ui.graphics.Color(0xFF000000)
        val tablero = List(TETRIS_FILAS) { fila -> List(TETRIS_COLS) { if (fila == 5) color else null } }
        val estado = EstadoPiezaTetris(PIEZAS_TETRIS[1], 0, fila = 5, col = 0)
        assertTrue(colisionaTetris(estado, tablero))
    }

    @Test
    fun `una pieza arriba del tablero visible no choca aunque este fuera de vista`() {
        val tablero = List(TETRIS_FILAS) { List(TETRIS_COLS) { null as androidx.compose.ui.graphics.Color? } }
        val estado = EstadoPiezaTetris(PIEZAS_TETRIS[0], 0, fila = -1, col = 0)
        assertFalse(colisionaTetris(estado, tablero))
    }

    @Test
    fun `la pieza fantasma cae hasta el fondo del tablero vacio`() {
        val tablero = List(TETRIS_FILAS) { List(TETRIS_COLS) { null as androidx.compose.ui.graphics.Color? } }
        val o = PIEZAS_TETRIS[1] // pieza O: ocupa 2 filas de alto
        val estado = EstadoPiezaTetris(o, 0, fila = 0, col = 0)
        val fantasma = posicionFantasma(estado, tablero)
        assertEquals(TETRIS_FILAS - 2, fantasma.fila)
    }

    @Test
    fun `la pieza fantasma se detiene justo encima de una pila existente`() {
        val color = androidx.compose.ui.graphics.Color(0xFF000000)
        val tablero = List(TETRIS_FILAS) { fila -> List(TETRIS_COLS) { if (fila == 10) color else null } }
        val o = PIEZAS_TETRIS[1]
        val estado = EstadoPiezaTetris(o, 0, fila = 0, col = 0)
        val fantasma = posicionFantasma(estado, tablero)
        assertEquals(8, fantasma.fila)
    }
}
