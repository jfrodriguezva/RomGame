# Fase 6 — Arcade en Godot

Inicio: 22 de septiembre de 2026

Estado: **en ejecución**. La fase 5 está terminada.

## Alcance

| Familia | Modalidades | Modalidad objetivo | Estado |
|---|---|---|---|
| Bloques | Bloques | Caída, rotación, líneas y progresión | Implementado; validación headless |
| La víbora | La víbora | Movimiento continuo, comida y colisión | Implementado; validación headless |
| Rompe ladrillos | Rompe ladrillos | Pala, rebotes, ladrillos y mejoras | Implementado; validación headless |
| Mosaico sorpresa | Mosaico sorpresa | Trazado y captura de territorio con enemigos | Implementado; validación headless |
| Vaqueros del ocaso | Vaqueros del ocaso | Acción lateral, plataformas, disparos y jefes | Pendiente |
| Comepuntos | Comepuntos | Laberinto, puntos, energizantes e IA perseguidora | Implementado; validación headless |
| Rescate de nieve | Rescate de nieve | Plataformas, nieve acumulativa y cadenas | Implementado; validación headless |
| Escuadrón estelar | Escuadrón estelar | Shooter sobre rieles, puntería y jefes | Pendiente |
| Gran premio | Gran premio, Carreras | Circuitos, rivales, vueltas y progresión | Pendiente |

## Integración terminada

- Godot 4.7.2 estable se integra como biblioteca AAR dentro del mismo APK.
- `GodotArcadeActivity` recibe el ID del catálogo y lo entrega a GDScript.
- El proyecto se exporta a recursos compilados mediante
  `tools/export-godot.ps1`; Android carga `project.binary` directamente.
- Se unificó el `FileProvider` de Godot con el usado para compartir dibujos.
- El APK compila, instala y el log confirma `ARCADE_READY:tetris`.

El emulador disponible usa SwiftShader. OpenGL excede su límite de uniformes y
Vulkan inicia la escena pero falla al presentar la cola (`VkResult 5`). Es una
limitación gráfica del emulador; la validación visual final debe repetirse en
un dispositivo o emulador con aceleración gráfica funcional.

## Primer bloque jugable

- Bloques conserva siete tetrominós, rotación con desplazamiento lateral,
  caída suave y dura, eliminación de una a cuatro líneas, puntuación, siguiente
  pieza, incremento de nivel y velocidad.
- La víbora conserva avance continuo, giro sin reversa inmediata, crecimiento,
  comida en celdas libres, colisión contra bordes/cuerpo y aumento de ritmo.
- Rompe ladrillos conserva pala arrastrable, lanzamiento, rebotes por cara,
  vidas, marcador y tres disposiciones progresivas de ladrillos.

Los tres scripts pasan exportación limpia y arranque headless con su ID. El
exportador elimina todos los `.gdc` y `.remap` anteriores para impedir que un
binario obsoleto oculte errores de GDScript.

## Segundo bloque jugable

- Mosaico sorpresa permite salir del perímetro seguro, trazar territorio,
  cerrar áreas mediante relleno desde el enemigo, perder vidas por cortar la
  estela y avanzar al capturar el 75 %.
- Comepuntos incluye laberinto, túneles, puntos, energizantes, tres rivales con
  persecución y huida temporal, vidas y tres rondas progresivamente rápidas.
- Rescate de nieve incorpora plataformas, salto, proyectiles acumulativos,
  conversión del enemigo en bola, cadenas contra otros rivales y tres niveles.

Los seis modos implementados pasan compilación desde fuente, exportación limpia
y arranque headless con el ID correspondiente.

## Criterio de fidelidad

Se conserva la modalidad, ritmo, controles, enemigos, estructura de niveles y
sistemas de los referentes. Nombres, personajes, arte, audio, textos, mapas y
niveles son originales para respetar propiedad intelectual.
