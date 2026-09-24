# RominaGame

RominaGame es una plataforma Android offline de juegos tradicionales. El
proyecto reúne materiales educativos, herramientas creativas, juegos de mesa y
Arcade en una sola aplicación nativa y completamente offline.

## Estado actual

- Aplicación Android nativa con Kotlin y Jetpack Compose.
- Sin React, Next.js, Capacitor, WebView ni dependencias de Node.
- Catálogo consolidado en 21 materiales y 100 modos, sin filtros por edad.
- Los juegos similares viven como modos de un mismo material y conservan su
  pantalla, niveles y progreso independiente.
- Los 11 Arcade están temporalmente ocultos hasta que se corrijan y validen.
- Pizarra completa restaurada y estudio de siete instrumentos con siete notas.
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
mantiene el código Arcade fuera del catálogo visible hasta su reparación.

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
