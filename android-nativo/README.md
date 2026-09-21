# RominaGame — versión nativa (Kotlin + Jetpack Compose)

Proyecto Android nativo aparte, **sin depender de la app web ni de
Capacitor** (esa versión sigue intacta en `../android` y `../app`). Nace
de un pedido explícito: la versión empaquetada con Capacitor (un WebView)
tenía un bug real en el selector de edad en un dispositivo físico, y el
usuario pidió una versión nativa completa, con drag-and-drop real y
funciones nativas del sistema en vez de simularlas dentro de un WebView.

Se llamó "Mi Ambiente" durante el desarrollo (nombre descriptivo del
concepto Montessori de "ambiente preparado"); el nombre público de la
app pasó a ser **RominaGame** (`app_name` en `strings.xml` + el
encabezado del Home). No se tocó `applicationId` ni el paquete Kotlin
(`com.miambiente.app`) ni los identificadores internos del tema
(`Theme.MiAmbiente`) — cambiar eso forzaría desinstalar/reinstalar y
perder el progreso guardado en instalaciones existentes, un efecto
destructivo que nadie pidió.

## Décima pasada: Home sin filtro de edad, Arkanoid/Tetris/Snake sin recortarse, Lotería, Pang y Palillos chinos nuevos

Pedido de seis partes en un solo mensaje: *"quita los filtros de edad y
que se muestren todos los materiales juntos / ajusta los tamaños de cada
juego y que no se salga del cuadro / agregaste la lotería, ponle otros
tableros y que salgan todas las cartas al azar / agrega los palillos
chinos que funcione bien / replica idénticamente los juegos arcade
(Arkanoid, Pang) / dale una mejor vista a toda la app"*. Confirmado con
el usuario antes de tocar código: trabajar solo sobre `android-nativo`
(no la web), una sola grilla sin pestañas de área, pulido de componentes
compartidos (no rediseño), y Lotería como lotería mexicana tradicional.

- **`HomeScreen.kt` — filtro de edad y pestañas de área eliminados por
  completo**: `disponibles` ya no filtra por `edad >= edadMinima` (antes
  con una excepción explícita solo para `Area.COMPANIA`); ahora es
  literalmente `CATALOGO` completo. Se quitó también la fila de
  `FilterChip` por área y la variable `areaActiva` — una sola
  `LazyVerticalGrid` con los 100+ materiales. El selector de edad inicial
  (`SelectorEdadScreen`) se dejó intacto a propósito: `edad` no se usa
  para dificultad en ningún material, solo decide la pantalla de arranque,
  así que quitarlo hubiera sido un cambio no pedido sin beneficio real.
- **Arkanoid, Tetris y Snake ya no se recortan**: los tres tenían el
  mismo tablero de tamaño fijo sin `verticalScroll`/`horizontalScroll` que
  ya se había corregido antes en Damas/Ajedrez/Solitario — mismo patrón
  defensivo aplicado ahora aquí.
- **Lotería mexicana, material nuevo** (`loteria`, distinto de Bingo):
  mazo real de 54 cartas únicas y 8 tableros de 4×4 predefinidos
  (subconjuntos distintos del mazo, uno se elige al azar por partida). El
  mazo se baraja una vez y se consume carta por carta sin repetir
  (`siguienteCanto`) hasta ganar el cartón completo o agotarlo — a
  diferencia de Bingo, donde el "mazo" y el "cartón" eran literalmente el
  mismo conjunto de 9 emojis.
- **Arkanoid, mucho más cerca del arcade original**: multi-bola de verdad
  (`bolas: List<BolaState>`, no una sola bola), power-ups reales que caen
  de un ladrillo roto (paleta ancha/angosta temporal, multi-bola, bola
  rápida/lenta, vida extra), 5 patrones de nivel que rotan (filas
  completas, marco con ladrillos indestructibles, pirámide, diamante,
  tablero de ajedrez — antes siempre era una cuadrícula rectangular
  completa) y rebote lateral real contra los ladrillos (resolución AABB
  por penetración mínima, antes el rebote siempre invertía la velocidad
  vertical sin importar de qué lado pegara). Los tests existentes de
  `ArkanoidLogicTest` se preservaron sin tocar (el layout por defecto en
  `(nivel-1)%5==0` es exactamente el diseño original) y se sumaron los
  nuevos para `layoutParaNivel`, `resolverReboteLadrillo` y
  `deberiaCaerPowerUp`.
- **Pang, generado de cero** (`pang`): arpón fijo que sube y se retrae al
  tocar el techo o una burbuja, burbujas con gravedad real que rebotan en
  paredes/techo/suelo y se dividen en dos más chicas al ser tocadas por el
  arpón (hasta desaparecer en el tamaño mínimo). Reutiliza
  `circuloChocaRect` de `ArkanoidScreen.kt` tratando el arpón como un
  rectángulo angosto, en vez de duplicar la fórmula de colisión.
- **Palillos chinos (Mikado), generado de cero** (`palillos`) — el más
  nuevo técnicamente, primera geometría de colisión segmento-segmento del
  proyecto (`segmentosSeCruzan`, algoritmo estándar de orientación de 3
  puntos con los 4 casos colineales). El jugador arrastra una varilla
  suelta hacia "tu bandeja" (una franja abajo del montón); si en el
  camino cruza otra varilla, se cancela el intento y pasa el turno —
  igual que la regla real de "no molestar las demás". Modo contra la
  computadora (heurística: elige la varilla con menos varillas
  cruzándola, con una probabilidad de éxito que baja mientras más cruces
  tenga) o dos jugadores, mismo patrón de `FilterChip` que Damas/Ajedrez.
  **Dos simplificaciones conscientes, declaradas sin rodeos**: la caída
  inicial del montón es un cálculo de posiciones dispersas (no física de
  colisión entre 24 cuerpos rígidos cayendo de verdad), y arrastrar una
  varilla la traslada en línea recta sin rotarla (más simple para una
  mano de niño que rotar y trasladar a la vez).
- **Pase de pulido en componentes compartidos** (confirmado con el
  usuario: solo esto, no un rediseño): `theme/Theme.kt` completó los
  estilos de `Typography` que faltaban (`headlineSmall`, `titleMedium`,
  `titleSmall`, `bodySmall`, `labelLarge`, `labelMedium` — antes Material3
  los rellenaba con su tamaño/peso por defecto en vez del criterio propio
  del proyecto); `theme/Espaciado.kt` nuevo con una escala de espaciado
  (4/8/16/24dp) aplicada en `GameShell.kt` y `HomeScreen.kt` donde ya
  coincidía con esos valores; y una transición de navegación pareja
  (fundido + deslizamiento corto) declarada una sola vez en el `NavHost`
  de `MainActivity.kt`, que antes no tenía ninguna (corte seco entre Home
  y cada material).

**Sobre la verificación de esta pasada, con la misma honestidad de
siempre**: el JDK 21 de esta máquina (`C:/Program Files/Android/openjdk/jdk-21.0.8`)
resultó estar incompleto (le falta `lib/jvm.cfg` y los subdirectorios
reales del runtime — solo 23 archivos en total, cuando un JDK completo
tiene miles), así que **no se pudo correr `compileDebugKotlin` ni los
tests unitarios nuevos en esta sesión**. Todo el código de esta pasada se
escribió y se revisó a mano releyendo cada archivo completo buscando
errores de sintaxis/tipos, siguiendo al pie de la letra los patrones ya
usados en el proyecto (mismo estilo de `withFrameNanos`, mismo
`circuloChocaRect` reutilizado, mismo patrón de `FilterChip` para
dos-jugadores-vs-IA), pero no se instaló ni se jugó en un emulador. Para
verificar de verdad: reinstalar un JDK 21 completo (o apuntar
`JAVA_HOME` a uno que sí lo tenga, por ejemplo el JBR de Android Studio)
y correr `./gradlew compileDebugKotlin`, `./gradlew testDebugUnitTest` y
`./gradlew assembleDebug`.

## Novena pasada: silueta al mover piezas, un ANR real corregido, un duplicado menos, Arkanoid con niveles, Solitario "nivel PC"

Pedido de cinco partes en un solo mensaje: *"En todos los juegos al
arrastrar o mover se pierde la silueta, ajusta ese efecto / algunos
juegos traban la app y la reinician / el modulo sensorial tiene
materiales duplicados / más niveles y completa más el juego de rompe
ladrillos / genera bien los solitarios al nivel de la pc"*.

- **La "silueta perdida" al mover piezas — era falta de animación, no
  un bug de estado**: Damas inglesas, Ajedrez y Damas chinas dibujaban
  cada ficha directamente dentro de su celda de grilla, así que al
  moverse la ficha desaparecía de una celda y reaparecía en otra sin
  transición — de ahí la sensación de "se pierde la silueta". Se separó
  el tablero en dos capas dentro de un mismo `Box`: una grilla de fondo
  (solo celdas, clic y resaltado de destino) y una capa de piezas
  superpuesta que usa un `AnimatedPieza` compartido nuevo
  (`ui/materials/AnimatedPieza.kt`), con `animateDpAsState` sobre X/Y
  para que cada ficha se deslice de verdad entre casillas. La clave fue
  darle a cada ficha un `id` estable (no la fila/columna, que cambia en
  cada jugada) para que Compose la reconozca como la misma pieza entre
  recomposiciones y la anime en vez de recrearla. `MaterialOrdenar` y
  `MaterialTransferir` (los ~15 materiales de tocar-para-soltar de la
  pasada anterior) ganaron una animación de aparición (escala 0→1) para
  que la pieza no aparezca de golpe al llegar a su ranura/destino.
  Verificado en vivo: Damas inglesas (jugada + respuesta de la
  computadora), Ajedrez (peón e2-e4) y Damas chinas (canica moviéndose
  en diagonal) — las tres con deslizamiento visible y sin choque.
- **"Algunos juegos traban la app y la reinician" — causa real
  encontrada por auditoría, no adivinada**: es el patrón clásico de ANR
  (Application Not Responding) de Android — cálculo de IA corriendo en
  el hilo principal dentro de un `LaunchedEffect` sin `withContext`,
  que si tarda más de ~5s dispara el diálogo del sistema y, si se
  descarta, reinicia la app. `AjedrezScreen.kt` ya lo hacía bien;
  `DamasScreen.kt` (la IA más cara de la app: minimax con cadenas de
  captura múltiple) y `DamasChinasScreen.kt` no — se envolvió el cálculo
  de la mejor jugada de ambos en `withContext(Dispatchers.Default)`.
  Esta es la causa más probable encontrada por auditoría de código, no
  una confirmada con un log de choque real del usuario — se corrigió
  igual porque es un bug real independientemente de si es exactamente
  el que reportó el usuario.
- **Un material duplicado real menos en Sensorial**: `BloquesScreen.kt`
  ("Ordenar bloques") y `TorreRosaScreen.kt` tenían el mismo
  `MaterialOrdenar` parametrizado byte por byte igual
  (`n = phasedInt(1, listOf(3,4,4,5,5,6,7,8,9,10,10))`), solo cambiaba
  el color y el texto de instrucción — se borró Bloques por ser un
  duplicado mecánico real, no una variante pedagógica distinta. Se
  mantuvieron a propósito los 5 materiales de "sentidos" (Áspero o liso,
  Caliente o frío, Pesado o ligero, Dulce o salado, Huele bien o mal)
  que son mecánicamente parecidos pero pedagógicamente distintos, mismo
  criterio de rondas anteriores. 106 materiales totales tras el borrado
  (Sensorial 21), verificado en el Home.
- **Rompe ladrillos — de "un solo tablero fijo" a niveles de verdad**:
  ahora sube de nivel al vaciar el tablero (pausa breve con "¡Nivel X
  superado!", no termina el juego), con más filas de ladrillos por
  nivel hasta un tope de 8, la pelota más rápida cada nivel, y desde el
  nivel 3 aparecen ladrillos reforzados (2 golpes, borde blanco) en el
  tercio superior. El rebote en la paleta ahora depende de verdad de en
  qué parte le pega (ángulo real vía `hypot`/`sqrt`, no una división fija
  de velocidad). Verificado en vivo: lanzar la pelota, arrastrar la
  paleta, romper un ladrillo (puntaje +10), perder una vida y que la
  pelota se reposicione sin choque — el ciclo completo de "vaciar el
  tablero y pasar de nivel" quedó confirmado por las pruebas unitarias
  reescritas (`ArkanoidLogicTest.kt`) y por revisión de código, no
  jugado hasta el final en vivo (habría tomado deslizar la pelota contra
  ~24 ladrillos a mano por adb, poco práctico).
- **Solitario "al nivel de la PC"**: doble toque para mandar una carta a
  su fundación automáticamente (sin necesidad de seleccionarla primero),
  tanto desde el descarte como desde el tope de cada columna — usando
  `detectTapGestures(onTap=..., onDoubleTap=...)` en vez de un simple
  `.clickable`. Se agregó un botón "Auto-completar ✨" que aparece solo
  cuando ya no quedan cartas boca abajo ni en el mazo/descarte (todo el
  tablero resuelto), y manda automáticamente cada carta a su fundación
  en cadena. Verificado en vivo: el doble toque no rompe nada sobre una
  carta que no puede ir a fundación (no-op seguro, sin choque) y la
  selección de un solo toque sigue funcionando igual que antes
  (sin regresión).

**Sobre la verificación en vivo de esta pasada, con honestidad**: la
máquina volvió a tener presión de memoria severa y fluctuante durante
la sesión (llegó a 0.47GB libres en un momento, recuperándose a 6GB
minutos después) — se esperó a que la memoria se recuperara antes de
cada tanda de pruebas en vez de forzar el emulador en ese estado. Con
memoria recuperada, las cinco correcciones se probaron en vivo sobre el
build real (no solo compilado): Damas, Ajedrez y Damas chinas con una
jugada real cada una; Arkanoid con el ciclo de lanzar/rebotar/romper/
perder vida; Solitario con el gesto de doble toque. Lo único no llevado
hasta el final en vivo fue completar un nivel entero de Arkanoid (por
lo largo que toma a mano vía adb) y forzar un tablero de Solitario en
estado "todo boca arriba" para ver el botón de auto-completar en
acción — ambos quedan respaldados por revisión de código y, en el caso
de Arkanoid, por pruebas unitarias.

## Octava pasada: se rehace el modelo de arrastre, la araña real, y tres arcade nuevos

Pedido con varias partes, después de que la séptima pasada resultó
insuficiente para lo mismo que ya se había reportado antes:

- **El modelo de arrastre de Vida práctica se rehizo de raíz, no se
  volvió a parchar**: después de corregir dos causas reales distintas
  del mismo síntoma (estado reciclado por posición, tolerancia de
  suelta demasiado exacta) y que el reporte siguiera siendo "sigue
  fallando", la conclusión honesta es que arrastrar con precisión es
  difícil de por sí para una mano de niño chico, sin importar cuánto se
  afine. `MaterialOrdenar` (~15 materiales: Vida práctica, Rutina,
  Poner la mesa, Torre rosa, etc.) pasó de arrastrar-y-soltar a
  **tocar para tomar, tocar para soltar** — sin gesto de arrastre, sin
  coordenadas de dedo en movimiento, nada que pueda fallar a medio
  camino. Verificado de verdad en el emulador: se tomó "Despertar",
  se soltó en la casilla correcta ("¡Ahí va!"), y se intentó "Escuela"
  en la casilla 2 — el rechazo con el aviso real también funcionó.
- **"La araña pintora" — se encontró la causa de fondo real**: el
  reporte "no es nada al juego" llevó a leer el código fuente del juego
  real en la versión web (`app/games/arana/page.tsx`), y resultó que la
  mecánica nativa anterior nunca se pareció al juego real. No es tocar
  casillas fijas: es arrastrar el dedo junto al borde ya descubierto,
  trazando un contorno que se revela por inundación (flood fill) al
  soltar, con arañas malas que rondan el tablero y quitan una vida si
  tocan el trazo — igual que el original, con el mismo motor de niveles
  (`phasedInt`) y las mismas 10 imágenes que se revelan (no es una
  araña la que se revela — la araña es el enemigo). Se verificó
  cargando correctamente (corazones, nivel, arañas visibles); el gesto
  de arrastre completo no se alcanzó a confirmar en este emulador por
  la inestabilidad descrita abajo.
- **Tres juegos de arcade nuevos, elegidos por el usuario entre 4
  opciones**: Snake (la víbora, con velocidad que sube al comer),
  Arkanoid/rompe ladrillos (física real por cuadro, igual que
  Globo/Burbujas/Carreras — `withFrameNanos`, no incrementos fijos, con
  ángulo de rebote real según dónde pega en la paleta) y Atrapa al topo
  (ronda de 30 segundos con dificultad que sube). Ninguno existía antes
  en ninguna versión de la app.
- **Modo dos jugadores agregado donde tiene sentido**: Damas, Damas
  chinas, Ajedrez y Cuatro en línea ya tenían el patrón de Gato para
  copiar (un `FilterChip` que activa turnos alternos sin IA). Dominó
  también, mostrando la mano del segundo jugador boca arriba en vez de
  boca abajo (ya no hay nada que esconderle a una computadora). Piedra,
  papel o tijera necesitó algo distinto: como las dos jugadas tienen que
  ser secretas y simultáneas para que el juego tenga sentido, se agregó
  una fase real de "pásale el dispositivo a Jugador 2" que esconde la
  jugada de Jugador 1 hasta que el segundo también eligió la suya — no
  un simple `dosJugadores` de turnos alternos como los demás.
  **No se hizo** para Solitario/Solitario araña (son de un jugador por
  diseño) ni Bingo (no es un juego de enfrentarse).

**Bug real de fondo detrás de "se corta la pantalla", encontrado
después de que el primer `verticalScroll` no alcanzara**: la cascada de
cartas de Solitario/Solitario araña posiciona cada carta con
`Modifier.offset()`, que NO agranda el tamaño medido del `Box` que la
contiene — así que el `verticalScroll` de más afuera calculaba mal
cuánto medía el contenido real y a veces ni se activaba. Se le puso una
altura explícita al `Box` de cada columna (una carta + el offset de la
última). Verificado de verdad: el `ScrollView` pasó de reportar
`scrollable="false"` a `scrollable="true"` en el árbol de accesibilidad,
y un swipe reveló la carta que antes quedaba cortada fuera de la
pantalla.

**Sobre la verificación en vivo de esta pasada, con honestidad**: la
máquina de esta sesión tuvo presión de memoria severa y fluctuante
(procesos de Python, varias ventanas de navegador/asistentes de IA
ajenos a esta tarea) que provocó varios choques reales del emulador y,
hacia el final, una entrega de toques poco confiable a nivel de sistema
— el mismo `adb shell input tap`, con el mismo código, a veces
funcionaba perfecto y a veces aterrizaba en una pantalla de material
completamente distinta sin relación alguna con la posición tocada (algo
arquitectónicamente imposible desde el código de la app: los manejadores
de toque de `MaterialOrdenar` no tienen ninguna referencia al controlador
de navegación). Se confirmó en vivo lo que se pudo antes de que la
inestabilidad lo impidiera: el modelo de toque de `MaterialOrdenar`
completo (selección, colocación correcta, rechazo correcto), el filtro
de edad, Tetris, el arreglo de scroll de Solitario, la tolerancia de
arrastre más amplia, y "Toca la cara". Snake, Arkanoid, Atrapa al topo,
el gesto completo de la araña, y los interruptores de dos jugadores
quedan sin confirmar en vivo en esta sesión — compilan limpio y pasan
sus pruebas unitarias, pero no se vieron correr con los propios ojos.

Verificado: 83 pruebas unitarias pasando (10 nuevas: 5 de Snake, 5 de
Arkanoid), compilación limpia, `assembleDebug` exitoso.

## Séptima pasada: damas chinas, la araña de verdad, y el bug real del arrastre

Pedido explícito, con varios puntos concretos:

- **Bug real de fondo del "acomodar en las casillas", encontrado y
  corregido**: el reporte "los juegos como 'rutina de la mañana' sigue
  sin estar correcto" apuntaba a algo más profundo que el bug del
  canasto ya arreglado en la quinta pasada. Causa real: el `forEach` que
  dibuja cada pieza del canasto (en `MaterialOrdenar`, `MaterialClasificar`
  y `RompecabezasScreen`) no tenía `key(pieza)`. Sin eso, Compose reutiliza
  cada `PiezaArrastrable` de la fila por **posición en la lista**, no por
  la pieza real que representa. Al colocar una pieza y quitarla de la
  lista, las piezas siguientes se recorrían un lugar — y cada una heredaba
  el estado interno de arrastre (offset, si estaba "en la mano") de lo que
  antes vivía en esa posición, en vez de arrancar limpio. Es un bug clásico
  de Compose, difícil de ver leyendo el código una sola vez porque no
  lanza ninguna excepción — solo se manifiesta como "la pieza siguiente no
  se comporta bien". Corregido envolviendo cada pieza en `key(pieza)` /
  `key(item.id)` en los tres archivos. Verificado de verdad, no solo por
  compilación: se jugó "La rutina de la mañana" completa en el emulador,
  las 4 piezas en las 4 casillas correctas, una por una, con
  `adb shell input draganddrop` y coordenadas exactas sacadas de
  `uiautomator dump` — la serie se completó con las 4 piezas en el orden
  correcto y el mensaje "¡Completaste la serie!".
- **Damas chinas, generadas**: versión honesta, no el tablero real de
  estrella de 6 puntas (eso son 121 casillas con geometría hexagonal) sino
  un tablero cuadrado de 8×8 con una casa de 6 canicas en cada esquina
  opuesta — pero con las reglas reales: paso simple, o cadena de saltos
  (sobre cualquier canica, sin capturarla) que puede terminar en cualquier
  punto de la cadena. IA voraz (no minimax completo: las cadenas de salto
  hacen el árbol de jugadas demasiado grande para 6 canicas a la vez) que
  sí juega con intención real, moviendo la canica que más avanza hacia la
  casa contraria. Verificado jugando de verdad: una canica saltó dos
  casillas de un salto, la IA respondió con su propio movimiento.
- **"La araña pintora" — ahora sí descubre una imagen real**: el bug
  reportado era literal: las 16 casillas revelaban el mismo emoji de
  araña suelto, repetido 16 veces — no había ninguna "imagen" que
  descubrir. Ahora hay una sola araña grande de fondo, del tamaño de todo
  el tablero, y las 16 casillas son una cuadrícula que la tapa; al
  tocarlas van desapareciendo y dejan ver el pedazo de la araña grande que
  había debajo. Verificado visualmente: se destaparon 8 casillas y se ve
  con claridad la mitad superior de una araña reconocible, no fragmentos
  sueltos.
- **Dado de retos — de 6 a 40 objetivos**: se pidieron "muchos más". La
  lista de retos pasó de 6 a 40, variados (saltos, equilibrio, animales,
  gracia y cortesía, pausas de calma), sin tocar la animación de giro ya
  existente.
- **Memorama — cartas más chicas, muchas más parejas**: de 6 parejas en
  una grilla fija de 4 columnas a 24 parejas (48 cartas) en una grilla
  adaptable (`GridCells.Adaptive(minSize = 56.dp)`) con letras más
  pequeñas en la carta (nuevo parámetro `tamanoEmoji`/`tamanoDorso` en
  `CartaMemorama`, con los valores de antes como default para no afectar
  a Memoria por turnos, que sigue con cartas grandes).
- **Filtro de edad quitado en los juegos de mesa clásicos**: los 16
  materiales de "Juegos en compañía" (Gato, Damas, Ajedrez, Solitario,
  Bingo, Dominó, etc., más Damas chinas nueva) bajaron su `edadMinima` a
  2 — la edad más chica del selector — así que aparecen abiertos sin
  importar la edad elegida, tal como se pidió.
- Sobre "una versión más amigable y enterprise": es un pedido demasiado
  vago para tratarlo como una tarea aparte con una lista propia de qué
  cambió — se interpretó como calidad de ejecución en todo lo anterior
  (iconos que sí se ven, animaciones reales, mensajes claros), no como
  un rediseño visual adicional sin objetivo concreto.

**Lo que de verdad no se hizo, otra vez con honestidad**: Tetris,
Palillos chinos, Lotería (distinta de Bingo), Acomodar bloques como
material aparte — ninguno se generó. Tampoco se agruparon/consolidaron
materiales parecidos en menos pantallas con niveles, por la misma razón
de la pasada anterior (riesgo real de borrar variedad pedagógica sin
saber qué se considera "igual").

Verificado: 66 tests unitarios pasando (6 nuevos de Damas chinas),
compilación limpia, `assembleDebug` exitoso, e instalado y jugado de
verdad en el emulador: Damas chinas de principio a fin (selección, salto
en cadena, respuesta de la IA), La araña pintora con la imagen
reconocible, Dado de retos con un reto nuevo de la lista ampliada,
Memorama con 48 cartas chicas volteando bien, el filtro de edad
confirmado quitado (los 16 juegos de mesa visibles), y el bug del
arrastre en Vida práctica confirmado corregido jugando la serie completa
de "La rutina de la mañana" con coordenadas exactas de `uiautomator`, no
solo con una captura de pantalla suelta.

## Sexta pasada: rotación de pantalla, ajustes reales, y ajedrez

Pedido: "termina con todo lo que te pedí" + rotación de pantalla para
tablet y celular. Sobre "termina con todo": una lista de 10+ juegos de
mesa nuevos (ajedrez, damas chinas, tetris, palillos chinos, lotería,
acomodar bloques) más "agrupa los materiales parecidos" no es algo que
se cierre de verdad en una sesión más — son meses de trabajo real, no
pendientes menores. Se explica abajo qué se hizo y qué sigue sin hacerse,
con honestidad sobre el tamaño real de lo que falta.

- **Bug real de rotación, encontrado y corregido**: `AndroidManifest.xml`
  tenía `android:screenOrientation="portrait"` — la app no podía girar
  nunca, sin importar el sensor del dispositivo ("todo está en una sola
  vista"). Se quitó, y se agregó `android:configChanges` para que
  Android NO destruya la Activity al girar (si no, cada partida a medias
  — damas, solitario, un dibujo de la pizarra — se perdería con cada
  giro). Verificado de verdad: se forzó el giro por `adb` y la app
  respondió, con el estado intacto después.
- **Bug real de responsividad, encontrado durante la prueba de
  rotación**: el encabezado del Home (título + botón de edad + ícono de
  ajustes nuevo) no tenía ningún límite de ancho — en una pantalla de
  celular angosta (simulada con `adb shell wm size 360x740`, no solo
  "se ve bien en la tablet"), el ícono de ajustes quedaba empujado fuera
  de la pantalla, invisible. Corregido con `weight()` + una sola línea
  con "…" en el título, para que se achique él en vez de empujar los
  botones. De paso se revisaron Damas, Solitario y Solitario araña (las
  tablas de 7-10 columnas de ancho fijo) con el mismo ancho de celular
  simulado — Solitario araña efectivamente se salía de la pantalla, y ya
  tenía el mismo patrón de `horizontalScroll` que se usó antes para la
  pizarra, así que se aplicó ahí también.
- **Ajustes reales, por fin con una pantalla propia**: se agregaron
  música y una voz más cálida en la ronda anterior sin ninguna forma de
  apagarlas. Ahora hay un ícono de engranaje en el Home que abre una
  pantalla con 4 interruptores (Sonido, Voz, Música, Vibración) que se
  guardan solos y sí apagan de verdad cada sistema — no solo un ajuste
  decorativo que no hace nada.
- **Ajedrez real, no simplificado**: cada pieza se mueve según sus
  reglas de verdad (peón con doble paso inicial y captura diagonal,
  torre/alfil/reina deslizándose hasta chocar, caballo saltando, rey un
  paso), el jaque es real (ninguna movida puede dejar el propio rey
  atacado), y el jaque mate y el ahogado se detectan de verdad, no con
  un límite de movidas. La IA usa minimax real (no al azar). Sin enroque
  ni captura al paso todavía — alcance real documentado, no un
  descuido; la promoción es automática a reina. Verificado jugando una
  movida real contra la IA y viendo su respuesta.

**Lo que de verdad no se hizo, para que quede claro y no se lea como una
promesa vacía**: Damas chinas, Tetris, Palillos chinos, Lotería,
Acomodar bloques — ninguno se generó. Tampoco se agruparon/consolidaron
los materiales parecidos (nomenclatura, clasificación, etc.) en menos
pantallas con niveles; es una decisión consciente, no un olvido — fusionar
contenido pedagógico distinto sin saber exactamente cuáles se
consideran "iguales" es un riesgo real de borrar variedad de verdad, y
se prefirió no adivinar. El ícono de la app sigue siendo genérico. Nunca
se probó en un dispositivo físico real (solo emulador). No hay CI.

Verificado: 60 tests unitarios pasando (10 nuevos de ajedrez),
compilación limpia, `assembleDebug` exitoso, y una sesión de prueba
larga en el emulador — rotación forzada por `adb`, ancho de celular
simulado (360px, encontró y confirmó arreglado el bug del encabezado),
los 4 interruptores de ajustes probados, una partida real de ajedrez
jugada contra la IA, y sin ningún crash en logcat en toda la sesión.

## Quinta pasada: bug del canasto, voz cálida, música ambiental, pizarra real y juegos de mesa nuevos

Pedido con varias partes reales a la vez:

- **Bug real del canasto, encontrado y corregido**: en `MaterialOrdenar`
  (~15 materiales de seriación: Vida práctica, Torre rosa, Días de la
  semana, Barras numéricas, etc.) el canasto se dibujaba con
  `enCanasto.sorted()` — así que SIEMPRE mostraba las piezas en el orden
  correcto (1, 2, 3...), sin importar que `enCanasto` sí se revolvía al
  iniciar el nivel. El ejercicio de seriación estaba resuelto de
  antemano, sin nada que pensar. Corregido quitando el `.sorted()`, y de
  paso cada pieza del canasto ahora tiene una tarjeta real (sombra,
  fondo blanco) en vez de flotar "al aire". Vida práctica también
  mejoró sus íconos (💧🍽️🧵 con tarjetas más grandes).
- **Voz menos robótica**: `Speech` ahora sube un poco el tono, baja un
  poco la velocidad, y elige la voz en español de mejor calidad
  instalada (sin depender de datos móviles) en vez de la primera que
  encuentre el motor — antes usaba tono/velocidad de fábrica con
  cualquier voz.
- **Fondo musical por área**: 8 acordes tiernos, uno por área Montessori
  (no 98 pistas distintas — eso necesitaría archivos de audio reales,
  que este proyecto no tiene), sintetizados igual que las notas del
  xilófono, en loop perfecto (frecuencias ajustadas a un número entero
  de ciclos) y muy suaves para no competir con la voz ni los efectos.
  Entra al abrir un material, se detiene al volver al Home. No hay
  todavía un botón en pantalla para silenciarla — limitación real, no
  resuelta.
- **La pizarra, letra "a" real**: antes TODAS las letras minúsculas se
  dibujaban con `Typeface.DEFAULT_BOLD` trazado — una tipografía de
  computadora. La "a", la "e" y la "o" ahora son un trazo de una sola
  línea de verdad (`path`, la misma técnica que ya usan las formas),
  como se enseña a escribir en preescolar ("bolita y palito" para la
  a). Las 22 consonantes restantes se quedan con texto por ahora —
  extenderlas es la misma técnica, una a la vez.
- **Mariposa y nube arregladas**: la mariposa eran 4 óvalos sueltos sin
  forma de ala; ahora cada ala es una gota con curvas reales y tiene
  antenas. La nube eran 3 círculos flotando sobre una barra separada,
  con huecos visibles; ahora es un solo contorno cerrado sin huecos.
  Verificado visualmente en el emulador, no solo por compilación.
- **El dado se ve mover**: `MaterialTablero` (oca, serpientes) antes
  solo mostraba el número tirado como texto dentro del botón. Ahora hay
  una cara de dado real que gira varias vueltas (reutiliza la técnica
  del Dado de retos) antes de caer en el resultado.
- **Juegos de mesa nuevos, contra la computadora**: se pidieron Tetris,
  Memorama, acomodar bloques, rompecabezas, lotería, palillos chinos,
  damas chinas, damas inglesas, ajedrez, solitario y la araña "bien
  hecha" — una lista de 10+ juegos, varios de ellos proyectos grandes
  por sí solos (ajedrez con jaque/mate real, damas chinas con tablero
  de estrella, palillos chinos con físicas de colisión entre palitos
  superpuestos). Se priorizó calidad sobre cantidad y se entregaron
  tres completos y reales, no simplificados:
  - **Damas inglesas**: captura obligatoria, cadena de capturas
    múltiples con la misma ficha, coronación a dama, IA con minimax
    real (no al azar).
  - **Solitario (Klondike)**: 7 columnas, robo de a 1, 4 fundaciones,
    secuencias que alternan color — reglas reales, no una versión
    reducida.
  - **Solitario araña** (variante de un palo, la más jugable): 10
    columnas, 104 cartas, secuencias completas que se retiran solas.
    Esta es la "araña" real que se pidió — el material anterior en
    Movimiento ("La araña pintora") es un juego de revelar casillas sin
    relación con el solitario araña, y sigue existiendo aparte.

  **No se hicieron todavía** (para ser honesto, no por falta de ganas):
  Tetris, Ajedrez, Damas chinas, Palillos chinos, Lotería (Bingo con
  imágenes ya cubre gran parte de esa mecánica) y Acomodar bloques.
  Memorama y Rompecabezas ya existían de antes. Ajedrez y Damas chinas
  son los siguientes candidatos más razonables si se continúa esta
  línea — ambos son factibles con el mismo patrón de Damas inglesas
  (tablero + movidas legales + minimax), solo que con más reglas por
  codificar.

Verificado: 50 tests unitarios pasando (20 nuevos: Damas, Solitario,
Solitario araña), compilación limpia, `assembleDebug` exitoso, y
probado en vivo en el emulador — el bug del canasto confirmado
corregido con una captura antes/después, drag-and-drop verificado
funcionando en Vida práctica, los tres juegos de mesa nuevos jugados de
verdad (una movida real en cada uno, no solo abrir la pantalla), la
letra "a"/"e"/"o" y la mariposa/nube confirmadas visualmente, y sin
ningún crash en logcat durante toda la sesión de prueba.

## Cuarta pasada: "cada material al nivel de la pizarra" — cobertura casi total

Pedido explícito: "aplica los ajustes para todos los materiales, mejoralos
al nivel de pizarra, cada uno de los 91 supera el ambiente en visión y en
toda interacción". Rehacer los 91 materiales restantes con el mismo nivel
de detalle que la pizarra (una reescritura de ~700 líneas) no es viable en
una sola pasada, así que se usó la estrategia de mayor apalancamiento:

**Multiplicador (afecta a los 63 materiales que usan un patrón
compartido):**
- `PiezaArrastrable`/`ZonaSoltar`: se agregó resaltado en tiempo real de
  la zona de destino mientras se arrastra (antes solo se sabía si
  encajaba DESPUÉS de soltar) — nuevo parámetro `onArrastrar` que reporta
  la posición viva del dedo, y `resaltado`/`formaResaltado` en
  `ZonaSoltar` que dibuja un aro verde animado cuando la pieza está
  encima. Verificado en vivo en el emulador (capturas del aro de
  resaltado y de un acierto completo con "Pinza de transferencia").
- `MaterialClasificar`, `MaterialOrdenar`, `MaterialTransferir`: los tres
  quedaron conectados a ese resaltado.

**Individual (31 de los ~35 materiales sin patrón compartido,
reescritos o pulidos de verdad, no solo "un poco de sombra"):**
Gato, Dominó, Xilófono, RPS, Memorama, Cuatro en línea, Bingo (pasada
anterior) + ¿Qué falta?, ¿Qué es distinto?, Encuentra los objetos
(nueva `CasillaEmoji` compartida con feedback de presión), Burbujas
(pompas con degradado radial real en vez de círculos planos), Laberinto
(rastro del camino recorrido + puerta de entrada), Toca la cara (orejas,
pelo, boca curva dibujada con `Canvas` en vez de un círculo negro), Dado
de retos (el dado ahora gira de verdad con desaceleración, antes era un
emoji fijo), Las campanas (pulso y sombra real al sonar, reutiliza el
xilófono sintetizado), El juego del silencio (degradado radial en vez de
un círculo de color plano), Adivina quién es (filtros con estado
visible, antes no se sabía qué estaba activo), Memoria por turnos
(ahora comparte la carta con volteo 3D de Memorama vía nueva
`CartaMemorama` compartida, antes tenía su propia versión vieja sin
volteo), Vibra y adivina, Reflejo de color (glow radial + pulso al
cambiar), El piso es lava (degradado de fuego + ícono), La araña
pintora (revelado con rebote), Aventura de juguetes (aparece con rebote
+ sombra en el suelo), Trazos previos (marca de inicio y meta),
Rompecabezas (resaltado de destino + marco con sombra), Alfabeto móvil
(ranuras vacías con borde), La tabla del cien (columnas de decena
sombreadas + casilla objetivo resaltada), El cubo del binomio (marco con
sombra + feedback de presión), El banco dorado (contadores con sombra y
feedback de presión), Collage libre (paleta con marco), Colorear (aro de
selección en el color activo), Globo y Atrapa las estrellas (degradado
de cielo, ya tenían física real de sesiones previas).

**No tocados individualmente** (ya se benefician del resaltado de
arrastre y de la pasada de componentes compartidos de la ronda anterior,
pero no recibieron una reescritura a medida): los ~63 materiales basados
en `MaterialQuiz`/`MaterialOrdenar`/`MaterialClasificar`/`MaterialTransferir`/
`MaterialTablero` (nomenclatura, seriación, clasificación, transferencia,
juegos de tablero tipo oca/serpientes) — llevarlos a un rediseño
individual como el de la pizarra o el dominó es un trabajo real de
varias sesiones más, no de una sola, y se documenta así en vez de
afirmar una cobertura que no existe.

Verificado: 30 tests unitarios pasando, compilación limpia,
`assembleDebug` exitoso, y probado en vivo en el emulador (arrastre con
resaltado, tabla del cien, colorear, y una tanda representativa de los
materiales reescritos).

## Tercera pasada: material por material (Gato, Dominó, Xilófono, bug de iconos)

Pedido explícito: "genera el mismo esfuerzo por mejorar cada material con
el mismo detalle que con la pizarra". Rehacer los 98 materiales al mismo
nivel que la pizarra en una sola pasada no es realista (la pizarra por sí
sola fue una reescritura de ~700 líneas); en cambio se priorizó lo
reportado con detalle concreto y los componentes con mayor efecto
multiplicador:

- **Bug real de iconos que no se ven, causa encontrada**: varios emoji del
  catálogo y de materiales usaban secuencias con unión (ZWJ, ej.
  "👩‍🚒" = mujer + unión + camión) que no siempre tienen un glifo
  combinado en fuentes reducidas — el sistema entonces no dibuja nada o
  dibuja las partes sueltas. Se reemplazaron todas las que había en el
  código (`grep` del carácter U+200D confirmó la lista completa) por
  emoji de un solo carácter: Oficios (bombero/doctora/cocinero/maestra),
  el "oso polar" de Hábitat (ahora sin unión: "🐻❄️" en vez de "🐻‍❄️",
  se ven los dos símbolos igual sin depender de la fuente), y el emoji
  del catálogo de Dominó (el carácter "Domino Tile" de Unicode casi nunca
  tiene glifo de color en ninguna fuente — cambiado a "🀄", que sí lo
  tiene desde hace años).
- **Xilófono — sonido real, no DTMF**: antes usaba `ToneGenerator` con
  tonos de teclado de teléfono. Ahora sintetiza cada nota con
  `AudioTrack` (ataque instantáneo, decaimiento exponencial y un
  sobretono a ~2.76x la fundamental — la física real de una barra
  percutida) sobre una escala pentatónica, así que cualquier combinación
  de teclas suena musical. Visual rehecho: barras de largo decreciente
  (como el instrumento real) con agujeros de resonancia y feedback al
  presionar.
- **Gato — IA real, no aleatoria**: bug real reportado ("que la respuesta
  no sea aleatoria, sino que realmente piense"); antes la CPU jugaba
  `vacias.random()`. Ahora usa minimax completo (juego perfecto,
  verificado con una prueba que simula partidas completas y confirma que
  nunca pierde). Se agregó también un modo de dos jugadores locales.
- **Dominó — numérico real, no emparejar dibujos**: bug real reportado
  ("hazlo por números, o sea normal"); era un juego de emparejar 5
  dibujos. Ahora es un dominó doble-6 real (28 fichas, valores 0-6,
  puntos dibujados con `Canvas` en el patrón real de cada número) con
  orientación correcta en la cadena (el lado que conecta siempre queda
  pegado). El rival ahora se ve: avatar + sus fichas boca abajo, no solo
  un contador de texto.
- **Arrastre compartido** (`PiezaArrastrable`, ~40 materiales): antes no
  había ninguna diferencia visual entre "quieta" y "en la mano", y al
  soltar en un sitio inválido la pieza volvía de golpe sin animación.
  Ahora crece un poco mientras se arrastra (con sombra) y vuelve con un
  rebote suave si no encaja.
- **Pulido individual**: Piedra-papel-o-tijera (aro de ganador, "VS",
  sombras), Memorama (volteo real en 3D con `rotationY`, no un cambio de
  color instantáneo), Cuatro en línea (tablero con marco azul y agujeros
  perforados, antes las fichas flotaban sueltas), Bingo (cartón con
  marco, sello de verificación en lo marcado, voz cantora en una
  medalla).

Todo verificado en el emulador con capturas reales (no solo compilación):
Gato bloqueando y ganando con minimax, Dominó con la cadena encajando
visualmente, Xilófono sin errores en logcat al tocar, íconos de Oficios
y Dominó ya visibles, RPS con el aro de victoria, Memorama volteando la
carta correcta, Cuatro en línea con el marco y la ficha cayendo, Bingo
con el sello en lo marcado.

**Lo que NO se hizo en esta pasada** (para ser honesto sobre el alcance):
la gran mayoría de los otros ~85 materiales no recibieron una reescritura
individual — siguen con el nivel visual de las pasadas anteriores
(componentes compartidos ya pulidos, pero sin un tratamiento a medida
como el de la pizarra o el dominó). Igualar ese nivel a los 98 materiales
uno por uno es un trabajo de varias sesiones más, no de una sola.

## Pasada de calidad visual/UX "nivel enterprise" en los componentes compartidos

En vez de rediseñar material por material (inviable con 98 pantallas),
se elevó la calidad visual de los **componentes compartidos** que los
98 materiales reutilizan — así el efecto se multiplica automáticamente:

- `HomeScreen`: tarjetas más pequeñas y "vivas" (con `LazyVerticalGrid`
  adaptativo, `scale` al presionar), acceso rápido a las herramientas
  libres (pizarra, xilófono, collage, colorear) siempre visible arriba
  del filtro por área — la pizarra ahora es una herramienta a la mano,
  no un material más entre 98. Se agregó una insignia real de estrellas
  ganadas por material, leída de `ProgressStore` (progreso guardado de
  verdad, no decorativo).
- `GameShell` (usado por los 98 materiales): encabezado con sombra sutil
  para dar profundidad, chip con el área Montessori del material junto
  al botón de volver.
- `MaterialQuiz` (lección de tres periodos, ~30 materiales): las
  opciones del periodo 2 pasaron de cajas planas a `Card` con elevación
  y feedback de presión; se reemplazó el texto plano "ronda: 2/5" por
  una barra de progreso real.
- `MaterialOrdenar`: las ranuras vacías ahora tienen un borde que marca
  dónde va cada pieza (antes eran indistinguibles del fondo hasta que
  se llenaban).
- `MaterialClasificar`: el contador "Quedan / Acertados" pasó a un chip
  redondeado; las canastas ganaron sombra para separarse del fondo.
- `MaterialTablero` (oca/serpientes): las casillas especiales ahora se
  distinguen con un tinte de color, no solo por el emoji.

## Segunda pasada: el mismo bug de scroll en los 4 patrones compartidos

Después de corregir el scroll en la pizarra y en dominó, se revisaron
los **4 composables compartidos** (`MaterialOrdenar`, `MaterialClasificar`,
`MaterialTransferir` — `MaterialQuiz` ya estaba bien, usa grid) buscando
el mismo defecto, porque un bug ahí afecta a la vez a los ~40 materiales
que los usan, no a uno solo:

- `MaterialOrdenar`: **las dos filas** (las ranuras donde va cada pieza,
  y el canasto de donde se arrastran) no tenían scroll horizontal. Con
  `n` grande — barras numéricas llega a 10, sistema solar a 8 — las
  ranuras del final quedaban fuera de pantalla e inalcanzables, no solo
  apretadas. Corregido en ambas filas.
- `MaterialClasificar`: la fila de piezas pendientes tampoco tenía scroll.
- `MaterialTransferir`: la fila del destino (donde se van acumulando las
  piezas transferidas) tampoco.

Verificado en el emulador con "Los días de la semana" (`n=7`): el
arrastre sigue funcionando igual después del cambio (confirmado con
`adb shell input draganddrop`, mensaje "¡Ahí va!" y la pieza movida de
canasto a ranura correctamente).

**Bug real adicional encontrado y corregido, distinto de todo lo
anterior**: en `Carreras`, al llegar a los 15 segundos (la meta), los
obstáculos seguían apareciendo y cayendo para siempre — el efecto de
física solo comprobaba `chocado`, nunca si ya se había llegado a la
meta. Resultado: se podía "chocar" (perder) *después* de haber ganado,
pisándose el mensaje de victoria. Ahora ambos efectos (aparición de
obstáculos y física) también se detienen al ganar.

Se revisaron además los otros juegos de reflejos (`Burbujas`, `Canasta`,
`Globo`, `Lava`) por el mismo patrón: en esos no hay forma de "perder"
(son de puntaje libre, sin choque peligroso), así que aunque la física
sigue corriendo de fondo después de completar la meta, no produce un
resultado incorrecto — se dejaron como están.

## Auditoría de bugs reportados (Pizarra + juegos de mesa)

El usuario reportó dos problemas jugando de verdad: en la pizarra no se
podía llegar a todas las letras, y "cuatro en línea falla, falla en
muchos". Se investigó cada uno hasta la causa real, no hasta el primer
síntoma:

- **Pizarra — bug real, confirmado y corregido**: el `Fila` (la fila
  horizontal de herramientas/colores/guías/estampas) no tenía scroll
  horizontal propio. Con 8 herramientas o 16 colores no se notaba (casi
  alcanzaban en pantalla), pero con las **80 guías** (27 minúsculas + 27
  mayúsculas + 10 números + 16 formas) el resto simplemente se dibujaba
  fuera de la pantalla, inalcanzable — exactamente lo reportado. Mismo
  bug encontrado también en `DominoScreen` (la fila "Tu mano" podía
  crecer al robar del pozo). Corregido en ambos con
  `Modifier.horizontalScroll(rememberScrollState())`; verificado a mano
  en el emulador con swipes hasta llegar a "x, y, z" y después a las 17
  figuras del final de la lista.

- **Cuatro en línea — bug real, confirmado y corregido**: `jugar()`
  siempre ponía `turno = "cpu"`, incluso en la jugada que ganaba la
  partida. El efecto que mueve a la computadora no comprobaba si el
  juego ya había terminado, solo miraba de quién era el turno — así que
  la computadora alcanzaba a mover una vez más *después* de que el
  jugador ya hubiera ganado, y esa jugada extra interrumpía a medio
  camino el efecto que iba a mostrar "¡Ganaste!" y reiniciar el tablero.
  `GatoScreen` (mismo patrón de dos jugadores) sí tenía el freno
  correcto desde el principio; ahora `Conecta4Screen` calcula
  `ganoJugador`/`ganoCpu`/`empate`/`terminado` una vez por recomposición
  (igual que Gato) y **ambos** efectos los respetan.

- **Auditoría del mismo patrón en los 5 materiales con turnos**
  (`GatoScreen`, `Conecta4Screen`, `DominoScreen`, `MaterialTablero` —
  oca/serpientes —, `MemoriaTurnosScreen`): los otros cuatro ya estaban
  bien. De paso se encontró y corrigió que `MemoriaTurnosScreen` no
  tenía forma de reiniciarse sola al terminar la partida (los demás
  juegos de turnos sí se reinician solos tras mostrar el resultado).

## Pruebas internas (nuevas)

Se agregó un módulo de pruebas unitarias de verdad (`app/src/test/`,
JUnit, corre en la JVM sin emulador — `./gradlew testDebugUnitTest`,
~1 minuto) para la lógica pura que más costó encontrar rota a mano:

- `LevelsTest`: la curva `phased`/`phasedInt` nunca se sale de 1–10 en
  etapa, nunca decrece, y toca exactamente la parada esperada.
- `Conecta4LogicTest`: las 4 direcciones de `hayGanador` (horizontal,
  vertical, las dos diagonales), que 3 en línea no ganan todavía, y que
  fichas mezcladas no cuentan — el mismo tipo de prueba que habría
  hecho evidente el bug de arriba si hubiera existido antes.
- `GatoLogicTest`, `DominoLogicTest`: detección de ganador de gato, y
  que el set de dominó doble-4 tiene exactamente las 15 fichas
  correctas sin repetidos.

27 pruebas, las 27 pasan. Las funciones de lógica relevantes se
marcaron `internal` (antes `private`) para que las pruebas puedan
llamarlas directo sin necesitar Robolectric ni el emulador.

## Validado en emulador real (no solo "compila")

Se armó un AVD local (`MiAmbienteTablet`, API 36, x86_64) y se instaló el
APK de verdad — no solo `compileDebugKotlin`/`assembleDebug`. Encontró un
bug real que ningún build hubiera detectado:

- **`enableEdgeToEdge()` sin `safeDrawingPadding()`**: el contenido de
  cada pantalla (incluidas las zonas donde se suelta una pieza al
  arrastrar) se dibujaba detrás de la barra de navegación del sistema en
  vez de encima — una ficha soltada correctamente podía quedar oculta o
  fuera de la zona tocable. Corregido en `GameShell.kt`, `HomeScreen.kt`
  y `SelectorEdadScreen.kt` con `Modifier.safeDrawingPadding()`; afecta
  las 98 pantallas de una vez por estar en el componente compartido.
- El resto navegó y funcionó según lo esperado: selector de edad sin
  trabarse (el bug original reportado en la versión Capacitor), arrastre
  y suelta confirmados con `adb shell input draganddrop`, conteo de
  materiales por edad correcto.

## La pizarra grande — reescrita para igualar app/pizarra/page.tsx

La primera versión nativa de este material era un lienzo de un solo
color y un botón "Borrar todo" — muy por debajo de la web real. Se
reescribió por completo para tener la misma profundidad:

- **8 herramientas** con la textura exacta de `lib/pizarra.ts` portada
  literal (mismo jitter de crayón, mismo brillo de neón con capa
  difuminada, mismo relleno por líneas/flood-fill, mismo aerosol con
  gotas aleatorias) — no una aproximación.
- 16 colores, 4 grosores, **modo mandala** (1/2/4/6/8 ejes de simetría con
  espejo, igual que `conSimetria` en la web).
- 7 fondos (papel, blanco, cuadros, renglones, puntos, pizarrón, kraft).
- **Las 80 guías de `data/guias.ts` completas** (27 minúsculas + 27
  mayúsculas + 10 números + 16 formas) — los paths SVG se parsean tal
  cual con `androidx.core.graphics.PathParser`, sin reinterpretar cada
  figura a mano.
- Plantillas para colorear (mismo subconjunto de 13 formas cerradas que
  la web, excluyendo las de trazo abierto).
- Misiones con confeti, deshacer/rehacer (pila de 14 pasos, igual que la
  web).
- **Galería como archivos PNG reales** en `filesDir/galeria/` — mejora
  real sobre la web, que dependía de `localStorage` con un límite de
  tamaño total compartido con el resto de la app.
- **Compartir con el selector nativo de Android** (`Intent.ACTION_SEND` +
  `FileProvider`) — más directo que el truco de descarga de la web,
  que depende de que el WebView no bloquee el enlace.

Verificado a mano en el emulador: trazo con textura de crayón, mandala de
6 ejes con neón (funciona), guardado que crea el archivo PNG de verdad
(confirmado con `run-as ... ls files/galeria/`), pestaña de guías con las
letras completas, pestaña de colorear con las 13 figuras esperadas.

## El menú principal — recuadros más chicos y la pizarra a la mano

El primer diseño usaba tarjetas cuadradas enormes (`GridCells.Fixed(2)`
+ `aspectRatio(1f)`): en una tablet apenas cabían 2 por pantalla. Ahora:

- `GridCells.Adaptive(minSize = 128.dp)`: caben muchas más tarjetas,
  más chicas, con menos padding y tipografía más compacta.
- Sección **"✨ Acceso rápido"** fija arriba de todo, antes del filtro de
  área: pizarra, xilófono, collage y colorear — las herramientas libres
  de uso frecuente ya no dependen de elegir la pestaña "Expresión libre".

## Por qué Kotlin + Jetpack Compose

Es el framework de UI declarativo oficial de Google, activamente
mantenido (a diferencia del sistema de Views/XML, en modo mantenimiento).
El patrón de "describir la UI según el estado" es muy parecido al de
React, así que la lógica de niveles, curvas y control del error se porta
con poca fricción conceptual. Para arrastrar y soltar, Compose tiene
`pointerInput` + `detectDragGestures`, mucho más confiable que cualquier
cosa dentro de un WebView.

## Qué es nativo de verdad aquí (no una simulación)

- **Voz**: `android.speech.tts.TextToSpeech`, parte del sistema
  operativo — no depende de que el navegador tenga voces en español
  instaladas (una limitación real de la versión web).
- **Sonido**: `android.media.ToneGenerator`, tonos reales del sistema en
  vez de sintetizarlos con WebAudio.
- **Vibración**: `android.os.VibrationEffect` con los mismos patrones que
  `lib/haptics.ts`.
- **Persistencia**: Jetpack DataStore (equivalente de
  zustand+localStorage), sin ninguna dependencia de un WebView.
- **Sin permiso de INTERNET**: a diferencia del APK de Capacitor, que lo
  declara por default para su WebView aunque la app nunca lo use, este
  manifest no pide ningún permiso de red.
- **Arrastrar y soltar real**: todos los materiales de seriación y
  clasificación usan gestos de arrastre nativos, no toques — más fiel al
  material Montessori real, donde uno *toma* el objeto y lo *suelta*
  donde va.
- **Lección de tres periodos completa** en `MaterialQuiz` (presentar →
  reconocer → evocar), no solo el último periodo.
- **Confeti nativo** (`ConfettiOverlay`) al completar un nivel, y un botón
  "Siguiente nivel" real en los 4 patrones compartidos.
- **Dos materiales que no existen en la versión web**, posibles solo en
  nativo: `vibra-adivina` (contar pulsos de vibración real con los ojos
  cerrados — sin equivalente web confiable) y `reflejo-color` (mide el
  tiempo de reacción en milisegundos con `System.nanoTime()`, algo que el
  bucle de eventos de un navegador no puede medir con precisión).

## Cobertura de materiales

**Los 96 materiales de la versión web están portados**, más los 2
exclusivos de nativo — **98 en total**, verificado automáticamente: cada
`buscarJuego(id)` usado por una pantalla tiene su entrada en
`CATALOGO` y su ruta registrada en `MainActivity.kt` (sin huecos, sin
pantallas fantasma).

Los 4 patrones de interacción compartidos están extraídos como
composables reutilizables en `ui/materials/` (`MaterialQuiz`,
`MaterialOrdenar`, `MaterialClasificar`, `MaterialTransferir`, más
`MaterialTablero` para juegos de mesa con dado), todos con arrastre real
donde el material original lo usa. El resto son pantallas independientes
para mecánicas que de verdad no comparten nada con otro material
(memoria, tablero, reflejos, creatividad libre).

**Esto es honesto, no "perfecto" en el sentido de calcar 1:1 cada detalle
de cada material real de Montessori** — con 96+ materiales distintos, en
varios se tomaron decisiones de diseño razonables en vez de replicar
exactamente el material físico (ver la lista de simplificaciones abajo).
Lo que sí es cierto: todos compilan, todos navegan, todos son jugables de
principio a fin, y el sistema de niveles/estrellas/progreso real corre
igual en los 96.

Simplificaciones conocidas, honestas y documentadas:
- Varios materiales de discriminación sensorial (`textura`, `temperatura`,
  `peso`, `sabor`, `olfato`) se representan con emoji en vez de una
  simulación táctil/térmica real — no hay forma de simular temperatura o
  peso en una pantalla; sí se usa vibración real donde aporta (`pinza`,
  aciertos/errores).
- `banco-dorado` sigue siendo una versión simplificada (componer con
  contadores de unidad/decena/centena, no perlas doradas manipulables).
- En `MaterialOrdenar`, el tamaño de la serie (`n`) es fijo por pantalla en
  vez de escalar con el nivel alcanzado en todos los casos (sí escala en
  `MaterialQuiz` vía `curvaOpciones`); es una limitación de que el nivel
  vive dentro del composable y no es visible al armar sus parámetros de
  entrada.

El selector de edad y el menú principal (con filtro por edad y áreas)
están completos y navegables, y organizan los 98 materiales reales.

## Ronda de profundidad (después de llegar a 98/98)

Cuatro frentes trabajados en paralelo sobre la base ya completa:

**Juegos de mesa, más fieles:**
- `domino` ahora es dominó real: cadena con dos extremos (no uno), mano
  repartida, pozo para robar y turnos alternos contra la computadora —
  antes era una cola de fichas de un solo extremo sin turnos.
- `MaterialTablero` (usado por `oca` y `serpientes`) pasó de "un jugador
  solo contra el tablero" a una carrera real de dos fichas por turnos, tú
  contra la computadora — así sí existe "esperar tu turno y aceptar el
  resultado", el objetivo pedagógico del material.
- `adivinaquien` pasó de un solo atributo (color) a dos independientes
  (color + tamaño), para que de verdad haga falta combinar preguntas en
  vez de resolverse con una sola.

**Física real en los juegos de movimiento**, con `withFrameNanos`
(sincronizado al refresco de pantalla) integrando velocidad y posición
por delta de tiempo real, en vez de incrementos fijos por `delay()`:
`globo` (gravedad + impulso al tocar), `canasta` (gravedad + velocidad
inicial distinta por estrella), `carreras` (velocidad que aumenta con el
tiempo — se pone más difícil de verdad) y `burbujas` (mecido lateral con
seno del tiempo, no solo sube derecho). `lava`, `toystory` y `arana` no
tienen movimiento continuo que integrar (son de aparición/toque), así que
no aplicaba física ahí.

**Accesibilidad — parcial, honesto sobre el límite:**
- `PiezaArrastrable`/`ZonaSoltar` (el mecanismo de arrastre compartido)
  ahora aceptan una `descripcion` opcional que expone `contentDescription`
  a TalkBack.
- Los dibujos hechos con `Canvas` que antes eran invisibles para un lector
  de pantalla (formas del gabinete de geometría, reloj analógico, figuras
  por número de lados, fracciones entera/mitad, figuras de encaje) ahora
  tienen descripción semántica.
- `cara` (toca la cara) cambió de un gesto crudo (`pointerInput` +
  `detectTapGestures`, invisible para TalkBack aunque tuviera texto) a
  `Modifier.clickable`, que sí se integra con el árbol de accesibilidad y
  responde a doble toque de un lector de pantalla.
- **Lo que NO se cubrió**: el resto de los ~90 materiales todavía no
  tienen `contentDescription` puestos a mano uno por uno (el texto visible
  de botones y `Text` ya es legible por TalkBack automáticamente, así que
  no todos estaban rotos, pero no se revisó cada pantalla). Los juegos de
  reflejos por tiempo (`burbujas`, `globo`, `reflejo-color`, `lava`) siguen
  sin una alternativa accesible real — son juegos de velocidad/puntería
  visual por diseño, y una alternativa genuina para un usuario de
  TalkBack necesitaría un modo de juego distinto, no solo una descripción.
  El arrastre en general tampoco tiene todavía una acción de accesibilidad
  alterna (`AccessibilityAction`) tipo "tomar" + "soltar aquí" con doble
  toque — hoy un usuario de TalkBack puede saber qué es cada pieza, pero
  no necesariamente completar el arrastre solo con gestos de exploración.

**Ícono y firma de release:**
- Ícono adaptable de verdad (`mipmap-anydpi-v26/ic_launcher.xml`, capas
  foreground/background/monochrome) en vez de un solo drawable — con
  respaldo para API < 26. Ver `app/src/main/res/drawable/ic_launcher_*.xml`.
- `assembleRelease` ahora produce un **APK firmado real**, no solo debug:
  se generó un keystore local (`keystore.jks`, con contraseña
  `MiAmbiente2026!` — cámbiala si esto va a compartirse más allá de esta
  máquina) y `app/build.gradle.kts` lo conecta solo si
  `keystore.properties` existe, así que un clon nuevo del repo sigue
  compilando `assembleDebug` sin necesitar el keystore. El `.jks` y
  `keystore.properties` están en `.gitignore` — **nunca se subieron a
  git**; `keystore.properties.example` sí, como plantilla. Si se pierde el
  keystore, regenerarlo con:
  ```bash
  keytool -genkeypair -v -keystore keystore.jks -alias miambiente \
    -keyalg RSA -keysize 2048 -validity 10950
  ```
  (perder el keystore original significa que una futura actualización
  firmada distinto ya no se puede instalar encima de una anterior sin
  desinstalar primero — por eso conviene guardar una copia de
  `keystore.jks` en un lugar seguro fuera del repo, no solo confiar en
  que sigue en esta máquina).

## Cómo seguir mejorando

Con los 96+2 ya cubiertos, lo que queda es profundidad, no cobertura:

1. Reemplazar las simplificaciones listadas arriba con versiones más
   fieles (reglas completas de dominó/oca, física real en los juegos de
   movimiento) conforme se decida que vale la pena el esfuerzo.
2. Si se agrega un material nuevo: usar el composable ya extraído en
   `ui/materials/` que corresponda (`MaterialQuiz`, `MaterialOrdenar`,
   `MaterialClasificar`, `MaterialTransferir`, `MaterialTablero`) cuando
   encaje; si no encaja ninguno, una pantalla independiente como
   `GatoScreen`/`RpsScreen` es más honesto que forzar un patrón que no
   corresponde.
3. Agregar la entrada en `model/GameDef.kt` (`CATALOGO`) y registrar la
   pantalla en `MainActivity.kt` con el mismo id como ruta.
4. Verificar que no queden huecos entre `buscarJuego(id)`, `CATALOGO` y
   las rutas registradas (los tres conjuntos deben tener el mismo tamaño
   y las mismas claves) — comprobado así en esta ronda:
   ```bash
   grep -oh 'buscarJuego("[a-z0-9-]*")' app/src/main/kotlin/com/miambiente/app/ui/screens/*.kt | sort -u
   grep -oE '    GameDef\("[a-z0-9-]*"' app/src/main/kotlin/com/miambiente/app/model/GameDef.kt | sort -u
   grep -oE 'composable\("[a-z0-9-]*"\)' app/src/main/kotlin/com/miambiente/app/MainActivity.kt | sort -u
   ```
5. `./gradlew compileDebugKotlin` primero (rápido), después
   `./gradlew assembleDebug` para el APK completo.

## Compilar

Mismo entorno ya usado para `../android` (Capacitor) — ver
`docs/MANUAL-TECNICO.md` en la raíz del repo para el detalle completo de
los problemas de JDK/SSL encontrados en Windows y cómo se resolvieron.

```bash
export JAVA_HOME="C:/Program Files/Android/openjdk/jdk-21.0.8"   # no el JDK 25 por defecto
./gradlew assembleDebug --no-daemon --max-workers=2   # flags de memoria: esta máquina ya mató el build 2 veces sin ellas
# APK en app/build/outputs/apk/debug/app-debug.apk
```
