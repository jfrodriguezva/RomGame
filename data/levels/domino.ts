import { levels100, phasedInt } from "@/lib/levels";
import { OBJETOS_POOL } from "./que-falta";

/**
 * Dominó de imágenes: encajar por dibujo, no por número. Versión
 * simplificada a propósito — la fila solo crece hacia la derecha (nunca
 * hacia los dos lados como el dominó real), para que el modelo mental sea
 * "agrega tu ficha al final", no "elige por dónde puede entrar".
 */
export interface DominoLevel {
  level: number;
  imagenes: number;
  fichasPorJugador: number;
}

export const DOMINO_LEVELS: DominoLevel[] = levels100((_, level) => ({
  imagenes: phasedInt(level, [3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7]),
  fichasPorJugador: phasedInt(level, [3, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7]),
}));

export interface Ficha {
  id: number;
  a: string;
  b: string;
}

/** Todas las fichas posibles para N imágenes, dobles incluidos. */
export function generarFichas(imagenes: number): Ficha[] {
  const iconos = OBJETOS_POOL.slice(0, imagenes);
  const fichas: Ficha[] = [];
  let id = 0;
  for (let i = 0; i < iconos.length; i++) {
    for (let j = i; j < iconos.length; j++) {
      fichas.push({ id: id++, a: iconos[i], b: iconos[j] });
    }
  }
  return fichas;
}
