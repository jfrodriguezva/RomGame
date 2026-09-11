import { levels100, phasedInt } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/**
 * La rutina de la mañana: despertar -> vestirse -> desayunar -> ir a la
 * escuela. Mismo motor que el ciclo del agua, secuencia de vida diaria en
 * vez de proceso natural.
 */
export interface EtapaRutina {
  valor: number;
  nombre: string;
  emoji: string;
}

export const ETAPAS_RUTINA: EtapaRutina[] = [
  { valor: 1, nombre: "Despertar", emoji: "⏰" },
  { valor: 2, nombre: "Vestirse", emoji: "👕" },
  { valor: 3, nombre: "Desayunar", emoji: "🥣" },
  { valor: 4, nombre: "Ir a la escuela", emoji: "🎒" },
];

export function etapaRutina(valor: number): EtapaRutina {
  return ETAPAS_RUTINA[Math.min(ETAPAS_RUTINA.length, Math.max(1, valor)) - 1];
}

export const RUTINA_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => ({
  cantidad: phasedInt(level, [2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4]),
  invertido: true,
  pista: true,
  cercanos: false,
  desde: 0,
}));
