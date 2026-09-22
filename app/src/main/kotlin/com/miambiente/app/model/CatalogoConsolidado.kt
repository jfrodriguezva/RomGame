package com.miambiente.app.model

enum class Categoria(val titulo: String, val emoji: String) {
    LOGICA("Lógica y rompecabezas", "🧩"),
    PALABRAS("Palabras e idiomas", "📖"),
    NUMEROS("Números", "🔢"),
    MUNDO("Mundo y naturaleza", "🌍"),
    CREATIVIDAD("Creatividad y música", "🎨"),
    COORDINACION("Coordinación y reflejos", "🎯"),
    MESA("Juegos de mesa", "🎲"),
    ARCADE("Arcade", "👾"),
}

data class FamiliaJuego(
    val id: String,
    val titulo: String,
    val emoji: String,
    val descripcion: String,
    val categoria: Categoria,
    val modos: List<String>,
    val motor: Motor = Motor.COMPOSE,
)

enum class Motor { COMPOSE, LIBGDX, GODOT }

private fun familia(id:String,titulo:String,emoji:String,descripcion:String,categoria:Categoria,vararg modos:String,motor:Motor=Motor.COMPOSE)=
    FamiliaJuego(id,titulo,emoji,descripcion,categoria,modos.toList(),motor)

val FAMILIAS = listOf(
    familia("percepcion","Percepción sensorial","🔴","Color, textura, peso, temperatura y sentidos",Categoria.LOGICA,"colores","textura","temperatura","peso","sabor","olfato","sentidos","sombras","diferencias",motor=Motor.LIBGDX),
    familia("formas-encajes","Formas y encajes","🔺","Geometría, cuerpos, lados y construcción",Categoria.LOGICA,"formas","solidos","lados","orificios","cilindros","binomio","rompecabezas",motor=Motor.LIBGDX),
    familia("orden-secuencias","Orden y secuencias","↔️","Series por tamaño, tiempo y procesos",Categoria.LOGICA,"torre-rosa","escalera-marron","dias-semana","estaciones","ciclo-vida","ciclo-agua",motor=Motor.LIBGDX),
    familia("memoria-observacion","Memoria y observación","🧠","Parejas, cambios y búsqueda visual",Categoria.LOGICA,"memorama","que-falta","memoria-turnos","objetos",motor=Motor.LIBGDX),
    familia("palabras-sonidos","Palabras y sonidos","🔤","Letras, fonética, rimas y sílabas",Categoria.PALABRAS,"vocales","abecedario","sonidos-iniciales","rimas","silabas","mayusculas","letras-lija","trazos",motor=Motor.LIBGDX),
    familia("construye-palabras","Construye palabras","🔡","Forma, clasifica y comprende palabras",Categoria.PALABRAS,"alfabeto-movil","singular-plural","el-la","ingles",motor=Motor.LIBGDX),
    familia("numeros-cantidades","Números y cantidades","🔢","Conteo, sistema decimal, tiempo y fracciones",Categoria.NUMEROS,"contar","husos","barras-numericas","numeros","tabla-cien","banco-dorado","pares-impares","reloj","mitades"),
    familia("clasifica-mundo","Clasifica el mundo","🧺","Seres vivos, alimentos, agua, día y transportes",Categoria.MUNDO,"seres-vivos","habitat","dieta-animal","fruta-verdura","estados-agua","dia-noche","transporte"),
    familia("naturaleza-planeta","Naturaleza y planeta","🌎","Animales, plantas, geografía, espacio y clima",Categoria.MUNDO,"animales","partes-planta","continentes","tierra-agua","sistema-solar","tiempo","banderas"),
    familia("personas-comunidad","Personas y comunidad","🧑‍🤝🧑","Cuerpo, emociones, profesiones y convivencia",Categoria.MUNDO,"cuerpo","cara","emociones","oficios","mesa-silencio"),
    familia("vida-practica","Vida práctica","🧫","Rutinas, higiene, mesa y coordinación fina",Categoria.COORDINACION,"vida-practica","pinza","tamanos","rutina","mesa","lavado-manos"),
    familia("taller-creativo","Taller creativo","🎨","Dibujo, color, collage y exploración musical",Categoria.CREATIVIDAD,"pizarra","colorear","collage","xilofono","instrumentos","patron"),
    familia("coordinacion-reflejos","Coordinación y reflejos","🎯","Objetivos móviles, ritmo y reacción",Categoria.COORDINACION,"burbujas","canasta","globo","topo","vibra-adivina","reflejo-color"),
    familia("laberintos","Laberintos y recorridos","🌀","Rutas, exploración y obstáculos",Categoria.LOGICA,"laberinto","arana"),

    familia("alineacion","Alineación y duelo","⭕","Gato, cuatro en línea y piedra-papel-tijera",Categoria.MESA,"gato","conecta4","rps"),
    familia("recorridos-tablero","Dados y recorridos","🎲","Oca, serpientes y retos",Categoria.MESA,"oca","serpientes","dado"),
    familia("cartas-solitario","Solitarios","🃏","Clásico y araña con dificultades",Categoria.MESA,"solitario","arana-cartas"),
    familia("loterias","Lotería y bingo","🎯","Cartones, cantos y reconocimiento",Categoria.MESA,"loteria","bingo"),
    familia("estrategia-tablero","Estrategia de tablero","♞","Damas, damas chinas y ajedrez",Categoria.MESA,"damas","damas-chinas","ajedrez"),
    familia("deduccion-fichas","Deducción y fichas","🕵️","Dominó y Adivina quién",Categoria.MESA,"domino","adivinaquien"),

    familia("tetris","Bloques","🧱","Completa líneas",Categoria.ARCADE,"tetris",motor=Motor.GODOT),
    familia("snake","La víbora","🐍","Come y no choques",Categoria.ARCADE,"snake",motor=Motor.GODOT),
    familia("arkanoid","Rompe ladrillos","🧱","Rebota y despeja la pantalla",Categoria.ARCADE,"arkanoid",motor=Motor.GODOT),
    familia("mosaico","Mosaico sorpresa","🖼️","Captura territorio",Categoria.ARCADE,"mosaico",motor=Motor.GODOT),
    familia("vaqueros","Vaqueros del ocaso","🤠","Acción lateral del oeste",Categoria.ARCADE,"vaqueros",motor=Motor.GODOT),
    familia("comepuntos","Comepuntos","🟡","Laberinto, puntos y perseguidores",Categoria.ARCADE,"comepuntos",motor=Motor.GODOT),
    familia("nieve","Rescate de nieve","⛄","Plataformas y bolas de nieve",Categoria.ARCADE,"nieve",motor=Motor.GODOT),
    familia("escuadron-estelar","Escuadrón estelar","🚀","Shooter espacial sobre rieles",Categoria.ARCADE,"escuadron-estelar",motor=Motor.GODOT),
    familia("gran-premio","Gran premio","🏁","Carreras de circuito",Categoria.ARCADE,"gran-premio","carreras",motor=Motor.GODOT),
)

val MODOS_EN_FAMILIAS: Set<String> = FAMILIAS.flatMap { it.modos }.toSet()
fun buscarFamilia(id:String)=FAMILIAS.find{it.id==id}
fun juegosDe(familia:FamiliaJuego)=familia.modos.mapNotNull(::buscarJuego)
