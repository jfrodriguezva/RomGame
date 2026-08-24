import { generateLevels, lerp, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface LavaLevel {
  level: number;
  speed: number; // px por tick
  gapEvery: number; // cada cuántas baldosas aparece un hueco de lava
}

export const LAVA_LEVELS: LavaLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  speed: lerp(2.4, 6.5, t),
  gapEvery: lerpInt(6, 3, t),
}));

export const TILE_WIDTH = 56;
export const TICK_MS = 50;
export const JUMP_MS = 420;
export const PLAYER_X = 90;
export const START_LIVES = 3;

export function buildTrack(gapEvery: number, length = 400): boolean[] {
  // true = baldosa segura, false = lava
  const track: boolean[] = [true, true, true, true, true];
  let sinceGap = 0;
  for (let i = track.length; i < length; i++) {
    sinceGap++;
    const canGap = sinceGap >= gapEvery && track[i - 1] === true;
    const isGap = canGap && Math.random() < 0.6;
    track.push(!isGap);
    if (isGap) sinceGap = 0;
  }
  return track;
}
