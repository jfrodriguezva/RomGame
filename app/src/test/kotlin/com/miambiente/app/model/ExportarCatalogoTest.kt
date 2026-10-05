package com.miambiente.app.model

import com.miambiente.app.theme.Area
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Exporta el catálogo real a `build/pagina/catalogo.json` para la página de
 * pruebas (https://jfrodriguezva.github.io/RomGame): así la lista de cosas
 * por revisar sale de los mismos objetos que usa la app y nunca se atrasa.
 * El JSON se arma a mano porque `org.json` no funciona en pruebas JVM.
 */
class ExportarCatalogoTest {
    private fun texto(s: String) = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

    private fun modo(j: GameDef) = buildString {
        append("{\"id\":").append(texto(j.id))
        append(",\"titulo\":").append(texto(j.title))
        append(",\"emoji\":").append(texto(j.emoji))
        append(",\"descripcion\":").append(texto(j.description))
        append(",\"tipo\":").append(texto(if (j.libre) "libre" else if (j.area == Area.COMPANIA) "partida" else "niveles"))
        append("}")
    }

    @Test
    fun `exporta el catalogo para la pagina de pruebas`() {
        val materiales = MATERIALES_CONSOLIDADOS.joinToString(",\n") { m ->
            buildString {
                append("{\"id\":").append(texto(m.id))
                append(",\"titulo\":").append(texto(m.titulo))
                append(",\"emoji\":").append(texto(m.emoji))
                append(",\"descripcion\":").append(texto(m.descripcion))
                append(",\"categoria\":").append(texto("${m.categoria.emoji} ${m.categoria.titulo}"))
                append(",\"modos\":[").append(juegosDe(m).joinToString(",") { modo(it) }).append("]}")
            }
        }
        val destino = File("build/pagina/catalogo.json")
        destino.parentFile.mkdirs()
        destino.writeText("{\"materiales\":[\n$materiales\n]}\n")
        assertTrue(destino.length() > 0)
    }
}
