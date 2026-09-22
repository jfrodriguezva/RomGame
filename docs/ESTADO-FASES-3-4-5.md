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
| Percepción sensorial | LibGDX | Nueve modos: clasificación por arrastre, quiz y diferencias; pulido visual en curso |
| Formas y encajes | LibGDX | Siete modos conectados con figuras renderizadas; paridad en curso |
| Orden y secuencias | LibGDX | Seis modos con orden canónico y arrastre; paridad visual en curso |
| Las diez familias normales restantes | Compose | Pendientes de migración |

El bloque lógico nuevo comparte infraestructura de render, entrada, selector
de 20 niveles, resultado y persistencia. Orden y secuencias ya distingue torre,
escalera, días, estaciones, ciclo de vida y ciclo del agua, conserva el orden
canónico y permite reordenar por arrastre. El bloque no se declara terminado
hasta que cada modalidad sustituya por completo su interacción Compose
original.

Percepción ya clasifica por arrastre texturas, temperatura, peso, sabores y
olores en destinos propios. Colores, sentidos y sombras conservan selección
visual, mientras Diferencias usa un tablero cuyo número de distractores crece
con el nivel. Formas dibuja geometría dentro de las opciones en vez de depender
solo de etiquetas.

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

- 109 pruebas unitarias sin fallos.
- Android Lint sin errores.
- APK debug generado e instalado.
- Percepción sensorial abierta desde el catálogo en emulador dentro de
  `LogicBlockGdxActivity`, sin excepción fatal ni ANR.
