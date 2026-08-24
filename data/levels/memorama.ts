import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface MemoramaLevel {
  level: number;
  pairs: number;
}

export const MEMORAMA_LEVELS: MemoramaLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  pairs: lerpInt(3, 15, t),
}));
