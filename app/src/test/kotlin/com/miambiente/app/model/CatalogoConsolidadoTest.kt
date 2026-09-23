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
        assertEquals(29, FAMILIAS.size)
        assertTrue(FAMILIAS.all{it.modos.isNotEmpty()})
    }

    @Test fun cadaCategoriaUsaElMotorAcordado(){
        assertTrue(FAMILIAS.filter { it.categoria == Categoria.ARCADE }.all { it.motor == Motor.GODOT })
        assertTrue(FAMILIAS.filter { it.categoria != Categoria.ARCADE }.all { it.motor == Motor.LIBGDX })
    }

    @Test fun elModeloNoContieneEdad(){
        val campos = GameDef::class.java.declaredFields.map { it.name.lowercase() }
        assertTrue(campos.none { "edad" in it || "age" in it })
    }
}
