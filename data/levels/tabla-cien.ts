import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * La tabla del cien.
 *
 * Cien casillas y fichas para colocar. Al principio faltan una o dos y el
 * rango llega solo al veinte; al final falta media tabla y hay que apoyarse en
 * la estructura de la decena para saber dónde va cada número.
 */
export interface TablaCienLevel {
  level: number;
  /** Número más alto que aparece en el tablero. */
  hasta: number;
  /** Cuántas casillas hay que llenar. */
  huecos: number;
  /** Muestra la fila de decenas como referencia. */
  guia: boolean;
}

export const TABLA_CIEN_LEVELS: TablaCienLevel[] = levels100((_, level) => ({
  hasta: phasedInt(level, [20, 20, 30, 40, 50, 60, 70, 80, 100, 100, 100]),
  huecos: phasedInt(level, [1, 2, 3, 4, 5, 7, 9, 12, 15, 18, 22]),
  guia: stageOf(level) <= 5,
}));
