import { levels100, phasedInt } from "@/lib/levels";

/** Pesado o ligero: sentido bárico, en canastas. */
export interface CosaPeso {
  emoji: string;
  nombre: string;
  peso: "pesado" | "ligero";
}

export const COSAS_PESO: CosaPeso[] = [
  { emoji: "🐘", nombre: "el elefante", peso: "pesado" },
  { emoji: "🪨", nombre: "la piedra", peso: "pesado" },
  { emoji: "🚗", nombre: "el carro", peso: "pesado" },
  { emoji: "🛋️", nombre: "el sillón", peso: "pesado" },

  { emoji: "🪶", nombre: "la pluma", peso: "ligero" },
  { emoji: "🎈", nombre: "el globo", peso: "ligero" },
  { emoji: "🧻", nombre: "el papel", peso: "ligero" },
  { emoji: "🫧", nombre: "la burbuja", peso: "ligero" },
];

export interface PesoLevel {
  level: number;
  cantidad: number;
}

export const PESO_LEVELS: PesoLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]), COSAS_PESO.length),
}));
