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
 * Catálogo nativo — todavía no son los 96 materiales de la versión web
 * (fuente de verdad: data/games.ts), pero ya cubre los 4 patrones
 * compartidos con varios ejemplos de cada uno, más materiales
 * independientes. Ver android-nativo/README.md para el plan de
 * continuar agregando en lotes.
 */
val CATALOGO = listOf(
    // --- MaterialQuiz (nomenclatura) ---
    GameDef("formas", "Gabinete de figuras", "🔺", "Círculo, cuadrado, triángulo", Area.SENSORIAL, 3, "Gabinete de geometría", "Reconocer figuras por su contorno y nombrarlas."),
    GameDef("cuerpo", "Partes del cuerpo", "🧍", "Cabeza, ojo, mano, brazo, pierna y pie", Area.CULTURA, 3, "Nomenclatura del cuerpo humano", "Vocabulario y conciencia del propio cuerpo."),
    GameDef("colores", "Los colores", "🎨", "Tabletas de color", Area.SENSORIAL, 3, "Cajas de color", "Nomenclatura de las tabletas de color."),
    GameDef("instrumentos", "Instrumentos musicales", "🎻", "Tambor, guitarra, piano, trompeta y violín", Area.SENSORIAL, 3, "Nomenclatura", "Nomenclatura de instrumentos musicales."),
    GameDef("oficios", "Oficios y profesiones", "👩‍🚒", "Bombero, doctora, cocinero, maestra, policía", Area.CULTURA, 4, "Nomenclatura", "Vocabulario de oficios y su utilidad social."),
    GameDef("transporte", "Medios de transporte", "🚂", "Tierra, aire o agua", Area.CULTURA, 3, "Clasificación de transportes", "Relacionar cada vehículo con el medio por el que se mueve."),

    // --- MaterialOrdenar (seriación, con arrastre) ---
    GameDef("torre-rosa", "Torre rosa", "🟪", "Ordena los cubos del más grande al más chico", Area.SENSORIAL, 3, "Torre rosa", "Discriminación visual del tamaño; seriación."),
    GameDef("dias-semana", "Los días de la semana", "📅", "Lunes a domingo, en orden", Area.CULTURA, 4, "Secuencia temporal", "Secuencia y vocabulario temporal."),
    GameDef("estaciones", "Las estaciones del año", "🍂", "Primavera a invierno, en orden", Area.CULTURA, 4, "Secuencia temporal", "Secuencia y vocabulario temporal."),
    GameDef("ciclo-vida", "El ciclo de la mariposa", "🐛", "Huevo, oruga, crisálida, mariposa", Area.CULTURA, 4, "Seriación por tiempo", "Seriación por secuencia temporal, no por tamaño."),

    // --- MaterialClasificar (el más reutilizado, con arrastre a canastas) ---
    GameDef("seres-vivos", "¿Vivo o no vivo?", "🌱", "Clasifica en la canasta correcta", Area.CULTURA, 3, "Clasificación científica", "Primera clasificación científica: vivo o no vivo."),
    GameDef("habitat", "¿Dónde vive?", "🏝️", "Selva, desierto, océano, polo", Area.CULTURA, 4, "Clasificación por hábitat", "Relacionar cada animal con su hábitat."),
    GameDef("dieta-animal", "¿Qué come?", "🦁", "Herbívoro, carnívoro u omnívoro", Area.CULTURA, 4, "Clasificación por dieta", "Clasificación biológica por tipo de alimentación."),
    GameDef("fruta-verdura", "Fruta o verdura", "🍎", "Clasificación botánica", Area.CULTURA, 3, "Clasificación botánica", "Primera clasificación botánica."),
    GameDef("el-la", "El o la", "📖", "Reconocer el género gramatical", Area.LENGUAJE, 4, "Género gramatical", "Reconocer el género gramatical de un sustantivo."),
    GameDef("pares-impares", "Pares e impares", "🔢", "¿Se reparte en parejas exactas?", Area.MATEMATICAS, 4, "Paridad", "¿La cantidad se puede repartir en parejas exactas?"),
    GameDef("tamanos", "Grande, mediano o chico", "📏", "Clasificación en tres canastas", Area.PRACTICA, 3, "Clasificación por tamaño", "Clasificación en tres canastas, no solo dos."),
    GameDef("estados-agua", "Estados del agua", "🧊", "Sólido, líquido o gas", Area.CULTURA, 4, "Estados de la materia", "Sólido, líquido o gas."),
    GameDef("dia-noche", "Día y noche", "🌗", "¿Es de día o de noche?", Area.CULTURA, 3, "Clasificación", "¿Es de día o de noche?"),
    GameDef("singular-plural", "Singular y plural", "🔤", "La palabra junto a su cantidad", Area.LENGUAJE, 4, "Gramática", "La palabra siempre junto a su cantidad concreta."),

    // --- MaterialTransferir (con arrastre) ---
    GameDef("pinza", "Pinza de transferencia", "🥢", "Mueve objetos de uno en uno", Area.PRACTICA, 3, "Ejercicio de vida práctica", "Mover objetos de uno en uno; tomar uno de más es el error."),
    GameDef("husos", "Los husos", "🧵", "Corresponde cantidad con número", Area.MATEMATICAS, 4, "Numeración", "Corresponder cantidad con número del 0 al 9."),

    // --- Independientes (sin patrón compartido) ---
    GameDef("gato", "Gato", "⭕", "Tres en línea", Area.COMPANIA, 4, "Juego de mesa", "Anticipar, esperar el turno y aceptar el resultado."),
    GameDef("rps", "Piedra, papel o tijera", "✂️", "El clásico juego de manos", Area.COMPANIA, 4, "Juego de mesa", "Reconocer un patrón simple: qué le gana a qué."),
)

fun buscarJuego(id: String): GameDef? = CATALOGO.find { it.id == id }
fun juegosPorArea(area: Area): List<GameDef> = CATALOGO.filter { it.area == area }
