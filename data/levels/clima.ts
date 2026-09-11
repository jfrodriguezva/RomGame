import { levels100, phasedInt } from "@/lib/levels";

/** ¿Qué me pongo? Vestirse según el clima: sol o lluvia. */
export interface RopaClima {
  emoji: string;
  nombre: string;
  clima: "sol" | "lluvia";
}

export const ROPA_CLIMA: RopaClima[] = [
  { emoji: "🩳", nombre: "los shorts", clima: "sol" },
  { emoji: "🕶️", nombre: "los lentes de sol", clima: "sol" },
  { emoji: "👒", nombre: "el sombrero", clima: "sol" },
  { emoji: "👙", nombre: "el traje de baño", clima: "sol" },

  { emoji: "☂️", nombre: "el paraguas", clima: "lluvia" },
  { emoji: "🧥", nombre: "el abrigo", clima: "lluvia" },
  { emoji: "👢", nombre: "las botas", clima: "lluvia" },
  { emoji: "🧤", nombre: "los guantes", clima: "lluvia" },
];

export interface ClimaLevel {
  level: number;
  cantidad: number;
}

export const CLIMA_LEVELS: ClimaLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]), ROPA_CLIMA.length),
}));
