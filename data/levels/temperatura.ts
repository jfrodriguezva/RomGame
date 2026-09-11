import { levels100, phasedInt } from "@/lib/levels";

/** Caliente o frío: clasificación en canastas, sentido térmico. */
export interface CosaTemperatura {
  emoji: string;
  nombre: string;
  temperatura: "caliente" | "frio";
}

export const COSAS_TEMPERATURA: CosaTemperatura[] = [
  { emoji: "☀️", nombre: "el sol", temperatura: "caliente" },
  { emoji: "🔥", nombre: "el fuego", temperatura: "caliente" },
  { emoji: "☕", nombre: "el café", temperatura: "caliente" },
  { emoji: "🍲", nombre: "la sopa", temperatura: "caliente" },

  { emoji: "❄️", nombre: "la nieve", temperatura: "frio" },
  { emoji: "🧊", nombre: "el hielo", temperatura: "frio" },
  { emoji: "🍦", nombre: "el helado", temperatura: "frio" },
  { emoji: "⛄", nombre: "el muñeco de nieve", temperatura: "frio" },
];

export interface TemperaturaLevel {
  level: number;
  cantidad: number;
}

export const TEMPERATURA_LEVELS: TemperaturaLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]), COSAS_TEMPERATURA.length),
}));
