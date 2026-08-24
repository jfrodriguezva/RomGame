import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface ObjetosLevelConfig {
  level: number;
  targetCount: number;
  totalOnScreen: number;
}

export const OBJETOS_POOL = [
  "🍎",
  "🚗",
  "🎈",
  "🐶",
  "🌸",
  "⚽",
  "🍦",
  "🎁",
  "🐟",
  "🎀",
  "🧸",
  "🍓",
  "🌙",
  "⭐",
  "🦋",
  "🐢",
  "🍭",
  "🎨",
  "🚂",
  "🐝",
  "🍩",
  "🎲",
];

export const OBJETOS_LEVELS: ObjetosLevelConfig[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  targetCount: lerpInt(3, Math.min(16, OBJETOS_POOL.length), t),
  totalOnScreen: lerpInt(12, 55, t),
}));
