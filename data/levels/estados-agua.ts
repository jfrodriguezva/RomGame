import { levels100, phasedInt } from "@/lib/levels";

/** Estados del agua: sólido, líquido, gas — tres canastas. */
export interface CosaEstado {
  emoji: string;
  nombre: string;
  estado: "solido" | "liquido" | "gas";
}

export const COSAS_ESTADO: CosaEstado[] = [
  { emoji: "🧊", nombre: "el hielo", estado: "solido" },
  { emoji: "❄️", nombre: "la nieve", estado: "solido" },
  { emoji: "🪨", nombre: "la piedra", estado: "solido" },

  { emoji: "💧", nombre: "el agua", estado: "liquido" },
  { emoji: "🥛", nombre: "la leche", estado: "liquido" },
  { emoji: "🌧️", nombre: "la lluvia", estado: "liquido" },

  { emoji: "☁️", nombre: "la nube", estado: "gas" },
  { emoji: "💨", nombre: "el vapor", estado: "gas" },
  { emoji: "🎈", nombre: "el aire del globo", estado: "gas" },
];

export interface EstadosAguaLevel {
  level: number;
  cantidad: number;
}

export const ESTADOS_AGUA_LEVELS: EstadosAguaLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 5, 6, 7, 8, 8, 9, 9, 9]), COSAS_ESTADO.length),
}));
