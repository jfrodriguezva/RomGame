import { levels100, phasedInt, stageOf } from "@/lib/levels";
import { FORMAS, type FormaDef } from "./formas";

/**
 * ¿Cuántos lados tiene? Reutiliza las figuras del gabinete de geometría
 * (formas.ts), pero clasifica por número de lados en vez de nombrar la
 * figura — puente entre geometría y número. Las formas curvas (círculo,
 * óvalo...) no entran: no tienen lados que contar.
 */
export interface FormaConLados {
  forma: FormaDef;
  lados: number;
}

const LADOS_POR_ID: Record<string, number> = {
  equilatero: 3,
  isosceles: 3,
  "rectangulo-tri": 3,
  escaleno: 3,
  obtusangulo: 3,
  cuadrado: 4,
  rectangulo: 4,
  rombo: 4,
  trapecio: 4,
  paralelogramo: 4,
  pentagono: 5,
  hexagono: 6,
  octagono: 8,
};

export const FORMAS_CON_LADOS: FormaConLados[] = FORMAS.filter((f) => f.id in LADOS_POR_ID).map(
  (forma) => ({ forma, lados: LADOS_POR_ID[forma.id] })
);

/** Qué números de lados entran en juego en cada etapa. */
const LADOS_POR_ETAPA: number[][] = [
  [3, 4],
  [3, 4],
  [3, 4],
  [3, 4, 5],
  [3, 4, 5],
  [3, 4, 5],
  [3, 4, 5, 6],
  [3, 4, 5, 6],
  [3, 4, 5, 6, 8],
  [3, 4, 5, 6, 8],
];

export interface LadosLevel {
  level: number;
  activos: number[];
  cantidad: number;
}

export const LADOS_LEVELS: LadosLevel[] = levels100((_, level) => {
  const activos = LADOS_POR_ETAPA[stageOf(level) - 1];
  const poolMax = FORMAS_CON_LADOS.filter((f) => activos.includes(f.lados)).length;
  const cantidad = Math.min(phasedInt(level, [3, 4, 5, 5, 6, 7, 8, 8, 9, 10, 10]), poolMax);
  return { activos, cantidad };
});

export function formasPara(activos: number[]): FormaConLados[] {
  return FORMAS_CON_LADOS.filter((f) => activos.includes(f.lados));
}
