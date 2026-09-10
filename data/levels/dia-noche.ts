import { levels100, phasedInt } from "@/lib/levels";

/** Día y noche: clasificación en canastas, igual que El o la. */
export interface CosaDiaNoche {
  emoji: string;
  nombre: string;
  momento: "dia" | "noche";
}

export const COSAS_DIA_NOCHE: CosaDiaNoche[] = [
  { emoji: "☀️", nombre: "el sol", momento: "dia" },
  { emoji: "🌈", nombre: "el arcoíris", momento: "dia" },
  { emoji: "🐓", nombre: "el gallo", momento: "dia" },
  { emoji: "🦋", nombre: "la mariposa", momento: "dia" },
  { emoji: "🌻", nombre: "el girasol", momento: "dia" },
  { emoji: "🏫", nombre: "la escuela", momento: "dia" },

  { emoji: "🌙", nombre: "la luna", momento: "noche" },
  { emoji: "⭐", nombre: "la estrella", momento: "noche" },
  { emoji: "🦉", nombre: "el búho", momento: "noche" },
  { emoji: "🦇", nombre: "el murciélago", momento: "noche" },
  { emoji: "🛌", nombre: "dormir", momento: "noche" },
  { emoji: "🕯️", nombre: "la vela", momento: "noche" },
];

export interface DiaNocheLevel {
  level: number;
  cantidad: number;
}

export const DIA_NOCHE_LEVELS: DiaNocheLevel[] = levels100((_, level) => ({
  cantidad: phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]),
}));
