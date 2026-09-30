package com.miambiente.app.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los juegos con pantalla propia deben cambiar con el nivel: antes todos
 * jugaban igual en los 100 niveles que anunciaban.
 */
class DificultadJuegosTest {
    private val niveles = 1..100

    private fun <T : Comparable<T>> noBaja(nombre: String, valores: List<T>) =
        assertTrue("$nombre baja en algún nivel", valores.zipWithNext().all { (a, b) -> b >= a })

    private fun <T : Comparable<T>> noSube(nombre: String, valores: List<T>) =
        assertTrue("$nombre sube en algún nivel", valores.zipWithNext().all { (a, b) -> b <= a })

    private fun <T : Comparable<T>> cambia(nombre: String, valores: List<T>) =
        assertTrue("$nombre no cambia con el nivel", valores.first() != valores.last())

    @Test
    fun `los juegos de coordinacion se vuelven mas exigentes`() {
        val canasta = niveles.map(::dificultadCanasta)
        noBaja("meta de canasta", canasta.map { it.meta }); noSube("paso de canasta", canasta.map { it.pasoMs })
        val burbujas = niveles.map(::dificultadBurbujas)
        noBaja("meta de burbujas", burbujas.map { it.meta }); noSube("aparición de burbujas", burbujas.map { it.aparicionMs })
        assertTrue(burbujas.all { it.velocidadMin < it.velocidadMax })
        val globo = niveles.map(::dificultadGlobo)
        noBaja("meta del globo", globo.map { it.meta }); noBaja("gravedad del globo", globo.map { it.gravedad })
        val reflejo = niveles.map(::dificultadReflejo)
        noBaja("rondas de reflejo", reflejo.map { it.rondas }); noSube("umbral de reflejo", reflejo.map { it.umbralMs })
        assertTrue(reflejo.all { it.esperaMinMs < it.esperaMaxMs })
        val vibra = niveles.map(::dificultadVibra)
        noBaja("pulsos", vibra.map { it.maxPulsos }); noSube("pausa entre pulsos", vibra.map { it.pausaMs })
        listOf(canasta.map { it.meta }, burbujas.map { it.meta }, globo.map { it.meta }, reflejo.map { it.rondas }, vibra.map { it.maxPulsos })
            .forEachIndexed { i, v -> cambia("juego $i", v) }
    }

    @Test
    fun `los juegos de observacion y memoria crecen y caben en pantalla`() {
        val parejas = niveles.map(::parejasMemorama)
        noBaja("parejas", parejas); assertEquals(3, parejas.first()); assertEquals(24, parejas.last())
        niveles.map(::dificultadDiferencias).forEach { assertTrue(it.lado in 2..5 && it.rondas >= 1) }
        niveles.map(::dificultadObjetos).forEach { assertTrue("más objetivos que casillas", it.objetivos < it.lado * it.lado) }
        val laberinto = niveles.map(::ladoLaberinto)
        noBaja("laberinto", laberinto); assertEquals(3, laberinto.first()); assertEquals(8, laberinto.last())
        val patron = niveles.map(::dificultadPatron)
        noBaja("melodía", patron.map { it.longitud }); cambia("melodía", patron.map { it.longitud })
        niveles.map(::dificultadBinomio).forEach { assertTrue(it.lado in 2..3 && it.colores in 2..4) }
    }

    @Test
    fun `numeros y palabras crecen con el nivel`() {
        val banco = niveles.map(::maximoBancoDorado)
        noBaja("banco dorado", banco); assertTrue(banco.last() <= 999)
        val tabla = niveles.map(::hastaTablaCien)
        noBaja("tabla del cien", tabla); assertEquals(10, tabla.first()); assertEquals(100, tabla.last())
        assertTrue(tabla.all { it % 10 == 0 })
        val alfabeto = niveles.map(::dificultadAlfabeto)
        noBaja("letras", alfabeto.map { it.longitud }); cambia("letras", alfabeto.map { it.longitud })
    }

    @Test
    fun `cada nivel del alfabeto tiene palabra y banco con sus letras`() {
        niveles.forEach { nivel ->
            val (palabra, banco) = rondaAlfabeto(nivel)
            val d = dificultadAlfabeto(nivel)
            assertEquals(d.longitud, palabra.second.length)
            assertEquals(d.longitud + d.distractores, banco.size)
            assertTrue(palabra.second.all { c -> banco.count { it == c } >= palabra.second.count { it == c } })
        }
    }

    @Test
    fun `el trazo exige pasar por la guia en orden`() {
        val guia = guiaTrazo(FormaTrazo.RECTA)
        assertEquals(guia.size, avanceTrazo(guia, guia, 0.05f))
        // Tocar solo el final no avanza: hay que empezar por el principio.
        assertEquals(0, avanceTrazo(guia, listOf(guia.last()), 0.05f))
        // Lejos de la guía tampoco.
        assertEquals(0, avanceTrazo(guia, guia.map { Punto(it.x, it.y + 0.3f) }, 0.05f))
    }

    @Test
    fun `las formas del trazo cambian y quedan dentro del lienzo`() {
        val formas = niveles.map { dificultadTrazo(it).forma }.toSet()
        assertEquals(FormaTrazo.entries.toSet(), formas)
        noSube("tolerancia", niveles.map { dificultadTrazo(it).tolerancia })
        FormaTrazo.entries.forEach { f ->
            assertTrue(guiaTrazo(f).all { it.x in 0f..1f && it.y in 0f..1f })
        }
    }

    @Test
    fun `los juegos clasicos rescatados tambien crecen con el nivel`() {
        val topo = niveles.map(::dificultadTopo)
        noBaja("meta del topo", topo.map { it.meta }); noSube("topo arriba", topo.map { it.arribaBaseMs }); noSube("pausa del topo", topo.map { it.pausaBaseMs })
        cambia("meta del topo", topo.map { it.meta })
        val snake = niveles.map(::dificultadSnake)
        noBaja("comida de la víbora", snake.map { it.meta }); noSube("paso de la víbora", snake.map { it.pasoMs })
        cambia("comida de la víbora", snake.map { it.meta })
        val tetris = niveles.map(::dificultadTetris)
        noBaja("líneas", tetris.map { it.lineas }); noSube("caída", tetris.map { it.caidaMs })
        cambia("líneas", tetris.map { it.lineas })
    }

    @Test
    fun `la cara suma partes y aciertos`() {
        val cara = niveles.map(::dificultadCara)
        noBaja("partes", cara.map { it.partes }); noBaja("aciertos", cara.map { it.aciertos })
        assertTrue(cara.all { it.partes in 3..5 })
    }
}
