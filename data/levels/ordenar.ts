import { levels100, phasedInt, stageOf, seeded } from "@/lib/levels";

/**
 * Curva de los materiales de seriación (torre rosa, escalera marrón).
 *
 * Las dificultades entran de una en una, como en el ambiente real:
 *
 *   Etapas 1-2   pocas piezas, muy distintas, con el número a la vista
 *   Etapa 3      se quita el número: solo queda el ojo
 *   Etapas 4-5   piezas de tamaños vecinos, la diferencia se vuelve sutil
 *   Etapas 6-7   se invierte la serie: del más chico al más grande
 *   Etapas 8-10  la serie completa, sin ayudas y con piezas contiguas
 */
export interface OrdenarLevel {
  level: number;
  /** Cuántas piezas entran en la serie. */
  cantidad: number;
  /** Del más grande al más chico (false) o al revés (true). */
  invertido: boolean;
  /** Muestra el número de la pieza como apoyo. */
  pista: boolean;
  /** Toma tamaños contiguos: la diferencia entre piezas es mínima. */
  cercanos: boolean;
  /** Desplazamiento del tramo de tamaños elegido. */
  desde: number;
}

export function nivelesOrdenar(maxPiezas = 10): OrdenarLevel[] {
  return levels100<OrdenarLevel>((_, level) => {
    const etapa = stageOf(level);
    const cantidad = Math.min(
      maxPiezas,
      phasedInt(level, [3, 4, 5, 5, 6, 7, 8, 9, 10, 10, 10])
    );
    const cercanos = etapa >= 4;
    const margen = Math.max(0, maxPiezas - cantidad);
    return {
      cantidad,
      invertido: etapa >= 6 && level % 2 === 0,
      pista: etapa <= 2,
      cercanos,
      desde: cercanos && margen > 0 ? Math.floor(seeded(level, 3) * (margen + 1)) : 0,
    };
  });
}
