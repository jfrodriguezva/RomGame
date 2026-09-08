import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * Conciencia fonológica: separar una palabra en sus golpes de voz antes de
 * relacionarla con letras. El niño clasifica cada palabra en la canasta de
 * "cuántas sílabas" tiene, con el mismo mecanismo de ¿Vivo o no vivo?
 * (escuchar/ver, elegir canasta, si no toca se dice y se reintenta).
 */
export interface PalabraSilabas {
  emoji: string;
  nombre: string;
  silabas: 1 | 2 | 3 | 4;
}

export const PALABRAS_SILABAS: PalabraSilabas[] = [
  { emoji: "☀️", nombre: "sol", silabas: 1 },
  { emoji: "🍞", nombre: "pan", silabas: 1 },
  { emoji: "🌊", nombre: "mar", silabas: 1 },
  { emoji: "💡", nombre: "luz", silabas: 1 },

  { emoji: "🐱", nombre: "gato", silabas: 2 },
  { emoji: "🏠", nombre: "casa", silabas: 2 },
  { emoji: "🦆", nombre: "pato", silabas: 2 },
  { emoji: "🐻", nombre: "oso", silabas: 2 },

  { emoji: "⚽", nombre: "pelota", silabas: 3 },
  { emoji: "🍅", nombre: "tomate", silabas: 3 },
  { emoji: "🐴", nombre: "caballo", silabas: 3 },
  { emoji: "🎩", nombre: "sombrero", silabas: 3 },

  { emoji: "🦋", nombre: "mariposa", silabas: 4 },
  { emoji: "🦖", nombre: "dinosaurio", silabas: 4 },
  { emoji: "🐘", nombre: "elefante", silabas: 4 },
];

/** Cuántas canastas (categorías de sílabas) hay activas en el nivel. */
export type CriterioSilabas = 2 | 3 | 4;

export interface SilabasLevel {
  level: number;
  criterio: CriterioSilabas;
  cantidad: number;
}

export const SILABAS_LEVELS: SilabasLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  return {
    criterio: etapa <= 3 ? 2 : etapa <= 6 ? 3 : 4,
    cantidad: phasedInt(level, [3, 4, 5, 5, 6, 7, 8, 9, 10, 11, 12]),
  };
});

export const CANASTAS_SILABAS: Record<CriterioSilabas, number[]> = {
  2: [1, 2],
  3: [1, 2, 3],
  4: [1, 2, 3, 4],
};

/** Palabras válidas para un criterio (solo las de sílabas activas). */
export function palabrasPara(criterio: CriterioSilabas): PalabraSilabas[] {
  const activos = CANASTAS_SILABAS[criterio];
  return PALABRAS_SILABAS.filter((p) => activos.includes(p.silabas));
}
