/**
 * Motor de progresión de niveles.
 *
 * Cada material (juego) tiene 100 niveles agrupados en 10 etapas de 10.
 * La idea Montessori detrás: el niño repite el mismo material muchas veces,
 * y el material va "aislando una dificultad nueva" cada tanto en vez de
 * volverse solo más rápido. Por eso la curva no es una recta: dentro de una
 * etapa la dificultad sube poco (repetición cómoda) y al cambiar de etapa da
 * un salto (nueva dificultad aislada).
 */

export const LEVEL_COUNT = 100;
export const DEFAULT_LEVEL_COUNT = LEVEL_COUNT;
export const STAGE_SIZE = 10;
export const STAGE_COUNT = LEVEL_COUNT / STAGE_SIZE;

export interface StageInfo {
  stage: number;
  name: string;
  emoji: string;
  from: number;
  to: number;
}

/** Las 10 etapas comparten nombre en toda la app para que el niño las reconozca. */
export const STAGES: StageInfo[] = [
  { stage: 1, name: "Primeros pasos", emoji: "🌱" },
  { stage: 2, name: "Ya lo entiendo", emoji: "🌿" },
  { stage: 3, name: "Con confianza", emoji: "🍀" },
  { stage: 4, name: "Más atento", emoji: "🐝" },
  { stage: 5, name: "Buena memoria", emoji: "🦋" },
  { stage: 6, name: "Manos expertas", emoji: "🌻" },
  { stage: 7, name: "Ojo fino", emoji: "🦉" },
  { stage: 8, name: "Gran reto", emoji: "🏔️" },
  { stage: 9, name: "Casi maestro", emoji: "🌟" },
  { stage: 10, name: "Maestro", emoji: "👑" },
].map((s) => ({
  ...s,
  from: (s.stage - 1) * STAGE_SIZE + 1,
  to: s.stage * STAGE_SIZE,
}));

export function stageOf(level: number): number {
  return Math.min(STAGE_COUNT, Math.max(1, Math.ceil(level / STAGE_SIZE)));
}

export function stageInfo(level: number): StageInfo {
  return STAGES[stageOf(level) - 1];
}

// ---------------------------------------------------------------------------
// Interpolación y curvas
// ---------------------------------------------------------------------------

export function clamp01(t: number): number {
  return t < 0 ? 0 : t > 1 ? 1 : t;
}

export function lerp(min: number, max: number, t: number): number {
  return min + (max - min) * clamp01(t);
}

export function lerpInt(min: number, max: number, t: number): number {
  return Math.round(lerp(min, max, t));
}

/** Arranca suave: los primeros niveles casi no suben. Ideal para lo motriz. */
export function easeIn(t: number): number {
  const x = clamp01(t);
  return x * x;
}

/** Sube rápido al principio y luego se aplana. Ideal para cantidad de opciones. */
export function easeOut(t: number): number {
  const x = clamp01(t);
  return 1 - (1 - x) * (1 - x);
}

/** Curva en S: cómodo al inicio, exigente al final, suave en medio. */
export function smooth(t: number): number {
  const x = clamp01(t);
  return x * x * (3 - 2 * x);
}

/**
 * Curva escalonada por etapas: dentro de la etapa sube apenas un poco,
 * y al cambiar de etapa hay un salto. Es la curva por defecto de la app.
 */
export function staged(level: number, softness = 0.25): number {
  const stage = stageOf(level) - 1;
  const within = ((level - 1) % STAGE_SIZE) / (STAGE_SIZE - 1);
  const base = stage / (STAGE_COUNT - 1);
  const step = 1 / (STAGE_COUNT - 1);
  return clamp01(base + within * step * softness * STAGE_COUNT * 0.1 + within * step * softness);
}

/**
 * Rampa por tramos: defines el valor en cada etapa y la función interpola
 * entre ellos. `stops` debe traer STAGE_COUNT valores (uno por etapa).
 *
 *   phased(level, [3, 4, 5, 6, 8, 10, 12, 14, 16, 18])
 */
export function phased(level: number, stops: number[]): number {
  if (stops.length === 0) return 0;
  const stage = stageOf(level);
  const a = stops[Math.min(stops.length - 1, stage - 1)];
  const b = stops[Math.min(stops.length - 1, stage)];
  const within = ((level - 1) % STAGE_SIZE) / STAGE_SIZE;
  return a + (b - a) * within;
}

export function phasedInt(level: number, stops: number[]): number {
  return Math.round(phased(level, stops));
}

/**
 * Variación controlada dentro de la etapa para que dos niveles seguidos no
 * sean idénticos cuando el parámetro principal ya tocó techo. Determinista:
 * el nivel 37 siempre se ve igual, algo importante para que el niño lo repita.
 */
export function wave(level: number, amount: number, period = 4): number {
  return Math.sin((level * Math.PI * 2) / period) * amount;
}

/** Recorre un arreglo de forma determinista según el nivel. */
export function cycle<T>(items: readonly T[], level: number, offset = 0): T {
  return items[(level - 1 + offset) % items.length];
}

/** Pseudoaleatorio determinista a partir del nivel (mismo nivel = mismo reto). */
export function seeded(level: number, salt = 1): number {
  const x = Math.sin(level * 12.9898 + salt * 78.233) * 43758.5453;
  return x - Math.floor(x);
}

/** Elige `n` elementos de forma determinista para un nivel dado. */
export function seededPick<T>(items: readonly T[], n: number, level: number, salt = 1): T[] {
  const copy = [...items];
  const out: T[] = [];
  for (let i = 0; i < n && copy.length > 0; i++) {
    const idx = Math.floor(seeded(level, salt + i) * copy.length);
    out.push(copy.splice(idx, 1)[0]);
  }
  return out;
}

/**
 * Genera N niveles con una curva de dificultad progresiva.
 * `t` va de 0 (nivel 1) a 1 (nivel N) y `level` es el número de nivel,
 * útil para curvas por etapas o variaciones deterministas.
 */
export function generateLevels<T extends { level: number }>(
  count: number,
  build: (t: number, level: number) => Omit<T, "level">
): T[] {
  return Array.from({ length: count }, (_, i) => {
    const level = i + 1;
    const t = count > 1 ? i / (count - 1) : 1;
    return { level, ...build(t, level) } as T;
  });
}

/** Atajo: los 100 niveles estándar de la app. */
export function levels100<T extends { level: number }>(
  build: (t: number, level: number) => Omit<T, "level">
): T[] {
  return generateLevels<T>(LEVEL_COUNT, build);
}

/** Estrellas que otorga un nivel: más nivel, más estrellas (1 a 5). */
export function starsForLevel(level: number): number {
  return Math.min(5, 1 + Math.floor((level - 1) / 20));
}
