import { generateLevels, lerp, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface CarrerasLevel {
  level: number;
  scrollSpeed: number;
  obstacleEveryMs: number;
}

export const CARRERAS_LEVELS: CarrerasLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  scrollSpeed: lerp(150, 420, t),
  obstacleEveryMs: Math.round(lerp(1200, 420, t)),
}));
