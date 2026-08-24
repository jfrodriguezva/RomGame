export function lerp(min: number, max: number, t: number): number {
  return min + (max - min) * t;
}

export function lerpInt(min: number, max: number, t: number): number {
  return Math.round(lerp(min, max, t));
}

/**
 * Genera N niveles con una curva de dificultad progresiva en vez de definirlos
 * uno por uno a mano. `t` va de 0 (nivel 1, más fácil) a 1 (nivel N, más difícil).
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

export const DEFAULT_LEVEL_COUNT = 30;
