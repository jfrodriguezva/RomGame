import { levels100, phasedInt } from "@/lib/levels";

/**
 * Huele bien o mal: sentido olfativo. El material real usa botes de olor;
 * aquí se nombra el objeto en vez de olerlo, la misma adaptación que ya se
 * usa en Caliente o frío y Pesado o ligero para sentidos que no se pueden
 * simular en pantalla.
 */
export interface CosaOlor {
  emoji: string;
  nombre: string;
  olor: "bien" | "mal";
}

export const COSAS_OLOR: CosaOlor[] = [
  { emoji: "🌹", nombre: "la rosa", olor: "bien" },
  { emoji: "🍰", nombre: "el pastel", olor: "bien" },
  { emoji: "🍋", nombre: "el limón", olor: "bien" },
  { emoji: "🌸", nombre: "la flor", olor: "bien" },

  { emoji: "🗑️", nombre: "la basura", olor: "mal" },
  { emoji: "🦨", nombre: "el zorrillo", olor: "mal" },
  { emoji: "🧦", nombre: "el calcetín sucio", olor: "mal" },
  { emoji: "🚬", nombre: "el humo", olor: "mal" },
];

export interface OlfatoLevel {
  level: number;
  cantidad: number;
}

export const OLFATO_LEVELS: OlfatoLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]), COSAS_OLOR.length),
}));
