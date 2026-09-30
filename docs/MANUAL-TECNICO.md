# RominaGame (Mi Ambiente) — Manual técnico

Actualizado el 30 de septiembre de 2026. Repositorio:
`git@github.com:jfrodriguezva/RomGame.git`.

La versión web (Next.js, React, Capacitor) se retiró en la fase 1; este
documento describe solo el proyecto Android vigente. Las reglas para agentes
están en `AGENTS.md`.

## 1. Stack

| Componente | Versión | Notas |
|---|---|---|
| Kotlin | 2.4.20 | Integrado por AGP 9: no se aplica `org.jetbrains.kotlin.android` |
| Android Gradle Plugin | 9.4.1 | |
| Gradle | 9.8.0 | Wrapper incluido; se ejecuta con JDK 21 |
| Jetpack Compose (BOM) | 2026.06.01 | Material 3 |
| Navigation Compose | 2.10.2 | |
| DataStore Preferences | 1.2.1 | Ajustes y progreso |
| SDK | `minSdk` 24, `compileSdk`/`targetSdk` 37 | Código fuente Java 17 |

No hay dependencias de red: la app no declara el permiso de Internet. El
único permiso es `VIBRATE`.

## 2. Arranque rápido

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug   # verificación estándar
./gradlew assembleRelease                              # release con R8
./gradlew connectedDebugAndroidTest                    # recorrido en emulador
```

**Red corporativa:** si Gradle o `sdkmanager` fallan con
`PKIX path building failed`, Java no confía en el certificado del proxy.
Hay que usar el almacén de Windows:
`$env:JAVA_OPTS='-Djavax.net.ssl.trustStoreType=Windows-ROOT'`.

## 3. Estructura

```
app/src/main/kotlin/com/miambiente/app/
  MainActivity.kt        navegación: inicio, ajustes, progreso, material/{id} y una ruta por modo
  RominaApp.kt           dueña única de Services durante la vida del proceso
  data/                  Services, ProgressStore, SettingsStore, ResumenAdulto,
                         Galeria (pizarra), Speech, Sound, AmbientMusic, Haptics, Guias
  model/                 GameDef (catálogo de 89 modos), CatalogoConsolidado (19 materiales),
                         Levels (motor de niveles), LogicaMateriales (reglas puras)
  theme/                 colores por área y tema
  ui/GameShell.kt        marco común: volver, selector de nivel, consigna, celebración
  ui/Pantallas.kt        mapa id → pantalla; una prueba verifica que no falte ninguna
  ui/materials/          patrones compartidos (MaterialOrdenar, MaterialTransferir,
                         MaterialQuiz, MaterialClasificar, MaterialState…)
  ui/screens/            93 pantallas: un modo, o una pantalla de la app
app/src/test/            pruebas JVM de reglas puras (sin emulador)
app/src/androidTest/     recorrido instrumentado de todas las pantallas
```

## 4. Catálogo

- `GameDef.kt` define los 89 modos (id, título, emoji, área, objetivo).
- `CatalogoConsolidado.kt` agrupa los modos en 19 materiales y 7 categorías.
  El inicio muestra materiales; cada material muestra sus modos.
- `Pantallas.kt` asocia cada id con su composable. `PantallasTest` falla si
  un modo no tiene pantalla, si hay pantallas huérfanas o si un id choca con
  las rutas fijas.

Para agregar un modo: entrada en `CATALOGO`, id en su material de
`MATERIALES_CONSOLIDADOS`, pantalla en `PANTALLAS`, y reglas puras con
pruebas si tiene lógica propia.

## 5. Materiales compartidos y lógica pura

La mayoría de los modos se arman con un patrón de `ui/materials`:

- **MaterialOrdenar** — seriación por toques: tomar una pieza del canasto y
  tocar su lugar. Reglas en `SerieOrdenar` (`model/LogicaMateriales.kt`).
- **MaterialTransferir** — pasar de uno en uno hasta un objetivo exacto;
  pasarse reinicia la bandeja. Reglas en `Transferencia`.
- **MaterialClasificar** — arrastrar cada objeto a su canasta. Reglas en
  `RondaClasificar`; los objetos por ronda salen de `CURVA_CLASIFICAR` (3 a 8,
  sin pasar del total del material).
- **MaterialQuiz** — nomenclatura en tres periodos (`periodoPara`,
  `generarOpciones`).
- **Rompecabezas** usa `SerieOrdenar` (pieza k en casilla k) y
  `escenaRompecabezas(nivel)`: 2×2, 3×3 o 4×4 con piezas distintas.
- **MaterialState** — nivel, acierto, intento y completar; registra aciertos
  y errores para el resumen del adulto.

Las pantallas no deben calcular la dificultad fuera del nivel: los patrones
reciben una función del nivel (`calcularN`, `calcularObjetivo`) y las curvas
viven con nombre en `LogicaMateriales.kt` (`CURVA_TORRE_ROSA`, etc.). Antes
seis pantallas llamaban `phasedInt(1, …)` y quedaban siempre en el nivel 1,
los 24 materiales de clasificación pedían siempre 6 objetos y el
rompecabezas era siempre de 4 piezas.

## 6. Motor de niveles

`model/Levels.kt`: 100 niveles en 10 etapas de 10 (`STAGE_SIZE`). `phased` y
`phasedInt` interpolan entre 11 paradas (niveles 1, 10, 20… 100).
`estrellasPara` da de 1 a 5 estrellas según el nivel.
`nivelInicialPorEdad` fija el nivel de arranque según la edad de Ajustes
(etapa 1 hasta 3 años, luego una etapa más por año hasta la 4); nunca oculta
ni bloquea niveles.

## 7. Persistencia

- **SettingsStore** (DataStore `ajustes`): nombre, edad, sonido, voz, música,
  vibración, modo calma.
- **ProgressStore** (DataStore `progreso`), por modo: niveles completados,
  nivel desbloqueado, estrellas, veces abierto, días en que se abrió (últimos
  60) y conteo de aciertos y errores. Mantiene una copia en memoria para que
  un modo sepa su nivel sin esperar una lectura asíncrona.
- **ResumenAdulto**: reglas puras de la tarjeta "Esta semana" (días activos,
  lo más practicado, lo que cuesta y una sugerencia).
- **Galeria**: dibujos de la pizarra como PNG en el almacenamiento privado
  (máximo 12) y borrador automático. Compartir usa `FileProvider`.

Todo es local; se borra al desinstalar.

## 8. Sonido, voz y música

- **Sound**: efectos sintetizados con `AudioTrack` y tonos de
  `ToneGenerator`; no hay archivos de audio.
- **Speech**: `TextToSpeech` del sistema. Prueba es-MX, es-US, es-ES y es;
  publica su estado (`LISTA`, `SIN_ESPANOL`, `SIN_MOTOR`) y Ajustes muestra un
  aviso cuando falta la voz.
- **AmbientMusic**: un acorde sintetizado por área con `AudioTrack`, en un
  hilo propio; se pausa en `onStop`.

## 9. Rendimiento

- Nada de decodificar imágenes ni escribir archivos en el hilo principal: la
  galería usa miniaturas reducidas (`leerMiniatura`) en `Dispatchers.IO`.
- Estados que cambian cada frame (p. ej. la altura del globo) se leen en
  lambdas de layout (`Modifier.offset { … }`) para no recomponer.
- Un campo de texto no debe leer directo de DataStore: se edita en estado
  local y se guarda en segundo plano (si no, se pierden letras).

## 10. Pruebas

| Tipo | Dónde | Qué cubre |
|---|---|---|
| Unitarias (JVM) | `app/src/test` | niveles, progreso, resumen del adulto, seriación y transferencia, catálogo y pantallas, dominó, gato, laberinto, quiz, objetos y diferencias |
| Instrumentada | `app/src/androidTest/RecorridoPantallasTest` | abre las 111 pantallas (inicio, ajustes, progreso, 19 materiales y 89 modos), deja correr su reloj y exige áreas táctiles de al menos 48dp |

La instrumentada necesita un emulador o dispositivo. Una prueba de estrés
opcional con `monkey`:

```bash
adb shell monkey -p com.miambiente.app --pct-syskeys 0 --throttle 50 -s 7 5000
```

## 11. Build de release

- R8 (`isMinifyEnabled`, `isShrinkResources`) activo; Compose, Navigation y
  DataStore traen sus reglas de consumo.
- Firma: `keystore.properties` en la raíz (fuera de git; plantilla en
  `keystore.properties.example`). Sin ese archivo el release queda sin firmar
  y el build no falla.

## 12. CI

`.github/workflows/build-apk.yml` corre en cada push a `main` y en cada PR
que toque `app/`, `gradle/` o los scripts de build: instala la plataforma
Android 37, ejecuta `testDebugUnitTest lintDebug assembleDebug
assembleRelease` y publica el APK debug como artefacto. El recorrido
instrumentado no corre en CI (requiere emulador).

## 13. Accesibilidad

- Áreas táctiles de al menos 48dp; en seriación el área se amplía alrededor
  de la pieza sin cambiar su tamaño visible, que es parte del ejercicio.
- Piezas y lugares con `contentDescription` y etiqueta de acción para
  lectores de pantalla; `MaterialTransferir` ofrece además una acción
  accesible "Mover al destino" en lugar de arrastrar.

## 14. Deuda y límites conocidos

- La voz no se ha verificado en un dispositivo con motor de voz en español.
- Las curvas de dificultad están calibradas a ojo.
- El recorrido instrumentado verifica que cada pantalla abre y responde, no
  que cada juego sea ganable; eso lo cubren las pruebas de reglas.
