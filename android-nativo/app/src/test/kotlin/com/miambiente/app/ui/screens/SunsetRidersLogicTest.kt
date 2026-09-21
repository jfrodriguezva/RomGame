package com.miambiente.app.ui.screens

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SunsetRidersLogicTest {

    @Test
    fun `un tiro alto solo se esquiva agachado`() {
        assertTrue(sobreviveDisparo(EstadoJugador.AGACHADO, TipoDisparo.ALTO))
        assertFalse(sobreviveDisparo(EstadoJugador.DE_PIE, TipoDisparo.ALTO))
        assertFalse(sobreviveDisparo(EstadoJugador.SALTANDO, TipoDisparo.ALTO))
    }

    @Test
    fun `un tiro bajo solo se esquiva saltando`() {
        assertTrue(sobreviveDisparo(EstadoJugador.SALTANDO, TipoDisparo.BAJO))
        assertFalse(sobreviveDisparo(EstadoJugador.DE_PIE, TipoDisparo.BAJO))
        assertFalse(sobreviveDisparo(EstadoJugador.AGACHADO, TipoDisparo.BAJO))
    }

    @Test
    fun `los bandidos se vuelven mas rapidos con la distancia, hasta un tope`() {
        assertTrue(velocidadBanditoParaDistancia(50) > velocidadBanditoParaDistancia(0))
        assertTrue(velocidadBanditoParaDistancia(500) <= 220f)
    }

    @Test
    fun `el intervalo de aparicion baja con la distancia, hasta un piso`() {
        assertTrue(intervaloSpawnParaDistancia(100) < intervaloSpawnParaDistancia(0))
        assertTrue(intervaloSpawnParaDistancia(1000) >= 600L)
    }
}
