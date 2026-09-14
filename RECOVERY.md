# RECOVERY.md — recuperar contexto del proyecto en otro chat

Pega este archivo (o su ruta) al inicio de una conversación nueva con Claude
Code para que recupere el contexto completo de este proyecto sin tener que
releer todo el repo desde cero.

## Qué es este proyecto

**Mi Ambiente** — ambiente Montessori digital offline para niños de 3 a 6
años. 90 materiales — 85 con 100 niveles cada uno (8 500 niveles) y 5
actividades libres sin niveles. La primera pantalla pide la edad del niño
(2 a 6 años) antes del menú y filtra el catálogo por ella — ver "Ronda 7"
más abajo. Todo en español, sin conexión, sin
cuentas, sin publicidad, sin enviar datos fuera del dispositivo. Es un
proyecto personal (no comercial) del usuario, pensado para uso familiar.

- **Repo:** `git@github.com:jfrodriguezva/RomGame.git` (GitHub:
  `jfrodriguezva/RomGame`)
- **Dueño / autor git:** Juan Francisco Rodriguez Vazquez
  (jfrv.paco@gmail.com)
- **Rama principal:** `main`
- **Última sincronización confirmada con `origin/main`:** `92aab23` —
  "Prepara el APK: rutas como carpeta, icono propio y permisos documentados"
  (2026-08-30). Desde entonces hay commits locales de documentación y de
  ampliación de contenido (ver `git log` para el estado real — puede que no
  se hayan subido con `git push` todavía).

> Estos datos son un snapshot. Al retomar, corre `git status`, `git log -5
> --oneline` y `git fetch origin` para confirmar el estado real antes de
> asumir nada de aquí.

## Cómo correr el proyecto

```bash
npm install
npm run dev        # http://localhost:40000
npm run android     # export + cap sync + gradlew assembleDebug -> APK
```

Requiere para `npm run android`: JDK 21 y Android SDK con
`JAVA_HOME`/`ANDROID_HOME` apuntando a ellos (detalles en `INSTALAR.md`).

## Dónde está la documentación

| Archivo | Contenido |
|---|---|
| `README.md` | Presentación del proyecto, filosofía Montessori, estructura |
| `INSTALAR.md` | Instalar el APK en una tablet Android (paso a paso) |
| `docs/MANUAL-USUARIO.md` | Manual de usuario completo (para padres) |
| `docs/MANUAL-TECNICO.md` | Manual técnico completo (arquitectura, stack, build) |
| `AGENTS.md` / `CLAUDE.md` | Instrucciones de proyecto para agentes de código (Next.js "no estándar": leer `node_modules/next/dist/docs/` antes de escribir código — ver nota abajo) |

Léelos en ese orden si necesitas reconstruir contexto completo. Los dos
manuales en `docs/` son la fuente más completa y están generados a partir
del estado real del código, no inventados.

## Resumen técnico (una pantalla)

- **Stack:** Next.js 16.3.2 (App Router) + React 19.2.8 + TypeScript +
  Tailwind CSS 4 + Zustand 5 (estado/persistencia) + Framer Motion + Phaser 4
  (materiales de movimiento) + Capacitor 8.5 (empaquetado Android) + Serwist
  (PWA en web).
- **Estructura:** `app/games/<slug>/` un material por carpeta (89) + `app/pizarra`,
  `app/padres`, `app/admin`. Componentes compartidos en `components/`
  (`GameShell`, `MaterialQuiz`, `MaterialOrdenar`, `TrazoGuiado`,
  `LevelSelector`). Contenido/curvas de dificultad en `data/` (`games.ts` es
  el catálogo maestro). Lógica de dominio en `lib/` (`levels.ts` = motor de
  etapas, `progressStore.ts` = persistencia local).
- **Niveles:** 10 etapas × 10 niveles por material, definidas con
  `phasedInt()` en `lib/levels.ts` / `data/levels/<slug>.ts`.
- **Sonido/voz:** sintetizados en runtime (WebAudio + SpeechSynthesis), cero
  archivos de audio en el repo.
- **Android:** `next.config.ts` exporta estático cuando `CAP_BUILD=1`;
  `scripts/build-android.mjs` orquesta export + `cap sync` + Gradle. APK de
  **debug** (no release, no Play Store). CI en
  `.github/workflows/build-apk.yml` sube el APK como artefacto en cada push
  relevante a `main`.
- **Privacidad:** único permiso Android `INTERNET` (sin uso real, lo agrega
  Capacitor por defecto). Cero llamadas de red. Progreso solo en
  `localStorage` del dispositivo.

## Decisiones de diseño que no son obvias leyendo el código

Guardar esto porque no está en comentarios ni se deduce fácil del código:

- El **control del error lo da el material, no la app** — nunca hay
  mensajes de "perdiste" ni feedback negativo fuerte. Si se cambia la
  lógica de un material, respetar este principio.
- Los niveles **no son una curva lineal**: dentro de una etapa la
  dificultad casi no cambia (repetición cómoda a propósito), y sube de
  golpe al cambiar de etapa. No "suavizar" esto sin que lo pida el usuario.
- Cinco actividades son **libres a propósito** (pizarra, juego del
  silencio, marcos de vestir, dado de retos, xilófono): no tienen niveles
  ni deberían tenerlos. `data/games.ts` expone `materialesConNiveles` y
  `TOTAL_NIVELES` (52 × 100 = 5 200) para no tener que recalcular esto a
  mano — si se agrega un material, ese número se actualiza solo.
- El APK es de **debug** intencionalmente (uso familiar/personal, no
  publicación en Play Store). No asumir que hay que migrar a firma de
  release salvo que el usuario lo pida explícitamente.
- Hay una deuda técnica **conocida y aceptada**: warnings de ESLint
  (React 19) por `setState` dentro de `useEffect` en varias páginas. No
  bloquea build ni funcionamiento; no "arreglarla" de oficio sin que se
  pida — es una limpieza pendiente, no un bug activo.

## Comparación conocida: vs. Montessori Preschool (L'Escapadou / Edoki Academy)

En una conversación previa se comparó este proyecto contra la app comercial
más conocida del género, **Montessori Preschool** (Edoki Academy /
L'Escapadou — iOS/Android, +1000 actividades, hecha por profesoras
Montessori certificadas, suscripción mensual/anual con prueba de 7 días, 8
idiomas, sin ads, funciona offline).

Veredicto resumido: los fundamentos pedagógicos de "Mi Ambiente" (control
del error, tres periodos, aislar la dificultad) están bien entendidos y
correctamente aplicados, no son cosméticos — notable para un proyecto
personal de un solo desarrollador. Donde la app comercial gana claramente es
volumen de contenido, pulido visual/sonoro profesional, alcance
multiplataforma real (iOS + Android vs. solo Android debug) y madurez de
producto/soporte. La ventaja real de "Mi Ambiente" es que es gratuito, 100%
privado (sin suscripción, sin telemetría), diseñado nativamente para
fonética en español (no traducido), y completamente editable por el dueño
del proyecto.

> Este veredicto es de la primera comparación (46 materiales, sin QA, sin
> pulido). El estado real hoy está en la sección de abajo — en volumen de
> contenido ya no hay brecha, está por encima (71 materiales, a pedido
> explícito del usuario de que fuera "superior a Montessori" en variedad);
> en pulido visual/sonoro sigue habiendo diferencia, aunque menos que al
> principio.

## Roadmap activo: emparejar a Montessori Preschool (en curso)

El usuario decidió explícitamente, tras la comparación de arriba, **cerrar la
brecha con Montessori Preschool sin salir del modelo personal/gratuito**.
Decisiones tomadas (no volver a preguntar esto, ya está resuelto):

1. **Se mantiene personal.** Sin tienda, sin firma de release, sin cuenta de
   desarrollador. No proponer publicar en Play/App Store salvo que el
   usuario lo pida de nuevo explícitamente.
2. **Emparejar volumen y variedad de contenido.** Sí, es el foco activo.
3. **Emparejar pulido visual/sonoro.** Sí, es el foco activo (después o en
   paralelo con el punto 2).
4. **Panel de padres:** no hace falta mejorarlo — es local, el usuario lo
   revisa a mano.
5. **Validación pedagógica externa (profesoras certificadas):** no importa,
   el proyecto no es comercial.
6. **Actualizaciones automáticas vía tienda:** no aplica (consecuencia del
   punto 1).

### Objetivo de variedad por área — superado ampliamente en todas

| Área | Original | Objetivo inicial | Estado ahora |
|---|---|---|---|
| 🌍 Cultura y naturaleza | 3 | 7-8 | **21** — muy por encima ✅ |
| 🔴 Sensorial | 12 | ya nutrida | **20** — se sumó bastante ✅ |
| ✍️ Lenguaje | 7 | 9-10 | **12** — por encima ✅ |
| 🔢 Matemáticas | 5 | 7-8 | **10** — por encima ✅ |
| 🫗 Vida práctica | 4 | 6-7 | **11** — por encima ✅ |
| 🎨 Expresión libre | 2 | 3-4 | **4** — dentro del rango ✅ |
| 🤝 Compañía / 🤸 Movimiento | 5 / 8 | ya nutridas | no se tocaron (Phaser, no encajan en el patrón de datos) |

**✅ CERRADO por instrucción explícita del usuario ("termina"), reabierto una
vez a pedido explícito para cerrar el hueco de verdad.** El punto 2 del
roadmap (volumen y variedad de contenido) se dio por completo tras 5
rondas (46 → 90 materiales, ver abajo). El usuario pidió explícitamente
retomarlo una sexta vez para construir el único candidato honesto que
había quedado documentado (ver más abajo): **`collage` — Collage
libre**, la interacción nueva para 🎨 Expresión libre que no encajaba en
ningún componente reutilizable. 90 → **91 materiales** (85 con 100
niveles = 8 500 niveles, **6 libres** — `collage` se suma a `pizarra`,
`xilofono`, `mesa-silencio` y los otros dos existentes, sin niveles por
diseño: imponerle una condición de "completo" a una actividad
deliberadamente sin consigna sería forzado).

**Por qué se cerró aquí y no se forzó una sexta ronda:** la ronda 5 ya
había rendido solo 5 materiales frente a los 7-11 de las rondas
anteriores — varias ideas candidatas (contar hacia atrás, comparativos de
tamaño, secuencias de patrones AB-AB, sonidos fuertes/suaves) se
descartaron por sentirse forzadas o redundantes. Esa señal, más la
instrucción directa de concluir, es la razón de cerrar aquí en vez de
seguir empujando el número.

**Construido en la ronda 6, a pedido explícito.** `app/games/collage/page.tsx`
+ `data/collage.ts` (5 categorías de estampas, 5 escenas de fondo en CSS
puro, sin imágenes). Interacción: tocar el lienzo coloca la estampa
elegida en ese punto (con rotación y escala aleatorias para que no se
sientan clonadas), tocar una estampa ya puesta la quita — sin arrastrar,
igual de simple que el sello de la pizarra. `libre: true` como
`pizarra`/`xilofono`: sin niveles, sin meta, sin condición de "completo"
— sería forzado imponerle una en una actividad que es, por diseño, sin
consigna. No reutiliza `MaterialQuiz`/`MaterialOrdenar`/
`MaterialClasificar`/`MaterialTransferir` porque ninguno modela
"posición libre en un plano 2D"; es la única pieza de ingeniería nueva
de las 6 rondas, no el patrón rápido de datos + componente existente.

**Decisión explícita del usuario sobre cómo seguir (no volver a preguntar
esto):** cuando se preguntó "¿en variedad ya está igual a Montessori?" la
respuesta honesta fue no, y se le ofrecieron 3 caminos (seguir en lotes
reales sin meta numérica / cambiar la métrica a niveles totales / bajar la
calidad para acercarse más al número). **Eligió el primero explícitamente:
seguir agregando materiales Montessori auténticos en lotes, sin perseguir
un número fijo, aceptando que probablemente nunca se llegue a "1000".**
Más tarde, en la misma sesión, dio instrucción expresa de **"continúa con
lote de materiales hasta que se repitan"** — es decir, seguir hasta notar
la señal de arriba, no parar antes ni forzar después. Cuando esa señal
apareció (ronda 5, ver abajo), el usuario confirmó explícitamente
**"termina"**: cerrar el punto 2 ahí, no seguir sumando. No hace falta
volver a plantear esta pregunta ni reabrir el punto 2 sin que el usuario
lo pida de nuevo.

**Calibración honesta sobre "superior a Montessori" (importante, no
repetir el error):** Montessori Preschool anuncia +1000 actividades
distintas; nosotros tenemos 91 materiales distintos (con 100 niveles de
profundidad cada uno, que ellos probablemente no tienen de la misma
forma). Si se cuenta "tipos de actividad distintos" (la métrica justa, la
que ellos usan en su propio marketing), **seguimos muy por debajo**.
Donde sí estamos a la par o mejor es en **cobertura de las 8 áreas del
currículo Montessori clásico** — esa es la métrica que se superó, no el
conteo bruto.

44 materiales nuevos en total esta sesión, en 5 rondas, todos reutilizando
componentes existentes (`MaterialQuiz`, `MaterialOrdenar`,
`MaterialClasificar`, `MaterialTransferir`). Ver el catálogo completo en
`data/games.ts` (buscar `nuevo: true`) para la lista exacta con área, slug
y objetivo pedagógico de cada uno — dejó de listarse material por material
aquí porque ya no es el lugar práctico para mantenerlo actualizado.

`components/MaterialClasificar.tsx` (el patrón más reutilizado del repo,
ya pasó de 20 materiales; algunos usan hasta 4 canastas activas por
nivel) y `components/MaterialTransferir.tsx` (2 materiales, `pinza`/
`husos`) — ver `docs/MANUAL-TECNICO.md` sección 4 para el comando que da
la lista exacta en cualquier momento. Ninguno tiene página propia
duplicada.

**No tocadas, y no hay plan de tocarlas:** 🤝 Compañía y 🤸 Movimiento. Sus
materiales son escenas de Phaser (canvas/física), no encajan en el patrón
de datos + componente compartido — agregar ahí es mucho más caro por
material. Si se quiere seguir creciendo variedad, las áreas de datos
siguen siendo el camino barato, pero ver la señal de arriba antes de
forzar una ronda más.

### Punto 3 (pulido visual/sonoro): CERRADO por instrucción explícita del usuario ("termina")

Decisión de diseño: **no** se rehace arte por material (no es realista para
90 páginas en una sesión, y cada ronda de materiales nuevos agranda la
brecha). En su lugar se mejoran los puntos compartidos
que tocan muchos materiales a la vez con una sola edición:

- **Audio** — `lib/audio.ts`, función `tone()`: capa una segunda voz
  desafinada ×1.006 sobre cada nota (calidez de "coro"). Un solo cambio,
  efecto en todos los materiales.
- **Textura visual** — `globals.css`, clase `.textura-papel` (ruido
  fractal SVG inline al 5%, modo `multiply`). Aplicada en
  `components/GameShell.tsx`, `app/page.tsx` y `app/padres/page.tsx`.
- **Celebración con el color del área — resuelto por completo.**
  `lib/montessori.ts` suma `acento`/`acentoOscuro` a cada `AreaInfo`.
  `StarReward.tsx` y `ConfettiOverlay.tsx` aceptan un `slug` opcional y lo
  usan. **Los 90 materiales con niveles ya tienen los dos** con el color
  de su área: 4 componentes compartidos + los 31 materiales de estilo
  antiguo (`StarReward`) + los mismos 31 más `dado`/`lava`
  (`ConfettiOverlay`, que no usan `StarReward` pero sí confeti). Ningún
  material quedó con la paleta/color genérico.
- **ESLint, ya sin ningún error real pendiente en `app/games/`.**
  `react-hooks/set-state-in-effect` resuelto con supresión justificada en
  los 4 componentes compartidos **y en los 31 materiales de estilo
  antiguo** (antes decía "no resuelto, deliberadamente" — se hizo en esta
  misma sesión, ver más abajo). De paso salieron dos bugs de verdad, no
  solo el patrón de siempre: `patron.tsx` y `serpientes.tsx` llamaban a una
  función antes de su declaración textual (funcionaba por el hoisting de
  `function`, pero el linter de React 19 lo marca — se reordenó el código,
  cero cambio de comportamiento), y `rps.tsx` usaba `Math.random()` dentro
  de una función que el linter no puede probar que solo se llama desde un
  clic (`react-hooks/purity`, documentado con supresión). También se
  corrigió una violación de pureza real en `ConfettiOverlay.tsx`: generaba
  las piezas del confeti con `Math.random()` dentro de `useMemo(..., [])`,
  que sí corre en fase de render — se cambió a un inicializador perezoso de
  `useState`, el lugar correcto para cómputo único que puede ser impuro.
- **Transición de la consigna** — `components/GameShell.tsx` anima el
  texto de instrucción con un fundido + desplazamiento de 6px al cambiar
  de ronda (`AnimatePresence`/`motion.p`), en vez de aparecer de golpe.
- **`prefers-reduced-motion` — un solo `<MotionConfig reducedMotion="user">`
  en `app/layout.tsx` envolviendo toda la app.** Framer Motion no
  respetaba la preferencia de accesibilidad del sistema en ningún
  material (ni una referencia en todo el repo antes de esto); ahora cada
  animación de cada material — springs de `StarReward`, confeti,
  transiciones de `MaterialQuiz`/`MaterialOrdenar`/`MaterialClasificar`/
  `MaterialTransferir`, la consigna, mandalas de la pizarra — respeta
  automáticamente la preferencia del sistema operativo, sin tocar un
  solo material. Es la edición más "sistémica" que quedaba: una sola
  línea, efecto en los 90.
- **Conteo de materiales desactualizado en metadata pública** —
  `app/layout.tsx` (descripción SEO/PWA) y `public/manifest.json` decían
  "43" y "46" materiales respectivamente, arrastrado de rondas
  anteriores; corregido a 90 en ambos.

Sigue sin tocarse, y **se cierra el punto 3 sin hacerlo**: ilustración
custom por material más allá de la que ya trajeron los materiales
nuevos. No es un atajo sistémico — es diseño genuino material por
material (90 páginas), fuera de alcance de una sesión de este proyecto
personal. Con el color de área, el ESLint, la animación de consigna y
`prefers-reduced-motion` ya resueltos en los 90 materiales, **no queda
ninguna edición compartida más que aplicar de una sola vez** — cualquier
mejora visual/sonora que siga es trabajo por material, no otra ronda de
este punto.

### QA y validación (nuevo esta sesión)

`npm run qa` (`scripts/qa-levels.ts`) valida las curvas de nivel de los
materiales agregados en esta sesión: forma correcta, sin `NaN`, y que
ningún nivel pida más elementos de los que el banco de datos tiene
disponibles. **Encontró 2 bugs reales** en la primera y segunda ronda de
materiales (`pares-impares` y `temperatura`: ambos pedían más elementos
por ronda de los que su propio banco tenía, copiado de un material con
banco más grande) que ningún build ni prueba manual superficial hubiera
notado — ver `docs/MANUAL-TECNICO.md` sección 14 para el detalle. La
tercera, cuarta y quinta ronda aplicaron `Math.min(..., banco.length)`
desde el diseño y pasaron limpias al primer intento — la lección se quedó
aprendida de verdad.

**Sigue pendiente probar cualquiera de los materiales con las manos en un
dispositivo real**: todo lo verificado hasta ahora, en las cinco rondas,
es build + HTTP/HTML + curvas de datos, nunca interacción táctil real. Con
90 materiales acumulados sin ni un minuto de uso real, esto pesa cada vez
más — es la validación más importante que falta, y no la puede hacer un
agente de código.

### Cómo seguir agregando materiales (patrón que ya funciona)

1. Elegir una idea Montessori real que encaje en un componente existente:
   `MaterialQuiz` (nomenclatura / tres periodos), `MaterialOrdenar`
   (seriación por tamaño **o por secuencia**, fijando `invertido: true` en
   la curva si el orden debe salir ascendente), `MaterialClasificar`
   (clasificación en canastas) o `MaterialTransferir` (transferencia por
   cantidad exacta). Los cuatro están en `components/`, con su firma
   documentada en `docs/MANUAL-TECNICO.md` sección 4.
2. Escribir `data/levels/<slug>.ts` con la curva de 100 niveles
   (`levels100` + `phasedInt`, aislando una dificultad nueva por etapa) —
   si el material tiene un banco de datos limitado (palabras, figuras...),
   asegurarse de que `cantidad`/`objetivo` nunca exceda lo disponible en
   niveles altos (ver el bug real que esto causó en `pares-impares`).
3. Escribir `app/games/<slug>/page.tsx` conectando el componente.
4. Agregar la entrada en `data/games.ts` (`id`, `slug`, `title`, `emoji`,
   `area`, `edad`, `material`, `objetivo`, `nuevo: true`).
5. Verificar: `npx tsc --noEmit`, `npx eslint <archivos>`, `npm run qa`
   (agregar la validación del material nuevo en `scripts/qa-levels.ts`),
   servidor de desarrollo (`npm run dev`, puerto 40000) y confirmar por
   HTTP que la ruta responde 200 y el título aparece en la portada. Antes
   de dar el trabajo por cerrado, correr también `npm run export` (el
   build real que usa el APK) — no solo el dev server.
6. Actualizar conteos en `README.md`, `docs/MANUAL-USUARIO.md`,
   `docs/MANUAL-TECNICO.md` y este archivo.

## Cómo retomar trabajo aquí

1. `git status` y `git fetch origin` para confirmar sincronía real.
2. Releer este archivo + `docs/MANUAL-TECNICO.md` si el trabajo es técnico,
   o `docs/MANUAL-USUARIO.md` si es sobre contenido/experiencia pedagógica.
3. Para cambios de dificultad de un material: tocar `data/levels/<slug>.ts`,
   no el componente.
4. Para un material nuevo: seguir el patrón de `app/games/<slug>/` +
   entrada en `data/games.ts` + curva en `data/levels/<slug>.ts`,
   reutilizando `GameShell`/`useMaterial`/etc. de `components/`.
5. Antes de generar código, revisar `AGENTS.md`: este repo usa una versión
   de Next.js con cambios respecto al training data del modelo, y pide leer
   `node_modules/next/dist/docs/` antes de escribir código Next.js nuevo.

## Ronda 7: feedback de uso real (bugs, tamaños, contenido, edad)

El usuario probó la app de verdad (no solo build/QA) y volvió con una lista
larga de problemas concretos y pedidos. Todo lo de abajo está hecho y
commiteado; nada quedó a medias.

**Bugs reales de gameplay, no solo pulido:**

- **`lava` reescrito por completo.** El primer arreglo (comprobar la
  baldosa siguiente por índice) no alcanzó — el usuario probó y "seguía
  fallando". El problema de fondo era el diseño (baldosas discretas +
  comprobación a tiempo fijo), no un número mal calculado. Se rehizo como
  el dinosaurio de Chrome: física continua, colisión revisada cada cuadro
  contra la posición real de las llamaradas, no un cálculo "en qué baldosa
  vas a caer" hecho una sola vez al saltar. Funciona igual en todos los
  niveles, sin depender de la velocidad.
- **`arana` — el lazo se cerraba a mitad del arrastre.** Apenas el trazo
  llegaba a 3 celdas y tocaba el borde, revelaba esas 3 celdas y reseteaba
  — obligaba a "pintar" el tablero de a 3 en 3 contra arañas activas,
  virtualmente imposible de ganar. El cierre pasa a soltar el dedo (un
  lazo grande de un solo gesto). Además celdas más grandes (hasta 64px,
  calculadas por el lado mayor de la cuadrícula) a pedido explícito.
- **`serpientes` — bug real de React, no de diseño.** `movePlayer` tenía
  sonidos y cambios de turno *dentro* del updater de `setState`, un efecto
  secundario en una función que React puede invocar más de una vez (modo
  estricto de desarrollo) — el turno avanzaba doble y los sonidos sonaban
  doble, desincronizando jugador y CPU. Se reescribió para leer la
  posición actual directo (no un updater funcional) y hacer los efectos
  una sola vez, fuera del `setState`. Vale la pena revisar `globo` y
  `burbujas` por el mismo patrón si algún día fallan de forma similar —
  no se tocaron esta ronda porque no fueron reportados como rotos.
- **`toystory` y `carreras` — controles sin `touch-action: none`.** En un
  teléfono real, un dedo sostenido sobre un botón de mover/saltar puede
  interpretarse como gesto de scroll y cancelar el toque a medio camino —
  el control se siente roto aunque la lógica del juego esté bien. Se
  agregó `touch-none` a los botones y al contenedor del canvas de Phaser
  en ambos.
- **`laberinto` — desbordaba la pantalla en niveles grandes.** Un
  laberinto de 20×20 a 32px por celda mide 640px, más ancho que cualquier
  teléfono. La celda ahora se calcula para que el tablero mida ~336px como
  máximo, así que crece en niveles chicos (hasta 72px) sin desbordar en
  los grandes.

**Pulido visual pedido explícitamente:**

- `toystory` (ToyStoryScene): cielo con degradado, sol y nubes con
  parallax, pasto texturizado, enemigos con carita, bandera que ondea,
  estirón al saltar y aplastón al aterrizar.
- `carreras` (RaceScene): coches con carrocería/parabrisas/llantas reales
  (4 colores de rival) en vez de rectángulos planos; líneas de carril que
  se desplazan cada cuadro.
- `doblar` — antes de eliminarlo (ver abajo) se le agregó patrón a
  cuadros, líneas de pliegue y sombra apilada, porque "no lo entiendo" era
  un problema real: un rectángulo liso no evoca tela.
- `vestir` — antes de eliminarlo, el cuerpo base (cara, pelo, brazos) dejó
  de desaparecer al elegir un vestido; antes el personaje entero se
  reemplazaba por la prenda flotando sin cuerpo.
- `globo` — empieza con 1 globo y sube a 2/3/4 por etapa (antes siempre
  era un solo globo fijo).

**Pizarra:**

- **Colorear** (nuevo apartado): figuras cerradas de `data/guias.ts`
  (ahora 13, se sumaron pez/mariposa/flor/nube/árbol) como plantilla
  sólida para pintar a mano libre con cualquier pincel — distinto de la
  guía de trazo (punteada, para repasar) y del material `colorear`
  (regiones que se rellenan tocándolas).
- **Misión** (nuevo apartado): 12 íconos piden un dibujo sin necesitar
  leer; "¡Listo!" celebra con confeti y ofrece otra misión al azar. El
  badge quedaba tapado por la barra superior (`top-3` vs. la barra en
  `top-0`) — se movió a `top-16`.
- Letras en minúscula agregadas a las guías (`GUIAS_POR_GRUPO.minusculas`)
  — el material real de letras de lija enseña minúscula primero.

**Contenido:**

- `collage`: botón "Deshacer" (quita solo la última estampa), 2
  categorías nuevas (comida, caritas), variantes sumadas a las
  existentes, 3 escenas nuevas (playa, atardecer, nieve).
- `colorear`: 4 diseños figurativos nuevos (estrella, corazón, globo,
  nube), de 8 a 12.
- **Eliminados a pedido explícito:** `vestir`, `doblar` (doblar la tela) y
  `clima` (¿qué me pongo?) — los tres de vida práctica. Sin rastro en el
  catálogo, `scripts/qa-levels.ts` ni las páginas.
- **Dos materiales nuevos a pedido explícito:**
  - `cara` (Toca la cara, cultura) — nomenclatura de la cara tocando
    directo sobre un dibujo (no eligiendo de una lista como
    `MaterialQuiz`). Los pares (ojo/oreja/ceja/mejilla) comparten un
    mismo id: tocar cualquiera de los dos cuenta, porque a esta edad no
    se enseña lateralidad.
  - `orificios` (Encaja la figura, sensorial) — encajes de formas
    geométricas: la pieza solo entra en su agujero exacto. Distinto del
    ya existente `formas` (nomenclatura por nombre); este es
    discriminación visual pura, sin nombrar nada.
  - Ninguno de los dos reutiliza los 4 componentes compartidos: ninguno
    modela "tocar una región dentro de una sola imagen" ni "dibujar cada
    opción como un recorte real" — son las primeras piezas nuevas de
    interacción desde `collage` en la ronda 6.

**Home rediseñado:** selector de área (chips con ícono + cantidad) en vez
de las 8 áreas completas expandidas una tras otra — antes había que hacer
scroll por todo el catálogo para llegar a la última área. "Ver todas"
conserva el scroll completo para quien lo prefiera.

**Selector de edad antes del tema (el pedido más grande de la ronda):**
nueva pantalla de entrada, antes del menú, que pide la edad (2 a 6 años) y
filtra el catálogo por ella — solo el mínimo de `edad: [min, max]`, así
que un niño mayor no pierde acceso a material "más chico", pero uno chico
no ve lo que requiere más edad. No hizo falta re-etiquetar los 90
materiales uno por uno: el campo `edad` ya existía en cada `GameDef` y ya
reflejaba en la práctica la secuencia Montessori real (8 de los 12
materiales de Lenguaje ya estaban en `edad: [4, 6]`, no `[3, 6]`). Se
puede cambiar después desde "Mamá y papá". Simulación antes de
implementar: edad 2 → 4 materiales, edad 3 → 50, edad 4 → 89, edad 5/6 →
90 (todo el catálogo). Ver `docs/MANUAL-TECNICO.md` sección 13 para el
detalle técnico (`lib/settings.ts`, `components/SelectorEdad.tsx`).

**Pendiente, explícitamente no resuelto esta ronda:** el usuario pidió
"buscar más juegos de mesa que sean mejores y más interactivos, dame
muchas opciones" — esto se respondió con una lista de propuestas en la
conversación (no en este archivo, para no comprometerse con ideas que el
usuario todavía no aprobó). Si se retoma en otra sesión sin ese contexto,
preguntar directamente qué tipo de juego de mesa le interesa antes de
construir nada — la ronda 5 (ver arriba) ya mostró el costo de generar
contenido sin esa validación previa.
