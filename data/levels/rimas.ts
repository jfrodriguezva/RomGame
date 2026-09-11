import { levels100, phasedInt } from "@/lib/levels";

/** Palabras que riman: dos familias fijas, para no cambiar el objetivo cada ronda. */
export interface PalabraRima {
  emoji: string;
  nombre: string;
  familia: "ato" | "on";
}

export const PALABRAS_RIMA: PalabraRima[] = [
  { emoji: "🐱", nombre: "gato", familia: "ato" },
  { emoji: "🦆", nombre: "pato", familia: "ato" },
  { emoji: "👞", nombre: "zapato", familia: "ato" },
  { emoji: "🍽️", nombre: "plato", familia: "ato" },

  { emoji: "🐭", nombre: "ratón", familia: "on" },
  { emoji: "🚚", nombre: "camión", familia: "on" },
  { emoji: "✈️", nombre: "avión", familia: "on" },
  { emoji: "🍈", nombre: "melón", familia: "on" },
];

export interface RimasLevel {
  level: number;
  cantidad: number;
}

export const RIMAS_LEVELS: RimasLevel[] = levels100((_, level) => ({
  cantidad: phasedInt(level, [2, 3, 3, 4, 4, 5, 6, 6, 7, 8, 8]),
}));
