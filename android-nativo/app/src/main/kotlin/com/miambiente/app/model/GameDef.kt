package com.miambiente.app.model

import com.miambiente.app.theme.Area

/** Puerto directo de GameDef en data/games.ts. */
data class GameDef(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val area: Area,
    val edadMinima: Int,
    val material: String,
    val objetivo: String,
    val libre: Boolean = false,
)

/**
 * Catálogo del primer lote nativo — no son los 96 materiales todavía,
 * son los suficientes para probar cada patrón compartido de punta a
 * punta (Quiz, Ordenar con arrastre, Clasificar con arrastre, y un
 * material independiente). Ver RECOVERY.md para el plan de continuar.
 */
val CATALOGO = listOf(
    GameDef(
        id = "formas",
        title = "Gabinete de figuras",
        emoji = "🔺",
        description = "Círculo, cuadrado, triángulo",
        area = Area.SENSORIAL,
        edadMinima = 3,
        material = "Gabinete de geometría",
        objetivo = "Reconocer figuras por su contorno y nombrarlas.",
    ),
    GameDef(
        id = "torre-rosa",
        title = "Torre rosa",
        emoji = "🟪",
        description = "Ordena los cubos del más grande al más chico",
        area = Area.SENSORIAL,
        edadMinima = 3,
        material = "Torre rosa",
        objetivo = "Discriminación visual del tamaño; seriación.",
    ),
    GameDef(
        id = "seres-vivos",
        title = "¿Vivo o no vivo?",
        emoji = "🌱",
        description = "Clasifica en la canasta correcta",
        area = Area.CULTURA,
        edadMinima = 3,
        material = "Clasificación científica",
        objetivo = "Primera clasificación científica: vivo o no vivo.",
    ),
    GameDef(
        id = "gato",
        title = "Gato",
        emoji = "⭕",
        description = "Tres en línea",
        area = Area.COMPANIA,
        edadMinima = 4,
        material = "Juego de mesa",
        objetivo = "Anticipar, esperar el turno y aceptar el resultado.",
    ),
)

fun buscarJuego(id: String): GameDef? = CATALOGO.find { it.id == id }
fun juegosPorArea(area: Area): List<GameDef> = CATALOGO.filter { it.area == area }
