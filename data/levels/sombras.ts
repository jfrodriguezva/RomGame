import { levels100, phasedInt } from "@/lib/levels";

export interface SombrasLevel {
  level: number;
  pairs: number;
}

export const SOMBRAS_POOL = [
  "\u{1F994}",
  "\u{1F995}",
  "\u{1F9A5}",
  "\u{1F99C}",
  "\u{1F996}",
  "\u{1F9A9}",
  "\u{1F428}",
  "\u{1F42C}",
  "\u{1F41F}",
  "\u{1F98A}",
  "\u{1F98C}",
  "\u{1F41B}",
  "\u{1F997}",
  "\u{1F9A2}",
  "\u{1F992}",
  "\u{1F98D}",
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

export const SOMBRAS_LEVELS: SombrasLevel[] = levels100((_, level) => ({
  pairs: Math.min(SOMBRAS_POOL.length, phasedInt(level, [3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 12])),
}));
