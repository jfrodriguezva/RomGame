package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DamasChinasLogicTest {

    @Test
    fun `tablero inicial tiene 6 canicas por bando en su propia casa`() {
        val tablero = tableroInicialChinas()
        assertEquals(6, tablero.count { it.esJugador })
        assertEquals(6, tablero.count { !it.esJugador })
        assertTrue(tablero.filter { it.esJugador }.all { (it.fila to it.col) in CASA_JUGADOR })
        assertTrue(tablero.filter { !it.esJugador }.all { (it.fila to it.col) in CASA_CPU })
    }

    @Test
    fun `una canica sola puede moverse un paso a cualquier casilla vecina vacia`() {
        val ficha = FichaChina(4, 4, esJugador = true)
        val destinos = destinosChinas(ficha, listOf(ficha))
        // 8 vecinas alrededor de (4,4), todas vacías.
        assertEquals(8, destinos.size)
    }

    @Test
    fun `una canica puede saltar por encima de otra sin capturarla`() {
        val saltante = FichaChina(4, 4, esJugador = true)
        val vecina = FichaChina(4, 5, esJugador = false)
        val tablero = listOf(saltante, vecina)
        val destinos = destinosChinas(saltante, tablero)
        assertTrue((4 to 6) in destinos)
        // La ficha saltada sigue en el tablero: en damas chinas no hay captura.
        val despues = aplicarMovidaChinas(tablero, MovidaChina(saltante, 4, 6))
        assertTrue(despues.any { it == vecina })
    }

    @Test
    fun `una cadena de saltos alcanza mas alla del primer salto`() {
        // (2,2) -> salta sobre (2,3) -> cae en (2,4) -> salta sobre (2,5) -> cae en (2,6).
        val saltante = FichaChina(2, 2, esJugador = true)
        val tablero = listOf(saltante, FichaChina(2, 3, esJugador = false), FichaChina(2, 5, esJugador = false))
        val destinos = destinosChinas(saltante, tablero)
        assertTrue((2 to 4) in destinos)
        assertTrue((2 to 6) in destinos)
    }

    @Test
    fun `gana el jugador cuando sus 6 canicas ocupan la casa contraria`() {
        // Las canicas de la computadora se dejan a medio camino (no en
        // ninguna de las dos casas) para no activar por accidente también
        // su propia condición de victoria en esta prueba.
        val cpuAMedioCamino = listOf(3 to 3, 3 to 4, 3 to 5, 4 to 3, 4 to 4, 4 to 5)
        val tablero = CASA_CPU.map { (f, c) -> FichaChina(f, c, esJugador = true) } +
            cpuAMedioCamino.map { (f, c) -> FichaChina(f, c, esJugador = false) }
        assertTrue(ganoJugadorChinas(tablero))
        assertFalse(ganoCpuChinas(tablero))
    }

    @Test
    fun `la IA elige un destino real para una de sus canicas`() {
        val tablero = tableroInicialChinas()
        val movida = mejorMovidaChinas(tablero)
        assertTrue(movida != null)
        assertFalse(movida!!.ficha.esJugador)
        assertTrue((movida.filaDestino to movida.colDestino) in destinosChinas(movida.ficha, tablero))
    }
}
