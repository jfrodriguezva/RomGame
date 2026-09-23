package com.miambiente.app.model

import com.miambiente.app.theme.Area

/** Definición local de un modo de juego, sin filtros ni bloqueos por edad. */
data class GameDef(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val area: Area,
    val material: String,
    val objetivo: String,
    val libre: Boolean = false,
)

/** Catálogo completo de modalidades agrupadas en familias visibles. */
val CATALOGO = listOf(
    // --- MaterialQuiz (nomenclatura) ---
    GameDef("formas", "Gabinete de figuras", "🔺", "Círculo, cuadrado, triángulo", Area.SENSORIAL, "Gabinete de geometría", "Reconocer figuras por su contorno y nombrarlas."),
    GameDef("cuerpo", "Partes del cuerpo", "🧍", "Cabeza, ojo, mano, brazo, pierna y pie", Area.CULTURA, "Nomenclatura del cuerpo humano", "Vocabulario y conciencia del propio cuerpo."),
    GameDef("colores", "Los colores", "🎨", "Tabletas de color", Area.SENSORIAL, "Cajas de color", "Nomenclatura de las tabletas de color."),
    GameDef("instrumentos", "Instrumentos musicales", "🎻", "Tambor, guitarra, piano, trompeta y violín", Area.SENSORIAL, "Nomenclatura", "Nomenclatura de instrumentos musicales."),
    GameDef("oficios", "Oficios y profesiones", "🚒", "Bombero, doctora, cocinero, maestra, policía", Area.CULTURA, "Nomenclatura", "Vocabulario de oficios y su utilidad social."),
    GameDef("transporte", "Medios de transporte", "🚂", "Tierra, aire o agua", Area.CULTURA, "Clasificación de transportes", "Relacionar cada vehículo con el medio por el que se mueve."),

    // --- MaterialOrdenar (seriación, con arrastre) ---
    GameDef("torre-rosa", "Torre rosa", "🟪", "Ordena los cubos del más grande al más chico", Area.SENSORIAL, "Torre rosa", "Discriminación visual del tamaño; seriación."),
    GameDef("dias-semana", "Los días de la semana", "📅", "Lunes a domingo, en orden", Area.CULTURA, "Secuencia temporal", "Secuencia y vocabulario temporal."),
    GameDef("estaciones", "Las estaciones del año", "🍂", "Primavera a invierno, en orden", Area.CULTURA, "Secuencia temporal", "Secuencia y vocabulario temporal."),
    GameDef("ciclo-vida", "El ciclo de la mariposa", "🐛", "Huevo, oruga, crisálida, mariposa", Area.CULTURA, "Seriación por tiempo", "Seriación por secuencia temporal, no por tamaño."),

    // --- MaterialClasificar (el más reutilizado, con arrastre a canastas) ---
    GameDef("seres-vivos", "¿Vivo o no vivo?", "🌱", "Clasifica en la canasta correcta", Area.CULTURA, "Clasificación científica", "Primera clasificación científica: vivo o no vivo."),
    GameDef("habitat", "¿Dónde vive?", "🏝️", "Selva, desierto, océano, polo", Area.CULTURA, "Clasificación por hábitat", "Relacionar cada animal con su hábitat."),
    GameDef("dieta-animal", "¿Qué come?", "🦁", "Herbívoro, carnívoro u omnívoro", Area.CULTURA, "Clasificación por dieta", "Clasificación biológica por tipo de alimentación."),
    GameDef("fruta-verdura", "Fruta o verdura", "🍎", "Clasificación botánica", Area.CULTURA, "Clasificación botánica", "Primera clasificación botánica."),
    GameDef("el-la", "El o la", "📖", "Reconocer el género gramatical", Area.LENGUAJE, "Género gramatical", "Reconocer el género gramatical de un sustantivo."),
    GameDef("pares-impares", "Pares e impares", "🔢", "¿Se reparte en parejas exactas?", Area.MATEMATICAS, "Paridad", "¿La cantidad se puede repartir en parejas exactas?"),
    GameDef("tamanos", "Grande, mediano o chico", "📏", "Clasificación en tres canastas", Area.PRACTICA, "Clasificación por tamaño", "Clasificación en tres canastas, no solo dos."),
    GameDef("estados-agua", "Estados del agua", "🧊", "Sólido, líquido o gas", Area.CULTURA, "Estados de la materia", "Sólido, líquido o gas."),
    GameDef("dia-noche", "Día y noche", "🌗", "¿Es de día o de noche?", Area.CULTURA, "Clasificación", "¿Es de día o de noche?"),
    GameDef("singular-plural", "Singular y plural", "🔤", "La palabra junto a su cantidad", Area.LENGUAJE, "Gramática", "La palabra siempre junto a su cantidad concreta."),

    // --- MaterialTransferir (con arrastre) ---
    GameDef("pinza", "Pinza de transferencia", "🥢", "Mueve objetos de uno en uno", Area.PRACTICA, "Ejercicio de vida práctica", "Mover objetos de uno en uno; tomar uno de más es el error."),
    GameDef("husos", "Los husos", "🧵", "Corresponde cantidad con número", Area.MATEMATICAS, "Numeración", "Corresponder cantidad con número del 0 al 9."),
    GameDef("contar", "Contar y tocar", "🔢", "Cuenta los objetos", Area.MATEMATICAS, "Fichas y contadores", "Correspondencia uno a uno entre objeto y número."),

    // --- MaterialQuiz: lote 2 ---
    GameDef("animales", "Sonidos de animales", "🐮", "Adivina quién habla", Area.CULTURA, "Zoología", "Reconocer animales y su sonido."),
    GameDef("solidos", "Cuerpos geométricos", "🔷", "Esfera, cubo, cono, cilindro, pirámide", Area.SENSORIAL, "Sólidos geométricos", "Nomenclatura de formas tridimensionales."),
    GameDef("sentidos", "Los cinco sentidos", "🖐️", "Vista, oído, olfato, gusto y tacto", Area.CULTURA, "Nomenclatura de los sentidos", "Vocabulario de los cinco sentidos y su función."),
    GameDef("partes-planta", "Partes de la planta", "🌱", "Raíz, tallo, hoja, flor y fruto", Area.CULTURA, "Nomenclatura de botánica", "Vocabulario de las partes de una planta."),
    GameDef("banderas", "Banderas del mundo", "🚩", "¿De qué país es esta bandera?", Area.CULTURA, "Banderas", "Reconocer banderas y ampliar la noción de otros países."),
    GameDef("sonidos-iniciales", "Veo veo", "👂", "¿Con qué sonido empieza?", Area.LENGUAJE, "Juego del yo veo", "Conciencia fonológica: oír el primer sonido de la palabra."),
    GameDef("vocales", "Las vocales", "🅰️", "Reconoce A E I O U", Area.LENGUAJE, "Letras de lija (vocales)", "Las cinco vocales: sonido, forma y trazo."),
    GameDef("abecedario", "El abecedario", "🔤", "Cada letra con su palabra", Area.LENGUAJE, "Objetos y tarjetas", "Asociar letra, sonido y una palabra que empieza con ella."),
    GameDef("ingles", "Primeras palabras en inglés", "🫱", "Pocas palabras, bien aprendidas", Area.LENGUAJE, "Tarjetas de vocabulario", "Primer contacto con un segundo idioma."),
    GameDef("emociones", "¿Cómo te sientes?", "😊", "Nombra lo que sientes", Area.PRACTICA, "Gracia y cortesía", "Reconocer y nombrar emociones propias y ajenas."),
    GameDef("tiempo", "El tiempo", "🌦️", "¿Qué tiempo hace?", Area.CULTURA, "Nomenclatura del clima", "Vocabulario de las condiciones del clima."),
    GameDef("reloj", "¿Qué hora es?", "🕐", "Las doce horas en punto", Area.MATEMATICAS, "El reloj", "Primer contacto con la hora en punto."),
    GameDef("sombras", "Empareja sombras", "🌗", "¿De quién es esa sombra?", Area.SENSORIAL, "Emparejamiento de siluetas", "Reconocer un objeto solo por su contorno."),
    GameDef("letras-lija", "Letras de lija", "✍️", "Escucha, traza y siente la letra", Area.LENGUAJE, "Letras de lija", "Une el sonido de la letra con el movimiento de escribirla."),

    // --- MaterialOrdenar: lote 2 ---
    GameDef("escalera-marron", "La escalera marrón", "🟫", "Del más ancho al más delgado", Area.SENSORIAL, "Escalera marrón", "Discriminación de grosor y construcción de una serie."),
    // "Ordenar bloques" (id "bloques") se quitó: era un duplicado real de
    // Torre rosa, no un material distinto — mismo patrón MaterialOrdenar,
    // misma fórmula de tamaños, sin ninguna cualidad propia más allá del
    // color. Bug real reportado ("el módulo sensorial tiene materiales
    // duplicados"); Torre rosa ya cubre la seriación por tamaño de verdad.
    GameDef("cilindros", "Cilindros con botón", "🎯", "Cada uno en su hueco", Area.SENSORIAL, "Bloques de cilindros", "Ajuste exacto por tamaño."),
    GameDef("sistema-solar", "El sistema solar", "🪐", "Ordena los planetas desde el Sol", Area.CULTURA, "Los planetas", "Secuencia y vocabulario del sistema solar."),
    GameDef("ciclo-agua", "El ciclo del agua", "💧", "Sol, nube, lluvia, río", Area.CULTURA, "Ciencias naturales: el ciclo del agua", "Secuencia de un proceso natural."),
    GameDef("rutina", "La rutina de la mañana", "⏰", "Despertar, vestirse, desayunar, ir a la escuela", Area.PRACTICA, "Secuencia de rutina diaria", "Orden y secuencia de una rutina de vida diaria."),
    GameDef("mesa", "Poner la mesa", "🍽️", "Mantel, plato, cubiertos, vaso", Area.PRACTICA, "Secuencia de poner la mesa", "Orden y secuencia de un trabajo de vida práctica clásico."),
    GameDef("lavado-manos", "Lavarse las manos", "🧼", "Mojar, jabón, tallar, enjuagar, secar", Area.PRACTICA, "Secuencia de higiene", "Orden y secuencia de un hábito de cuidado personal."),
    GameDef("barras-numericas", "Barras numéricas", "📏", "La cantidad se ve y se toca", Area.MATEMATICAS, "Barras rojas y azules", "Cantidad concreta antes que número: del 1 al 10."),
    GameDef("numeros", "Números en orden", "➡️", "Conecta del 1 en adelante", Area.MATEMATICAS, "Cadena de cuentas", "Orden y sucesión de los números."),

    // --- MaterialClasificar: lote 2 ---
    GameDef("textura", "Áspero o liso", "🤚", "Toca con los ojos: ¿pincha o resbala?", Area.SENSORIAL, "Tablillas ásperas y lisas", "Refinar el tacto: discriminar superficies ásperas y lisas."),
    GameDef("temperatura", "Caliente o frío", "🌡️", "¿Está caliente o frío?", Area.SENSORIAL, "Sentido térmico", "Refinar la percepción de temperatura."),
    GameDef("peso", "Pesado o ligero", "⚖️", "¿Pesa mucho o poco?", Area.SENSORIAL, "Sentido bárico", "Refinar la percepción del peso."),
    GameDef("sabor", "Dulce o salado", "🍬", "¿Dulce o salado?", Area.SENSORIAL, "Sentido gustativo", "Refinar la percepción del sabor."),
    GameDef("olfato", "Huele bien o mal", "👃", "¿Huele bien o mal?", Area.SENSORIAL, "Sentido olfativo", "Refinar la percepción del olfato."),
    GameDef("mayusculas", "Mayúsculas y minúsculas", "🔠", "Clasifica según cómo se ve la letra", Area.LENGUAJE, "Mayúsculas y minúsculas", "Reconocer visualmente las dos formas de una misma letra."),
    GameDef("lados", "¿Cuántos lados tiene?", "📐", "Clasifica figuras por sus lados", Area.MATEMATICAS, "Geometría: conteo de lados", "Relacionar la forma geométrica con su cantidad de lados."),
    GameDef("mitades", "Mitades y enteros", "🍕", "¿Está entera o a la mitad?", Area.MATEMATICAS, "Primer contacto con la fracción", "Distinguir una figura entera de su mitad."),
    GameDef("rimas", "Palabras que riman", "🎵", "¿Con cuál rima?", Area.LENGUAJE, "Conciencia fonológica: rima", "Reconocer el sonido final de las palabras."),
    GameDef("silabas", "Cuenta las sílabas", "👏", "Escucha y clasifica por golpes de voz", Area.LENGUAJE, "Conciencia fonológica", "Separar una palabra en sus partes antes de relacionarla con letras."),
    GameDef("continentes", "Los continentes", "🌍", "El mundo y sus animales", Area.CULTURA, "Mapa de continentes", "Ubicar los continentes y lo que vive en cada uno."),
    GameDef("tierra-agua", "Formas de tierra y agua", "🏝️", "Isla, lago, montaña...", Area.CULTURA, "Formas de tierra y agua", "Vocabulario geográfico y la relación entre tierra y agua."),
    GameDef("orificios", "Encaja la figura", "🕳️", "Cada figura en su agujero exacto", Area.SENSORIAL, "Encajes de formas geométricas", "Discriminación visual precisa."),

    // --- MaterialOrdenar: lote 3 ---
    GameDef("vida-practica", "Vida práctica", "🫗", "Verter, servir y abotonar", Area.PRACTICA, "Ejercicios de vida práctica", "Movimiento preciso, secuencia de pasos y cuidado del entorno."),

    // --- Independientes (sin patrón compartido) ---
    // Juegos de mesa clásicos.
    GameDef("gato", "Gato", "⭕", "Tres en línea", Area.COMPANIA, "Juego de mesa", "Anticipar, esperar el turno y aceptar el resultado."),
    GameDef("rps", "Piedra, papel o tijera", "✂️", "El clásico juego de manos", Area.COMPANIA, "Juego de mesa", "Reconocer un patrón simple: qué le gana a qué."),
    GameDef("memorama", "Juego de memoria", "🧠", "Encuentra las parejas", Area.SENSORIAL, "Juego de memoria a distancia", "Memoria visual y concentración sostenida."),
    GameDef("que-falta", "¿Qué falta?", "🔍", "Memoriza la bandeja y di qué desapareció", Area.COMPANIA, "Juego de Kim", "Memoria de trabajo y observación."),
    GameDef("diferencias", "¿Qué es distinto?", "🔍", "Encuentra lo diferente", Area.SENSORIAL, "Pares y contrastes", "Discriminación visual fina y atención al detalle."),
    GameDef("objetos", "Encuentra los objetos", "🔎", "Busca entre muchos", Area.SENSORIAL, "Búsqueda visual", "Atención selectiva y rastreo visual ordenado."),
    GameDef("memoria-turnos", "Memoria por turnos", "🧠", "Encuentra más parejas que la computadora", Area.COMPANIA, "Juego de mesa", "Esperar el turno, memoria y aceptar perder o ganar sin drama."),
    GameDef("oca", "El juego de la oca", "🦢", "Tira el dado, oca, puente o pozo", Area.COMPANIA, "Juego de mesa", "Esperar el turno y aceptar el azar."),
    GameDef("serpientes", "Serpientes y escaleras", "🐍", "Tira el dado y avanza", Area.COMPANIA, "Juego de mesa", "Contar avanzando y tolerar la sorpresa."),
    GameDef("dado", "Dado de retos", "🎲", "Tira el dado y muévete", Area.COMPANIA, "Movimiento dirigido", "Escuchar una consigna y ejecutarla con el cuerpo.", libre = true),
    GameDef("patron", "Las campanas", "🔔", "Repite la melodía", Area.SENSORIAL, "Campanas Montessori", "Memoria auditiva y discriminación de tonos."),
    GameDef("pizarra", "La pizarra grande", "🖍️", "Dibuja lo que quieras", Area.CREATIVA, "Pizarra y trazo libre", "Expresión libre, trazo amplio y experimentación con el color.", libre = true),
    GameDef("collage", "Collage libre", "🖼️", "Coloca estampas donde quieras", Area.CREATIVA, "Collage y composición libre", "Composición espacial libre y motricidad fina de precisión.", libre = true),
    GameDef("colorear", "Colorear", "🎨", "Pinta el dibujo", Area.CREATIVA, "Dibujo dirigido", "Color, límites y paciencia; también relaja."),
    GameDef("xilofono", "Xilófono", "🎼", "Toca y escucha, sin reglas", Area.CREATIVA, "Instrumento de exploración sonora", "Exploración musical libre.", libre = true),
    GameDef("mesa-silencio", "El juego del silencio", "🤫", "Respira y escucha", Area.PRACTICA, "Juego del silencio", "Autorregulación, escucha y control voluntario del cuerpo.", libre = true),
    GameDef("cara", "Toca la cara", "🙂", "Toca la parte que se pide", Area.CULTURA, "Nomenclatura de la cara", "Vocabulario de la cara, tocando directo sobre el dibujo."),
    GameDef("alfabeto-movil", "Alfabeto móvil", "🔡", "Forma la palabra con letras", Area.LENGUAJE, "Alfabeto móvil", "Escribir antes de saber escribir: componer palabras con sonidos."),
    GameDef("trazos", "Trazos previos", "〰️", "Sigue la línea punteada", Area.LENGUAJE, "Resaques metálicos", "Mano firme y control del trazo antes de escribir."),
    GameDef("rompecabezas", "Rompecabezas", "🧩", "Arma la imagen", Area.SENSORIAL, "Encajes y puzzles", "Relación parte-todo y orientación espacial."),
    GameDef("binomio", "El cubo del binomio", "🧊", "Arma el cubo de colores", Area.SENSORIAL, "Cubo del binomio", "Patrón espacial y orden; base sensorial del álgebra."),
    GameDef("tabla-cien", "La tabla del cien", "💯", "Coloca del 1 al 100", Area.MATEMATICAS, "Tabla del cien", "Secuencia numérica y estructura de la decena."),
    GameDef("banco-dorado", "El banco dorado", "🟡", "Unidades, decenas y centenas", Area.MATEMATICAS, "Perlas doradas", "Sistema decimal a la vista: componer números grandes."),
    GameDef("bingo", "Bingo con imágenes", "🎱", "Escucha, busca y marca en tu cartón", Area.COMPANIA, "Juego de mesa", "Vocabulario, atención y correspondencia."),
    GameDef("loteria", "Lotería mexicana", "🃏", "El gritón canta y tú marcas tu cartón", Area.COMPANIA, "Lotería mexicana", "Escuchar con atención, reconocer por el nombre y esperar su turno."),
    GameDef("domino", "Dominó", "🀄", "Encaja tu ficha por número", Area.COMPANIA, "Juego de mesa", "Correspondencia numérica y esperar el turno."),
    GameDef("conecta4", "Cuatro en línea", "🔵", "Alinea cuatro fichas antes que la computadora", Area.COMPANIA, "Juego de mesa", "Planeación simple y anticipar la jugada del otro."),
    GameDef("adivinaquien", "Adivina quién es", "🕵️", "Pregunta y descubre", Area.COMPANIA, "Juego de deducción", "Razonamiento lógico por eliminación."),
    GameDef("damas", "Damas inglesas", "⚫", "Captura y corona tus fichas", Area.COMPANIA, "Juego de mesa", "Planeación a varios pasos y anticipar capturas del rival."),
    GameDef("damas-chinas", "Damas chinas", "🔺", "Lleva tus canicas al otro lado de la estrella", Area.COMPANIA, "Juego de mesa", "Planeación de rutas y saltos encadenados."),
    // "🎴" en vez de un carácter del bloque Unicode "Playing Cards" (como
    // "🂡"): ese bloque casi nunca tiene glifo de color en las fuentes —
    // el mismo bug de iconos que no se ven, encontrado y corregido antes
    // con el dominó.
    GameDef("solitario", "Solitario", "🎴", "Ordena las cartas por palo y color", Area.COMPANIA, "Juego de cartas", "Paciencia, clasificación y estrategia en solitario."),
    GameDef("arana-cartas", "Solitario araña", "🕸️", "Arma secuencias del As al Rey", Area.COMPANIA, "Juego de cartas", "Planeación a varios pasos y memoria de lo ya visto."),
    GameDef("ajedrez", "Ajedrez", "♞", "Da jaque mate al rey contrario", Area.COMPANIA, "Juego de mesa", "Planeación a varios pasos y anticipar la jugada del rival."),
    GameDef("tetris", "Tetris", "🧱", "Acomoda las piezas y completa líneas", Area.COMPANIA, "Juego de mesa", "Rotación mental, planeación espacial y reflejos."),
    GameDef("snake", "La víbora", "🐍", "Come y no choques", Area.COMPANIA, "Juego arcade", "Planeación de ruta, reflejos y control del error."),
    GameDef("arkanoid", "Rompe ladrillos", "🧱", "Rebota la pelota y rompe todos los ladrillos", Area.COMPANIA, "Juego arcade", "Coordinación ojo-mano y anticipar trayectorias."),
    GameDef("topo", "Atrapa al topo", "🐹", "Tócalo antes de que se esconda", Area.COMPANIA, "Juego arcade", "Tiempo de reacción y atención sostenida."),
    GameDef("mosaico", "Mosaico sorpresa", "🖼️", "Descubre la imagen sin tocar a los guardianes", Area.COMPANIA, "Arcade de territorio", "Planeación espacial, prudencia y control de impulsos."),
    GameDef("vaqueros", "Vaqueros del ocaso", "🤠", "Protege el pueblo y atrapa a los bandidos", Area.COMPANIA, "Arcade de puntería", "Atención visual, precisión y velocidad de reacción."),
    GameDef("comepuntos", "Comepuntos", "🟡", "Come todos los puntos y evita a los fantasmas", Area.COMPANIA, "Laberinto arcade", "Planeación de rutas, orientación y anticipación."),
    GameDef("nieve", "Rescate de nieve", "⛄", "Convierte a los traviesos en bolas de nieve", Area.COMPANIA, "Arcade de plataformas", "Secuenciación, precisión y atención dividida."),
    GameDef("escuadron-estelar", "Escuadrón estelar", "🚀", "Pilota, apunta y protege la galaxia", Area.COMPANIA, "Arcade espacial", "Coordinación ojo-mano y anticipación de trayectorias."),
    GameDef("gran-premio", "Gran premio", "🏁", "Cambia de carril y llega primero", Area.COMPANIA, "Arcade de carreras", "Reflejos, anticipación y toma rápida de decisiones."),

    // --- Movimiento y coordinación (independientes) ---
    GameDef("laberinto", "Laberinto", "🌀", "Encuentra la salida", Area.MOVIMIENTO, "Control del movimiento", "Planear una ruta y seguirla sin chocar."),
    GameDef("burbujas", "Burbujas", "🫧", "Truena las burbujas", Area.MOVIMIENTO, "Coordinación ojo-mano", "Precisión del dedo sobre un objetivo en movimiento."),
    GameDef("canasta", "Atrapa las estrellas", "🧺", "Mueve la canasta", Area.MOVIMIENTO, "Coordinación ojo-mano", "Anticipar una trayectoria y responder a tiempo."),
    GameDef("globo", "El globo volador", "🎈", "No dejes que caiga", Area.MOVIMIENTO, "Coordinación ojo-mano", "Ritmo y constancia del toque."),
    GameDef("arana", "La araña pintora", "🕷️", "Descubre la imagen", Area.MOVIMIENTO, "Recorrido y estrategia", "Recorrer un espacio completo evitando obstáculos."),
    GameDef("carreras", "Carreras", "🏎️", "Esquiva y llega a la meta", Area.MOVIMIENTO, "Reflejos", "Atención sostenida y respuesta veloz."),

    // --- Exclusivos de la versión nativa (no existen en la web) ---
    GameDef("vibra-adivina", "Vibra y adivina", "📳", "Siente los pulsos y cuenta", Area.SENSORIAL, "Percepción táctil", "Refinar el tacto sintiendo patrones de vibración reales — imposible en la versión web."),
    GameDef("reflejo-color", "Reflejo de color", "⚡", "Toca en cuanto cambie de color", Area.MOVIMIENTO, "Tiempo de reacción", "Medir el tiempo de reacción real en milisegundos — solo posible con entrada táctil nativa."),
)

fun buscarJuego(id: String): GameDef? = CATALOGO.find { it.id == id }
fun juegosPorArea(area: Area): List<GameDef> = CATALOGO.filter { it.area == area }
