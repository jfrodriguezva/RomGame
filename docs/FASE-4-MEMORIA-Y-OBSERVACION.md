# Fase 4 — Memoria y observación

Fecha de cierre del bloque: 22 de septiembre de 2026

Este documento registra el primer juego consolidado potenciado durante la
fase 4. No representa el cierre de toda la fase: los demás juegos normales se
trabajarán uno por uno; los juegos de mesa y Arcade permanecen reservados para
las fases 5 y 6.

## Alcance entregado

La ficha consolidada **Memoria y observación** abre un juego LibGDX nativo con
cuatro modalidades diferenciadas:

1. **Parejas:** descubre cartas y completa todos los pares.
2. **Qué falta:** memoriza una secuencia y reconoce el elemento retirado.
3. **Contra CPU:** alterna turnos contra un oponente automático y gana por
   cantidad de pares.
4. **Búsqueda visual:** localiza el símbolo objetivo entre distractores.

Cada modalidad dispone de 20 niveles. La dificultad cambia el tamaño del
tablero, la cantidad de símbolos, el tiempo de observación o el número de
distractores según corresponda; no se limita a acelerar el mismo tablero.

## Flujo y progreso

- Menú propio de modalidades.
- Selector de nivel con controles táctiles.
- Instrucciones visibles y feedback durante la partida.
- Estados de victoria o derrota, puntuación y avance al siguiente nivel.
- Reinicio y regreso al menú sin abandonar la actividad Android.
- Persistencia local separada por modalidad mediante `ProgressStore`.
- Registro de partidas en la familia consolidada para las estadísticas del
  catálogo.

## Arquitectura y pruebas

- La lógica determinista está en `MemoryRules.kt`, separada del renderizado.
- El juego y su bucle de entrada/render están en `MemoriaGame.kt`.
- `MemoriaGdxActivity` conecta el resultado de LibGDX con el progreso Android.
- Se añadieron pruebas unitarias para dificultad, baraja de pares y generación
  de búsqueda visual.

Validación del bloque:

- 103 pruebas unitarias totales, sin fallos (99 de la aplicación y 4 del
  motor de juego).
- Android Lint sin errores.
- APK debug generado correctamente.
- Recorrido en emulador: apertura desde el catálogo, menú LibGDX con los cuatro
  modos y salida sin bloqueo ni ANR.

## Pendiente de la fase 4

Continuar con las familias normales consolidadas, priorizando las de reglas
claras (clasificación, secuencias y encajes). Audio, arte final y efectos más
elaborados se incorporarán por juego sin convertirlos en una plantilla
genérica. Juegos de mesa y Arcade no se modificarán en esta fase.
