import { generateLevels, lerp, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface BurbujasLevel {
  level: number;
  spawnMs: number;
  maxBubbles: number;
}

export const BURBUJAS_LEVELS: BurbujasLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  spawnMs: Math.round(lerp(1200, 380, t)),
  maxBubbles: lerpInt(6, 18, t),
}));

export const BUBBLE_COLORS = [
  "from-cyan-300 to-sky-400",
  "from-pink-300 to-rose-400",
  "from-lime-300 to-green-400",
  "from-amber-300 to-yellow-400",
  "from-violet-300 to-purple-400",
];
