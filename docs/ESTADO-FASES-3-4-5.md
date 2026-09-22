# Estado verificable de las fases 3, 4 y 5

Última actualización: 22 de septiembre de 2026

## Criterio de terminado

Una fase no se considera terminada solo por integrar el motor o mostrar un
menú. Cada familia debe conservar sus modalidades, ejecutar su juego nuevo,
guardar progreso, tener pruebas de reglas y pasar pruebas, lint y recorrido en
emulador. Las pantallas Compose anteriores se conservan hasta alcanzar esa
paridad.

## Fase 3 — migración a LibGDX

Estado: **en ejecución**.

| Familia normal | Motor | Estado |
|---|---|---|
| Memoria y observación | LibGDX | Migrada, cuatro modos funcionales |
| Percepción sensorial | LibGDX | Migrada: nueve modos con clasificación por arrastre, selección y diferencias |
| Formas y encajes | LibGDX | Migrada: siete modos con quiz, clasificación, encaje, orden, patrón y rompecabezas |
| Orden y secuencias | LibGDX | Migrada: seis modos con orden canónico y arrastre |
| Las diez familias normales restantes | Compose | Pendientes de migración |

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
pruebas. Las pantallas Compose históricas permanecen temporalmente como
referencia hasta la limpieza final de la fase.

## Fase 4 — potenciación individual

Estado: **en ejecución, detenida hasta cerrar fase 3**.

- Memoria y observación: primer juego terminado en esta fase.
- Las otras trece familias normales: pendientes.
- Juegos de mesa y Arcade: fuera del alcance de esta fase.

## Fase 5 — juegos de mesa

Estado: **no iniciada**.

No comenzará hasta cerrar las fases 3 y 4. Comprende seis familias: Alineación
y duelo, Dados y recorridos, Solitarios, Lotería y bingo, Estrategia de tablero
y Deducción y fichas. El criterio incluye reglas completas, drag-and-drop,
alternativa accesible por toque, turnos y pruebas.

## Validación del checkpoint actual

- 111 pruebas unitarias sin fallos.
- Android Lint sin errores.
- APK debug generado e instalado.
- Percepción sensorial abierta desde el catálogo en emulador dentro de
  `LogicBlockGdxActivity`, sin excepción fatal ni ANR.
- Binomio y Rompecabezas abiertos y manipulados en emulador, sin excepción
  fatal ni ANR.
