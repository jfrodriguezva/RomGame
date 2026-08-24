import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface NumerosLevel {
  level: number;
  count: number;
}

export const NUMEROS_LEVELS: NumerosLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  count: lerpInt(3, 26, t),
}));
