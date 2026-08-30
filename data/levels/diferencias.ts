import { levels100, phasedInt } from "@/lib/levels";

export interface DiferenciasLevelConfig {
  level: number;
  totalItems: number;
  diffCount: number;
}

/**
 * Pares y contrastes. Al principio hay pocos objetos y una sola diferencia
 * evidente; al final el tablero esta lleno y hay quince cosas distintas.
 */
export const DIFERENCIAS_LEVELS: DiferenciasLevelConfig[] = levels100((_, level) => ({
  totalItems: phasedInt(level, [6, 8, 10, 12, 16, 20, 24, 28, 33, 38, 44]),
  diffCount: phasedInt(level, [1, 2, 2, 3, 4, 5, 6, 8, 10, 12, 15]),
}));

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
