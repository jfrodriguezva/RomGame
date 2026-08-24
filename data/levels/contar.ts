import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface ContarLevel {
  level: number;
  maxCount: number;
}

export const CONTAR_LEVELS: ContarLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  maxCount: lerpInt(3, 20, t),
}));

export const CONTAR_OBJETOS = ["⭐", "🍎", "🎈", "🌸", "🍓", "🐚"];
