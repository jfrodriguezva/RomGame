import type { ToyStoryCharacter } from "@/phaser/ToyStoryScene";
import { generateLevels, lerp, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export const TOYSTORY_CHARACTERS: ToyStoryCharacter[] = [
  { id: "woody", emoji: "🤠", color: "#ca8a04" },
  { id: "buzz", emoji: "🚀", color: "#16a34a" },
  { id: "jessie", emoji: "👩‍🌾", color: "#dc2626" },
];

export interface ToyStoryLevel {
  level: number;
  enemySpeed: number;
  enemyCount: number;
}

export const TOYSTORY_LEVELS: ToyStoryLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  enemySpeed: lerp(45, 140, t),
  enemyCount: lerpInt(1, 6, t),
}));
