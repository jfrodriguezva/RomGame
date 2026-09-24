package com.miambiente.app.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GodotArcadeActivityTest {
    @Test fun `todos los arcade fuerzan el render compatible`() {
        val games = listOf("tetris", "snake", "arkanoid", "mosaico", "vaqueros", "comepuntos", "nieve", "escuadron-estelar", "gran-premio", "carreras")
        games.forEach { game ->
            val args = arcadeCommandLine(emptyList(), game)
            assertTrue(args.containsAll(listOf("--rendering-method", "gl_compatibility", "--rendering-driver", "opengl3")))
            assertEquals("--game-id=$game", args.last())
        }
    }
}
