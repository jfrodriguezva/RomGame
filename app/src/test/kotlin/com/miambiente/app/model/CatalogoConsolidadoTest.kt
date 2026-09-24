package com.miambiente.app.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogoConsolidadoTest {
    @Test
    fun `cada juego no arcade aparece exactamente en un material`() {
        val visibles = MATERIALES_CONSOLIDADOS.flatMap { it.modos }
        val esperados = CATALOGO.map { it.id }.filterNot { it in IDS_ARCADE_OCULTOS }
        assertEquals("Hay modos repetidos entre materiales", visibles.toSet().size, visibles.size)
        assertEquals(esperados.toSet(), visibles.toSet())
    }

    @Test
    fun `ningun arcade aparece en el catalogo consolidado`() {
        val visibles = MATERIALES_CONSOLIDADOS.flatMap { it.modos }.toSet()
        assertTrue(visibles.intersect(IDS_ARCADE_OCULTOS).isEmpty())
        assertTrue(CategoriaMaterial.entries.none { it.name == "ARCADE" })
    }
}
