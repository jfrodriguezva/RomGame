import { levels100, phasedInt } from "@/lib/levels";

/**
 * Encajes de formas geométricas: la pieza sólo entra en el agujero de su
 * misma forma exacta, igual que el material real (el cilindro con botón,
 * el gabinete de geometría). El control del error es físico en el
 * original — aquí es visual: si la forma no coincide, no "entra".
 */
export const FORMAS_POOL = [
  "circulo",
  "cuadrado",
  "triangulo",
  "rombo",
  "estrella",
  "corazon",
  "hexagono",
  "rectangulo",
] as const;

export type FormaId = (typeof FORMAS_POOL)[number];

export const FORMA_PATH: Record<FormaId, string> = {
  circulo: "M50 10 A40 40 0 1 1 49.9 10 Z",
  cuadrado: "M15 15 H85 V85 H15 Z",
  triangulo: "M50 10 L90 85 H10 Z",
  rombo: "M50 6 L94 50 L50 94 L6 50 Z",
  estrella: "M50 4 L62 37 H96 L69 58 L80 92 L50 72 L20 92 L31 58 L4 38 H38 Z",
  corazon: "M50 88 C8 58 10 22 34 18 C44 16 50 24 50 32 C50 24 56 16 66 18 C90 22 92 58 50 88 Z",
  hexagono: "M25 12 H75 L92 50 L75 88 H25 L8 50 Z",
  rectangulo: "M8 26 H92 V74 H8 Z",
};

export const NOMBRE_FORMA: Record<FormaId, string> = {
  circulo: "Círculo",
  cuadrado: "Cuadrado",
  triangulo: "Triángulo",
  rombo: "Rombo",
  estrella: "Estrella",
  corazon: "Corazón",
  hexagono: "Hexágono",
  rectangulo: "Rectángulo",
};

export interface OrificiosLevel {
  level: number;
  /** Formas que pueden aparecer en este nivel. */
  activos: FormaId[];
  /** Cuántos agujeros se muestran por ronda (algunos son distractores). */
  opciones: number;
}

export const ORIFICIOS_LEVELS: OrificiosLevel[] = levels100((_, level) => {
  const nActivos = phasedInt(level, [3, 3, 4, 4, 5, 5, 6, 7, 8, 8, 8]);
  const opciones = phasedInt(level, [2, 3, 3, 3, 4, 4, 4, 5, 5, 6, 6]);
  return {
    activos: FORMAS_POOL.slice(0, nActivos),
    opciones: Math.min(opciones, nActivos),
  };
});
