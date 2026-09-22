# RominaGame

RominaGame es una plataforma Android offline de juegos tradicionales. El
proyecto está en reconstrucción para ofrecer menos juegos aislados y más juegos
completos, con modos, niveles, progresión y controles táctiles de calidad.

## Estado actual

- Aplicación Android nativa con Kotlin y Jetpack Compose.
- Sin React, Next.js, Capacitor, WebView ni dependencias de Node.
- Catálogo y progreso completamente locales.
- Android 7.0 o posterior (`minSdk 24`).
- La consolidación del catálogo y la migración a motores de juego se realizará
  por fases.
- Fase 2 terminada: 111 accesos se consolidaron en 29 juegos con modos y ocho
  categorías; el selector de edad fue retirado.
- Fase 3 terminada para el primer bloque: LibGDX 1.14.2 está integrado.
- Fase 4 en ejecución: `Memoria y observación` ya ofrece cuatro modos, 20
  niveles por modo, progreso persistente y reglas probadas.
- Fase 3 continúa con el bloque lógico en LibGDX: Percepción sensorial, Formas
  y encajes y Orden y secuencias ya tienen una base jugable; su paridad por
  modalidad sigue en desarrollo.

La estrategia completa está en
[`docs/PLAN-RECONSTRUCCION-ANDROID.md`](docs/PLAN-RECONSTRUCCION-ANDROID.md) y el
inventario de la aplicación retirada en
[`docs/INVENTARIO-FASE-0.md`](docs/INVENTARIO-FASE-0.md).
El cierre del primer juego de la fase 4 se detalla en
[`docs/FASE-4-MEMORIA-Y-OBSERVACION.md`](docs/FASE-4-MEMORIA-Y-OBSERVACION.md).
El estado exacto y los criterios de cierre están en
[`docs/ESTADO-FASES-3-4-5.md`](docs/ESTADO-FASES-3-4-5.md).

## Tecnología objetivo

- Jetpack Compose para navegación, catálogo, ajustes y progreso.
- LibGDX para juegos normales y juegos de mesa.
- Godot para la categoría Arcade.

## Compilar y verificar

Requisitos: JDK 21 y Android SDK configurado mediante `ANDROID_HOME`.

En Windows:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --no-daemon
```

En Linux o macOS:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug --no-daemon
```

El APK debug se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Instalar mediante ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

La aplicación no necesita conexión ni solicita permiso de Internet.
