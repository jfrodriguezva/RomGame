import { levels100, phasedInt } from "@/lib/levels";

export interface BloquesLevel {
  level: number;
  count: number;
}

export const BLOQUES_LEVELS: BloquesLevel[] = levels100((_, level) => ({
  count: phasedInt(level, [3, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14]),
}));

export const BLOQUE_COLORS = [
  "#f87171",
  "#fb923c",
  "#facc15",
  "#4ade80",
  "#38bdf8",
  "#818cf8",
  "#c084fc",
  "#f472b6",
];
