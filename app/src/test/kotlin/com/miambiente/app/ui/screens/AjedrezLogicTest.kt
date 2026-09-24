package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AjedrezLogicTest {

    @Test
    fun `el tablero inicial tiene 16 piezas por bando en sus casillas reales`() {
        val tablero = tableroInicialAjedrez()
        assertEquals(16, tablero.count { it.esJugador })
        assertEquals(16, tablero.count { !it.esJugador })
        assertTrue(tablero.any { it.esJugador && it.tipo == TipoPieza.REY && it.fila == 7 && it.col == 4 })
        assertTrue(tablero.any { !it.esJugador && it.tipo == TipoPieza.REY && it.fila == 0 && it.col == 4 })
    }

    @Test
    fun `un peon puede avanzar uno o dos desde su fila inicial`() {
        val peon = PiezaAjedrez(6, 4, TipoPieza.PEON, esJugador = true)
        val movidas = movidasPseudoLegales(peon, listOf(peon))
        assertTrue(movidas.any { it.filaDestino == 5 && it.colDestino == 4 })
        assertTrue(movidas.any { it.filaDestino == 4 && it.colDestino == 4 })
    }

    @Test
    fun `un peon no puede avanzar dos si ya se movio de su fila inicial`() {
        val peon = PiezaAjedrez(5, 4, TipoPieza.PEON, esJugador = true)
        val movidas = movidasPseudoLegales(peon, listOf(peon))
        assertFalse(movidas.any { it.filaDestino == 3 })
    }

    @Test
    fun `un peon solo captura en diagonal si hay una pieza rival ahi`() {
        val peon = PiezaAjedrez(4, 4, TipoPieza.PEON, esJugador = true)
        val rival = PiezaAjedrez(3, 5, TipoPieza.PEON, esJugador = false)
        val movidas = movidasPseudoLegales(peon, listOf(peon, rival))
        assertTrue(movidas.any { it.filaDestino == 3 && it.colDestino == 5 && it.captura == rival })
        assertFalse(movidas.any { it.filaDestino == 3 && it.colDestino == 3 })
    }

    @Test
    fun `una torre se desliza pero se detiene al chocar con una pieza`() {
        val torre = PiezaAjedrez(4, 4, TipoPieza.TORRE, esJugador = true)
        val bloqueo = PiezaAjedrez(4, 6, TipoPieza.PEON, esJugador = false)
        val movidas = movidasPseudoLegales(torre, listOf(torre, bloqueo))
        assertTrue(movidas.any { it.filaDestino == 4 && it.colDestino == 6 })
        assertFalse(movidas.any { it.filaDestino == 4 && it.colDestino == 7 })
    }

    @Test
    fun `un caballo salta en forma de L sin que le importen las piezas de en medio`() {
        val caballo = PiezaAjedrez(4, 4, TipoPieza.CABALLO, esJugador = true)
        val movidas = movidasPseudoLegales(caballo, listOf(caballo))
        assertTrue(movidas.any { it.filaDestino == 2 && it.colDestino == 3 })
        assertEquals(8, movidas.size)
    }

    @Test
    fun `una movida que deja el propio rey en jaque no es legal`() {
        // Rey del jugador en (7,4), torre rival en la misma columna sin nada
        // en medio: cualquier movida que no bloquee o capture es ilegal.
        val rey = PiezaAjedrez(7, 4, TipoPieza.REY, esJugador = true)
        val otraPieza = PiezaAjedrez(7, 0, TipoPieza.PEON, esJugador = true)
        val torreRival = PiezaAjedrez(0, 4, TipoPieza.TORRE, esJugador = false)
        val tablero = listOf(rey, otraPieza, torreRival)
        val movidas = movidasLegalesAjedrez(tablero, esJugador = true)
        assertFalse(movidas.any { it.pieza == otraPieza })
    }

    @Test
    fun `jaque mate real - el rey acorralado sin ninguna movida legal`() {
        // Mate clasico de torre y rey en la esquina: la torre corta la
        // columna 0 (cubre 0,0 y 1,0) y el rey blanco en (1,2) cubre las
        // dos casillas restantes de escape, (0,1) y (1,1) — verificado a
        // mano casilla por casilla, no solo "se ve bien".
        val reyNegro = PiezaAjedrez(0, 0, TipoPieza.REY, esJugador = false)
        val reyBlanco = PiezaAjedrez(1, 2, TipoPieza.REY, esJugador = true)
        val torreBlanca = PiezaAjedrez(7, 0, TipoPieza.TORRE, esJugador = true)
        val tablero = listOf(reyNegro, reyBlanco, torreBlanca)
        assertTrue(reyEnJaque(tablero, esJugador = false))
        assertTrue(movidasLegalesAjedrez(tablero, esJugador = false).isEmpty())
    }

    @Test
    fun `la IA elige siempre una movida legal de verdad`() {
        val tablero = tableroInicialAjedrez()
        val movida = mejorMovidaAjedrez(tablero)
        assertTrue(movida in movidasLegalesAjedrez(tablero, esJugador = false))
    }

    @Test
    fun `un peon que llega a la ultima fila se corona reina`() {
        val peon = PiezaAjedrez(1, 4, TipoPieza.PEON, esJugador = true)
        val tablero = listOf(peon)
        val movida = movidasPseudoLegales(peon, tablero).first { it.filaDestino == 0 }
        val nuevo = aplicarMovidaAjedrez(tablero, movida)
        assertEquals(TipoPieza.REINA, nuevo.first().tipo)
    }
}
