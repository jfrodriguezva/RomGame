# RominaGame

Versión actual: **1.0.0**.

RominaGame es una plataforma Android offline de juegos tradicionales,
educativos, de mesa y Arcade. La aplicación anfitriona es Kotlin/Compose; los
juegos normales y de mesa se ejecutan en LibGDX y Arcade en Godot integrado.

## Estado

- Fases 0–6 terminadas y auditadas.
- 29 familias, 100 modalidades y ocho categorías, sin filtros por edad.
- 20 familias LibGDX y nueve familias Godot.
- Progreso y configuración locales mediante DataStore.
- Sin React, Next.js, Capacitor, WebView, servidor ni permiso de Internet.
- Fase 7: calidad, distribución y preparación de publicación.
- Taller creativo migrado a LibGDX: Pizarra completa y estudio musical de
  siete notas con xilófono, piano, guitarra, flauta, trompeta, acordeón y arpa.

La historia y los criterios están en
[Plan de reconstrucción](docs/PLAN-RECONSTRUCCION-ANDROID.md). El cierre de la
fase 7 está en [FASE-7-CALIDAD-Y-DISTRIBUCION.md](docs/FASE-7-CALIDAD-Y-DISTRIBUCION.md).

## Verificación

Requisitos: JDK 21 y Android SDK definido en `ANDROID_HOME`.

```powershell
.\gradlew.bat :game-core:test :app:testDebugUnitTest lintDebug assembleDebug bundleRelease --no-daemon
```

Artefactos:

- APK instalable: `app/build/outputs/apk/debug/app-debug.apk`
- AAB de release sin firma en CI: `app/build/outputs/bundle/release/app-release.aab`

Para regenerar los recursos compilados de Arcade:

```powershell
.\tools\export-godot.ps1
```

El AAB queda listo para firmarse al proporcionar un `keystore.properties` local basado en
`keystore.properties.example`. Los secretos de firma no se versionan.
