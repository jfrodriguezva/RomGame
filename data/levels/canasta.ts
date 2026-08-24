import { generateLevels, lerp, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface CanastaLevel {
  level: number;
  fallSpeed: number;
  spawnMs: number;
}

export const CANASTA_LEVELS: CanastaLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  fallSpeed: lerp(0.6, 2.4, t),
  spawnMs: Math.round(lerp(1500, 480, t)),
}));

export const TICK_MS = 50;
export const BASKET_STEP = 8;
export const CATCH_RADIUS = 10;
