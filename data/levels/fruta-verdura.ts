import { levels100, phasedInt } from "@/lib/levels";

/** Fruta o verdura: clasificación botánica, en canastas. */
export interface AlimentoTipo {
  emoji: string;
  nombre: string;
  tipo: "fruta" | "verdura";
}

export const ALIMENTOS: AlimentoTipo[] = [
  { emoji: "🍎", nombre: "la manzana", tipo: "fruta" },
  { emoji: "🍌", nombre: "el plátano", tipo: "fruta" },
  { emoji: "🍇", nombre: "las uvas", tipo: "fruta" },
  { emoji: "🍓", nombre: "la fresa", tipo: "fruta" },

  { emoji: "🥕", nombre: "la zanahoria", tipo: "verdura" },
  { emoji: "🥦", nombre: "el brócoli", tipo: "verdura" },
  { emoji: "🍅", nombre: "el jitomate", tipo: "verdura" },
  { emoji: "🥬", nombre: "la lechuga", tipo: "verdura" },
];

export interface FrutaVerduraLevel {
  level: number;
  cantidad: number;
}

export const FRUTA_VERDURA_LEVELS: FrutaVerduraLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 6, 7, 8, 8, 8, 8, 8, 8]), ALIMENTOS.length),
}));
