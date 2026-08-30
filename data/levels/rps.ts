import { levels100, phased } from "@/lib/levels";

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

export const RPS_LEVELS: RpsLevel[] = levels100((_, level) => ({
  counterChance: phased(level, [0, 0.05, 0.12, 0.2, 0.28, 0.36, 0.44, 0.52, 0.6, 0.68, 0.75]),
}));
