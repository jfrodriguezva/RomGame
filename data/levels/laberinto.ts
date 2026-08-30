import { levels100, phasedInt } from "@/lib/levels";

export interface LaberintoLevel {
  level: number;
  size: number;
}

export const LABERINTO_LEVELS: LaberintoLevel[] = levels100((_, level) => ({
  size: phasedInt(level, [4, 5, 6, 7, 8, 9, 11, 13, 15, 17, 20]),
}));

export interface MazeCell {
  top: boolean;
  right: boolean;
  bottom: boolean;
  left: boolean;
  visited: boolean;
}

export function generateMaze(size: number): MazeCell[][] {
  const cells: MazeCell[][] = Array.from({ length: size }, () =>
    Array.from({ length: size }, () => ({
      top: true,
      right: true,
      bottom: true,
      left: true,
      visited: false,
    }))
  );

  function carve(r: number, c: number) {
    cells[r][c].visited = true;
    const dirs: [number, number, keyof MazeCell, keyof MazeCell][] = [
      [0, 1, "right", "left"],
      [0, -1, "left", "right"],
      [1, 0, "bottom", "top"],
      [-1, 0, "top", "bottom"],
    ];
    for (let i = dirs.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [dirs[i], dirs[j]] = [dirs[j], dirs[i]];
    }
    for (const [dr, dc, wallA, wallB] of dirs) {
      const nr = r + dr;
      const nc = c + dc;
      if (nr >= 0 && nr < size && nc >= 0 && nc < size && !cells[nr][nc].visited) {
        (cells[r][c][wallA] as boolean) = false;
        (cells[nr][nc][wallB] as boolean) = false;
        carve(nr, nc);
      }
    }
  }

  carve(0, 0);
  return cells;
}
