# Inventario de la fase 0

Fecha: 21 de septiembre de 2026

> Fase completada: el proyecto que figuraba como `android-nativo` fue
> promovido a la raíz del repositorio durante la fase 1. Las rutas indicadas
> abajo describen el estado existente al levantar el inventario.

## Resultado

El repositorio tiene 480 archivos rastreados y tres implementaciones/capas:

| Capa | Ruta | Archivos rastreados | Tamaño aproximado | Decisión |
|---|---:|---:|---:|---|
| Next/React | `app`, `components`, `data`, `lib`, `phaser`, `scripts` | 231 | 721 KB | Retirar |
| Recursos web | `public` | 4 | 2.7 KB | Clasificar y retirar |
| Capacitor Android | `android` | 54 | 288 KB | Retirar |
| Android nativo | `android-nativo` | 172 | 798 KB | Conservar como fuente de verdad |
| CI | `.github` | 1 | 1.4 KB | Reescribir para Android nativo |

El conteo no incluye directorios generados ignorados por Git como
`node_modules`, `.next`, `out` o carpetas `build`.

## Recursos web revisados

`public` contiene solamente:

- `icon.svg`: icono exclusivo de la PWA; Android nativo ya tiene sus propios
  recursos de icono.
- `manifest.json`: manifiesto web sin uso nativo.
- `audio/README.md`: explica que no existen archivos de audio.
- `images/README.md`: explica que no existen imágenes de juego almacenadas.

No hay sprites, fotografías, música, efectos ni fuentes binarias que deban
rescatarse de la aplicación web.

## Funcionalidad cuya fuente vigente es Android nativo

- Catálogo y definiciones: `android-nativo/app/src/main/kotlin/.../model`.
- Pantallas y reglas: `android-nativo/app/src/main/kotlin/.../ui`.
- Progreso y configuración: DataStore nativo.
- Audio, voz y vibración: servicios Android nativos.
- Pruebas: `android-nativo/app/src/test`.
- Empaquetado: Gradle Android, `minSdk 24`, `targetSdk 36`.

El frontend no es fuente de datos necesaria para ejecutar el APK nativo.

## Elementos que se retirarán en fase 1

- Código: `app`, `components`, `data`, `lib`, `phaser`, `scripts`.
- Empaquetado antiguo: `android` y `capacitor.config.ts`.
- Recursos web: `public`.
- Configuración Node/web: `package.json`, `package-lock.json`,
  `next.config.ts`, `next-env.d.ts`, `tsconfig.json`, `postcss.config.mjs`,
  `eslint.config.mjs`.
- Artefactos locales: `.next`, `node_modules` y `out` cuando existan.
- Documentación exclusivamente web/Capacitor, reemplazada por documentación
  Android vigente.

## Elementos que se conservarán

- `.git` y el historial, que permiten recuperar cualquier archivo retirado.
- `.github`, reescrito para construir `android-nativo`.
- `android-nativo` completo y sus cambios locales.
- `docs/PLAN-RECONSTRUCCION-ANDROID.md`.
- Instrucciones de agentes, actualizadas para quitar reglas exclusivas de
  Next.js.

## Línea base de calidad

Antes de la fase 1, el proyecto Android nativo había verificado:

- 97 pruebas unitarias aprobadas.
- `lintDebug` sin errores.
- `assembleDebug` correcto.
- APK instalable en Android 16.

La fase 1 debe terminar igual o mejor. La eliminación web no se considerará
terminada si modifica el comportamiento o rompe la construcción nativa.
