# RominaGame

RominaGame es una plataforma Android offline de juegos tradicionales. El
proyecto reúne materiales educativos, herramientas creativas y juegos de mesa
en una sola aplicación nativa y completamente offline, pensada para niñas y
niños.

## Estado actual

- Aplicación Android nativa con Kotlin y Jetpack Compose.
- Sin React, Next.js, Capacitor, WebView ni dependencias de Node.
- Catálogo consolidado en 19 materiales y 90 modos, sin filtros por edad.
- Los juegos similares viven como modos de un mismo material y conservan su
  pantalla, niveles y progreso independiente.
- Sin categoría Arcade: se retiraron los 11 juegos de ritmo/reflejos por no
  superar la validación funcional.
- Juegos de mesa reducidos a los infantiles: gato, piedra papel o tijera,
  memoria por turnos y dominó, más dos juegos nuevos de observación
  (Encuentra los objetos y Encuentra las diferencias). Se retiraron ajedrez,
  damas, damas chinas, solitarios, oca, serpientes y escaleras, dado de
  retos, lotería, bingo, cuatro en línea y adivina quién.
- Pizarra completa restaurada; el xilófono integra siete instrumentos con siete notas.
- Catálogo y progreso completamente locales.
- Android 7.0 o posterior (`minSdk 24`).
- Versión de aplicación: **1.0.0**.

La estrategia completa está en
[`docs/PLAN-RECONSTRUCCION-ANDROID.md`](docs/PLAN-RECONSTRUCCION-ANDROID.md) y el
inventario de la aplicación retirada en
[`docs/INVENTARIO-FASE-0.md`](docs/INVENTARIO-FASE-0.md).

## Tecnología actual

- Jetpack Compose para navegación, catálogo, ajustes y progreso.
- Jetpack Compose contiene las pantallas jugables restauradas. La integración
  experimental que dejaba juegos en blanco se retiró de la versión 1.0.0.

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

## Instalar mediante ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

La aplicación no necesita conexión ni solicita permiso de Internet.
