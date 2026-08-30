import { levels100, phasedInt } from "@/lib/levels";

export interface BurbujasLevel {
  level: number;
  spawnMs: number;
  maxBubbles: number;
}

export const BURBUJAS_LEVELS: BurbujasLevel[] = levels100((_, level) => ({
  spawnMs: phasedInt(level, [1400, 1250, 1120, 1000, 900, 800, 710, 620, 540, 460, 380]),
  maxBubbles: phasedInt(level, [5, 6, 7, 8, 9, 11, 13, 15, 17, 20, 24]),
}));

export const BUBBLE_COLORS = [
  "from-cyan-300 to-sky-400",
  "from-pink-300 to-rose-400",
  "from-lime-300 to-green-400",
  "from-amber-300 to-yellow-400",
  "from-violet-300 to-purple-400",
];
