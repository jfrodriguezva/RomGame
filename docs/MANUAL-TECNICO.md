# RominaGame — Manual técnico

## Arquitectura

- `app`: anfitrión Android, navegación Compose, catálogo, ajustes, DataStore,
  actividades LibGDX e integración de Godot.
- `game-core`: reglas Kotlin/JVM deterministas, independientes del render.
- `app/src/main/assets`: proyecto y recursos compilados de Godot.
- `tools/export-godot.ps1`: exportación reproducible de GDScript a recursos
  consumidos por la biblioteca Android de Godot.

No existe capa web, WebView ni servidor. Compose no ejecuta juegos: las 20
familias normales y de mesa usan LibGDX; las nueve familias Arcade usan Godot.

La versión Android declarada es `1.0.0` (`versionCode` 1). Esta entrega no se
publica ni firma para distribución.

## Taller creativo

`CreativeStudioGame` implementa Pizarra y el estudio musical sobre LibGDX. La
pizarra mantiene un `Pixmap` transparente para los trazos, historial acotado a
14 estados y composición del fondo al exportar PNG. La galería se almacena en
el directorio privado `files/galeria`, que es el único expuesto al selector de
compartir mediante `FileProvider`.

`CreativeStudioRules` concentra herramientas, paleta, simetrías, notas e
instrumentos verificables sin Android. `SoundPlayer` genera al iniciar las 49
combinaciones de siete notas por siete instrumentos como PCM local y las
reproduce con `AudioTrack`; no descarga muestras ni requiere Internet.

## Catálogo y motores

`CatalogoConsolidado.kt` define 29 `FamiliaJuego`. Cada modalidad aparece una
sola vez. `Motor.LIBGDX` abre `LogicBlockGdxActivity`, `MemoriaGdxActivity` o
`AlignmentGdxActivity`; `Motor.GODOT` abre `GodotArcadeActivity` con
`--game-id`.

`RomGameProgressPlugin` comunica los cambios de nivel desde GDScript a
`ProgressStore`. La migración `migrarFamilias` conserva los identificadores
anteriores y crea agregados de familia una sola vez.

## Compilación

Se requiere JDK 21; bytecode y compatibilidad fuente se mantienen en Java 17.

```powershell
$env:ANDROID_HOME = "C:\Android\Sdk"
.\gradlew.bat :game-core:test :app:testDebugUnitTest lintDebug assembleDebug bundleRelease --no-daemon
```

El APK debug es universal. El AAB permite que Google Play entregue únicamente
las bibliotecas nativas necesarias para el ABI del dispositivo.

## Godot

Después de cambiar cualquier `.gd`:

```powershell
.\tools\export-godot.ps1
```

El exportador elimina `.gdc` y `.remap` anteriores antes de compilar para que
un recurso obsoleto no oculte errores. Los archivos fuente y compilados se
versionan porque CI no descarga el editor Godot.

## Firma

Copiar `keystore.properties.example` a `keystore.properties` y definir almacén,
alias y contraseñas. Tanto el archivo como la llave deben permanecer fuera de
Git. Sin esas propiedades, `bundleRelease` produce el artefacto sin firma para
validación de CI.

## CI y contratos

GitHub Actions ejecuta explícitamente las pruebas de `game-core`, pruebas del
módulo Android, lint, APK debug y AAB release sin firma. También publica ambos
artefactos; la firma de distribución se realiza fuera de CI con secretos.
Las pruebas de catálogo verifican cobertura única, ausencia de edad, motor por
categoría y correspondencia entre IDs Arcade y recursos Godot.

## Limitación de validación gráfica

El emulador local disponible usa SwiftShader: inicializa Godot, registra el
plugin y recibe argumentos, pero falla al presentar Vulkan. La matriz de diez
Arcade se valida por compilación y ejecución headless. Antes de distribuir una
versión estable debe completarse un recorrido visual en un dispositivo con GPU
compatible.
