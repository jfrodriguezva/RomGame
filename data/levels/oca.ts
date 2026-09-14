import { levels100, phasedInt } from "@/lib/levels";

/**
 * El juego de la oca: como serpientes y escaleras, pero con casillas de
 * tres efectos en vez de solo "sube/baja" — más variedad de a dónde puede
 * llevar un mismo tiro de dado.
 */
export interface OcaLevel {
  level: number;
  length: number;
  cols: number;
  ocasCount: number;
  pozosCount: number;
  puentesPares: number;
}

export const OCA_LEVELS: OcaLevel[] = levels100((_, level) => ({
  length: phasedInt(level, [20, 26, 32, 38, 44, 50, 56, 62, 68, 74, 80]),
  cols: 6,
  ocasCount: phasedInt(level, [2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5]),
  pozosCount: phasedInt(level, [1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4]),
  puentesPares: phasedInt(level, [0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 2]),
}));

export type CasillaTipo = "oca" | "pozo" | "puente";

export interface Casilla {
  tipo: CasillaTipo;
  /** Solo en puentes: a dónde salta quien cae en esta casilla. */
  saltaA?: number;
}

export const RETROCESO_POZO = 4;

export function buildOcaBoard(config: OcaLevel): Map<number, Casilla> {
  const casillas = new Map<number, Casilla>();
  const usadas = new Set<number>([1, config.length]);

  function libre(n: number) {
    return n > 1 && n < config.length && !usadas.has(n);
  }

  for (let i = 0; i < config.ocasCount; i++) {
    let intentos = 0;
    while (intentos++ < 60) {
      const n = 2 + Math.floor(Math.random() * (config.length - 3));
      if (libre(n)) {
        usadas.add(n);
        casillas.set(n, { tipo: "oca" });
        break;
      }
    }
  }

  for (let i = 0; i < config.pozosCount; i++) {
    let intentos = 0;
    while (intentos++ < 60) {
      const n = 2 + Math.floor(Math.random() * (config.length - 3));
      if (libre(n)) {
        usadas.add(n);
        casillas.set(n, { tipo: "pozo" });
        break;
      }
    }
  }

  for (let i = 0; i < config.puentesPares; i++) {
    let intentos = 0;
    while (intentos++ < 60) {
      const desde = 2 + Math.floor(Math.random() * (config.length - 6));
      const hasta = desde + 4 + Math.floor(Math.random() * 6);
      if (libre(desde) && libre(hasta) && hasta < config.length) {
        usadas.add(desde);
        usadas.add(hasta);
        casillas.set(desde, { tipo: "puente", saltaA: hasta });
        break;
      }
    }
  }

  return casillas;
}

export function cellPosition(n: number, cols: number): { row: number; col: number } {
  const row = Math.floor((n - 1) / cols);
  const posInRow = (n - 1) % cols;
  const col = row % 2 === 0 ? posInRow : cols - 1 - posInRow;
  return { row, col };
}
