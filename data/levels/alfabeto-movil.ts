import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * Alfabeto móvil.
 *
 * En Montessori el niño compone palabras con letras sueltas mucho antes de
 * saber trazarlas: primero se escribe, después se lee. Aquí se ve un objeto y
 * se arma su nombre sonido por sonido.
 *
 * La ayuda se retira despacio: primero la palabra está escrita arriba, luego
 * solo el dibujo, y al final ni siquiera hay huecos que digan cuántas letras
 * faltan.
 */
export interface AlfabetoLevel {
  level: number;
  /** Largo máximo de las palabras del nivel. */
  largo: number;
  /** Letras de más en el canasto, para que no sea por descarte. */
  extras: number;
  /** Muestra la palabra escrita como modelo. */
  modelo: boolean;
  rondas: number;
}

export const ALFABETO_LEVELS: AlfabetoLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  return {
    largo: phasedInt(level, [3, 3, 4, 4, 4, 5, 5, 6, 7, 8, 8]),
    extras: phasedInt(level, [0, 1, 1, 2, 2, 3, 3, 4, 5, 6, 6]),
    modelo: etapa <= 3,
    rondas: phasedInt(level, [1, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5]),
  };
});
