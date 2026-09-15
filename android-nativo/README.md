# Mi Ambiente — versión nativa (Kotlin + Jetpack Compose)

Proyecto Android nativo aparte, **sin depender de la app web ni de
Capacitor** (esa versión sigue intacta en `../android` y `../app`). Nace
de un pedido explícito: la versión empaquetada con Capacitor (un WebView)
tenía un bug real en el selector de edad en un dispositivo físico, y el
usuario pidió una versión nativa completa, con drag-and-drop real y
funciones nativas del sistema en vez de simularlas dentro de un WebView.

## Por qué Kotlin + Jetpack Compose

Es el framework de UI declarativo oficial de Google, activamente
mantenido (a diferencia del sistema de Views/XML, en modo mantenimiento).
El patrón de "describir la UI según el estado" es muy parecido al de
React, así que la lógica de niveles, curvas y control del error se porta
con poca fricción conceptual. Para arrastrar y soltar, Compose tiene
`pointerInput` + `detectDragGestures`, mucho más confiable que cualquier
cosa dentro de un WebView.

## Qué es nativo de verdad aquí (no una simulación)

- **Voz**: `android.speech.tts.TextToSpeech`, parte del sistema
  operativo — no depende de que el navegador tenga voces en español
  instaladas (una limitación real de la versión web).
- **Sonido**: `android.media.ToneGenerator`, tonos reales del sistema en
  vez de sintetizarlos con WebAudio.
- **Vibración**: `android.os.VibrationEffect` con los mismos patrones que
  `lib/haptics.ts`.
- **Persistencia**: Jetpack DataStore (equivalente de
  zustand+localStorage), sin ninguna dependencia de un WebView.
- **Sin permiso de INTERNET**: a diferencia del APK de Capacitor, que lo
  declara por default para su WebView aunque la app nunca lo use, este
  manifest no pide ningún permiso de red.
- **Arrastrar y soltar real**: todos los materiales de seriación y
  clasificación usan gestos de arrastre nativos, no toques — más fiel al
  material Montessori real, donde uno *toma* el objeto y lo *suelta*
  donde va.
- **Lección de tres periodos completa** en `MaterialQuiz` (presentar →
  reconocer → evocar), no solo el último periodo.
- **Confeti nativo** (`ConfettiOverlay`) al completar un nivel, y un botón
  "Siguiente nivel" real en los 4 patrones compartidos.
- **Dos materiales que no existen en la versión web**, posibles solo en
  nativo: `vibra-adivina` (contar pulsos de vibración real con los ojos
  cerrados — sin equivalente web confiable) y `reflejo-color` (mide el
  tiempo de reacción en milisegundos con `System.nanoTime()`, algo que el
  bucle de eventos de un navegador no puede medir con precisión).

## Cobertura de materiales

**Los 96 materiales de la versión web están portados**, más los 2
exclusivos de nativo — **98 en total**, verificado automáticamente: cada
`buscarJuego(id)` usado por una pantalla tiene su entrada en
`CATALOGO` y su ruta registrada en `MainActivity.kt` (sin huecos, sin
pantallas fantasma).

Los 4 patrones de interacción compartidos están extraídos como
composables reutilizables en `ui/materials/` (`MaterialQuiz`,
`MaterialOrdenar`, `MaterialClasificar`, `MaterialTransferir`, más
`MaterialTablero` para juegos de mesa con dado), todos con arrastre real
donde el material original lo usa. El resto son pantallas independientes
para mecánicas que de verdad no comparten nada con otro material
(memoria, tablero, reflejos, creatividad libre).

**Esto es honesto, no "perfecto" en el sentido de calcar 1:1 cada detalle
de cada material real de Montessori** — con 96+ materiales distintos, en
varios se tomaron decisiones de diseño razonables en vez de replicar
exactamente el material físico (ver la lista de simplificaciones abajo).
Lo que sí es cierto: todos compilan, todos navegan, todos son jugables de
principio a fin, y el sistema de niveles/estrellas/progreso real corre
igual en los 96.

Simplificaciones conocidas, honestas y documentadas:
- Varios materiales de discriminación sensorial (`textura`, `temperatura`,
  `peso`, `sabor`, `olfato`) se representan con emoji en vez de una
  simulación táctil/térmica real — no hay forma de simular temperatura o
  peso en una pantalla; sí se usa vibración real donde aporta (`pinza`,
  aciertos/errores).
- `domino`, `oca`/`serpientes` (via `MaterialTablero`), `adivinaquien` y
  `banco-dorado` son versiones simplificadas de sus reglas completas, no
  el juego de mesa físico exacto.
- En `MaterialOrdenar`, el tamaño de la serie (`n`) es fijo por pantalla en
  vez de escalar con el nivel alcanzado en todos los casos (sí escala en
  `MaterialQuiz` vía `curvaOpciones`); es una limitación de que el nivel
  vive dentro del composable y no es visible al armar sus parámetros de
  entrada.
- Los juegos de reflejos/movimiento (`burbujas`, `canasta`, `globo`,
  `lava`, `carreras`, `toystory`, `arana`, `laberinto`) usan física
  aproximada con `LaunchedEffect`+`delay`, no un motor de físicas real.

El selector de edad y el menú principal (con filtro por edad y áreas)
están completos y navegables, y organizan los 98 materiales reales.

## Cómo seguir mejorando

Con los 96+2 ya cubiertos, lo que queda es profundidad, no cobertura:

1. Reemplazar las simplificaciones listadas arriba con versiones más
   fieles (reglas completas de dominó/oca, física real en los juegos de
   movimiento) conforme se decida que vale la pena el esfuerzo.
2. Si se agrega un material nuevo: usar el composable ya extraído en
   `ui/materials/` que corresponda (`MaterialQuiz`, `MaterialOrdenar`,
   `MaterialClasificar`, `MaterialTransferir`, `MaterialTablero`) cuando
   encaje; si no encaja ninguno, una pantalla independiente como
   `GatoScreen`/`RpsScreen` es más honesto que forzar un patrón que no
   corresponde.
3. Agregar la entrada en `model/GameDef.kt` (`CATALOGO`) y registrar la
   pantalla en `MainActivity.kt` con el mismo id como ruta.
4. Verificar que no queden huecos entre `buscarJuego(id)`, `CATALOGO` y
   las rutas registradas (los tres conjuntos deben tener el mismo tamaño
   y las mismas claves) — comprobado así en esta ronda:
   ```bash
   grep -oh 'buscarJuego("[a-z0-9-]*")' app/src/main/kotlin/com/miambiente/app/ui/screens/*.kt | sort -u
   grep -oE '    GameDef\("[a-z0-9-]*"' app/src/main/kotlin/com/miambiente/app/model/GameDef.kt | sort -u
   grep -oE 'composable\("[a-z0-9-]*"\)' app/src/main/kotlin/com/miambiente/app/MainActivity.kt | sort -u
   ```
5. `./gradlew compileDebugKotlin` primero (rápido), después
   `./gradlew assembleDebug` para el APK completo.

## Compilar

Mismo entorno ya usado para `../android` (Capacitor) — ver
`docs/MANUAL-TECNICO.md` en la raíz del repo para el detalle completo de
los problemas de JDK/SSL encontrados en Windows y cómo se resolvieron.

```bash
export JAVA_HOME="C:/Program Files/Android/openjdk/jdk-21.0.8"   # no el JDK 25 por defecto
./gradlew assembleDebug --no-daemon --max-workers=2   # flags de memoria: esta máquina ya mató el build 2 veces sin ellas
# APK en app/build/outputs/apk/debug/app-debug.apk
```
