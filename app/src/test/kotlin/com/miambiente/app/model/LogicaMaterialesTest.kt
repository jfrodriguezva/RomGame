package com.miambiente.app.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Reglas de seriación (MaterialOrdenar) y transferencia (MaterialTransferir). */
class LogicaMaterialesTest {
    private fun serie(n: Int) = SerieOrdenar.nueva(n) { it.reversed() }

    @Test
    fun `una serie nueva tiene todas las piezas en el canasto`() {
        val s = serie(4)
        assertEquals(listOf(4, 3, 2, 1), s.enCanasto)
        assertTrue(s.colocadas.isEmpty())
        assertFalse(s.completa)
    }

    @Test
    fun `tocar un lugar sin pieza tomada no hace nada`() {
        val s = serie(3)
        assertEquals(s to Resultado.NADA, s.tocarLugar(1))
    }

    @Test
    fun `tocar la misma pieza dos veces la suelta`() {
        val s = serie(3).tocarPieza(2)
        assertEquals(2, s.seleccionada)
        assertNull(s.tocarPieza(2).seleccionada)
    }

    @Test
    fun `una pieza que ya se coloco no se puede volver a tomar`() {
        val (s, _) = serie(3).tocarPieza(1).tocarLugar(1)
        assertNull(s.tocarPieza(1).seleccionada)
    }

    @Test
    fun `pieza en su lugar es acierto y sale del canasto`() {
        val (s, r) = serie(3).tocarPieza(2).tocarLugar(2)
        assertEquals(Resultado.ACIERTO, r)
        assertEquals(setOf(2), s.colocadas)
        assertFalse(2 in s.enCanasto)
        assertNull(s.seleccionada)
    }

    @Test
    fun `pieza en otro lugar es error y vuelve al canasto sin castigo`() {
        val antes = serie(3)
        val (s, r) = antes.tocarPieza(3).tocarLugar(1)
        assertEquals(Resultado.ERROR, r)
        assertEquals(antes.enCanasto, s.enCanasto)
        assertTrue(s.colocadas.isEmpty())
        assertNull(s.seleccionada)
    }

    @Test
    fun `la ultima pieza completa la serie`() {
        var s = serie(3)
        val resultados = (1..3).map { p -> s.tocarPieza(p).tocarLugar(p).also { s = it.first }.second }
        assertEquals(listOf(Resultado.ACIERTO, Resultado.ACIERTO, Resultado.COMPLETO), resultados)
        assertTrue(s.completa)
        assertTrue(s.enCanasto.isEmpty())
    }

    @Test
    fun `transferir hasta el objetivo exacto completa`() {
        var t = Transferencia.nueva(objetivo = 3, origen = 9)
        val resultados = (1..3).map { t.transferirUno().also { t = it.first }.second }
        assertEquals(listOf(Resultado.ACIERTO, Resultado.ACIERTO, Resultado.COMPLETO), resultados)
        assertEquals(6, t.enOrigen)
    }

    @Test
    fun `transferir de mas no ocurre despues de completar`() {
        var t = Transferencia.nueva(1, 5)
        t = t.transferirUno().first
        assertEquals(t to Resultado.NADA, t.transferirUno())
    }

    @Test
    fun `el origen nunca es menor que el objetivo`() {
        val t = Transferencia.nueva(objetivo = 7, origen = 3)
        assertEquals(7, t.total)
    }

    @Test
    fun `pasarse del objetivo reinicia la bandeja`() {
        // El estado de "pasarse" solo puede darse si el objetivo cambia a la
        // mitad; se construye a mano para cubrir la rama del error.
        val t = Transferencia(objetivo = 2, total = 9, enDestino = 3)
        val (nueva, r) = t.transferirUno()
        assertEquals(Resultado.ERROR, r)
        assertEquals(0, nueva.enDestino)
        assertEquals(9, nueva.enOrigen)
    }

    @Test
    fun `las series crecen con el nivel`() {
        // El error que motivó separar esta lógica: las series quedaban
        // fijas en el tamaño del nivel 1.
        mapOf(
            "torre rosa" to CURVA_TORRE_ROSA,
            "cilindros" to CURVA_CILINDROS,
            "escalera" to CURVA_ESCALERA_MARRON,
            "contar" to CURVA_CONTAR,
            "husos" to CURVA_HUSOS,
            "pinza" to CURVA_PINZA,
        ).forEach { (nombre, curva) ->
            val tamanos = (1..100).map { phasedInt(it, curva) }
            assertTrue("$nombre no crece", tamanos.last() > tamanos.first())
            assertTrue("$nombre retrocede", tamanos.zipWithNext().all { (a, b) -> b >= a })
        }
    }

    private val pool = (1..10).map { "obj$it" to if (it % 2 == 0) "par" else "impar" }

    @Test
    fun `clasificar en la canasta correcta es acierto y en otra es error`() {
        val ronda = RondaClasificar(listOf("a" to "x", "b" to "y"))
        val (tras, r) = ronda.clasificar("a", "y")
        assertEquals(Resultado.ERROR, r)
        assertEquals(ronda, tras)
        val (bien, r2) = ronda.clasificar("a", "x")
        assertEquals(Resultado.ACIERTO, r2)
        assertEquals(listOf("b" to "y"), bien.pendientes)
        assertEquals(1, bien.acertados)
    }

    @Test
    fun `el ultimo objeto completa la ronda`() {
        val (ronda, r) = RondaClasificar(listOf("a" to "x")).clasificar("a", "x")
        assertEquals(Resultado.COMPLETO, r)
        assertTrue(ronda.completa)
    }

    @Test
    fun `un objeto ya clasificado no cuenta dos veces`() {
        val (ronda, _) = RondaClasificar(listOf("a" to "x", "b" to "x")).clasificar("a", "x")
        assertEquals(ronda to Resultado.NADA, ronda.clasificar("a", "x"))
    }

    @Test
    fun `la ronda toma tantos objetos como pide el nivel sin pasarse del material`() {
        val (elegidos, ronda) = RondaClasificar.nueva(pool, 4, semilla = 1) { it }
        assertEquals(4, elegidos.size)
        assertEquals(4, ronda.pendientes.size)
        assertEquals(10, RondaClasificar.nueva(pool, 50, 1) { it }.first.size)
    }

    @Test
    fun `repetir un nivel da la misma ronda`() {
        assertEquals(
            RondaClasificar.nueva(pool, 5, 7) { it }.first,
            RondaClasificar.nueva(pool, 5, 7) { it }.first,
        )
    }

    @Test
    fun `clasificar crece de 3 a 8 objetos con el nivel`() {
        assertEquals(3, phasedInt(1, CURVA_CLASIFICAR))
        assertEquals(8, phasedInt(100, CURVA_CLASIFICAR))
        val tamanos = (1..100).map { phasedInt(it, CURVA_CLASIFICAR) }
        assertTrue(tamanos.zipWithNext().all { (a, b) -> b >= a })
    }

    @Test
    fun `el rompecabezas crece de 2x2 a 4x4 con piezas distintas`() {
        assertEquals(2, escenaRompecabezas(1).lado)
        assertEquals(4, escenaRompecabezas(100).lado)
        (1..100).map(::escenaRompecabezas).forEach { e ->
            assertEquals(e.lado * e.lado, e.piezas.size)
            assertEquals("piezas repetidas en ${e.lado}x${e.lado}", e.piezas.size, e.piezas.toSet().size)
        }
    }

    @Test
    fun `la torre rosa va de 3 a 10 cubos`() {
        assertEquals(3, phasedInt(1, CURVA_TORRE_ROSA))
        assertEquals(10, phasedInt(100, CURVA_TORRE_ROSA))
    }
}
