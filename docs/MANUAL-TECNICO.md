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
  games/<slug>/           un material por carpeta (90)
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
- **MaterialQuiz** — lección de tres periodos. El listado de qué materiales
  lo usan crece seguido; `grep -rl "components/MaterialQuiz" app/games`
  da la lista exacta en cualquier momento.
- **MaterialOrdenar** — seriación. No solo por tamaño (torre rosa, escalera
  marrón): también sirve para ordenar por secuencia temporal, lógica o
  motriz (ciclo de la mariposa, sistema solar, doblar la tela, ciclo del
  agua, rutina de la mañana, días de la semana, poner la mesa, las
  estaciones del año, lavarse las manos), pasando `invertido: true` fijo
  en la curva de niveles para que la fila salga en orden ascendente — ver
  la nota en `data/levels/ciclo-vida.ts`.
- **TrazoGuiado** — recorrer un glifo con el dedo (letras de lija, trazos).
- **LevelSelector** — selector de los 100 niveles en 10 etapas.
- **MaterialClasificar** — clasificación en canastas: un objeto pendiente a
  la vez y N canastas donde soltarlo. Es, con diferencia, el patrón más
  reutilizado del repo (`grep -rl "components/MaterialClasificar"
  app/games` da la lista exacta y cuántos son en cualquier momento — ya
  pasó de 20). Cada material solo define de dónde salen los elementos, a
  qué canasta pertenece cada uno y cómo se dibujan — el control del error,
  el conteo y el cierre de nivel viven en el componente. Algunos ya usan
  hasta 4 canastas activas por nivel (¿Dónde vive?, con
  selva/desierto/océano/polo entrando de a poco por etapa).
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
vez de un ámbar genérico. **Ya cubre los 90 materiales con niveles**:
primero se conectó en los 4 componentes compartidos, y después se agregó
`slug="<slug>"` a los 31 materiales de estilo más antiguo que llaman a
`StarReward` directamente. Si se agrega un material nuevo de estilo
antiguo (fuera de los 4 componentes), no olvidar pasar `slug`.

`ConfettiOverlay.tsx` sigue el mismo criterio (acepta `slug`, arma una
paleta de 4 tonos con `mezclar()` entre `acento`/`acentoOscuro` y blanco).
**Ya cubre los 90 materiales con niveles**: 4 componentes compartidos +
los 33 materiales que lo llaman directo (los 31 de `StarReward` más
`dado`/`lava`, que no usan `StarReward` pero sí confeti). Ningún material
se quedó con la paleta genérica.

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

- **ESLint: `npm run lint` da 0 errores en todo el repo.** `react-hooks/set-state-in-effect`
  tenía instancias en los 4 componentes compartidos y en ~28 de los 31
  materiales de estilo antiguo (algunos con 2), más `burbujas.tsx`,
  `canasta.tsx`, `globo.tsx` y `pizarra.tsx`. Todas llevan ahora un
  comentario `eslint-disable-next-line` justificando por qué el efecto es
  necesario (genera contenido al azar, habla, celebra con un
  temporizador, o sincroniza con localStorage/una prop que cambia —
  ninguna es una derivación pura calculable en el render).
  De paso se corrigieron dos bugs reales, no solo se documentó el patrón
  conocido: `patron.tsx`/`serpientes.tsx` llamaban una función antes de su
  declaración textual (el linter de React 19 lo marca aunque `function`
  hoistea y funcionaba bien — se reordenó el código) y `rps.tsx` tenía
  `Math.random()` en una función que el linter no puede probar que solo
  se llama desde un clic (`react-hooks/purity`, documentado). También se
  corrigió `ConfettiOverlay.tsx`, que sí violaba pureza de verdad
  (`Math.random()` dentro de `useMemo(..., [])`, que corre en render) —
  cambiado a inicializador perezoso de `useState`. El build empaquetado
  de Capacitor en `android/app/src/main/assets/public/` (JS minificado,
  no fuente) quedó fuera del lint vía `globalIgnores` en
  `eslint.config.mjs` — antes generaba ~46 errores falsos de código
  ajeno (React/framer-motion minificados).
- Tiempos y umbrales en `data/levels/` calibrados a ojo, no medidos con
  usuarios reales.
- Ninguno de los 91 materiales se ha probado interactivamente con un dedo
  real en un dispositivo — todo lo verificado hasta ahora es build +
  HTTP/HTML + curvas de datos (`npm run qa`).
- `StarReward` y `ConfettiOverlay` ya toman el color del área en los 90
  materiales con niveles (ver sección 7) — resuelto.
- **Punto 3 del roadmap (pulido visual/sonoro) cerrado.** `app/layout.tsx`
  envuelve toda la app en `<MotionConfig reducedMotion="user">`: cada
  animación de framer-motion, en los 91 materiales, respeta la
  preferencia de accesibilidad del sistema con una sola edición. Ver
  `RECOVERY.md` para el detalle completo de lo que se cerró.

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

La tercera, cuarta y quinta ronda de materiales aplicaron
`Math.min(..., banco.length)` directamente en cada curva desde el
principio, siguiendo la lección de arriba, y las tres pasaron `npm run qa`
limpias al primer intento — la lección se quedó aprendida de verdad, no
solo documentada.
