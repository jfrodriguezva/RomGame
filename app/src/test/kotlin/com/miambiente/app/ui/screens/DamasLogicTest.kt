package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DamasLogicTest {

    @Test
    fun `tablero inicial tiene 12 fichas por bando`() {
        val tablero = tableroInicialDamas()
        assertEquals(12, tablero.count { it.esJugador })
        assertEquals(12, tablero.count { !it.esJugador })
    }

    @Test
    fun `todas las fichas iniciales caen en casillas jugables`() {
        tableroInicialDamas().forEach { f -> assertTrue(casillaJugable(f.fila, f.col)) }
    }

    @Test
    fun `una ficha simple del jugador solo avanza hacia adelante, sin retroceder`() {
        val ficha = FichaDamas(fila = 5, col = 2, esJugador = true)
        val tablero = listOf(ficha)
        val movidas = movidasDeFicha(ficha, tablero)
        assertTrue(movidas.all { it.filaDestino == 4 })
    }

    @Test
    fun `la captura es obligatoria cuando esta disponible`() {
        // Ficha del jugador en (4,3); ficha rival en (3,2) con casilla libre en (2,1) detras.
        val jugador = FichaDamas(4, 3, esJugador = true)
        val rival = FichaDamas(3, 2, esJugador = false)
        val otraJugador = FichaDamas(6, 6, esJugador = true) // tendria movidas simples si no hubiera captura obligatoria
        val tablero = listOf(jugador, rival, otraJugador)
        val movidas = movidasLegales(tablero, esJugador = true)
        assertTrue(movidas.all { it.capturada != null })
        assertTrue(movidas.any { it.ficha == jugador && it.filaDestino == 2 && it.colDestino == 1 })
    }

    @Test
    fun `aplicar una captura quita la ficha capturada del tablero`() {
        val jugador = FichaDamas(4, 3, esJugador = true)
        val rival = FichaDamas(3, 2, esJugador = false)
        val tablero = listOf(jugador, rival)
        val movida = movidasDeFicha(jugador, tablero).first()
        val nuevo = aplicarMovidaDamas(tablero, movida)
        assertTrue(nuevo.none { !it.esJugador })
        assertEquals(1, nuevo.size)
    }

    @Test
    fun `una ficha del jugador se corona dama al llegar a la fila 0`() {
        val ficha = FichaDamas(1, 2, esJugador = true)
        val tablero = listOf(ficha)
        val movida = movidasDeFicha(ficha, tablero).first { it.filaDestino == 0 }
        val nuevo = aplicarMovidaDamas(tablero, movida)
        assertTrue(nuevo.first().esDama)
    }

    @Test
    fun `la IA elige una movida legal de verdad, no al azar fuera de las reglas`() {
        val tablero = tableroInicialDamas()
        val movida = mejorMovidaDamas(tablero)
        assertTrue(movida in movidasLegales(tablero, esJugador = false))
    }
}
