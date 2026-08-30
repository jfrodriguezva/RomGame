import { nivelesOrdenar, type OrdenarLevel } from "./ordenar";

/**
 * Escalera marrón: diez prismas del mismo largo y distinto grosor.
 *
 * A diferencia de la torre rosa, aquí solo cambia una dimensión, así que la
 * comparación es más difícil: hay que mirar el canto, no el bulto.
 */
export type EscaleraLevel = OrdenarLevel;
export const ESCALERA_LEVELS: EscaleraLevel[] = nivelesOrdenar(10);

/** Grosor del prisma en pixeles. */
export function grosorPrisma(valor: number): number {
  return 10 + valor * 8;
}
