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
- **Arrastrar y soltar real**: torre rosa y "¿vivo o no vivo?" usan
  gestos de arrastre nativos, no toques — más fiel al material Montessori
  real, donde uno *toma* el objeto y lo *suelta* donde va.

## Qué NO es todavía

**Esto es la primera ronda, no el puerto completo.** Hay 96 materiales en
la versión web; aquí hay 4, elegidos para probar cada patrón de
interacción de punta a punta:

| Material nativo | Patrón que prueba | Equivalente web |
|---|---|---|
| `formas` (Gabinete de figuras) | Nomenclatura (Quiz) | `MaterialQuiz` |
| `torre-rosa` (Torre rosa) | Seriación, con arrastre real | `MaterialOrdenar` |
| `seres-vivos` (¿Vivo o no vivo?) | Clasificación, con arrastre real | `MaterialClasificar` |
| `gato` (Gato) | Material independiente, sin patrón compartido | `app/games/gato` |

Con **un solo ejemplo de cada patrón**, se dejó la lógica de nivel/acierto
local en cada pantalla en vez de extraer un `MaterialQuiz`/`MaterialOrdenar`
genérico reutilizable — en la versión web esos componentes nacieron de ver
el patrón repetirse muchas veces, no al revés. Extraerlos ahora, con un
solo caso de cada uno, sería adivinar la forma correcta sin evidencia.
Cuando haya un segundo material de cada patrón, ahí corresponde.

El selector de edad y el menú principal (con filtro por edad y áreas) sí
están completos y navegables.

## Cómo seguir agregando materiales

1. Elegir un material real de `data/games.ts` (la fuente de verdad sigue
   siendo la versión web) y su curva en `data/levels/<slug>.ts`.
2. Portar la curva a Kotlin usando `phased`/`phasedInt` de
   `model/Levels.kt` — son la misma función, mismo comportamiento.
3. Si es el segundo material que usa un patrón ya visto (Quiz, Ordenar,
   Clasificar, Transferir), extraer un composable reutilizable en vez de
   copiar la pantalla anterior — es la señal de que ya no es prematuro.
4. Agregar la entrada en `model/GameDef.kt` (`CATALOGO`).
5. Registrar la pantalla en `MainActivity.kt` (`Ruta` + `composable(...)`).
6. Verificar: `./gradlew compileDebugKotlin` primero (rápido), después
   `./gradlew assembleDebug` para el APK completo.

## Compilar

Mismo entorno ya usado para `../android` (Capacitor) — ver
`docs/MANUAL-TECNICO.md` en la raíz del repo para el detalle completo de
los problemas de JDK/SSL encontrados en Windows y cómo se resolvieron.

```bash
export JAVA_HOME="C:/Program Files/Android/openjdk/jdk-21.0.8"   # no el JDK 25 por defecto
./gradlew assembleDebug
# APK en app/build/outputs/apk/debug/app-debug.apk
```
