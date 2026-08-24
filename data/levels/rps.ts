import { generateLevels, lerp, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export type RpsChoice = "piedra" | "papel" | "tijera";

export const RPS_CHOICES: { id: RpsChoice; emoji: string; label: string; beats: RpsChoice }[] = [
  { id: "piedra", emoji: "🪨", label: "Piedra", beats: "tijera" },
  { id: "papel", emoji: "📄", label: "Papel", beats: "piedra" },
  { id: "tijera", emoji: "✂️", label: "Tijera", beats: "papel" },
];

export interface RpsLevel {
  level: number;
  counterChance: number; // probabilidad de que la CPU intente adivinar tu jugada frecuente
}

export const RPS_LEVELS: RpsLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  counterChance: lerp(0, 0.65, t),
}));
