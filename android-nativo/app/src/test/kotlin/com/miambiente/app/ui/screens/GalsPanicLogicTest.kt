package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GalsPanicLogicTest {

    @Test
    fun `bordeInicial marca exactamente el anillo exterior`() {
        val borde = bordeInicial(5, 4)
        // 5x4 = 20 celdas; interior es (5-2)x(4-2) = 3x2 = 6; borde = 14.
        assertEquals(14, borde.size)
        assertTrue(0 in borde) // esquina
        assertFalse(6 in borde) // celda (1,1): interior real, no es borde
    }

    @Test
    fun `celdaBorde distingue interior de borde`() {
        assertTrue(celdaBorde(0, 5, 4))
        assertFalse(celdaBorde(6, 5, 4)) // fila 1, col 1: interior
        assertTrue(celdaBorde(19, 5, 4)) // esquina opuesta
    }

    @Test
    fun `vecinos4 no se sale de la grilla`() {
        assertEquals(2, vecinos4(0, 5, 4).size) // esquina: solo 2 vecinos válidos
        assertEquals(4, vecinos4(7, 5, 4).size) // celda interior real de un tablero mas grande
    }

    @Test
    fun `sellarTrazo reclama ambas bolsas cuando no hay enemigos`() {
        // Grilla 5x5: borde ya seguro, interior 3x3 (9 celdas: 6,7,8,11,12,13,16,17,18).
        val cols = 5
        val filas = 5
        val safe = bordeInicial(cols, filas)
        val trazo = setOf(7, 12, 17) // corte vertical por la columna del medio
        val resultado = sellarTrazo(safe, trazo, cols, filas)
        // Las dos columnas restantes (6,11,16) y (8,13,18) deben quedar reclamadas.
        assertTrue(listOf(6, 11, 16, 8, 13, 18).all { it in resultado })
    }

    @Test
    fun `sellarTrazo no reclama la bolsa que tiene un enemigo adentro`() {
        val cols = 5
        val filas = 5
        val safe = bordeInicial(cols, filas)
        val trazo = setOf(7, 12, 17)
        val resultado = sellarTrazo(safe, trazo, cols, filas, celdasEnemigos = setOf(13))
        // La columna derecha (8,13,18) tiene un enemigo: no se reclama.
        assertFalse(8 in resultado)
        assertFalse(13 in resultado)
        assertFalse(18 in resultado)
        // La columna izquierda sigue reclamándose completa.
        assertTrue(listOf(6, 11, 16).all { it in resultado })
    }

    @Test
    fun `porcentajeReclamado sube al reclamar mas interior`() {
        val cols = 5
        val filas = 5
        val soloBorde = bordeInicial(cols, filas)
        val antes = porcentajeReclamado(soloBorde, cols, filas)
        val despues = porcentajeReclamado(soloBorde + setOf(6, 7, 8), cols, filas)
        assertTrue(despues > antes)
    }
}
