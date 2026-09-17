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
