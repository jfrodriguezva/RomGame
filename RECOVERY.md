# RECOVERY.md — recuperar contexto del proyecto en otro chat

Pega este archivo (o su ruta) al inicio de una conversación nueva con Claude
Code para que recupere el contexto completo de este proyecto sin tener que
releer todo el repo desde cero.

## Qué es este proyecto

**Mi Ambiente** — ambiente Montessori digital offline para niños de 3 a 6
años. 57 materiales × 100 niveles (5 700 niveles) + una pizarra de dibujo
libre. Todo en español, sin conexión, sin cuentas, sin publicidad, sin
enviar datos fuera del dispositivo. Es un proyecto personal (no comercial)
del usuario, pensado para uso familiar.

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
- **Estructura:** `app/games/<slug>/` un material por carpeta (56) + `app/pizarra`,
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
- Cuatro actividades son **libres a propósito** (pizarra, juego del
  silencio, marcos de vestir, dado de retos): no tienen niveles ni deberían
  tenerlos.
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

### Objetivo de variedad por área (acordado con el usuario)

| Área | Antes de esta ronda | Objetivo | Estado |
|---|---|---|---|
| 🌍 Cultura y naturaleza | 3 | 7-8 | **7** — dentro del rango ✅ |
| ✍️ Lenguaje | 7 | 9-10 | **9** — dentro del rango ✅ |
| 🫗 Vida práctica | 4 | 6-7 | **6** — dentro del rango ✅ |
| 🔢 Matemáticas | 5 | 7-8 | **7** — dentro del rango ✅ |
| 🎨 Expresión libre | 2 | 3-4 | **3** — dentro del rango ✅ |
| 🔴 Sensorial / 🤝 Compañía / 🤸 Movimiento | 12 / 5 / 8 | ya nutridas | no se tocaron |

**El punto 2 del roadmap (volumen y variedad de contenido) está completo.**
Las 8 áreas están dentro de su rango objetivo. 46 → 57 materiales en total.

Materiales agregados en total (todos reutilizan componentes existentes, sin
tocar `components/` — hasta que se extraigan los dos patrones nuevos, ver
abajo):

- `ciclo-vida` (cultura) — `MaterialOrdenar` reutilizado para secuencia
  temporal, no tamaño.
- `tierra-agua` (cultura) — `MaterialQuiz` con iconos SVG propios de dos
  colores (no hay emoji de "península", etc.).
- `sistema-solar` (cultura) — `MaterialOrdenar` de nuevo, orden por
  distancia al Sol.
- `partes-planta` (cultura) — `MaterialQuiz`, con dos iconos SVG propios
  (raíz, tallo) donde tampoco hay emoji claro.
- `silabas` (lenguaje) — patrón "clasificación en canastas".
- `el-la` (lenguaje) — mismo patrón de canastas, género gramatical.
- `pinza` (vida práctica) — patrón nuevo "transferencia por cantidad
  exacta": tomar de a una pieza de una bandeja a otra, pasarse del
  objetivo es el error.
- `husos` (matemáticas) — mismo patrón de transferencia, con el caso
  especial del 0 (confirmar "no lleva ninguno" en vez de tomar piezas).
- `doblar` (vida práctica) — `MaterialOrdenar` para una secuencia motriz
  (extendida → mitad → cuarto → guardada), no tamaño ni tiempo.
- `pares-impares` (matemáticas) — mismo patrón de canastas, paridad.
- `xilofono` (expresión libre) — libre, sin niveles; reutiliza
  `playNote()` de `lib/audio.ts` (ya existía) y colorea cada barra con el
  tono de una de las 8 áreas del ambiente.

**Actualización: ya extraídos.** `components/MaterialClasificar.tsx` y
`components/MaterialTransferir.tsx` existen — los 6 materiales de arriba
(`seres-vivos`, `silabas`, `el-la`, `pares-impares`, `pinza`, `husos`) los
usan y ya no tienen página propia duplicada. El próximo material de
cualquiera de esas dos formas debe usar el componente compartido
directamente, no copiar una página existente — ver
`docs/MANUAL-TECNICO.md` sección 4 para la firma de cada uno.

### Punto 3 (pulido visual/sonoro): arrancado con el enfoque sistémico

Decisión de diseño: **no** se rehace arte por material (no es realista para
57 páginas en una sesión). En su lugar se mejoran los puntos compartidos que
tocan TODOS los materiales a la vez con una sola edición:

- **Audio** — `lib/audio.ts`, función `tone()`: ahora capa una segunda voz
  desafinada ×1.006 sobre cada nota (calidez de "coro"). Un solo cambio,
  efecto en los 57 materiales.
- **Textura visual** — `globals.css`, clase `.textura-papel` (ruido
  fractal SVG inline al 5%, modo `multiply`). Aplicada en
  `components/GameShell.tsx` (todos los materiales), `app/page.tsx`
  (portada) y `app/padres/page.tsx`.

No se tocó (deliberado, para no reventar el alcance de una sesión):
`StarReward`/`ConfettiOverlay` (ya estaban bien logrados, no se identificó
una mejora clara de bajo riesgo), ilustración custom por material más allá
de la que ya se hizo al crear los materiales nuevos, y `lib/speech.ts` (la
tabla de fonemas ya se revisó y está bien).

Si se retoma este punto, el siguiente candidato de mayor apalancamiento es
seguir por este mismo camino: buscar otro punto único que toque muchos
materiales a la vez, antes que abrir página por página.

Sobre pulido visual/sonoro (punto 3): todavía no se ha tocado. Cuando se
retome, la propuesta ya discutida con el usuario es: (a) sistema de
ilustración SVG propio aplicado primero a los materiales más usados, (b) un
pase de animación con Framer Motion en transiciones clave, (c) enriquecer
`lib/audio.ts` (hoy una escala pentatónica simple) y la tabla de fonemas de
`lib/speech.ts`.

### Cómo seguir agregando materiales (patrón que ya funciona)

1. Elegir una idea Montessori real que encaje en un componente existente:
   `MaterialQuiz` (nomenclatura / tres periodos), `MaterialOrdenar`
   (seriación por tamaño **o por secuencia**, fijando `invertido: true` en
   la curva si el orden debe salir ascendente), o el patrón de
   clasificación en canastas de `seres-vivos.tsx` (copiar esa página como
   base si no encaja en los dos componentes de arriba).
2. Escribir `data/levels/<slug>.ts` con la curva de 100 niveles
   (`levels100` + `phasedInt`, aislando una dificultad nueva por etapa).
3. Escribir `app/games/<slug>/page.tsx` conectando el componente.
4. Agregar la entrada en `data/games.ts` (`id`, `slug`, `title`, `emoji`,
   `area`, `edad`, `material`, `objetivo`, `nuevo: true`).
5. Verificar: `npx tsc --noEmit`, `npx eslint <archivos>`, servidor de
   desarrollo (`npm run dev`, puerto 40000) y confirmar por HTTP que la
   ruta responde 200 y el título aparece en la portada.
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
