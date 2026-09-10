import { levels100, phasedInt } from "@/lib/levels";

/**
 * Áspero o liso: réplica digital de las tablillas ásperas y lisas.
 * Clasificación en canastas, igual que El o la / Día y noche.
 */
export interface ObjetoTextura {
  emoji: string;
  nombre: string;
  textura: "aspero" | "liso";
}

export const OBJETOS_TEXTURA: ObjetoTextura[] = [
  { emoji: "🪨", nombre: "la piedra", textura: "aspero" },
  { emoji: "🌵", nombre: "el cactus", textura: "aspero" },
  { emoji: "🧱", nombre: "el ladrillo", textura: "aspero" },
  { emoji: "🍍", nombre: "la piña", textura: "aspero" },
  { emoji: "🦔", nombre: "el erizo", textura: "aspero" },
  { emoji: "🌳", nombre: "la corteza del árbol", textura: "aspero" },

  { emoji: "🪞", nombre: "el espejo", textura: "liso" },
  { emoji: "🧊", nombre: "el hielo", textura: "liso" },
  { emoji: "🥚", nombre: "el huevo", textura: "liso" },
  { emoji: "🎈", nombre: "el globo", textura: "liso" },
  { emoji: "🍎", nombre: "la manzana", textura: "liso" },
  { emoji: "🧼", nombre: "el jabón", textura: "liso" },
];

export interface TexturaLevel {
  level: number;
  cantidad: number;
}

export const TEXTURA_LEVELS: TexturaLevel[] = levels100((_, level) => ({
  cantidad: phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]),
}));
