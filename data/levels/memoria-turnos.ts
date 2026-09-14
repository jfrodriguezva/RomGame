import { levels100, phasedInt } from "@/lib/levels";

/**
 * Memoria por turnos: contra la computadora, no contra reloj. Empieza en 3
 * parejas y llega a 10 — menos que el memorama solo (16), porque aquí cada
 * ronda además espera el turno del rival.
 */
export interface MemoriaTurnosLevel {
  level: number;
  pairs: number;
}

export const MEMORIA_TURNOS_LEVELS: MemoriaTurnosLevel[] = levels100((_, level) => ({
  pairs: phasedInt(level, [3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 10]),
}));
