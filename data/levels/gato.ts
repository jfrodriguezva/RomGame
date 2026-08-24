import { generateLevels, lerp, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface GatoLevel {
  level: number;
  mistakeChance: number; // probabilidad de que la CPU juegue al azar en vez de óptimo
}

export const GATO_LEVELS: GatoLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  mistakeChance: lerp(0.9, 0.05, t),
}));

export const LINES = [
  [0, 1, 2],
  [3, 4, 5],
  [6, 7, 8],
  [0, 3, 6],
  [1, 4, 7],
  [2, 5, 8],
  [0, 4, 8],
  [2, 4, 6],
];
