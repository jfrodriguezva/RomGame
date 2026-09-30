package com.miambiente.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

/** Reglas puras del progreso guardado (sin DataStore ni emulador). */
class ProgressLogicTest {
    @Test
    fun `completados vacios o nulos dan un conjunto vacio`() {
        assertEquals(emptySet<Int>(), leerCompletados(null))
        assertEquals(emptySet<Int>(), leerCompletados(""))
    }

    @Test
    fun `completados se leen sin duplicados y toleran basura`() {
        assertEquals(setOf(1, 2, 5), leerCompletados("1,2,,5,2, x"))
    }

    @Test
    fun `completar un nivel desbloquea el siguiente`() {
        assertEquals(2, nivelTrasCompletar(null, 1))
        assertEquals(8, nivelTrasCompletar(7, 7))
    }

    @Test
    fun `repetir un nivel viejo no retrocede el desbloqueo`() {
        assertEquals(30, nivelTrasCompletar(30, 4))
    }

    @Test
    fun `el desbloqueo nunca pasa de 100`() {
        assertEquals(100, nivelTrasCompletar(100, 100))
    }
}
