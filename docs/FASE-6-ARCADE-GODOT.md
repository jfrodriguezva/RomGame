# Fase 6 — Arcade en Godot

Inicio: 22 de septiembre de 2026

Estado: **en ejecución**. La fase 5 está terminada.

## Alcance

| Familia | Modalidades | Modalidad objetivo | Estado |
|---|---|---|---|
| Bloques | Bloques | Caída, rotación, líneas y progresión | Pendiente |
| La víbora | La víbora | Movimiento continuo, comida y colisión | Pendiente |
| Rompe ladrillos | Rompe ladrillos | Pala, rebotes, ladrillos y mejoras | Pendiente |
| Mosaico sorpresa | Mosaico sorpresa | Trazado y captura de territorio con enemigos | Pendiente |
| Vaqueros del ocaso | Vaqueros del ocaso | Acción lateral, plataformas, disparos y jefes | Pendiente |
| Comepuntos | Comepuntos | Laberinto, puntos, energizantes e IA perseguidora | Pendiente |
| Rescate de nieve | Rescate de nieve | Plataformas, nieve acumulativa y cadenas | Pendiente |
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

## Criterio de fidelidad

Se conserva la modalidad, ritmo, controles, enemigos, estructura de niveles y
sistemas de los referentes. Nombres, personajes, arte, audio, textos, mapas y
niveles son originales para respetar propiedad intelectual.
