package com.miambiente.app.ui.screens

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas internas de `hayGanador` (Cuatro en línea) — nacieron de un bug
 * real encontrado jugando en el emulador: la computadora podía mover una
 * vez de más después de que el jugador ya había ganado, porque el efecto
 * de la CPU no conocía el resultado de esta función. La lógica de
 * detección en sí (`hayGanador`) siempre fue correcta; lo que faltaba era
 * que ambos efectos la respetaran. Estas pruebas fijan el contrato de la
 * función para que un cambio futuro no repita el problema por otro lado.
 */
class Conecta4LogicTest {

    private fun tableroVacio() = List<String?>(CONECTA4_COLS * CONECTA4_FILAS) { null }

    private fun colocar(tablero: List<String?>, col: Int, fila: Int, ficha: String): List<String?> =
        tablero.toMutableList().also { it[fila * CONECTA4_COLS + col] = ficha }

    @Test
    fun `tablero vacio no tiene ganador`() {
        assertFalse(hayGanador(tableroVacio(), "🔴"))
    }

    @Test
    fun `cuatro horizontales seguidas ganan`() {
        var t = tableroVacio()
        for (col in 0..3) t = colocar(t, col, 3, "🔴")
        assertTrue(hayGanador(t, "🔴"))
        assertFalse(hayGanador(t, "🔵"))
    }

    @Test
    fun `tres seguidas NO ganan todavia`() {
        var t = tableroVacio()
        for (col in 0..2) t = colocar(t, col, 3, "🔴")
        assertFalse(hayGanador(t, "🔴"))
    }

    @Test
    fun `cuatro verticales seguidas ganan`() {
        var t = tableroVacio()
        for (fila in 0..3) t = colocar(t, 2, fila, "🔵")
        assertTrue(hayGanador(t, "🔵"))
    }

    @Test
    fun `diagonal descendente gana`() {
        var t = tableroVacio()
        // (0,0) (1,1) (2,2) (3,3) — diagonal de arriba-izq a abajo-der.
        for (i in 0..3) t = colocar(t, i, i, "🔴")
        assertTrue(hayGanador(t, "🔴"))
    }

    @Test
    fun `diagonal ascendente gana`() {
        var t = tableroVacio()
        // (0,3) (1,2) (2,1) (3,0) — diagonal de abajo-izq a arriba-der.
        for (i in 0..3) t = colocar(t, i, 3 - i, "🔵")
        assertTrue(hayGanador(t, "🔵"))
    }

    @Test
    fun `fichas mezcladas no cuentan como ganador`() {
        var t = tableroVacio()
        t = colocar(t, 0, 3, "🔴"); t = colocar(t, 1, 3, "🔴")
        t = colocar(t, 2, 3, "🔵"); t = colocar(t, 3, 3, "🔴")
        assertFalse(hayGanador(t, "🔴"))
        assertFalse(hayGanador(t, "🔵"))
    }

    @Test
    fun `la otra diagonal (subiendo) tambien se detecta, no solo una direccion`() {
        // Distinto de la prueba anterior: confirma que ninguna de las 4
        // direcciones revisadas por hayGanador se quedó sin probar.
        var t = tableroVacio()
        t = colocar(t, 1, 3, "🔴")
        t = colocar(t, 2, 2, "🔴")
        t = colocar(t, 3, 1, "🔴")
        t = colocar(t, 4, 0, "🔴")
        assertTrue(hayGanador(t, "🔴"))
    }
}
