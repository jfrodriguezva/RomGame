import { levels100, phasedInt } from "@/lib/levels";

/** Medios de transporte: clasificación por dónde se mueven — tierra, aire, agua. */
export interface Vehiculo {
  emoji: string;
  nombre: string;
  medio: "tierra" | "aire" | "agua";
}

export const VEHICULOS: Vehiculo[] = [
  { emoji: "🚗", nombre: "el carro", medio: "tierra" },
  { emoji: "🚌", nombre: "el autobús", medio: "tierra" },
  { emoji: "🚲", nombre: "la bicicleta", medio: "tierra" },

  { emoji: "✈️", nombre: "el avión", medio: "aire" },
  { emoji: "🚁", nombre: "el helicóptero", medio: "aire" },
  { emoji: "🎈", nombre: "el globo aerostático", medio: "aire" },

  { emoji: "🚢", nombre: "el barco", medio: "agua" },
  { emoji: "⛵", nombre: "el velero", medio: "agua" },
  { emoji: "🛥️", nombre: "la lancha", medio: "agua" },
];

export interface TransporteLevel {
  level: number;
  cantidad: number;
}

export const TRANSPORTE_LEVELS: TransporteLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 5, 6, 7, 8, 8, 9, 9, 9]), VEHICULOS.length),
}));
