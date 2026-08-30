import { levels100, phasedInt } from "@/lib/levels";

export interface SerpientesLevel {
  level: number;
  length: number;
  linksCount: number;
  cols: number;
}

export const SERPIENTES_LEVELS: SerpientesLevel[] = levels100((_, level) => ({
  length: phasedInt(level, [18, 24, 30, 36, 42, 48, 54, 60, 66, 72, 78]),
  linksCount: phasedInt(level, [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]),
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
