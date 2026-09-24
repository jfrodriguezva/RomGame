package com.miambiente.app.model

enum class CategoriaMaterial(val titulo: String, val emoji: String) {
    LOGICA("Lógica y rompecabezas", "🧩"),
    PALABRAS("Palabras e idiomas", "📖"),
    NUMEROS("Números", "🔢"),
    MUNDO("Mundo y naturaleza", "🌍"),
    CREATIVIDAD("Creatividad y música", "🎨"),
    COORDINACION("Coordinación y vida práctica", "🎯"),
    MESA("Juegos de mesa", "🎲"),
}

data class MaterialConsolidado(
    val id: String,
    val titulo: String,
    val emoji: String,
    val descripcion: String,
    val categoria: CategoriaMaterial,
    val modos: List<String>,
)

private fun material(
    id: String,
    titulo: String,
    emoji: String,
    descripcion: String,
    categoria: CategoriaMaterial,
    vararg modos: String,
) = MaterialConsolidado(id, titulo, emoji, descripcion, categoria, modos.toList())

/**
 * Catálogo visible: reúne actividades semejantes sin borrar sus pantallas,
 * reglas, niveles ni progreso. Los Arcade quedan deliberadamente fuera hasta
 * que vuelvan a superar una validación funcional individual.
 */
val MATERIALES_CONSOLIDADOS = listOf(
    material("percepcion", "Percepción sensorial", "🔴", "Color, textura, peso, temperatura y sentidos", CategoriaMaterial.LOGICA, "colores", "textura", "temperatura", "peso", "sabor", "olfato", "sentidos", "sombras", "diferencias"),
    material("formas-encajes", "Formas y encajes", "🔺", "Geometría, cuerpos, lados y construcción", CategoriaMaterial.LOGICA, "formas", "solidos", "lados", "orificios", "cilindros", "binomio", "rompecabezas"),
    material("orden-secuencias", "Orden y secuencias", "↔️", "Series por tamaño, tiempo y procesos", CategoriaMaterial.LOGICA, "torre-rosa", "escalera-marron", "dias-semana", "estaciones", "ciclo-vida", "ciclo-agua"),
    material("memoria-observacion", "Memoria y observación", "🧠", "Parejas, cambios y búsqueda visual", CategoriaMaterial.LOGICA, "memorama", "que-falta", "objetos"),
    material("laberintos", "Laberintos y recorridos", "🌀", "Rutas, exploración y obstáculos", CategoriaMaterial.LOGICA, "laberinto", "arana"),

    material("palabras-sonidos", "Palabras y sonidos", "🔤", "Letras, fonética, rimas y sílabas", CategoriaMaterial.PALABRAS, "vocales", "abecedario", "sonidos-iniciales", "rimas", "silabas", "mayusculas", "letras-lija", "trazos"),
    material("construye-palabras", "Construye palabras", "🔡", "Forma, clasifica y comprende palabras", CategoriaMaterial.PALABRAS, "alfabeto-movil", "singular-plural", "el-la", "ingles"),

    material("numeros-cantidades", "Números y cantidades", "🔢", "Conteo, sistema decimal, tiempo y fracciones", CategoriaMaterial.NUMEROS, "contar", "husos", "barras-numericas", "numeros", "tabla-cien", "banco-dorado", "pares-impares", "reloj", "mitades"),

    material("clasifica-mundo", "Clasifica el mundo", "🧺", "Seres vivos, alimentos, agua, día y transportes", CategoriaMaterial.MUNDO, "seres-vivos", "habitat", "dieta-animal", "fruta-verdura", "estados-agua", "dia-noche", "transporte"),
    material("naturaleza-planeta", "Naturaleza y planeta", "🌎", "Animales, plantas, geografía, espacio y clima", CategoriaMaterial.MUNDO, "animales", "partes-planta", "continentes", "tierra-agua", "sistema-solar", "tiempo", "banderas"),
    material("personas-comunidad", "Personas y comunidad", "🧑‍🤝‍🧑", "Cuerpo, emociones, profesiones y convivencia", CategoriaMaterial.MUNDO, "cuerpo", "cara", "emociones", "oficios", "mesa-silencio"),

    material("taller-creativo", "Taller creativo y musical", "🎨", "Dibujo, color, collage y siete instrumentos", CategoriaMaterial.CREATIVIDAD, "pizarra", "colorear", "collage", "xilofono", "patron"),

    material("vida-practica", "Vida práctica", "🫗", "Rutinas, higiene, mesa y coordinación fina", CategoriaMaterial.COORDINACION, "vida-practica", "pinza", "tamanos", "rutina", "mesa", "lavado-manos"),
    material("coordinacion", "Coordinación y reflejos", "🎯", "Precisión, ritmo, vibración y reacción", CategoriaMaterial.COORDINACION, "burbujas", "canasta", "globo", "vibra-adivina", "reflejo-color"),

    material("alineacion", "Alineación y duelo", "⭕", "Gato, cuatro en línea y piedra, papel o tijera", CategoriaMaterial.MESA, "gato", "conecta4", "rps"),
    material("recorridos-tablero", "Dados y recorridos", "🎲", "Oca, serpientes, escaleras y retos", CategoriaMaterial.MESA, "oca", "serpientes", "dado"),
    material("memoria-mesa", "Memoria por turnos", "🧠", "Encuentra parejas contra la computadora", CategoriaMaterial.MESA, "memoria-turnos"),
    material("cartas-solitario", "Solitarios", "🃏", "Solitario clásico y araña", CategoriaMaterial.MESA, "solitario", "arana-cartas"),
    material("loterias", "Lotería y bingo", "🎯", "Cartones, cantos y reconocimiento", CategoriaMaterial.MESA, "loteria", "bingo"),
    material("estrategia-tablero", "Estrategia de tablero", "♞", "Damas, damas chinas y ajedrez", CategoriaMaterial.MESA, "damas", "damas-chinas", "ajedrez"),
    material("deduccion-fichas", "Deducción y fichas", "🕵️", "Dominó y Adivina quién", CategoriaMaterial.MESA, "domino", "adivinaquien"),
)

fun buscarMaterialConsolidado(id: String) = MATERIALES_CONSOLIDADOS.find { it.id == id }
fun juegosDe(material: MaterialConsolidado) = material.modos.mapNotNull(::buscarJuego)

val IDS_ARCADE_OCULTOS = setOf(
    "tetris", "snake", "arkanoid", "topo", "mosaico", "vaqueros",
    "comepuntos", "nieve", "escuadron-estelar", "gran-premio", "carreras",
)
