import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface DiferenciasLevelConfig {
  level: number;
  totalItems: number;
  diffCount: number;
}

export const DIFERENCIAS_LEVELS: DiferenciasLevelConfig[] = generateLevels(
  DEFAULT_LEVEL_COUNT,
  (t) => ({
    totalItems: lerpInt(6, 42, t),
    diffCount: lerpInt(2, 18, t),
  })
);

export const DIFERENCIAS_POOL = [
  "🌸",
  "🦋",
  "🍄",
  "🌟",
  "🍀",
  "🐚",
  "🌈",
  "🎀",
  "🍎",
  "🎈",
  "⭐",
  "🌻",
  "🍓",
  "🧸",
  "🎁",
  "🌼",
  "🍬",
  "🐞",
  "🦄",
  "💎",
  "🍭",
  "🎠",
  "🪁",
  "🧁",
];
