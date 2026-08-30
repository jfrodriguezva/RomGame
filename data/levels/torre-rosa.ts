import { nivelesOrdenar, type OrdenarLevel } from "./ordenar";

/**
 * Torre rosa: diez cubos de 1 a 10 centímetros de arista.
 *
 * El material real es de un solo color justamente para que la única
 * diferencia perceptible sea el tamaño. Aquí pasa lo mismo: un solo rosa.
 */
export type TorreLevel = OrdenarLevel;
export const TORRE_LEVELS: TorreLevel[] = nivelesOrdenar(10);

/** Lado del cubo en pixeles, del más pequeño al más grande. */
export function ladoCubo(valor: number): number {
  return 26 + valor * 11;
}
