import { levels100, phasedInt } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/** Lavarse las manos: mojar -> jabón -> tallar -> enjuagar -> secar. */
export interface EtapaLavado {
  valor: number;
  nombre: string;
  emoji: string;
}

export const ETAPAS_LAVADO: EtapaLavado[] = [
  { valor: 1, nombre: "Mojar", emoji: "🚿" },
  { valor: 2, nombre: "Jabón", emoji: "🧼" },
  { valor: 3, nombre: "Tallar", emoji: "👏" },
  { valor: 4, nombre: "Enjuagar", emoji: "💧" },
  { valor: 5, nombre: "Secar", emoji: "🧻" },
];

export function etapaLavado(valor: number): EtapaLavado {
  return ETAPAS_LAVADO[Math.min(ETAPAS_LAVADO.length, Math.max(1, valor)) - 1];
}

export const LAVADO_MANOS_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => ({
  cantidad: phasedInt(level, [2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 5]),
  invertido: true,
  pista: true,
  cercanos: false,
  desde: 0,
}));
