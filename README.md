# RominaGame

RominaGame es una plataforma Android offline de juegos tradicionales. El
proyecto reúne materiales educativos, herramientas creativas y juegos de mesa
en una sola aplicación nativa y completamente offline, pensada para niñas y
niños.

## Estado actual

- Aplicación Android nativa con Kotlin y Jetpack Compose.
- Sin React, Next.js, Capacitor, WebView ni dependencias de Node.
- Catálogo consolidado en 21 materiales y 94 modos, sin filtros por edad.
- Los juegos similares viven como modos de un mismo material y conservan su
  pantalla, niveles y progreso independiente.
- Sin categoría Arcade. De los 11 arcade retirados se rescataron cuatro,
  con niveles reales: Atrapa al topo (en Coordinación y reflejos) y La
  víbora, Acomoda las piezas y Rompe ladrillos (material "Juegos clásicos").
  El resto queda en la etiqueta `archivo/arcades`.
- Juegos de mesa infantiles: gato, piedra papel o tijera, memoria por
  turnos, dominó, Encuentra los objetos, Encuentra las diferencias y
  Palillos chinos. Se retiraron ajedrez, damas, damas chinas, solitarios,
  oca, serpientes y escaleras, dado de retos, lotería, bingo, cuatro en
  línea y adivina quién (también en `archivo/arcades`).
- Pizarra completa restaurada; el xilófono integra siete instrumentos con siete notas.
- Catálogo y progreso completamente locales.
- Android 7.0 o posterior (`minSdk 24`).
- Versión de aplicación: **1.2.0**. Todos los modos con niveles tienen
  selector y dificultad que crece de verdad en sus 100 niveles.

Documentación:

- [`docs/MANUAL-USUARIO.md`](docs/MANUAL-USUARIO.md) — uso de la app, ajustes y
  vista del adulto.
- [`docs/MANUAL-TECNICO.md`](docs/MANUAL-TECNICO.md) — arquitectura, pruebas,
  build y publicación.
- [`docs/PLAN-RECONSTRUCCION-ANDROID.md`](docs/PLAN-RECONSTRUCCION-ANDROID.md) —
  estrategia y decisiones vigentes.
- [`docs/INVENTARIO-FASE-0.md`](docs/INVENTARIO-FASE-0.md) — inventario de la
  aplicación web retirada.

## Tecnología actual

- Kotlin 2.4, Jetpack Compose, Android Gradle Plugin 9.4 y Gradle 9.8.
- `compileSdk`/`targetSdk` 37, `minSdk` 24. R8 activo en release.
- Todas las pantallas (navegación, catálogo, ajustes, progreso y juegos) son
  Compose; no hay motores externos.

## Recuperación 1.0.0

La versión 1.0.0 vuelve a la última base Android nativa completa y recupera las
pantallas que se habían sustituido durante las fases experimentales. Conserva
el proyecto sin React/Next/Capacitor, restaura la salida común de cada juego y
retira por completo el código Arcade que no superó su validación.

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

Con un emulador o dispositivo conectado, el recorrido que abre las 118
pantallas y verifica áreas táctiles de 48dp:

```bash
./gradlew connectedDebugAndroidTest
```

## Instalar mediante ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

La aplicación no necesita conexión ni solicita permiso de Internet.
