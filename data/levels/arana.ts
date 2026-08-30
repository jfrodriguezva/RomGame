import { levels100, phasedInt, cycle } from "@/lib/levels";

export interface AranaLevel {
  level: number;
  rows: number;
  cols: number;
  emoji: string;
  enemyMs: number; // cada cuánto se mueve la araña mala
  enemyCount: number;
}

const REVEAL_EMOJIS = ["🏰", "🦄", "👑", "🌈", "🧚", "🎀", "🐬", "🦋", "🍰", "🌸"];

export const ARANA_LEVELS: AranaLevel[] = levels100((_, level) => ({
  rows: phasedInt(level, [4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]),
  cols: phasedInt(level, [4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 16]),
  emoji: cycle(REVEAL_EMOJIS, level),
  enemyMs: phasedInt(level, [1300, 1180, 1070, 960, 860, 770, 680, 590, 500, 420, 340]),
  enemyCount: phasedInt(level, [1, 1, 1, 2, 2, 2, 3, 3, 4, 4, 5]),
}));

export const REVEAL_THRESHOLD = 0.85;
export const START_LIVES = 3;
