package com.miambiente.app.ui

import com.miambiente.app.model.CATALOGO
import com.miambiente.app.model.MATERIALES_CONSOLIDADOS
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Cada juego necesita una ruta de navegación: si falta, abrirlo desde el
 * inicio hace fallar la app. Antes las rutas se escribían a mano en
 * MainActivity y nada lo verificaba.
 */
class PantallasTest {
    @Test
    fun `cada juego del catalogo tiene pantalla`() {
        val sinPantalla = CATALOGO.map { it.id }.filterNot { it in PANTALLAS }
        assertTrue("Juegos sin pantalla: $sinPantalla", sinPantalla.isEmpty())
    }

    @Test
    fun `cada modo de cada material tiene pantalla`() {
        val sinPantalla = MATERIALES_CONSOLIDADOS.flatMap { it.modos }.filterNot { it in PANTALLAS }
        assertTrue("Modos sin pantalla: $sinPantalla", sinPantalla.isEmpty())
    }

    @Test
    fun `no hay pantallas huerfanas fuera del catalogo`() {
        val ids = CATALOGO.map { it.id }.toSet()
        val huerfanas = PANTALLAS.keys.filterNot { it in ids }
        assertTrue("Pantallas sin juego en el catálogo: $huerfanas", huerfanas.isEmpty())
    }

    @Test
    fun `ninguna pantalla choca con las rutas fijas`() {
        val fijas = setOf("inicio", "ajustes", "progreso")
        assertTrue(PANTALLAS.keys.none { it in fijas || it.startsWith("material/") })
    }
}
