import { levels100, phased, phasedInt } from "@/lib/levels";

export interface LavaLevel {
  level: number;
  speed: number; // px por tick, velocidad de carrera
  obstacleEveryMs: number; // cada cuánto aparece una llamarada
}

export const LAVA_LEVELS: LavaLevel[] = levels100((_, level) => ({
  speed: phased(level, [3.2, 3.6, 4.0, 4.6, 5.2, 5.8, 6.4, 7.2, 8.0, 8.8, 9.6]),
  obstacleEveryMs: phasedInt(level, [1900, 1750, 1600, 1450, 1300, 1180, 1060, 950, 850, 760, 680]),
}));

export const TICK_MS = 30;
export const PLAYER_X = 70;
export const PLAYER_WIDTH = 34;
export const OBSTACLE_WIDTH = 30;
export const JUMP_MS = 600;
/** Altura máxima del salto: por encima de esta línea, la llamarada no toca. */
export const JUMP_HEIGHT = 68;
export const CLEAR_HEIGHT = 26;
export const START_LIVES = 3;
