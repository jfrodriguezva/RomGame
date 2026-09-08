import { levels100, stageOf } from "@/lib/levels";

/**
 * Los husos: nueve compartimentos numerados del 0 al 9. El niño no lee el
 * número, cuenta los husos y los coloca en el compartimento correspondiente
 * — incluido el 0, "el compartimento que se queda vacío a propósito".
 */
export interface HusosLevel {
  level: number;
  /** El número del compartimento que toca en este nivel (0 a 9). */
  objetivo: number;
}

/** Qué números entran en juego en cada etapa. El 0 aparece desde la etapa 3. */
const NUMEROS_POR_ETAPA: number[][] = [
  [1, 2, 3],
  [1, 2, 3, 4],
  [0, 1, 2, 3, 4],
  [0, 1, 2, 3, 4, 5],
  [0, 2, 4, 5, 6],
  [0, 3, 5, 6, 7],
  [0, 4, 6, 7, 8],
  [0, 5, 7, 8, 9],
  [0, 6, 7, 8, 9],
  [0, 1, 3, 6, 9],
];

export const HUSOS_LEVELS: HusosLevel[] = levels100((_, level) => {
  const opciones = NUMEROS_POR_ETAPA[stageOf(level) - 1];
  return { objetivo: opciones[(level - 1) % opciones.length] };
});
