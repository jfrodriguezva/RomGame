import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * Barras numéricas rojas y azules.
 *
 * La barra del cinco no es "el número cinco": es una barra que mide cinco.
 * Primero se cuenta con el dedo, después se le pone nombre, y solo al final
 * aparecen la suma y el complemento a diez. Ese orden —cantidad, símbolo,
 * operación— es el corazón de las matemáticas Montessori.
 */
export type ModoBarras = "contar" | "nombrar" | "construir" | "sumar" | "completar";

export interface BarrasLevel {
  level: number;
  modo: ModoBarras;
  /** Barra más larga que puede aparecer. */
  max: number;
  opciones: number;
}

const MODOS: ModoBarras[] = [
  "contar",
  "contar",
  "nombrar",
  "nombrar",
  "construir",
  "construir",
  "sumar",
  "sumar",
  "completar",
  "completar",
];

export const BARRAS_LEVELS: BarrasLevel[] = levels100((_, level) => ({
  modo: MODOS[stageOf(level) - 1],
  max: phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10]),
  opciones: phasedInt(level, [2, 3, 3, 4, 4, 4, 5, 5, 6, 6, 6]),
}));

/** Color de cada decímetro de la barra: se alternan rojo y azul. */
export function colorSegmento(indice: number): string {
  return indice % 2 === 0 ? "#c0392b" : "#2c5f9e";
}
