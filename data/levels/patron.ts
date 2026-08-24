import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface PatronLevel {
  level: number;
  buttons: number;
  sequenceLength: number;
}

export const PATRON_LEVELS: PatronLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  buttons: Math.min(5, lerpInt(3, 5, t)),
  sequenceLength: lerpInt(3, 20, t),
}));

export const PATRON_BUTTONS = [
  { color: "bg-red-400", emoji: "🔴" },
  { color: "bg-blue-400", emoji: "🔵" },
  { color: "bg-yellow-400", emoji: "🟡" },
  { color: "bg-green-400", emoji: "🟢" },
  { color: "bg-purple-400", emoji: "🟣" },
];
