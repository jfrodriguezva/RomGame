import { levels100, phasedInt } from "@/lib/levels";

/**
 * Pares e impares: la primera noción, viendo si una cantidad se puede
 * repartir en parejas exactas o si sobra una. Se clasifica igual que
 * ¿Vivo o no vivo? o El o la.
 */
export interface CantidadParidad {
  cantidad: number;
  paridad: "par" | "impar";
}

export const CANTIDADES_PARIDAD: CantidadParidad[] = Array.from({ length: 8 }, (_, i) => {
  const cantidad = i + 2; // 2..9
  return { cantidad, paridad: cantidad % 2 === 0 ? "par" : ("impar" as const) };
});

export interface ParesImparesLevel {
  level: number;
  /** Cuántas tarjetas hay que clasificar en el nivel. */
  cantidad: number;
  /** Hasta qué número entran en juego (crece con el nivel). */
  maxNumero: number;
}

export const PARES_IMPARES_LEVELS: ParesImparesLevel[] = levels100((_, level) => {
  const maxNumero = phasedInt(level, [4, 4, 5, 6, 6, 7, 8, 8, 9, 9, 9]);
  // Nunca puede haber más tarjetas por ronda que cantidades distintas
  // disponibles hasta maxNumero (cantidadesPara(maxNumero).length).
  const cantidad = Math.min(phasedInt(level, [3, 4, 5, 5, 6, 7, 8, 8, 9, 10, 10]), maxNumero - 1);
  return { cantidad, maxNumero };
});

export function cantidadesPara(maxNumero: number): CantidadParidad[] {
  return CANTIDADES_PARIDAD.filter((c) => c.cantidad <= maxNumero);
}
