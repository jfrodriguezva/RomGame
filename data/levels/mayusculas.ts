import { levels100, phasedInt } from "@/lib/levels";
import { ALFABETO_ORDEN } from "@/data/trazos-letras";

/**
 * Mayúsculas y minúsculas: la misma letra tiene dos formas, y hay que
 * reconocerlas como la misma. Usa el orden de presentación del abecedario
 * (data/trazos-letras.ts) para no mostrar letras que el niño aún no vio.
 */
export interface LetraCaso {
  letra: string;
  caso: "mayuscula" | "minuscula";
}

export const LETRAS_CASO: LetraCaso[] = ALFABETO_ORDEN.flatMap((letra) => [
  { letra, caso: "mayuscula" as const },
  { letra, caso: "minuscula" as const },
]);

export interface MayusculasLevel {
  level: number;
  /** Cuántas letras del alfabeto entran en juego. */
  letras: number;
  cantidad: number;
}

export const MAYUSCULAS_LEVELS: MayusculasLevel[] = levels100((_, level) => {
  const letras = Math.min(
    ALFABETO_ORDEN.length,
    phasedInt(level, [4, 5, 6, 8, 10, 12, 15, 18, 22, 27, 27])
  );
  return {
    letras,
    cantidad: Math.min(phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]), letras * 2),
  };
});

export function letrasPara(n: number): LetraCaso[] {
  const alfabeto = new Set(ALFABETO_ORDEN.slice(0, n));
  return LETRAS_CASO.filter((l) => alfabeto.has(l.letra));
}
