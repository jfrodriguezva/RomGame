# RECOVERY.md — recuperar contexto del proyecto en otro chat

Pega este archivo (o su ruta) al inicio de una conversación nueva con Claude
Code para que recupere el contexto completo de este proyecto sin tener que
releer todo el repo desde cero.

## Qué es este proyecto

**Mi Ambiente** — ambiente Montessori digital offline para niños de 3 a 6
años. 46 materiales × 100 niveles (4 200 niveles) + una pizarra de dibujo
libre. Todo en español, sin conexión, sin cuentas, sin publicidad, sin
enviar datos fuera del dispositivo. Es un proyecto personal (no comercial)
del usuario, pensado para uso familiar.

- **Repo:** `git@github.com:jfrodriguezva/RomGame.git` (GitHub:
  `jfrodriguezva/RomGame`)
- **Dueño / autor git:** Juan Francisco Rodriguez Vazquez
  (jfrv.paco@gmail.com)
- **Rama principal:** `main`
- **Último commit conocido en este snapshot:** `92aab23` — "Prepara el APK:
  rutas como carpeta, icono propio y permisos documentados" (2026-08-30)
- **Estado en ese momento:** working tree limpio, `main` sincronizado 1:1
  con `origin/main`.

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
- **Estructura:** `app/games/<slug>/` un material por carpeta (45) + `app/pizarra`,
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
