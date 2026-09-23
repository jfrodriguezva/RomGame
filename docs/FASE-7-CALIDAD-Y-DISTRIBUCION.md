# Fase 7 — Calidad y distribución

Inicio: 22 de septiembre de 2026

Estado: **terminada**.

## Objetivo

Convertir el resultado funcional de las fases 0–6 en una entrega repetible,
auditable y preparada para distribución Android, sin ampliar el catálogo.

## Alcance

- Auditar los contratos de las fases 0–6 y corregir divergencias.
- Eliminar definitivamente el dato de edad del modelo.
- Migrar progreso de IDs anteriores hacia las familias consolidadas.
- Verificar automáticamente la asignación LibGDX/Godot y los recursos Arcade.
- Reducir la superficie Android: las actividades de motor son internas.
- Construir y conservar APK debug y AAB release en CI.
- Actualizar manuales, estado y procedimiento de firma.
- Ejecutar pruebas, lint, build release, instalación y smoke test.

## Criterios de cierre

1. Pruebas de reglas y Android aprobadas desde limpio.
2. Lint sin errores.
3. `assembleDebug` y `bundleRelease` correctos.
4. APK instalado y navegación principal comprobada en emulador.
5. Matriz Godot compilada y los diez IDs arrancan en headless.
6. Firma documentada y secretos fuera del repositorio.
7. Árbol Git limpio con un punto de control independiente.

La revisión visual de Godot en hardware con GPU queda como requisito previo a
publicar en una tienda, porque el emulador SwiftShader disponible no puede
presentar la escena Vulkan.

## Evidencia de cierre

- 72 pruebas de reglas y 12 pruebas Android aprobadas: 84 en total.
- Lint Android sin errores.
- Construcción limpia de 111 tareas, incluyendo APK debug y AAB release.
- APK de 322,781,809 bytes instalado correctamente en `emulator-5554`.
- AAB release sin firma de 111,691,307 bytes generado correctamente y firma
  local documentada mediante `keystore.properties`.
- Las diez modalidades Godot arrancaron sin errores en ejecución headless.
- Inicio, 29 familias, categoría Arcade y sus nueve fichas comprobados mediante
  la jerarquía de accesibilidad del emulador.
- Bloques se abrió desde la navegación interna; Android registró
  `RomGameProgress` y recibió `--game-id=tetris`.
- El intento externo de abrir `GodotArcadeActivity` fue rechazado por Android,
  confirmando que la actividad ya no está exportada.
