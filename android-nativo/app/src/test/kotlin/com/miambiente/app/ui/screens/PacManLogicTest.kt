package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PacManLogicTest {

    @Test
    fun `el mapa tiene exactamente 4 pellets de poder en las esquinas abiertas`() {
        assertEquals(4, PELLETS_POR_DEFECTO.size)
        assertTrue(PELLETS_POR_DEFECTO.all { it in CELDAS_LIBRES_PACMAN })
    }

    @Test
    fun `destinoValido rechaza moverse contra un muro`() {
        // Celda (1,4) = fila 1 col 4, abierta; arriba de ella (fila 0) es muro.
        val celda = 1 * PACMAN_COLS + 4
        assertNull(destinoValido(celda, DireccionPacman.ARRIBA, PACMAN_COLS, CELDAS_LIBRES_PACMAN))
    }

    @Test
    fun `destinoValido acepta moverse a una celda libre`() {
        val celda = 1 * PACMAN_COLS + 4
        val destino = destinoValido(celda, DireccionPacman.DERECHA, PACMAN_COLS, CELDAS_LIBRES_PACMAN)
        assertEquals(celda + 1, destino)
    }

    @Test
    fun `distanciaManhattan mide filas mas columnas`() {
        assertEquals(0, distanciaManhattan(10, 10, 9))
        // (1,1) a (1,4): misma fila, 3 columnas de distancia.
        assertEquals(3, distanciaManhattan(1 * 9 + 1, 1 * 9 + 4, 9))
        // (1,1) a (3,1): misma columna, 2 filas de distancia.
        assertEquals(2, distanciaManhattan(1 * 9 + 1, 3 * 9 + 1, 9))
    }

    // Ambos en el pasillo abierto de la fila 3 (sin muros entre medio), para
    // que un solo paso codicioso sí pueda acercar o alejar de verdad — con
    // el fantasma "encajonado" entre muros arriba/abajo (como en
    // CASA_FANTASMAS) el único movimiento posible a veces es lateral, y un
    // paso codicioso de 1 no siempre reduce la distancia real (necesitaría
    // rodear el muro), así que esa combinación no sirve para esta prueba.
    private val JUGADOR_PASILLO = 3 * PACMAN_COLS + 7
    private val FANTASMA_PASILLO = 3 * PACMAN_COLS + 1

    @Test
    fun `el fantasma persiguiendo se acerca al jugador`() {
        val distanciaAntes = distanciaManhattan(FANTASMA_PASILLO, JUGADOR_PASILLO, PACMAN_COLS)
        val nuevaPos = moverFantasmaPersiguiendo(FANTASMA_PASILLO, JUGADOR_PASILLO, PACMAN_COLS, PACMAN_FILAS, CELDAS_LIBRES_PACMAN)
        assertTrue(distanciaManhattan(nuevaPos, JUGADOR_PASILLO, PACMAN_COLS) < distanciaAntes)
    }

    @Test
    fun `el fantasma asustado se aleja del jugador`() {
        val distanciaAntes = distanciaManhattan(FANTASMA_PASILLO, JUGADOR_PASILLO, PACMAN_COLS)
        val nuevaPos = moverFantasmaAsustado(FANTASMA_PASILLO, JUGADOR_PASILLO, PACMAN_COLS, PACMAN_FILAS, CELDAS_LIBRES_PACMAN)
        assertTrue(distanciaManhattan(nuevaPos, JUGADOR_PASILLO, PACMAN_COLS) >= distanciaAntes)
    }

    @Test
    fun `la celda emboscada queda por delante del jugador en su direccion`() {
        val jugador = 4 * PACMAN_COLS + 4
        val objetivo = celdaObjetivoEmboscada(jugador, DireccionPacman.DERECHA, PACMAN_COLS, PACMAN_FILAS, pasos = 3)
        assertEquals(4 * PACMAN_COLS + 7, objetivo)
    }

    @Test
    fun `la celda emboscada se recorta a los limites del tablero`() {
        val jugador = 0 * PACMAN_COLS + 1
        val objetivo = celdaObjetivoEmboscada(jugador, DireccionPacman.ARRIBA, PACMAN_COLS, PACMAN_FILAS, pasos = 5)
        assertEquals(0, objetivo / PACMAN_COLS)
    }
}
