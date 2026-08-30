import { levels100, phasedInt } from "@/lib/levels";

export interface MemoramaLevel {
  level: number;
  pairs: number;
}

/**
 * Memoria a distancia. Empieza con tres parejas y termina con dieciseis.
 */
export const MEMORAMA_LEVELS: MemoramaLevel[] = levels100((_, level) => ({
  pairs: phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16]),
}));
