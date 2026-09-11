import { levels100, phasedInt } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/** Las estaciones del año, en orden. Mismo motor que el ciclo del agua. */
export interface Estacion {
  valor: number;
  nombre: string;
  emoji: string;
}

export const ESTACIONES: Estacion[] = [
  { valor: 1, nombre: "Primavera", emoji: "🌸" },
  { valor: 2, nombre: "Verano", emoji: "☀️" },
  { valor: 3, nombre: "Otoño", emoji: "🍂" },
  { valor: 4, nombre: "Invierno", emoji: "❄️" },
];

export function estacionDe(valor: number): Estacion {
  return ESTACIONES[Math.min(ESTACIONES.length, Math.max(1, valor)) - 1];
}

export const ESTACIONES_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => ({
  cantidad: phasedInt(level, [2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4]),
  invertido: true,
  pista: true,
  cercanos: false,
  desde: 0,
}));
