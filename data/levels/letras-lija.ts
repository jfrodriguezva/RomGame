import { levels100, phased, phasedInt, stageOf } from "@/lib/levels";
import { ALFABETO_ORDEN, VOCALES_ORDEN } from "@/data/trazos-letras";

/**
 * Letras de lija.
 *
 * El material original es una tarjeta con la letra recortada en papel de lija:
 * el niño la recorre con dos dedos mientras el adulto dice su sonido. Se toca,
 * se oye y se ve al mismo tiempo, y esa es toda la lección.
 *
 * El orden no es alfabético: primero las vocales, después las consonantes que
 * se pueden alargar con la voz (mmm, sss, lll), y al final las difíciles.
 */
export interface LetrasLijaLevel {
  level: number;
  /** Letras disponibles en este nivel. */
  pool: string[];
  /** Cuántas letras se trazan para completar el nivel. */
  rondas: number;
  /** Margen alrededor del trazo, en unidades del lienzo. */
  tolerancia: number;
  /** Los puntos de apoyo desaparecen en las últimas etapas. */
  mostrarPuntos: boolean;
}

export const LETRAS_LIJA_LEVELS: LetrasLijaLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  const cuantas = phasedInt(level, [5, 7, 9, 12, 15, 18, 21, 24, 27, 27, 27]);
  return {
    pool: etapa <= 2 ? VOCALES_ORDEN : ALFABETO_ORDEN.slice(0, cuantas),
    rondas: phasedInt(level, [1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4]),
    tolerancia: phased(level, [14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4]),
    mostrarPuntos: etapa <= 7,
  };
});
