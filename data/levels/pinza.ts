import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * Pinza de transferencia: mover objetos de uno en uno de una bandeja a otra.
 * El control del error es la cantidad exacta, no la app: si ya se juntaron
 * los que hacían falta, tomar uno más es el error (como en el material real,
 * donde ya no caben en su sitio).
 */
export interface PinzaLevel {
  level: number;
  /** Cuántas piezas hay que transferir. */
  objetivo: number;
  /** Cuántas hay disponibles en la bandeja de origen (>= objetivo). */
  origen: number;
}

export const PINZA_LEVELS: PinzaLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  const objetivo = phasedInt(level, [2, 3, 3, 4, 5, 6, 7, 8, 9, 10, 10]);
  // En las etapas altas sobran piezas en el origen: hay que saber parar a tiempo.
  const extra = etapa <= 3 ? 0 : etapa <= 6 ? 1 : 2;
  return { objetivo, origen: objetivo + extra };
});

export const OBJETOS_PINZA = ["🫘", "🌰", "🔘", "🧊", "🍬"];
