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
    GameDef("contar", "Contar y tocar", "🔢", "Cuenta los objetos", Area.MATEMATICAS, 3, "Fichas y contadores", "Correspondencia uno a uno entre objeto y número."),

    // --- MaterialQuiz: lote 2 ---
    GameDef("animales", "Sonidos de animales", "🐮", "Adivina quién habla", Area.CULTURA, 3, "Zoología", "Reconocer animales y su sonido."),
    GameDef("solidos", "Cuerpos geométricos", "🔷", "Esfera, cubo, cono, cilindro, pirámide", Area.SENSORIAL, 4, "Sólidos geométricos", "Nomenclatura de formas tridimensionales."),
    GameDef("sentidos", "Los cinco sentidos", "🖐️", "Vista, oído, olfato, gusto y tacto", Area.CULTURA, 3, "Nomenclatura de los sentidos", "Vocabulario de los cinco sentidos y su función."),
    GameDef("partes-planta", "Partes de la planta", "🌱", "Raíz, tallo, hoja, flor y fruto", Area.CULTURA, 3, "Nomenclatura de botánica", "Vocabulario de las partes de una planta."),
    GameDef("banderas", "Banderas del mundo", "🚩", "¿De qué país es esta bandera?", Area.CULTURA, 4, "Banderas", "Reconocer banderas y ampliar la noción de otros países."),
    GameDef("sonidos-iniciales", "Veo veo", "👂", "¿Con qué sonido empieza?", Area.LENGUAJE, 3, "Juego del yo veo", "Conciencia fonológica: oír el primer sonido de la palabra."),
    GameDef("vocales", "Las vocales", "🅰️", "Reconoce A E I O U", Area.LENGUAJE, 3, "Letras de lija (vocales)", "Las cinco vocales: sonido, forma y trazo."),
    GameDef("abecedario", "El abecedario", "🔤", "Cada letra con su palabra", Area.LENGUAJE, 4, "Objetos y tarjetas", "Asociar letra, sonido y una palabra que empieza con ella."),
    GameDef("ingles", "Primeras palabras en inglés", "🫱", "Pocas palabras, bien aprendidas", Area.LENGUAJE, 4, "Tarjetas de vocabulario", "Primer contacto con un segundo idioma."),
    GameDef("emociones", "¿Cómo te sientes?", "😊", "Nombra lo que sientes", Area.PRACTICA, 3, "Gracia y cortesía", "Reconocer y nombrar emociones propias y ajenas."),
    GameDef("tiempo", "El tiempo", "🌦️", "¿Qué tiempo hace?", Area.CULTURA, 3, "Nomenclatura del clima", "Vocabulario de las condiciones del clima."),
    GameDef("reloj", "¿Qué hora es?", "🕐", "Las doce horas en punto", Area.MATEMATICAS, 5, "El reloj", "Primer contacto con la hora en punto."),
    GameDef("sombras", "Empareja sombras", "🌗", "¿De quién es esa sombra?", Area.SENSORIAL, 3, "Emparejamiento de siluetas", "Reconocer un objeto solo por su contorno."),
    GameDef("letras-lija", "Letras de lija", "✍️", "Escucha, traza y siente la letra", Area.LENGUAJE, 3, "Letras de lija", "Une el sonido de la letra con el movimiento de escribirla."),

    // --- MaterialOrdenar: lote 2 ---
    GameDef("escalera-marron", "La escalera marrón", "🟫", "Del más ancho al más delgado", Area.SENSORIAL, 3, "Escalera marrón", "Discriminación de grosor y construcción de una serie."),
    GameDef("bloques", "Ordenar bloques", "🧱", "Del más chico al más grande", Area.SENSORIAL, 3, "Serie de tamaños", "Seriación: colocar en orden por una sola cualidad."),
    GameDef("cilindros", "Cilindros con botón", "🎯", "Cada uno en su hueco", Area.SENSORIAL, 3, "Bloques de cilindros", "Ajuste exacto por tamaño."),
    GameDef("sistema-solar", "El sistema solar", "🪐", "Ordena los planetas desde el Sol", Area.CULTURA, 4, "Los planetas", "Secuencia y vocabulario del sistema solar."),
    GameDef("ciclo-agua", "El ciclo del agua", "💧", "Sol, nube, lluvia, río", Area.CULTURA, 4, "Ciencias naturales: el ciclo del agua", "Secuencia de un proceso natural."),
    GameDef("rutina", "La rutina de la mañana", "⏰", "Despertar, vestirse, desayunar, ir a la escuela", Area.PRACTICA, 3, "Secuencia de rutina diaria", "Orden y secuencia de una rutina de vida diaria."),
    GameDef("mesa", "Poner la mesa", "🍽️", "Mantel, plato, cubiertos, vaso", Area.PRACTICA, 3, "Secuencia de poner la mesa", "Orden y secuencia de un trabajo de vida práctica clásico."),
    GameDef("lavado-manos", "Lavarse las manos", "🧼", "Mojar, jabón, tallar, enjuagar, secar", Area.PRACTICA, 3, "Secuencia de higiene", "Orden y secuencia de un hábito de cuidado personal."),
    GameDef("barras-numericas", "Barras numéricas", "📏", "La cantidad se ve y se toca", Area.MATEMATICAS, 3, "Barras rojas y azules", "Cantidad concreta antes que número: del 1 al 10."),
    GameDef("numeros", "Números en orden", "➡️", "Conecta del 1 en adelante", Area.MATEMATICAS, 4, "Cadena de cuentas", "Orden y sucesión de los números."),

    // --- MaterialClasificar: lote 2 ---
    GameDef("textura", "Áspero o liso", "🤚", "Toca con los ojos: ¿pincha o resbala?", Area.SENSORIAL, 3, "Tablillas ásperas y lisas", "Refinar el tacto: discriminar superficies ásperas y lisas."),
    GameDef("temperatura", "Caliente o frío", "🌡️", "¿Está caliente o frío?", Area.SENSORIAL, 3, "Sentido térmico", "Refinar la percepción de temperatura."),
    GameDef("peso", "Pesado o ligero", "⚖️", "¿Pesa mucho o poco?", Area.SENSORIAL, 3, "Sentido bárico", "Refinar la percepción del peso."),
    GameDef("sabor", "Dulce o salado", "🍬", "¿Dulce o salado?", Area.SENSORIAL, 3, "Sentido gustativo", "Refinar la percepción del sabor."),
    GameDef("olfato", "Huele bien o mal", "👃", "¿Huele bien o mal?", Area.SENSORIAL, 3, "Sentido olfativo", "Refinar la percepción del olfato."),
    GameDef("mayusculas", "Mayúsculas y minúsculas", "🔠", "Clasifica según cómo se ve la letra", Area.LENGUAJE, 4, "Mayúsculas y minúsculas", "Reconocer visualmente las dos formas de una misma letra."),
    GameDef("lados", "¿Cuántos lados tiene?", "📐", "Clasifica figuras por sus lados", Area.MATEMATICAS, 4, "Geometría: conteo de lados", "Relacionar la forma geométrica con su cantidad de lados."),
    GameDef("mitades", "Mitades y enteros", "🍕", "¿Está entera o a la mitad?", Area.MATEMATICAS, 4, "Primer contacto con la fracción", "Distinguir una figura entera de su mitad."),
    GameDef("rimas", "Palabras que riman", "🎵", "¿Con cuál rima?", Area.LENGUAJE, 4, "Conciencia fonológica: rima", "Reconocer el sonido final de las palabras."),
    GameDef("silabas", "Cuenta las sílabas", "👏", "Escucha y clasifica por golpes de voz", Area.LENGUAJE, 4, "Conciencia fonológica", "Separar una palabra en sus partes antes de relacionarla con letras."),
    GameDef("continentes", "Los continentes", "🌍", "El mundo y sus animales", Area.CULTURA, 4, "Mapa de continentes", "Ubicar los continentes y lo que vive en cada uno."),
    GameDef("tierra-agua", "Formas de tierra y agua", "🏝️", "Isla, lago, montaña...", Area.CULTURA, 4, "Formas de tierra y agua", "Vocabulario geográfico y la relación entre tierra y agua."),
    GameDef("orificios", "Encaja la figura", "🕳️", "Cada figura en su agujero exacto", Area.SENSORIAL, 3, "Encajes de formas geométricas", "Discriminación visual precisa."),

    // --- Independientes (sin patrón compartido) ---
    GameDef("gato", "Gato", "⭕", "Tres en línea", Area.COMPANIA, 4, "Juego de mesa", "Anticipar, esperar el turno y aceptar el resultado."),
    GameDef("rps", "Piedra, papel o tijera", "✂️", "El clásico juego de manos", Area.COMPANIA, 4, "Juego de mesa", "Reconocer un patrón simple: qué le gana a qué."),
)

fun buscarJuego(id: String): GameDef? = CATALOGO.find { it.id == id }
fun juegosPorArea(area: Area): List<GameDef> = CATALOGO.filter { it.area == area }
