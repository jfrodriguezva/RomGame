import { levels100, phased, phasedInt } from "@/lib/levels";

export interface LavaLevel {
  level: number;
  speed: number; // px por tick
  gapEvery: number; // cada cuántas baldosas aparece un hueco de lava
}

export const LAVA_LEVELS: LavaLevel[] = levels100((_, level) => ({
  speed: phased(level, [2.2, 2.6, 3.0, 3.5, 4.0, 4.5, 5.0, 5.6, 6.2, 6.8, 7.5]),
  gapEvery: phasedInt(level, [8, 7, 7, 6, 6, 5, 5, 4, 4, 3, 3]),
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
