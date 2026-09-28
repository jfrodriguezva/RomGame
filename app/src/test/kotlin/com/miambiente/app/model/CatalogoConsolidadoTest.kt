package com.miambiente.app.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogoConsolidadoTest {
    @Test
    fun `cada juego del catalogo aparece exactamente en un material`() {
        val visibles = MATERIALES_CONSOLIDADOS.flatMap { it.modos }
        val esperados = CATALOGO.map { it.id }
        assertEquals("Hay modos repetidos entre materiales", visibles.toSet().size, visibles.size)
        assertEquals(esperados.toSet(), visibles.toSet())
    }

    @Test
    fun `no existe categoria arcade`() {
        assertTrue(CategoriaMaterial.entries.none { it.name == "ARCADE" })
    }
}
