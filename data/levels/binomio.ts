import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * Cubo del binomio (y del trinomio en las últimas etapas).
 *
 * El niño no sabe que está manipulando (a+b)³; solo sabe que las piezas
 * encajan de una forma y de ninguna otra. El álgebra llega años después, y
 * cuando llega, la mano ya la conoce.
 */
export interface BinomioLevel {
  level: number;
  /** 2 = binomio, 3 = trinomio, 4 = reto extra. */
  lado: number;
  /** El modelo sigue a la vista mientras se arma. */
  modeloVisible: boolean;
  /** Segundos que se ve el modelo antes de esconderse. */
  vistazo: number;
}

export const BINOMIO_LEVELS: BinomioLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  return {
    lado: phasedInt(level, [2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4]),
    modeloVisible: etapa <= 6,
    vistazo: etapa <= 6 ? 0 : etapa <= 8 ? 5 : 3,
  };
});

/** Colores del material: rojo, azul y negro, como las caras del cubo. */
export const BINOMIO_COLORES = ["#b23b34", "#2f5f9e", "#3a3a3a", "#c78a2f"];
