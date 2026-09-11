import { levels100, phasedInt } from "@/lib/levels";

/** Grande, mediano o chico: clasificación en tres canastas, no dos. */
export interface CosaTamano {
  emoji: string;
  nombre: string;
  tamano: "grande" | "mediano" | "chico";
}

export const COSAS_TAMANO: CosaTamano[] = [
  { emoji: "🐘", nombre: "el elefante", tamano: "grande" },
  { emoji: "🚌", nombre: "el autobús", tamano: "grande" },
  { emoji: "🌳", nombre: "el árbol", tamano: "grande" },

  { emoji: "🐕", nombre: "el perro", tamano: "mediano" },
  { emoji: "🚗", nombre: "el carro", tamano: "mediano" },
  { emoji: "🌿", nombre: "la planta", tamano: "mediano" },

  { emoji: "🐭", nombre: "el ratón", tamano: "chico" },
  { emoji: "🛴", nombre: "la patineta", tamano: "chico" },
  { emoji: "🌱", nombre: "el brote", tamano: "chico" },
];

export interface TamanosLevel {
  level: number;
  cantidad: number;
}

export const TAMANOS_LEVELS: TamanosLevel[] = levels100((_, level) => ({
  cantidad: phasedInt(level, [3, 4, 5, 6, 6, 7, 7, 8, 9, 9, 9]),
}));
