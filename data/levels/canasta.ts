import { levels100, phased, phasedInt } from "@/lib/levels";

export interface CanastaLevel {
  level: number;
  fallSpeed: number;
  spawnMs: number;
}

export const CANASTA_LEVELS: CanastaLevel[] = levels100((_, level) => ({
  fallSpeed: phased(level, [0.55, 0.75, 0.95, 1.15, 1.35, 1.6, 1.85, 2.1, 2.4, 2.7, 3.1]),
  spawnMs: phasedInt(level, [1700, 1550, 1400, 1250, 1100, 970, 850, 740, 640, 550, 460]),
}));

export const TICK_MS = 50;
export const BASKET_STEP = 8;
export const CATCH_RADIUS = 10;
