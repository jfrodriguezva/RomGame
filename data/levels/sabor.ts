import { levels100, phasedInt } from "@/lib/levels";

/** Dulce o salado: sentido gustativo, en canastas. */
export interface CosaSabor {
  emoji: string;
  nombre: string;
  sabor: "dulce" | "salado";
}

export const COSAS_SABOR: CosaSabor[] = [
  { emoji: "🍭", nombre: "la paleta", sabor: "dulce" },
  { emoji: "🍰", nombre: "el pastel", sabor: "dulce" },
  { emoji: "🍯", nombre: "la miel", sabor: "dulce" },
  { emoji: "🍎", nombre: "la manzana", sabor: "dulce" },

  { emoji: "🍟", nombre: "las papas fritas", sabor: "salado" },
  { emoji: "🧀", nombre: "el queso", sabor: "salado" },
  { emoji: "🥨", nombre: "el pretzel", sabor: "salado" },
  { emoji: "🥓", nombre: "el tocino", sabor: "salado" },
];

export interface SaborLevel {
  level: number;
  cantidad: number;
}

export const SABOR_LEVELS: SaborLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]), COSAS_SABOR.length),
}));
