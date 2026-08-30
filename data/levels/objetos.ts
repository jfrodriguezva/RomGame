import { levels100, phasedInt } from "@/lib/levels";

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

/**
 * Busqueda visual. Sube por dos lados a la vez: mas cosas que encontrar y un
 * fondo cada vez mas poblado donde esconderlas.
 */
export const OBJETOS_LEVELS: ObjetosLevelConfig[] = levels100((_, level) => ({
  targetCount: phasedInt(level, [2, 3, 3, 4, 5, 6, 7, 8, 10, 12, 14]),
  totalOnScreen: phasedInt(level, [10, 14, 18, 22, 26, 32, 38, 44, 50, 56, 64]),
}));
