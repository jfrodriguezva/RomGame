import { levels100, phasedInt } from "@/lib/levels";

export interface NumerosLevel {
  level: number;
  count: number;
}

/** Cadena de cuentas: unir los numeros en orden, cada vez mas largos. */
export const NUMEROS_LEVELS: NumerosLevel[] = levels100((_, level) => ({
  count: phasedInt(level, [3, 4, 5, 6, 8, 10, 12, 15, 18, 22, 26]),
}));
