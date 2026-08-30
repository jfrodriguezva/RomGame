import { levels100, phased } from "@/lib/levels";

export interface GatoLevel {
  level: number;
  mistakeChance: number; // probabilidad de que la CPU juegue al azar en vez de óptimo
}

/**
 * La maquina empieza distraida y termina jugando perfecto. Que el nino pueda
 * ganar al principio importa: la confianza va antes que el reto.
 */
export const GATO_LEVELS: GatoLevel[] = levels100((_, level) => ({
  mistakeChance: phased(level, [0.92, 0.85, 0.76, 0.66, 0.55, 0.44, 0.33, 0.22, 0.13, 0.05, 0]),
}));

export const LINES = [
  [0, 1, 2],
  [3, 4, 5],
  [6, 7, 8],
  [0, 3, 6],
  [1, 4, 7],
  [2, 5, 8],
  [0, 4, 8],
  [2, 4, 6],
];
