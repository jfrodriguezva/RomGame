import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface PuzzleLevel {
  level: number;
  gridSize: number; // gridSize x gridSize pieces
}

export const PUZZLE_LEVELS: PuzzleLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  gridSize: lerpInt(2, 7, t),
}));

// Emoji grande que se "arma" pieza por pieza (marcador de posición).
export const PUZZLE_IMAGES = ["🏰", "👑", "🦄", "🌈", "🧚", "👗", "🐬", "🦋"];
