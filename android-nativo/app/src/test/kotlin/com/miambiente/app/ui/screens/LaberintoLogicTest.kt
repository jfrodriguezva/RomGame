package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LaberintoLogicTest {
    @Test fun `todos los laberintos generados tienen salida alcanzable`() {
        repeat(100) { semilla ->
            val mapa = generarLaberinto(5, 5, semilla)
            val pendientes = ArrayDeque<Int>().apply { add(mapa.entrada) }
            val vistos = mutableSetOf(mapa.entrada)
            while (pendientes.isNotEmpty()) {
                val actual = pendientes.removeFirst()
                val candidatos = listOf(actual - mapa.columnas, actual + mapa.columnas, actual - 1, actual + 1)
                candidatos.filter { it in mapa.caminos && it !in vistos }.forEach { vistos += it; pendientes += it }
            }
            assertTrue("semilla $semilla sin solución", mapa.salida in vistos)
        }
    }

    @Test fun `dimensiones y accesos son consistentes`() {
        val mapa = generarLaberinto(5, 6, 42)
        assertEquals(11, mapa.filas)
        assertEquals(13, mapa.columnas)
        assertTrue(mapa.entrada in mapa.caminos)
        assertTrue(mapa.salida in mapa.caminos)
    }
}
