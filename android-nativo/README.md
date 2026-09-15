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

**Esto sigue sin ser el puerto completo.** Hay 96 materiales en la versión
web; aquí hay **24**, ya con los 4 patrones de interacción extraídos como
composables reutilizables en `ui/materials/` (`MaterialQuiz`,
`MaterialOrdenar`, `MaterialClasificar`, `MaterialTransferir`), todos con
arrastre real (`pointerInput` + `detectDragGestures`) donde el material
original lo usa. Faltan 72.

| Patrón | Materiales nativos |
|---|---|
| `MaterialQuiz` (nomenclatura) | `formas`, `cuerpo`, `colores`, `instrumentos`, `oficios` |
| `MaterialOrdenar` (seriación, con arrastre) | `torre-rosa`, `dias-semana`, `estaciones`, `ciclo-vida` |
| `MaterialClasificar` (con arrastre a canastas) | `seres-vivos`, `habitat`, `dieta-animal`, `fruta-verdura`, `el-la`, `pares-impares`, `tamanos`, `estados-agua`, `dia-noche`, `singular-plural`, `transporte` |
| `MaterialTransferir` (cantidad exacta, con arrastre) | `pinza`, `husos` |
| Independientes (sin patrón compartido) | `gato`, `rps` |

Simplificaciones conocidas, pendientes de mejorar en una pasada futura:
- `MaterialQuiz` solo implementa el periodo de "reconocer" (elegir el
  nombre correcto); la lección de tres periodos completa (nombrar,
  reconocer, evocar) de la versión web no está portada aún.
- En `MaterialOrdenar`, el tamaño de la serie (`n`) es fijo por pantalla en
  vez de escalar con el nivel alcanzado (sí escala en `MaterialQuiz` vía
  `curvaOpciones`); es una limitación de que el nivel vive dentro del
  composable y no es visible al armar sus parámetros de entrada.

El selector de edad y el menú principal (con filtro por edad y áreas) sí
están completos y navegables, y ya filtran/organizan los 24 materiales
reales.

## Cómo seguir agregando materiales

1. Elegir un material real de `data/games.ts` (la fuente de verdad sigue
   siendo la versión web) y su curva en `data/levels/<slug>.ts`.
2. Portar la curva a Kotlin usando `phased`/`phasedInt` de
   `model/Levels.kt` — son la misma función, mismo comportamiento.
3. Usar el composable ya extraído en `ui/materials/` que corresponda
   (`MaterialQuiz`, `MaterialOrdenar`, `MaterialClasificar`,
   `MaterialTransferir`) — los 4 patrones ya están extraídos, no hace
   falta copiar una pantalla existente como base.
4. Agregar la entrada en `model/GameDef.kt` (`CATALOGO`).
5. Registrar la pantalla en `MainActivity.kt` — el `composable(...)` usa
   el mismo id que el `GameDef`, no hace falta tocar `Ruta` para
   materiales (esa clase ya solo tiene `EDAD`/`INICIO`).
6. Verificar: `./gradlew compileDebugKotlin` primero (rápido), después
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
