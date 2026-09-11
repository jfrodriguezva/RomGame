import { levels100, phasedInt } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/**
 * El ciclo del agua: sol calienta -> se forma nube -> llueve -> vuelve al
 * río o mar. Mismo motor que el ciclo de la mariposa, orden por proceso
 * natural en vez de tamaño. Los cuatro tienen emoji claro, no hace falta
 * dibujar nada.
 */
export interface EtapaAgua {
  valor: number;
  nombre: string;
  emoji: string;
}

export const ETAPAS_AGUA: EtapaAgua[] = [
  { valor: 1, nombre: "El sol calienta el agua", emoji: "☀️" },
  { valor: 2, nombre: "Se forma una nube", emoji: "☁️" },
  { valor: 3, nombre: "Llueve", emoji: "🌧️" },
  { valor: 4, nombre: "El agua vuelve al río", emoji: "🌊" },
];

export function etapaAgua(valor: number): EtapaAgua {
  return ETAPAS_AGUA[Math.min(ETAPAS_AGUA.length, Math.max(1, valor)) - 1];
}

export const CICLO_AGUA_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => ({
  cantidad: phasedInt(level, [2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4]),
  invertido: true,
  pista: true,
  cercanos: false,
  desde: 0,
}));
