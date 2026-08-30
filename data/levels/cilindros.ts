import { levels100, phasedInt, seeded, stageOf } from "@/lib/levels";

/**
 * Bloques de cilindros con botón.
 *
 * El control del error del material real es implacable y silencioso: si te
 * equivocaste en uno, al final te sobra un cilindro y falta un hueco. Nadie
 * tiene que decir nada. Aquí se conserva esa idea: el hueco equivocado
 * simplemente no acepta el cilindro.
 */
export type VariacionCilindro = "diametro" | "altura" | "ambas";

export interface CilindrosLevel {
  level: number;
  cantidad: number;
  variacion: VariacionCilindro;
  /** Rango de tamaños más estrecho: la diferencia se vuelve mínima. */
  desde: number;
}

export const CILINDROS_LEVELS: CilindrosLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  const cantidad = phasedInt(level, [3, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10]);
  const margen = Math.max(0, 10 - cantidad);
  return {
    cantidad,
    variacion: etapa <= 3 ? "diametro" : etapa <= 6 ? "altura" : "ambas",
    desde: etapa >= 5 && margen > 0 ? Math.floor(seeded(level, 7) * (margen + 1)) : 0,
  };
});
