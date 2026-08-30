import type { ToyStoryCharacter } from "@/phaser/ToyStoryScene";
import { levels100, phased, phasedInt } from "@/lib/levels";

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

export const TOYSTORY_LEVELS: ToyStoryLevel[] = levels100((_, level) => ({
  enemySpeed: phased(level, [42, 52, 62, 73, 84, 96, 108, 121, 135, 150, 166]),
  enemyCount: phasedInt(level, [1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7]),
}));
