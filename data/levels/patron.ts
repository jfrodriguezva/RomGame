import { levels100, phasedInt } from "@/lib/levels";

export interface PatronLevel {
  level: number;
  buttons: number;
  sequenceLength: number;
}

export const PATRON_LEVELS: PatronLevel[] = levels100((_, level) => ({
  buttons: phasedInt(level, [3, 3, 4, 4, 5, 5, 6, 6, 7, 8, 8]),
  sequenceLength: phasedInt(level, [2, 3, 4, 5, 6, 8, 10, 12, 15, 18, 22]),
}));

export const PATRON_BUTTONS = [
  { color: "bg-red-400", emoji: "🔴" },
  { color: "bg-blue-400", emoji: "🔵" },
  { color: "bg-yellow-400", emoji: "🟡" },
  { color: "bg-green-400", emoji: "🟢" },
  { color: "bg-purple-400", emoji: "🟣" },
  { color: "bg-orange-400", emoji: "\u{1F7E0}" },
  { color: "bg-cyan-400", emoji: "\u{1FA75}" },
  { color: "bg-pink-400", emoji: "\u{1FA77}" },
];
