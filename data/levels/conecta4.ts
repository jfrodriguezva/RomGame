import { levels100, phasedInt } from "@/lib/levels";

/**
 * Cuatro en línea: el tablero crece de 4×4 (nivel 1) al clásico 7×6
 * (niveles altos) — la única variable que cambia es el tamaño, la regla
 * de "cuatro seguidas" es siempre la misma.
 */
export interface Conecta4Level {
  level: number;
  cols: number;
  filas: number;
}

export const CONECTA4_LEVELS: Conecta4Level[] = levels100((_, level) => ({
  cols: phasedInt(level, [4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7]),
  filas: phasedInt(level, [4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6]),
}));

export type Ficha = "jugador" | "cpu";
export type Tablero = (Ficha | null)[][];

export function tableroVacio(filas: number, cols: number): Tablero {
  return Array.from({ length: filas }, () => Array<Ficha | null>(cols).fill(null));
}

/** Fila donde caería una ficha en esta columna, o -1 si está llena. */
export function filaDestino(tablero: Tablero, col: number): number {
  for (let f = tablero.length - 1; f >= 0; f--) {
    if (tablero[f][col] === null) return f;
  }
  return -1;
}

const DIRECCIONES = [
  [0, 1],
  [1, 0],
  [1, 1],
  [1, -1],
] as const;

export function hayGanador(tablero: Tablero): Ficha | null {
  const filas = tablero.length;
  const cols = tablero[0]?.length ?? 0;
  for (let f = 0; f < filas; f++) {
    for (let c = 0; c < cols; c++) {
      const ficha = tablero[f][c];
      if (!ficha) continue;
      for (const [df, dc] of DIRECCIONES) {
        let seguidas = 1;
        for (let i = 1; i < 4; i++) {
          const nf = f + df * i;
          const nc = c + dc * i;
          if (nf < 0 || nf >= filas || nc < 0 || nc >= cols || tablero[nf][nc] !== ficha) break;
          seguidas++;
        }
        if (seguidas >= 4) return ficha;
      }
    }
  }
  return null;
}

export function tableroLleno(tablero: Tablero): boolean {
  return tablero.every((fila) => fila.every((c) => c !== null));
}
