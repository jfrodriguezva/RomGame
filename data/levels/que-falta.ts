import { levels100, phasedInt } from "@/lib/levels";

/**
 * ¿Qué falta? — la versión digital del juego de Kim: una bandeja de
 * objetos para memorizar, uno desaparece, hay que decir cuál era. Solo
 * crece la cantidad de objetos en la bandeja, nunca el tiempo para
 * memorizar (se aísla una sola dificultad, como en el resto de la app).
 */
export const OBJETOS_POOL = [
  "🍎", "🍌", "🍊", "🍓", "🍉", "🥕", "🧸", "🚗", "⚽", "🎈",
  "🔑", "🧦", "👟", "🎩", "🕶️", "🎸", "🥁", "✂️", "🖍️", "🎨",
  "🍞", "🥛", "🐘", "🐱", "🐶", "🦁", "🐢", "🎁",
];

export interface QueFaltaLevel {
  level: number;
  cantidad: number;
}

export const QUE_FALTA_LEVELS: QueFaltaLevel[] = levels100((_, level) => ({
  cantidad: phasedInt(level, [3, 3, 4, 4, 5, 5, 6, 7, 7, 8, 8]),
}));

export const MEMORIZAR_MS = 3000;
