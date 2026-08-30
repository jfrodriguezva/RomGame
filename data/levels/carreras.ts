import { levels100, phased, phasedInt } from "@/lib/levels";

export interface CarrerasLevel {
  level: number;
  scrollSpeed: number;
  obstacleEveryMs: number;
}

export const CARRERAS_LEVELS: CarrerasLevel[] = levels100((_, level) => ({
  scrollSpeed: phased(level, [140, 170, 200, 232, 265, 300, 336, 374, 414, 456, 500]),
  obstacleEveryMs: phasedInt(level, [1400, 1290, 1180, 1070, 960, 860, 770, 680, 600, 520, 440]),
}));
