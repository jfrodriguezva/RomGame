import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface SerpientesLevel {
  level: number;
  length: number;
  linksCount: number;
  cols: number;
}

export const SERPIENTES_LEVELS: SerpientesLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  length: lerpInt(20, 64, t),
  linksCount: lerpInt(2, 10, t),
  cols: 6,
}));

export interface BoardLink {
  from: number;
  to: number;
  type: "snake" | "ladder";
}

export function buildBoard(length: number, linksCount: number): Map<number, BoardLink> {
  const links = new Map<number, BoardLink>();
  const used = new Set<number>([1, length]);
  let attempts = 0;

  while (links.size < linksCount && attempts < 200) {
    attempts++;
    const isLadder = Math.random() < 0.5;
    const a = 2 + Math.floor(Math.random() * (length - 3));
    const spread = 4 + Math.floor(Math.random() * 12);
    const b = isLadder ? Math.min(length - 1, a + spread) : Math.max(2, a - spread);
    if (used.has(a) || used.has(b) || a === b) continue;
    used.add(a);
    used.add(b);
    links.set(a, { from: a, to: b, type: isLadder ? "ladder" : "snake" });
  }
  return links;
}

export function cellPosition(n: number, cols: number): { row: number; col: number } {
  const row = Math.floor((n - 1) / cols);
  const posInRow = (n - 1) % cols;
  const col = row % 2 === 0 ? posInRow : cols - 1 - posInRow;
  return { row, col };
}
