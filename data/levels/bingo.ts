import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * Bingo con imágenes: se anuncia un dibujo (con voz), se busca y se toca
 * en el cartón. Las primeras etapas piden completar una sola línea; desde
 * la etapa 6 hay que llenar el cartón entero — una dificultad nueva a la
 * vez, como el resto de la app.
 */
export interface BingoLevel {
  level: number;
  tamano: number;
  lineaSolo: boolean;
}

export const BINGO_LEVELS: BingoLevel[] = levels100((_, level) => ({
  tamano: phasedInt(level, [2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4]),
  lineaSolo: stageOf(level) <= 6,
}));
