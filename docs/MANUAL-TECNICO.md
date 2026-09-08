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
  games/<slug>/           un material por carpeta (56)
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
- **MaterialQuiz** — lección de tres periodos (formas, emociones, animales,
  letras, palabras en inglés, tierra y agua, partes de la planta).
- **MaterialOrdenar** — seriación. No solo por tamaño (torre rosa, escalera
  marrón): también sirve para ordenar por secuencia temporal, lógica o
  motriz (ciclo de la mariposa, sistema solar, doblar la tela), pasando
  `invertido: true` fijo en la curva de niveles para que la fila salga en
  orden ascendente — ver la nota en `data/levels/ciclo-vida.ts`.
- **TrazoGuiado** — recorrer un glifo con el dedo (letras de lija, trazos).
- **LevelSelector** — selector de los 100 niveles en 10 etapas.

Un cuarto patrón, usado por `seres-vivos`, `silabas`, `el-la` y
`pares-impares` pero aún no extraído a un componente compartido, es la
**clasificación en canastas**: se arma `useMaterial` a mano dentro de la
página (sin `MaterialQuiz`/`MaterialOrdenar`), con un objeto pendiente a la
vez y N canastas donde soltarlo. Con cuatro materiales ya en este patrón,
extraerlo a un `MaterialClasificar` compartido en `components/` es ya
trabajo pendiente, no solo una opción a futuro.

Un quinto patrón, usado por `pinza` y `husos`, es la **transferencia por
cantidad exacta**: una bandeja de origen y una de destino, se toma de a una
pieza, y pasarse del objetivo cuenta como error (no hay límite superior
impuesto por la UI, es el mismo material el que "no tiene dónde poner" la
pieza de más). Tampoco está extraído a un componente — si se agrega un
tercer material con esta forma, igual que arriba, vale la pena un
`MaterialTransferir` compartido.

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
| `npm run export` | Export estático (`CAP_BUILD=1`) |
| `npm run android` | Export + sync + build del APK debug |
| `npm run android:abrir` | Abre `android/` en Android Studio |

## 13. Deuda técnica conocida

- ESLint (reglas React 19) marca `setState` dentro de `useEffect` en varias
  páginas. No rompe build ni app; pendiente de limpiar.
- Tiempos y umbrales en `data/levels/` calibrados a ojo, no medidos con
  usuarios reales.
