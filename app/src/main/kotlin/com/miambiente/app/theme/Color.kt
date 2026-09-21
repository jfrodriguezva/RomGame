package com.miambiente.app.theme

import androidx.compose.ui.graphics.Color

// Paleta base: papel, madera, lino — igual que globals.css / textura-papel
// en la versión web. Ver docs/MANUAL-TECNICO.md sección 7.
val Papel = Color(0xFFFDFAF5)
val Tinta = Color(0xFF3F342C)
val TextoSuave = Color(0xFF6B6258)
val TextoTenue = Color(0xFFA39A8C)
val Blanco = Color(0xFFFFFFFF)

/** Colores de acento por área — puerto directo de AREAS en lib/montessori.ts. */
enum class Area(val label: String, val emoji: String, val proposito: String) {
    PRACTICA("Vida práctica", "🫗", "Movimiento preciso, coordinación, cuidado del entorno."),
    SENSORIAL("Sensorial", "🔴", "Refinar los sentidos: lo que ya conoce el cuerpo, nombrado."),
    LENGUAJE("Lenguaje", "✍️", "Del sonido a la letra, y de la letra a la palabra."),
    MATEMATICAS("Matemáticas", "🔢", "Cantidad concreta antes que número abstracto."),
    CULTURA("Cultura y naturaleza", "🌍", "El mundo, los seres vivos y su clasificación."),
    CREATIVA("Expresión libre", "🎨", "Crear sin consigna, sin puntaje y sin prisa."),
    COMPANIA("Juegos en compañía", "🤝", "Turnos, gracia y cortesía."),
    MOVIMIENTO("Movimiento", "🤸", "Control del cuerpo y coordinación ojo-mano"),
}

data class AreaColores(val fondo: Color, val texto: Color, val acento: Color, val acentoOscuro: Color)

fun coloresDe(area: Area): AreaColores = when (area) {
    Area.PRACTICA -> AreaColores(Color(0xFFE9F0E4), Color(0xFF3D5C34), Color(0xFF8BBF6A), Color(0xFF4C7A3A))
    Area.SENSORIAL -> AreaColores(Color(0xFFFBE9E7), Color(0xFF8A3B32), Color(0xFFE08A7A), Color(0xFFB5493A))
    Area.LENGUAJE -> AreaColores(Color(0xFFEAF1F8), Color(0xFF2F5C82), Color(0xFF6FA6CC), Color(0xFF3E7AA3))
    Area.MATEMATICAS -> AreaColores(Color(0xFFF3ECF8), Color(0xFF5B3B7A), Color(0xFFA97FC7), Color(0xFF7A4FA3))
    Area.CULTURA -> AreaColores(Color(0xFFEAF3EF), Color(0xFF2E5C4C), Color(0xFF6EBBA0), Color(0xFF3B8A6D))
    Area.CREATIVA -> AreaColores(Color(0xFFF6F0E4), Color(0xFF7A6234), Color(0xFFD8C39A), Color(0xFFA9895A))
    Area.COMPANIA -> AreaColores(Color(0xFFFDF1E4), Color(0xFF8A5A2B), Color(0xFFE0B586), Color(0xFFB5732F))
    Area.MOVIMIENTO -> AreaColores(Color(0xFFE8F2F5), Color(0xFF2F6B7A), Color(0xFF6FBACC), Color(0xFF3E8FA3))
}
