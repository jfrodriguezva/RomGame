package com.miambiente.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SnowBrosLogicTest {

    @Test
    fun `hacen falta 3 golpes para atrapar a un enemigo`() {
        var enemigo = EnemigoNieve(id = 0, x = 10f, velX = 5f)
        enemigo = registrarGolpe(enemigo)
        assertFalse(enemigo.atrapado)
        enemigo = registrarGolpe(enemigo)
        assertFalse(enemigo.atrapado)
        enemigo = registrarGolpe(enemigo)
        assertTrue(enemigo.atrapado)
        assertEquals(0f, enemigo.velX) // se detiene al quedar atrapado
    }

    @Test
    fun `un golpe de mas no rompe el contador`() {
        var enemigo = EnemigoNieve(id = 0, x = 10f, velX = 5f, golpes = 3, atrapado = true)
        enemigo = registrarGolpe(enemigo)
        assertEquals(GOLPES_PARA_ATRAPAR, enemigo.golpes)
        assertTrue(enemigo.atrapado)
    }

    @Test
    fun `patear solo funciona sobre un enemigo ya atrapado y quieto`() {
        val libre = EnemigoNieve(id = 0, x = 10f, velX = 5f)
        val pateadoSinAtrapar = patearEnemigo(libre, direccion = 1)
        assertFalse(pateadoSinAtrapar.rodando)

        val atrapado = EnemigoNieve(id = 1, x = 10f, velX = 0f, golpes = 3, atrapado = true)
        val rodando = patearEnemigo(atrapado, direccion = 1)
        assertTrue(rodando.rodando)
        assertEquals(VEL_RODADA, rodando.velX)

        val rodandoOtraVez = patearEnemigo(rodando, direccion = -1)
        assertEquals(VEL_RODADA, rodandoOtraVez.velX) // ya estaba rodando: no se le cambia la direccion
    }

    @Test
    fun `patear hacia la izquierda da velocidad negativa`() {
        val atrapado = EnemigoNieve(id = 0, x = 10f, velX = 0f, golpes = 3, atrapado = true)
        val rodando = patearEnemigo(atrapado, direccion = -1)
        assertEquals(-VEL_RODADA, rodando.velX)
    }
}
