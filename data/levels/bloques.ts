import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface BloquesLevel {
  level: number;
  count: number;
}

export const BLOQUES_LEVELS: BloquesLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  count: lerpInt(3, 14, t),
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
