import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * El juego del "yo veo".
 *
 * La guía dice "veo veo algo que empieza con mmm" y el niño busca el objeto.
 * Es puro oído: no aparece ninguna letra escrita hasta bien entrado el
 * material, porque primero hay que oír el sonido dentro de la palabra.
 *
 * En las últimas etapas se pregunta por el sonido final, que es bastante más
 * difícil de aislar que el inicial.
 */
export interface SonidosLevel {
  level: number;
  opciones: number;
  rondas: number;
  /** Se busca el primer sonido o el último. */
  posicion: "inicial" | "final";
  /** Muestra también la letra escrita junto al sonido. */
  mostrarLetra: boolean;
}

export const SONIDOS_LEVELS: SonidosLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  return {
    opciones: phasedInt(level, [2, 3, 3, 4, 4, 5, 6, 6, 8, 8, 9]),
    rondas: phasedInt(level, [3, 3, 4, 4, 5, 5, 6, 6, 7, 8, 8]),
    posicion: etapa >= 8 ? "final" : "inicial",
    mostrarLetra: etapa >= 4,
  };
});
