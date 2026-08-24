import { generateLevels, lerp, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface AranaLevel {
  level: number;
  rows: number;
  cols: number;
  emoji: string;
  enemyMs: number; // cada cuánto se mueve la araña mala
  enemyCount: number;
}

const REVEAL_EMOJIS = ["🏰", "🦄", "👑", "🌈", "🧚", "🎀", "🐬", "🦋", "🍰", "🌸"];

export const ARANA_LEVELS: AranaLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t, level) => ({
  rows: lerpInt(4, 14, t),
  cols: lerpInt(4, 16, t),
  emoji: REVEAL_EMOJIS[level % REVEAL_EMOJIS.length],
  enemyMs: Math.round(lerp(1100, 320, t)),
  enemyCount: lerpInt(1, 2, t),
}));

export const REVEAL_THRESHOLD = 0.85;
export const START_LIVES = 3;
