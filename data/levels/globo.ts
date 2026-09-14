import { levels100, phased, phasedInt } from "@/lib/levels";

export interface GloboLevel {
  level: number;
  fallSpeed: number; // % de pantalla por tick
  bounceStrength: number; // % que sube al tocar
  balloonCount: number; // cuántos globos hay que mantener en el aire a la vez
}

export const GLOBO_LEVELS: GloboLevel[] = levels100((_, level) => ({
  fallSpeed: phased(level, [0.45, 0.6, 0.78, 0.96, 1.16, 1.38, 1.62, 1.88, 2.16, 2.46, 2.8]),
  bounceStrength: phased(level, [28, 27, 26, 24, 23, 21, 20, 18, 17, 16, 15]),
  // Empieza con un solo globo (primeras 3 etapas) y suma uno más por etapa
  // hasta 4: la dificultad crece en cantidad de cosas que atender a la vez,
  // no solo en velocidad de caída.
  balloonCount: phasedInt(level, [1, 1, 1, 2, 2, 2, 3, 3, 3, 4]),
}));

export const TICK_MS = 50;
