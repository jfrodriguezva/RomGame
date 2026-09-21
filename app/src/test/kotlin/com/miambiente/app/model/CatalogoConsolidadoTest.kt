package com.miambiente.app.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogoConsolidadoTest {
    @Test fun cadaJuegoPerteneceAUnaSolaFamilia(){
        val modos=FAMILIAS.flatMap{it.modos}
        assertEquals("Hay modos repetidos entre familias",modos.size,modos.toSet().size)
        assertEquals("La matriz debe cubrir todo el catálogo",CATALOGO.map{it.id}.toSet(),modos.toSet())
    }

    @Test fun catalogoVisibleQuedaReducido(){
        assertTrue(FAMILIAS.size in 25..35)
        assertTrue(FAMILIAS.all{it.modos.isNotEmpty()})
    }
}
