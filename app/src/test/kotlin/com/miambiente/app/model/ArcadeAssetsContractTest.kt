package com.miambiente.app.model

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ArcadeAssetsContractTest {
    @Test fun cadaModoArcadeTieneFuenteYRecursoCompilado() {
        val assets = File("src/main/assets")
        val loader = File(assets, "scripts/main.gd").readText()
        val arcades = FAMILIAS.filter { it.motor == Motor.GODOT }.flatMap { it.modos }

        arcades.forEach { id ->
            assertTrue("Falta el ID $id en el cargador Godot", loader.contains("\"$id\""))
        }

        listOf(
            "blocks", "snake", "breakout", "territory", "cowboy_run",
            "maze_chase", "snow_rescue", "star_squadron", "racing",
        ).forEach { script ->
            assertTrue(File(assets, "scripts/$script.gd").isFile)
            assertTrue(File(assets, "scripts/$script.gdc").isFile)
            assertTrue(File(assets, "scripts/$script.gd.remap").isFile)
        }
    }
}
