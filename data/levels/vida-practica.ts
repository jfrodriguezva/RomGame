import { levels100, phased, phasedInt } from "@/lib/levels";

/**
 * Ejercicios de vida práctica.
 *
 * Son los primeros trabajos del ambiente y los más queridos por los niños:
 * trasvasar, abotonar, verter, clasificar. No enseñan contenidos, enseñan a
 * trabajar: agarrar bien, seguir un orden, terminar lo empezado y dejar todo
 * como estaba.
 */
export type Tarea = "trasvasar" | "abotonar" | "verter" | "clasificar";

export interface VidaPracticaLevel {
  level: number;
  tarea: Tarea;
  /** Cuántas piezas, botones o elementos hay que trabajar. */
  cantidad: number;
  /** Margen admitido al verter, en porcentaje del vaso. */
  precision: number;
}

const CICLO: Tarea[] = ["trasvasar", "abotonar", "verter", "clasificar"];

export const VIDA_PRACTICA_LEVELS: VidaPracticaLevel[] = levels100((_, level) => ({
  // Las cuatro tareas se turnan: cada nivel trae una distinta.
  tarea: CICLO[(level - 1) % CICLO.length],
  cantidad: phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14]),
  precision: phased(level, [16, 14, 12, 11, 10, 9, 8, 7, 6, 5, 4]),
}));

export const OBJETOS_TRASVASE = ["\u{1FAD8}", "\u{1F330}", "\u{1F95C}", "\u{1F33D}", "\u{1FAD0}"];

export const PARES_CLASIFICAR = [
  {
    criterio: "por color",
    a: { nombre: "Rojos", items: ["\u{1F34E}", "\u{1F345}", "\u{1F353}", "\u{1F338}"] },
    b: { nombre: "Amarillos", items: ["\u{1F34B}", "\u{1F33B}", "\u{1F9C0}", "\u{1F31F}"] },
  },
  {
    criterio: "grandes y chicos",
    a: { nombre: "Grandes", items: ["\u{1F418}", "\u{1F433}", "\u{1F992}", "\u{1F995}"] },
    b: { nombre: "Chicos", items: ["\u{1F41C}", "\u{1F41D}", "\u{1F41E}", "\u{1F997}"] },
  },
  {
    criterio: "de comer y de vestir",
    a: { nombre: "Se come", items: ["\u{1F35E}", "\u{1F9C0}", "\u{1F34C}", "\u{1F955}"] },
    b: { nombre: "Se viste", items: ["\u{1F455}", "\u{1F45F}", "\u{1F9E6}", "\u{1F452}"] },
  },
  {
    criterio: "de la casa y del jardín",
    a: { nombre: "De casa", items: ["\u{1F6CF}\uFE0F", "\u{1FA91}", "\u{1F5BC}\uFE0F", "\u{1F4DA}"] },
    b: { nombre: "Del jardín", items: ["\u{1F333}", "\u{1F33F}", "\u{1F41B}", "\u{1FAB4}"] },
  },
];
