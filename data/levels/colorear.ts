import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface ColorZone {
  id: string;
  cx: number;
  cy: number;
  r: number;
}

function round2(n: number): number {
  return Math.round(n * 100) / 100;
}

// Genera una mandala/flor con `count` círculos (1 centro + pétalos alrededor).
function generateZones(count: number): ColorZone[] {
  const zones: ColorZone[] = [{ id: "centro", cx: 150, cy: 150, r: count > 10 ? 20 : 28 }];
  const petals = count - 1;
  const ringR = petals > 12 ? 95 : 70;
  const petalR = petals > 12 ? 16 : petals > 6 ? 22 : 30;
  for (let i = 0; i < petals; i++) {
    const angle = (i / petals) * Math.PI * 2;
    zones.push({
      id: `petalo-${i}`,
      cx: round2(150 + ringR * Math.cos(angle)),
      cy: round2(150 + ringR * Math.sin(angle)),
      r: petalR,
    });
  }
  return zones;
}

export interface ColorearLevel {
  level: number;
  zones: ColorZone[];
}

export const COLOREAR_LEVELS: ColorearLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  zones: generateZones(lerpInt(3, 25, t)),
}));

export const COLOREAR_PALETTE = [
  "#ef4444",
  "#f97316",
  "#eab308",
  "#22c55e",
  "#06b6d4",
  "#3b82f6",
  "#a855f7",
  "#ec4899",
];
