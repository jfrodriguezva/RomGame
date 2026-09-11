# Mi Ambiente — Manual técnico

Repositorio: `git@github.com:jfrodriguezva/RomGame.git`

## 1. Stack

| Paquete | Versión | Rol |
|---|---|---|
| Next.js | 16.3.2 | App Router, export estático para el APK |
| React / React DOM | 19.2.8 | UI, client components |
| TypeScript | ^5 | Tipado en todo el repo |
| Tailwind CSS | ^4 | Estilos utilitarios |
| Zustand | ^5 | Estado global + persistencia en localStorage |
| Framer Motion | ^13 | Animaciones |
| Phaser | ^4.2 | Motor para materiales de movimiento/canvas |
| Capacitor (core/android/cli) | ^8.5 | Empaqueta el export estático como APK |
| Serwist / @serwist/next | ^9.5 | Service worker / PWA en la versión web |
| ESLint | ^9 | Lint con reglas de React 19 |
| tsx | ^4 | Ejecuta TypeScript directo (`npm run qa`) |

## 2. Arranque rápido

```bash
npm install
npm run dev        # http://localhost:40000
npm run android     # export + cap sync + gradlew assembleDebug
```

## 3. Estructura del repositorio

```
app/
  page.tsx              inicio, por áreas
  pizarra/               lienzo de dibujo libre
  padres/                progreso, ajustes y acompañamiento
  admin/                 editor de puntos para imágenes propias
  games/<slug>/           un material por carpeta (77)
components/
  GameShell.tsx           marco común: header, nivel, consigna, error
  MaterialQuiz.tsx        lección de tres periodos
  MaterialOrdenar.tsx     seriación
  TrazoGuiado.tsx         recorrer un glifo con el dedo
  LevelSelector.tsx       100 niveles en 10 etapas
data/
  games.ts                catálogo: área, edad, material, objetivo
  levels/<slug>.ts         curva de dificultad de cada material
  trazos-letras.ts         trazos de letras y números
  palabras.ts              banco de palabras cortas y fonéticas
  assets.ts / guias.ts     guías punteadas de la pizarra
lib/
  levels.ts                motor de etapas y curvas (phasedInt)
  montessori.ts             áreas, tres periodos, control del error
  useMaterial.ts            estado común: nivel, acierto, intento
  progressStore.ts          progreso local (zustand + localStorage)
  audio.ts / speech.ts / haptics.ts
phaser/                     escenas Phaser para movimiento
android/                    proyecto nativo generado por Capacitor
scripts/build-android.mjs   orquesta export + cap sync + gradle
```

## 4. Anatomía de un material

Casi todos los materiales bajo `app/games/<slug>` se arman componiendo:

- **GameShell** — marco común: encabezado, indicador de nivel, consigna y el
  lenguaje del control del error.
- **useMaterial** — hook de estado: nivel actual, acierto, intento, cierre.
- **MaterialQuiz** — lección de tres periodos (12 materiales: formas,
  emociones, animales, letras, palabras en inglés, tierra y agua, partes
  de la planta, sentidos, banderas, cuerpo, instrumentos, el reloj).
- **MaterialOrdenar** — seriación. No solo por tamaño (torre rosa, escalera
  marrón): también sirve para ordenar por secuencia temporal, lógica o
  motriz (ciclo de la mariposa, sistema solar, doblar la tela, ciclo del
  agua, rutina de la mañana), pasando `invertido: true` fijo en la curva de
  niveles para que la fila salga en orden ascendente — ver la nota en
  `data/levels/ciclo-vida.ts`.
- **TrazoGuiado** — recorrer un glifo con el dedo (letras de lija, trazos).
- **LevelSelector** — selector de los 100 niveles en 10 etapas.
- **MaterialClasificar** — clasificación en canastas: un objeto pendiente a
  la vez y N canastas donde soltarlo. Ya 18 materiales lo usan (¿Vivo o no
  vivo?, Cuenta las sílabas, El o la, Pares e impares, Áspero o liso,
  Mayúsculas y minúsculas, ¿Cuántos lados tiene?, Día y noche, Mitades y
  enteros, Palabras que riman, Singular y plural, Caliente o frío,
  Grande/mediano/chico, ¿Qué come?, Estados del agua, Pesado o ligero,
  Dulce o salado, ¿Qué me pongo?). Cada material solo define de dónde
  salen los elementos, a qué canasta pertenece cada uno y cómo se dibujan
  — el control del error, el conteo y el cierre de nivel viven en el
  componente. Es, con diferencia, el patrón más reutilizado del repo.
- **MaterialTransferir** — transferencia por cantidad exacta: una bandeja
  de origen y una de destino, se toma de a una pieza, y pasarse del
  objetivo es el error (Pinza de transferencia, Los husos). Los husos usa
  el slot `extra` para el compartimento numerado y el botón especial del
  cero — ver `app/games/husos/page.tsx`.

## 5. El motor de niveles

`lib/levels.ts` define 10 etapas de 10 niveles. `phasedInt` interpola entre
los valores de cada etapa:

```ts
// Memoria: de tres parejas a dieciséis, con un tramo largo y estable en medio
export const MEMORAMA_LEVELS = levels100((_, level) => ({
  pairs: phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16]),
}));
```

Cambiar la dificultad de un material es editar `data/levels/<slug>.ts`, no
tocar el componente.

## 6. Estado y persistencia

`progressStore.ts` (Zustand + `localStorage`) guarda por material: niveles
completados, estrellas y veces jugado. Sin backend ni sincronización — todo
vive en el dispositivo y se pierde si se desinstala, por diseño.

## 7. Sonido y voz sin un solo archivo

No hay ningún `.mp3` en el repo:

- `lib/audio.ts` sintetiza efectos con WebAudio sobre una escala
  pentatónica. La función interna `tone()` capa dos osciladores por nota (uno
  a la frecuencia exacta, otro a ×1.006) para dar calidez de "coro" en vez de
  un tono fino de juguete — como es el único punto de síntesis del archivo,
  el cambio se siente en los 57 materiales sin tocarlos uno por uno.
- `lib/speech.ts` usa la síntesis de voz del sistema en español, con tabla
  de fonemas.
- `lib/haptics.ts` usa `navigator.vibrate`.

**Textura de papel.** `globals.css` define `.textura-papel`: un `::before`
con ruido fractal (`feTurbulence`) al 5% de opacidad en modo `multiply`,
generado inline como SVG en un data URI (sin archivo de imagen). Aplicada en
`GameShell.tsx` (todos los materiales), `app/page.tsx` (portada) y
`app/padres/page.tsx`. Mismo razonamiento que el audio: un punto de
aplicación compartido en vez de rehacer el fondo de cada pantalla.

**Celebración con el color del área.** `lib/montessori.ts` agrega `acento` y
`acentoOscuro` (hex plano) a cada `AreaInfo`. `StarReward.tsx` acepta un
`slug` opcional, resuelve el área con `getGame()` + `AREAS` (igual que
`GameShell`) y usa esos colores en la estrella y el botón de "Siguiente" en
vez de un ámbar genérico. Conectado en los 4 componentes compartidos
(`MaterialQuiz`, `MaterialOrdenar`, `MaterialClasificar`,
`MaterialTransferir`); los materiales de estilo más antiguo que llaman a
`StarReward` directamente sin `slug` (~31, ver `RECOVERY.md`) siguen con el
color de respaldo.

## 8. Catálogo de datos

`data/games.ts` define `GameDef` (slug, título, emoji, área, edad, material,
objetivo, libre) y expone `games`, `getGame(slug)`, `gamesByArea(area)`.

## 9. Empaquetado a Android

`next.config.ts` genera export estático cuando `CAP_BUILD=1`; el resto del
tiempo Next corre como servidor normal.

| Comando | Qué hace |
|---|---|
| `npm run export` | Solo export estático a `out/` |
| `npm run android` | Export + `cap sync` + `gradlew assembleDebug` |
| `npm run android:abrir` | Abre `android/` en Android Studio |

El APK resultante es de **debug**, firmado con la clave de depuración de
Android — no sirve para publicar en Play Store (exige firma de release).

## 10. CI en GitHub Actions

`.github/workflows/build-apk.yml`: en cada push a `main` que toque
`app/`, `components/`, `data/`, `lib/`, `phaser/`, `public/`, `android/`,
`package.json`, `next.config.ts`, `scripts/` o el propio workflow.

1. Checkout + Node.js 22 + `npm ci`
2. `npm run export`
3. JDK 21 (Temurin) + Android SDK
4. `chmod +x android/gradlew` + `npx cap sync android`
5. `./gradlew assembleDebug` en `android/`
6. Sube `app-debug.apk` como artefacto del run

## 11. Permisos y superficie de red

Un solo permiso Android: `INTERNET` (añadido por Capacitor por omisión, sin
uso real). Sin `fetch`/XHR hacia fuera del dispositivo. En navegador (no en
el APK), Serwist registra un service worker para que la versión web también
funcione offline como PWA.

## 12. Comandos de referencia

| Comando | Qué hace |
|---|---|
| `npm run dev` | Servidor de desarrollo, puerto 40000 |
| `npm run build` | Build de producción (Next server) |
| `npm run start` | Sirve el build de producción |
| `npm run lint` | ESLint sobre todo el repo |
| `npm run qa` | Valida las curvas de niveles (ver sección 14) |
| `npm run export` | Export estático (`CAP_BUILD=1`) |
| `npm run android` | Export + sync + build del APK debug |
| `npm run android:abrir` | Abre `android/` en Android Studio |

## 13. Deuda técnica conocida

- ESLint (reglas React 19) marca `setState` dentro de `useEffect` en varias
  páginas y en los 4 componentes compartidos. No rompe build ni app;
  pendiente de limpiar.
- Tiempos y umbrales en `data/levels/` calibrados a ojo, no medidos con
  usuarios reales.
- El patrón "clasificación en canastas" ya tiene 4 usos y el de
  "transferencia por cantidad exacta" 2 (ambos extraídos a componentes, ver
  sección 4) — pero ni uno ni otro se ha probado interactivamente con un
  dedo real en un dispositivo, solo por HTTP/HTML.
- `StarReward` solo toma el color del área en los ~20 materiales que pasan
  por los 4 componentes compartidos; los ~31 de estilo más antiguo que la
  llaman directo siguen con el color de respaldo (ver sección 7).

## 14. QA automatizado de niveles

`scripts/qa-levels.ts` (`npm run qa`, usa `tsx`) importa las curvas de
nivel de los materiales de esta sesión y valida, para los 100 niveles de
cada uno:

- Forma correcta (100 niveles, campo `level` secuencial, sin `NaN`).
- Que ningún nivel pida más elementos de los que hay disponibles en el
  banco de datos correspondiente (`disponibles(config).length`).
- Reglas propias de cada material (p. ej. `husos`: `objetivo` siempre 0-9).
- Integridad del catálogo completo: slugs únicos, `id === slug`, edades en
  rango.

No sustituye probarlo con las manos, pero ya encontró dos bugs reales:

1. `pares-impares` pedía hasta 10 tarjetas por ronda cuando como máximo hay
   8 cantidades distintas posibles.
2. `temperatura` pedía hasta 12 objetos por ronda (copiado de un material
   con banco de 12) cuando su propio banco solo tiene 8.

En ambos casos `MaterialClasificar` recorta con `Math.min` en vez de
fallar, así que ningún build ni prueba manual superficial lo hubiera
notado — el nivel simplemente mostraba menos elementos de los previstos,
cortando la curva de dificultad antes de tiempo. Los dos se arreglaron
limitando `cantidad` al tamaño real del banco dentro del generador de
niveles, no ajustando la tabla de paradas a mano.

**Lección:** al copiar el patrón de un material existente para uno nuevo,
revisar siempre el tamaño del banco de datos propio — no asumir que es el
mismo que el del material copiado.

Al agregar un material nuevo con curva propia, agregar también su
validación aquí.

La tercera ronda de materiales (`reloj`, `dieta-animal`, `estados-agua`,
`peso`, `sabor`, `clima`, `rutina`) aplicó `Math.min(..., banco.length)`
directamente en cada curva desde el principio, siguiendo la lección de
arriba, y pasó `npm run qa` limpia al primer intento.
