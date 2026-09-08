import { levels100, phasedInt, stageOf } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/**
 * Los ocho planetas, en orden de distancia al Sol.
 *
 * Reutiliza el mismo motor de seriación que el ciclo de la mariposa: el
 * orden no es por tamaño sino por posición. `invertido` se deja siempre en
 * true para que la fila salga en orden ascendente (Mercurio -> Neptuno); ver
 * la nota de `ciclo-vida.ts` para por qué.
 */
export interface Planeta {
  valor: number;
  nombre: string;
  color: string;
  diametro: number;
  anillo?: boolean;
}

export const PLANETAS: Planeta[] = [
  { valor: 1, nombre: "Mercurio", color: "#9c9a94", diametro: 28 },
  { valor: 2, nombre: "Venus", color: "#d9c08f", diametro: 40 },
  { valor: 3, nombre: "Tierra", color: "#4f83a6", diametro: 42 },
  { valor: 4, nombre: "Marte", color: "#b5563b", diametro: 32 },
  { valor: 5, nombre: "Júpiter", color: "#c98a4b", diametro: 62 },
  { valor: 6, nombre: "Saturno", color: "#d8c179", diametro: 56, anillo: true },
  { valor: 7, nombre: "Urano", color: "#7fc8c4", diametro: 46 },
  { valor: 8, nombre: "Neptuno", color: "#3b5998", diametro: 44 },
];

export function planetaDe(valor: number): Planeta {
  return PLANETAS[Math.min(PLANETAS.length, Math.max(1, valor)) - 1];
}

export const SISTEMA_SOLAR_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => {
  const etapa = stageOf(level);
  return {
    cantidad: phasedInt(level, [2, 3, 3, 4, 5, 5, 6, 7, 8, 8, 8]),
    invertido: true,
    pista: etapa <= 3,
    cercanos: false,
    desde: 0,
  };
});
