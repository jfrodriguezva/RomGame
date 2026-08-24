import { generateLevels, lerp, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface GloboLevel {
  level: number;
  fallSpeed: number; // % de pantalla por tick
  bounceStrength: number; // % que sube al tocar
}

export const GLOBO_LEVELS: GloboLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  fallSpeed: lerp(0.5, 2.4, t),
  bounceStrength: lerp(24, 14, t),
}));

export const TICK_MS = 50;
