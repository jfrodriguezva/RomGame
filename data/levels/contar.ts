import { levels100, phasedInt } from "@/lib/levels";

export interface ContarLevel {
  level: number;
  maxCount: number;
}

/**
 * Correspondencia uno a uno. Durante la primera mitad no se pasa del seis,
 * que es donde de verdad se afianza el conteo; despues crece hasta veinte.
 */
export const CONTAR_LEVELS: ContarLevel[] = levels100((_, level) => ({
  maxCount: phasedInt(level, [2, 3, 4, 5, 6, 8, 10, 12, 15, 18, 20]),
}));

export const CONTAR_OBJETOS = ["⭐", "🍎", "🎈", "🌸", "🍓", "🐚"];
