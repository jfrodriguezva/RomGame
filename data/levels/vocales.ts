import { generateLevels, lerp, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface Point {
  x: number;
  y: number;
}

export interface VocalDef {
  letter: string;
  word: string;
  emoji: string;
  points: Point[];
}

export const VOCALES: VocalDef[] = [
  {
    letter: "A",
    word: "Araña",
    emoji: "🕷️",
    points: [
      { x: 20, y: 85 },
      { x: 50, y: 15 },
      { x: 80, y: 85 },
    ],
  },
  {
    letter: "E",
    word: "Elefante",
    emoji: "🐘",
    points: [
      { x: 78, y: 15 },
      { x: 22, y: 15 },
      { x: 22, y: 50 },
      { x: 58, y: 50 },
      { x: 22, y: 53 },
      { x: 22, y: 85 },
      { x: 78, y: 85 },
    ],
  },
  {
    letter: "I",
    word: "Iglú",
    emoji: "🧊",
    points: [
      { x: 50, y: 15 },
      { x: 50, y: 85 },
    ],
  },
  {
    letter: "O",
    word: "Oso",
    emoji: "🐻",
    points: Array.from({ length: 17 }, (_, i) => {
      const angle = (i / 16) * Math.PI * 2 - Math.PI / 2;
      return {
        x: Math.round((50 + 32 * Math.cos(angle)) * 100) / 100,
        y: Math.round((50 + 32 * Math.sin(angle)) * 100) / 100,
      };
    }),
  },
  {
    letter: "U",
    word: "Uva",
    emoji: "🍇",
    points: [
      { x: 25, y: 15 },
      { x: 25, y: 55 },
      { x: 28, y: 72 },
      { x: 38, y: 83 },
      { x: 50, y: 86 },
      { x: 62, y: 83 },
      { x: 72, y: 72 },
      { x: 75, y: 55 },
      { x: 75, y: 15 },
    ],
  },
];

export interface VocalesLevel {
  level: number;
  tolerance: number;
  options: number;
}

export const VOCALES_LEVELS: VocalesLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  tolerance: lerp(14, 5, t),
  options: Math.min(5, Math.round(3 + t * 2)),
}));
