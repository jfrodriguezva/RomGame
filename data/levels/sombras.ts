import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface SombrasLevel {
  level: number;
  pairs: number;
}

export const SOMBRAS_POOL = [
  "🐶",
  "🐱",
  "🐰",
  "🦁",
  "🐻",
  "🐸",
  "🦋",
  "🐢",
  "🐘",
  "🦆",
  "🐬",
  "🦄",
  "🐨",
  "🦒",
  "🐧",
  "🦊",
];

export const SOMBRAS_LEVELS: SombrasLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  pairs: lerpInt(3, SOMBRAS_POOL.length, t),
}));
