package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas de la lógica pura de la lotería — JVM, sin emulador.
 *
 * Lo que importa verificar aquí es que el cartón siempre se pueda repartir
 * (el mazo tiene que alcanzar para 4x4 sin repetir cartas) y que las
 * dificultades entren en el nivel que les toca, en los 100 niveles.
 */
class LoteriaLogicTest {

    @Test
    fun `el mazo alcanza para el carton mas grande sin repetir`() {
        val maximo = (1..100).maxOf { ladoParaNivel(it) * ladoParaNivel(it) }
        assertEquals(16, maximo)
        assertTrue("el mazo debe tener al menos $maximo cartas", MAZO_LOTERIA.size >= maximo)
    }

    @Test
    fun `no hay cartas repetidas en el mazo`() {
        val nombres = MAZO_LOTERIA.map { it.nombre }
        assertEquals(nombres.size, nombres.distinct().size)
        val emojis = MAZO_LOTERIA.map { it.emoji }
        assertEquals(emojis.size, emojis.distinct().size)
    }

    @Test
    fun `el carton crece de 2x2 a 4x4 y nunca se sale de ese rango`() {
        assertEquals(2, ladoParaNivel(1))
        assertEquals(4, ladoParaNivel(100))
        for (nivel in 1..100) {
            val lado = ladoParaNivel(nivel)
            assertTrue("nivel $nivel dio lado $lado", lado in 2..4)
        }
        // Nunca decrece: un nivel más alto no puede traer un cartón más chico.
        for (nivel in 2..100) {
            assertTrue(
                "el lado bajó del nivel ${nivel - 1} al $nivel",
                ladoParaNivel(nivel) >= ladoParaNivel(nivel - 1),
            )
        }
    }

    @Test
    fun `el carton reparte tantas cartas distintas como casillas`() {
        for (nivel in listOf(1, 25, 50, 75, 100)) {
            val carton = repartirCarton(nivel)
            val lado = ladoParaNivel(nivel)
            assertEquals(lado * lado, carton.size)
            assertEquals(carton.size, carton.map { it.nombre }.distinct().size)
        }
    }

    @Test
    fun `la carta se muestra hasta la etapa 6 y despues solo se oye`() {
        assertTrue(mostrarCartaParaNivel(1))
        assertTrue(mostrarCartaParaNivel(60))
        assertFalse(mostrarCartaParaNivel(61))
        assertFalse(mostrarCartaParaNivel(100))
    }

    @Test
    fun `cantar cartas de fuera empieza en la etapa 4`() {
        assertFalse(cantaCartasFueraParaNivel(1))
        assertFalse(cantaCartasFueraParaNivel(30))
        assertTrue(cantaCartasFueraParaNivel(31))
        assertTrue(cantaCartasFueraParaNivel(100))
    }

    @Test
    fun `siempre quedan cartas fuera del carton para poder cantarlas`() {
        // Si el cartón se comiera el mazo entero, "no la tengo" nunca podría
        // ser la respuesta correcta y el botón sería una trampa.
        for (nivel in 31..100) {
            val casillas = ladoParaNivel(nivel) * ladoParaNivel(nivel)
            assertTrue(
                "en el nivel $nivel no sobran cartas fuera del cartón",
                MAZO_LOTERIA.size - casillas > 0,
            )
        }
    }
}
