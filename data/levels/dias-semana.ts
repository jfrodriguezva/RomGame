import { levels100, phasedInt } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/** Los días de la semana, en orden. Mismo motor que el ciclo del agua. */
export interface DiaSemana {
  valor: number;
  nombre: string;
}

export const DIAS_SEMANA: DiaSemana[] = [
  { valor: 1, nombre: "Lunes" },
  { valor: 2, nombre: "Martes" },
  { valor: 3, nombre: "Miércoles" },
  { valor: 4, nombre: "Jueves" },
  { valor: 5, nombre: "Viernes" },
  { valor: 6, nombre: "Sábado" },
  { valor: 7, nombre: "Domingo" },
];

export function diaSemana(valor: number): DiaSemana {
  return DIAS_SEMANA[Math.min(DIAS_SEMANA.length, Math.max(1, valor)) - 1];
}

export const DIAS_SEMANA_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => ({
  cantidad: phasedInt(level, [2, 2, 3, 3, 4, 4, 5, 6, 7, 7, 7]),
  invertido: true,
  pista: true,
  cercanos: false,
  desde: 0,
}));
