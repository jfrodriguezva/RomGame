import { levels100, phasedInt } from "@/lib/levels";

export interface PuzzleLevel {
  level: number;
  gridSize: number; // gridSize x gridSize pieces
}

/** De cuatro piezas a sesenta y cuatro. */
export const PUZZLE_LEVELS: PuzzleLevel[] = levels100((_, level) => ({
  gridSize: phasedInt(level, [2, 2, 3, 3, 4, 4, 5, 5, 6, 7, 8]),
}));

// Emoji grande que se "arma" pieza por pieza (marcador de posición).
export const PUZZLE_IMAGES = ["🏰", "👑", "🦄", "🌈", "🧚", "👗", "🐬", "🦋"];
