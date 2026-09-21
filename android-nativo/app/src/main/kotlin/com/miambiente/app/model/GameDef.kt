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
    GameDef("oficios", "Oficios y profesiones", "🚒", "Bombero, doctora, cocinero, maestra, policía", Area.CULTURA, 4, "Nomenclatura", "Vocabulario de oficios y su utilidad social."),
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
    // "Ordenar bloques" (id "bloques") se quitó: era un duplicado real de
    // Torre rosa, no un material distinto — mismo patrón MaterialOrdenar,
    // misma fórmula de tamaños, sin ninguna cualidad propia más allá del
    // color. Bug real reportado ("el módulo sensorial tiene materiales
    // duplicados"); Torre rosa ya cubre la seriación por tamaño de verdad.
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

    // --- MaterialOrdenar: lote 3 ---
    GameDef("vida-practica", "Vida práctica", "🫗", "Verter, servir y abotonar", Area.PRACTICA, 3, "Ejercicios de vida práctica", "Movimiento preciso, secuencia de pasos y cuidado del entorno."),

    // --- Independientes (sin patrón compartido) ---
    // Juegos de mesa clásicos: se pidió quitar el filtro de edad para que
    // aparezcan siempre abiertos, sin importar la edad elegida (edadMinima
    // baja a 2, la edad más chica que existe en el selector).
    GameDef("gato", "Gato", "⭕", "Tres en línea", Area.COMPANIA, 2, "Juego de mesa", "Anticipar, esperar el turno y aceptar el resultado."),
    GameDef("rps", "Piedra, papel o tijera", "✂️", "El clásico juego de manos", Area.COMPANIA, 2, "Juego de mesa", "Reconocer un patrón simple: qué le gana a qué."),
    GameDef("memorama", "Juego de memoria", "🧠", "Encuentra las parejas", Area.SENSORIAL, 3, "Juego de memoria a distancia", "Memoria visual y concentración sostenida."),
    GameDef("que-falta", "¿Qué falta?", "🔍", "Memoriza la bandeja y di qué desapareció", Area.COMPANIA, 3, "Juego de Kim", "Memoria de trabajo y observación."),
    GameDef("diferencias", "¿Qué es distinto?", "🔍", "Encuentra lo diferente", Area.SENSORIAL, 4, "Pares y contrastes", "Discriminación visual fina y atención al detalle."),
    GameDef("objetos", "Encuentra los objetos", "🔎", "Busca entre muchos", Area.SENSORIAL, 4, "Búsqueda visual", "Atención selectiva y rastreo visual ordenado."),
    GameDef("memoria-turnos", "Memoria por turnos", "🧠", "Encuentra más parejas que la computadora", Area.COMPANIA, 2, "Juego de mesa", "Esperar el turno, memoria y aceptar perder o ganar sin drama."),
    GameDef("oca", "El juego de la oca", "🦢", "Tira el dado, oca, puente o pozo", Area.COMPANIA, 2, "Juego de mesa", "Esperar el turno y aceptar el azar."),
    GameDef("serpientes", "Serpientes y escaleras", "🐍", "Tira el dado y avanza", Area.COMPANIA, 2, "Juego de mesa", "Contar avanzando y tolerar la sorpresa."),
    GameDef("dado", "Dado de retos", "🎲", "Tira el dado y muévete", Area.COMPANIA, 3, "Movimiento dirigido", "Escuchar una consigna y ejecutarla con el cuerpo.", libre = true),
    GameDef("patron", "Las campanas", "🔔", "Repite la melodía", Area.SENSORIAL, 4, "Campanas Montessori", "Memoria auditiva y discriminación de tonos."),
    GameDef("pizarra", "La pizarra grande", "🖍️", "Dibuja lo que quieras", Area.CREATIVA, 2, "Pizarra y trazo libre", "Expresión libre, trazo amplio y experimentación con el color.", libre = true),
    GameDef("collage", "Collage libre", "🖼️", "Coloca estampas donde quieras", Area.CREATIVA, 2, "Collage y composición libre", "Composición espacial libre y motricidad fina de precisión.", libre = true),
    GameDef("colorear", "Colorear", "🎨", "Pinta el dibujo", Area.CREATIVA, 3, "Dibujo dirigido", "Color, límites y paciencia; también relaja."),
    GameDef("xilofono", "Xilófono", "🎼", "Toca y escucha, sin reglas", Area.CREATIVA, 3, "Instrumento de exploración sonora", "Exploración musical libre.", libre = true),
    GameDef("mesa-silencio", "El juego del silencio", "🤫", "Respira y escucha", Area.PRACTICA, 3, "Juego del silencio", "Autorregulación, escucha y control voluntario del cuerpo.", libre = true),
    GameDef("cara", "Toca la cara", "🙂", "Toca la parte que se pide", Area.CULTURA, 2, "Nomenclatura de la cara", "Vocabulario de la cara, tocando directo sobre el dibujo."),
    GameDef("alfabeto-movil", "Alfabeto móvil", "🔡", "Forma la palabra con letras", Area.LENGUAJE, 4, "Alfabeto móvil", "Escribir antes de saber escribir: componer palabras con sonidos."),
    GameDef("trazos", "Trazos previos", "〰️", "Sigue la línea punteada", Area.LENGUAJE, 3, "Resaques metálicos", "Mano firme y control del trazo antes de escribir."),
    GameDef("rompecabezas", "Rompecabezas", "🧩", "Arma la imagen", Area.SENSORIAL, 3, "Encajes y puzzles", "Relación parte-todo y orientación espacial."),
    GameDef("binomio", "El cubo del binomio", "🧊", "Arma el cubo de colores", Area.SENSORIAL, 4, "Cubo del binomio", "Patrón espacial y orden; base sensorial del álgebra."),
    GameDef("tabla-cien", "La tabla del cien", "💯", "Coloca del 1 al 100", Area.MATEMATICAS, 4, "Tabla del cien", "Secuencia numérica y estructura de la decena."),
    GameDef("banco-dorado", "El banco dorado", "🟡", "Unidades, decenas y centenas", Area.MATEMATICAS, 4, "Perlas doradas", "Sistema decimal a la vista: componer números grandes."),
    GameDef("bingo", "Bingo con imágenes", "🎱", "Escucha, busca y marca en tu cartón", Area.COMPANIA, 2, "Juego de mesa", "Vocabulario, atención y correspondencia."),
    GameDef("loteria", "Lotería mexicana", "🃏", "El gritón canta y tú marcas tu cartón", Area.COMPANIA, 2, "Lotería mexicana", "Escuchar con atención, reconocer por el nombre y esperar su turno."),
    GameDef("domino", "Dominó", "🀄", "Encaja tu ficha por número", Area.COMPANIA, 2, "Juego de mesa", "Correspondencia numérica y esperar el turno."),
    GameDef("conecta4", "Cuatro en línea", "🔵", "Alinea cuatro fichas antes que la computadora", Area.COMPANIA, 2, "Juego de mesa", "Planeación simple y anticipar la jugada del otro."),
    GameDef("adivinaquien", "Adivina quién es", "🕵️", "Pregunta y descubre", Area.COMPANIA, 2, "Juego de deducción", "Razonamiento lógico por eliminación."),
    GameDef("damas", "Damas inglesas", "⚫", "Captura y corona tus fichas", Area.COMPANIA, 2, "Juego de mesa", "Planeación a varios pasos y anticipar capturas del rival."),
    GameDef("damas-chinas", "Damas chinas", "🔺", "Lleva tus canicas al otro lado de la estrella", Area.COMPANIA, 2, "Juego de mesa", "Planeación de rutas y saltos encadenados."),
    // "🎴" en vez de un carácter del bloque Unicode "Playing Cards" (como
    // "🂡"): ese bloque casi nunca tiene glifo de color en las fuentes —
    // el mismo bug de iconos que no se ven, encontrado y corregido antes
    // con el dominó.
    GameDef("solitario", "Solitario", "🎴", "Ordena las cartas por palo y color", Area.COMPANIA, 2, "Juego de cartas", "Paciencia, clasificación y estrategia en solitario."),
    GameDef("arana-cartas", "Solitario araña", "🕸️", "Arma secuencias del As al Rey", Area.COMPANIA, 2, "Juego de cartas", "Planeación a varios pasos y memoria de lo ya visto."),
    GameDef("ajedrez", "Ajedrez", "♞", "Da jaque mate al rey contrario", Area.COMPANIA, 2, "Juego de mesa", "Planeación a varios pasos y anticipar la jugada del rival."),
    GameDef("tetris", "Tetris", "🧱", "Acomoda las piezas y completa líneas", Area.COMPANIA, 2, "Juego de mesa", "Rotación mental, planeación espacial y reflejos."),
    GameDef("snake", "La víbora", "🐍", "Come y no choques", Area.COMPANIA, 2, "Juego arcade", "Planeación de ruta, reflejos y control del error."),
    GameDef("arkanoid", "Rompe ladrillos", "🧱", "Rebota la pelota y rompe todos los ladrillos", Area.COMPANIA, 2, "Juego arcade", "Coordinación ojo-mano y anticipar trayectorias."),
    GameDef("topo", "Atrapa al topo", "🐹", "Tócalo antes de que se esconda", Area.COMPANIA, 2, "Juego arcade", "Tiempo de reacción y atención sostenida."),

    // --- Movimiento y coordinación (independientes) ---
    GameDef("laberinto", "Laberinto", "🌀", "Encuentra la salida", Area.MOVIMIENTO, 4, "Control del movimiento", "Planear una ruta y seguirla sin chocar."),
    GameDef("burbujas", "Burbujas", "🫧", "Truena las burbujas", Area.MOVIMIENTO, 2, "Coordinación ojo-mano", "Precisión del dedo sobre un objetivo en movimiento."),
    GameDef("canasta", "Atrapa las estrellas", "🧺", "Mueve la canasta", Area.MOVIMIENTO, 3, "Coordinación ojo-mano", "Anticipar una trayectoria y responder a tiempo."),
    GameDef("globo", "El globo volador", "🎈", "No dejes que caiga", Area.MOVIMIENTO, 3, "Coordinación ojo-mano", "Ritmo y constancia del toque."),
    GameDef("arana", "La araña pintora", "🕷️", "Descubre la imagen", Area.MOVIMIENTO, 4, "Recorrido y estrategia", "Recorrer un espacio completo evitando obstáculos."),
    GameDef("lava", "El piso es lava", "🌋", "No toques el suelo", Area.MOVIMIENTO, 4, "Reflejos", "Reacción rápida y control del salto."),
    GameDef("toystory", "Aventura de juguetes", "🤠", "Salta y explora", Area.MOVIMIENTO, 4, "Recorrido de obstáculos", "Coordinación, ritmo y persistencia ante el reto."),
    GameDef("carreras", "Carreras", "🏎️", "Esquiva y llega a la meta", Area.MOVIMIENTO, 4, "Reflejos", "Atención sostenida y respuesta veloz."),

    // --- Exclusivos de la versión nativa (no existen en la web) ---
    GameDef("vibra-adivina", "Vibra y adivina", "📳", "Siente los pulsos y cuenta", Area.SENSORIAL, 4, "Percepción táctil", "Refinar el tacto sintiendo patrones de vibración reales — imposible en la versión web."),
    GameDef("reflejo-color", "Reflejo de color", "⚡", "Toca en cuanto cambie de color", Area.MOVIMIENTO, 4, "Tiempo de reacción", "Medir el tiempo de reacción real en milisegundos — solo posible con entrada táctil nativa."),
)

fun buscarJuego(id: String): GameDef? = CATALOGO.find { it.id == id }
fun juegosPorArea(area: Area): List<GameDef> = CATALOGO.filter { it.area == area }
