import { levels100, phasedInt } from "@/lib/levels";

/**
 * Mitades y enteros: primer contacto con la fracción, siempre concreta
 * (una figura partida a la mitad) antes que el símbolo ½.
 */
export type FormaFraccion = "circulo" | "cuadrado" | "triangulo";
export type EstadoFraccion = "entero" | "mitad";

export interface FiguraFraccion {
  id: string;
  forma: FormaFraccion;
  estado: EstadoFraccion;
}

export const FIGURAS_FRACCION: FiguraFraccion[] = [
  { id: "circulo-entero", forma: "circulo", estado: "entero" },
  { id: "circulo-mitad", forma: "circulo", estado: "mitad" },
  { id: "cuadrado-entero", forma: "cuadrado", estado: "entero" },
  { id: "cuadrado-mitad", forma: "cuadrado", estado: "mitad" },
  { id: "triangulo-entero", forma: "triangulo", estado: "entero" },
  { id: "triangulo-mitad", forma: "triangulo", estado: "mitad" },
];

export interface MitadesLevel {
  level: number;
  cantidad: number;
}

export const MITADES_LEVELS: MitadesLevel[] = levels100((_, level) => ({
  cantidad: phasedInt(level, [2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6]),
}));
