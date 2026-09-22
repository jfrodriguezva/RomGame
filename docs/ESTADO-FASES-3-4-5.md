# Estado verificable de las fases 3, 4 y 5

Última actualización: 22 de septiembre de 2026

## Criterio de terminado

Una fase no se considera terminada solo por integrar el motor o mostrar un
menú. Cada familia debe conservar sus modalidades, ejecutar su juego nuevo,
guardar progreso, tener pruebas de reglas y pasar pruebas, lint y recorrido en
emulador. Las pantallas Compose sustituidas se eliminan al alcanzar esa
paridad.

## Fase 3 — migración a LibGDX

Estado: **terminada**.

| Familia normal | Motor | Estado |
|---|---|---|
| Memoria y observación | LibGDX | Migrada, cuatro modos funcionales |
| Percepción sensorial | LibGDX | Migrada: nueve modos con clasificación por arrastre, selección y diferencias |
| Formas y encajes | LibGDX | Migrada: siete modos con quiz, clasificación, encaje, orden, patrón y rompecabezas |
| Orden y secuencias | LibGDX | Migrada: seis modos con orden canónico y arrastre |
| Palabras y sonidos | LibGDX | Migrada: ocho modos de fonética, alfabeto y trazo |
| Construye palabras | LibGDX | Migrada: cuatro modos de construcción, clasificación e inglés |
| Números y cantidades | LibGDX | Migrada: nueve modos de conteo, orden, clasificación y reconocimiento |
| Clasifica el mundo | LibGDX | Migrada: siete clasificaciones temáticas por arrastre |
| Naturaleza y planeta | LibGDX | Migrada: siete modos de reconocimiento, clasificación y orden |
| Personas y comunidad | LibGDX | Migrada: cinco modos de reconocimiento y secuencia |
| Vida práctica | LibGDX | Migrada: seis modos de clasificación y procesos ordenados |
| Taller creativo | LibGDX | Migrada: seis modos de dibujo, color, collage, música y patrón |
| Coordinación y reflejos | LibGDX | Migrada: seis modos de objetivo, arrastre y reacción |
| Laberintos y recorridos | LibGDX | Migrada: dos recorridos con camino y dificultad creciente |

El bloque lógico nuevo comparte infraestructura de render, entrada, selector
de 20 niveles, resultado y persistencia. Orden y secuencias ya distingue torre,
escalera, días, estaciones, ciclo de vida y ciclo del agua, conserva el orden
canónico y permite reordenar por arrastre.

Percepción ya clasifica por arrastre texturas, temperatura, peso, sabores y
olores en destinos propios. Colores, sentidos y sombras conservan selección
visual, mientras Diferencias usa un tablero cuyo número de distractores crece
con el nivel. Formas dibuja geometría dentro de las opciones en vez de depender
solo de etiquetas. También separa clasificación por lados, encaje en tres
orificios, seriación de cilindros, copia del patrón del binomio y rompecabezas
de cuatro piezas con destino exacto.

**Bloque lógico de fase 3 cerrado:** las tres familias abren desde el catálogo,
conservan sus 22 modalidades, guardan progreso y sus reglas están cubiertas por
pruebas.

El bloque de lenguaje incorpora un motor propio: cuestionarios fonéticos,
secuencia progresiva del abecedario, formación letra por letra, clasificación
gramatical por arrastre y trazos táctiles. Conserva las doce modalidades de
las dos familias consolidadas y 20 niveles por modalidad.

El bloque curricular central conserva 34 modalidades en cinco familias. Su
motor distingue conteo táctil, clasificación por arrastre, secuencias
reordenables y selección contextual. Cada modalidad dispone de 20 niveles y
guarda progreso con el identificador de su familia consolidada.

Las 14 familias normales ya cuentan con motor LibGDX. El último bloque añade
lienzo táctil, coloreado, collage por arrastre, teclado musical, patrones,
objetivos con detección estricta, canasta por arrastre y laberintos navegables.
El modo Globo ignora explícitamente cualquier toque fuera del objetivo.

La limpieza final eliminó 86 rutas educativas y 86 pantallas Compose
sustituidas. Permanecen 23 archivos de pantalla: navegación/configuración,
Juegos de mesa y Arcade. Una compilación desde `clean` confirmó que ninguna
pantalla retirada seguía siendo dependencia del producto.

## Fase 4 — potenciación individual

Estado: **terminada**.

- Memoria y observación: tutorial por modalidad, pausa con temporizadores
  detenidos, reinicio y valoración por estrellas.
- Percepción sensorial, Formas y encajes y Orden y secuencias: tutorial, pausa,
  reinicio y valoración por estrellas añadidos; pulido individual en curso.
- Palabras y sonidos y Construye palabras: tutorial por modalidad, pausa,
  reinicio y valoración por estrellas añadidos.
- Números, Clasifica el mundo, Naturaleza y planeta, Personas y comunidad y
  Vida práctica: 34 modalidades con tutorial contextual, pausa, reinicio y
  valoración por estrellas.
- Taller creativo, Coordinación y reflejos y Laberintos y recorridos: tutorial,
  pausa que bloquea gestos, reinicio y valoración por estrellas añadidos.
- Juegos de mesa y Arcade: fuera del alcance de esta fase.

Las 14 familias normales y sus 84 modalidades quedaron potenciadas. Todas
comparten controles de modalidad, nivel, reinicio y pausa, además de tutorial
contextual y resultado con estrellas, sin eliminar la mecánica propia de cada
juego.

## Fase 5 — juegos de mesa

Estado: **siguiente fase; preparación iniciada tras cerrar la fase 4**.

No comenzará hasta cerrar las fases 3 y 4. Comprende seis familias: Alineación
y duelo, Dados y recorridos, Solitarios, Lotería y bingo, Estrategia de tablero
y Deducción y fichas. El criterio incluye reglas completas, drag-and-drop,
alternativa accesible por toque, turnos y pruebas.

El inventario de las 15 modalidades y el criterio de paridad están detallados
en `FASE-5-JUEGOS-DE-MESA.md`. Se conservarán las reglas ya implementadas y la
migración comenzará por Alineación y duelo.

Primer bloque en ejecución: Gato, Cuatro en línea y Piedra-papel-tijera ya
tienen reglas puras y superficie LibGDX con arrastre más alternativa por toque.
Gato fue validado en emulador. Los modos locales, tutorial y pausa ya están
integrados; el bloque no se considera cerrado hasta completar el recorrido de
las tres variantes y retirar sus pantallas sustituidas.

Segundo bloque en validación: Oca, Serpientes y Dado de retos ya usan LibGDX.
Oca fue recorrida con tirada, resaltado y arrastre estricto de la ficha. Las
reglas cubren llegada exacta, casillas especiales y 40 retos distintos.

Tercer bloque en validación: Klondike y Araña trasladaron sus reglas reales a
`game-core` y ya abren una mesa LibGDX con arrastre de cartas y secuencias.
Klondike fue abierto desde el catálogo y se validó el robo al descarte.

## Validación del checkpoint actual

- 148 pruebas unitarias vigentes sin fallos. Dos pruebas del laberinto Compose
  retirado fueron sustituidas por las pruebas de reglas del laberinto LibGDX.
- Android Lint sin errores.
- APK debug generado e instalado.
- Percepción sensorial abierta desde el catálogo en emulador dentro de
  `LogicBlockGdxActivity`, sin excepción fatal ni ANR.
- Binomio y Rompecabezas abiertos y manipulados en emulador, sin excepción
  fatal ni ANR.
- Palabras y sonidos abierto desde el catálogo en emulador con sus ocho modos.
- Números y cantidades abierto desde el catálogo en emulador con sus nueve
  modos; tutorial de Contar y pausa bloqueante validados; APK correcto y sin
  excepción fatal ni ANR.
- Globo validado en emulador: tocar el fondo mantiene 0 puntos y tocar el
  objetivo incrementa el contador; sin excepción fatal ni ANR.
- Taller creativo validado con tutorial, trazo y pausa; Memoria validada con
  tutorial y pausa de temporizadores, sin solapamiento de controles.
- Compilación limpia, lint y APK correctos después de eliminar físicamente las
  pantallas sustituidas; aplicación instalada y abierta en `MainActivity`.
