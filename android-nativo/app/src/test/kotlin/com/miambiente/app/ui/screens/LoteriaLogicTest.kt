package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoteriaLogicTest {

    @Test
    fun `el mazo de 54 cartas no tiene emojis repetidos`() {
        assertEquals(54, MAZO_LOTERIA.size)
        assertEquals(MAZO_LOTERIA.size, MAZO_LOTERIA.distinct().size)
    }

    @Test
    fun `los 8 tableros tienen exactamente 16 cartas cada uno`() {
        assertEquals(8, TABLEROS_LOTERIA.size)
        assertTrue(TABLEROS_LOTERIA.all { it.size == 16 })
        assertTrue(TABLEROS_LOTERIA.all { it.distinct().size == 16 })
    }

    @Test
    fun `cada tablero es subconjunto del mazo de 54 cartas`() {
        val mazo = MAZO_LOTERIA.toSet()
        assertTrue(TABLEROS_LOTERIA.all { tablero -> tablero.all { it in mazo } })
    }

    @Test
    fun `mazoBarajado no repite ni pierde cartas`() {
        val barajado = mazoBarajado()
        assertEquals(MAZO_LOTERIA.size, barajado.size)
        assertEquals(MAZO_LOTERIA.toSet(), barajado.toSet())
    }

    @Test
    fun `tableroAleatorio siempre devuelve uno de los tableros predefinidos`() {
        val elegido = tableroAleatorio()
        assertTrue(elegido in TABLEROS_LOTERIA)
    }

    @Test
    fun `siguienteCanto consume una carta y no la repite hasta agotar el mazo`() {
        var mazo = listOf("a", "b", "c")
        val cantadas = mutableListOf<String>()
        while (true) {
            val resultado = siguienteCanto(mazo) ?: break
            val (carta, resto) = resultado
            cantadas.add(carta)
            mazo = resto
        }
        assertEquals(listOf("a", "b", "c"), cantadas)
        assertNull(siguienteCanto(emptyList()))
    }

    @Test
    fun `cartonCompleto es true solo cuando todas las cartas del tablero estan marcadas`() {
        val tablero = listOf("🐶", "🐱", "🐰")
        assertFalse(cartonCompleto(tablero, setOf("🐶")))
        assertFalse(cartonCompleto(tablero, setOf("🐶", "🐱")))
        assertTrue(cartonCompleto(tablero, setOf("🐶", "🐱", "🐰")))
        assertTrue(cartonCompleto(tablero, setOf("🐶", "🐱", "🐰", "🦋")))
    }
}
