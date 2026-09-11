import { levels100, phasedInt } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/** Poner la mesa: mantel -> plato -> cubiertos -> vaso. */
export interface EtapaMesa {
  valor: number;
  nombre: string;
  emoji?: string;
}

export const ETAPAS_MESA: EtapaMesa[] = [
  { valor: 1, nombre: "Mantel" },
  { valor: 2, nombre: "Plato", emoji: "🍽️" },
  { valor: 3, nombre: "Cubiertos", emoji: "🍴" },
  { valor: 4, nombre: "Vaso", emoji: "🥤" },
];

export function etapaMesa(valor: number): EtapaMesa {
  return ETAPAS_MESA[Math.min(ETAPAS_MESA.length, Math.max(1, valor)) - 1];
}

export const MESA_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => ({
  cantidad: phasedInt(level, [2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4]),
  invertido: true,
  pista: true,
  cercanos: false,
  desde: 0,
}));
