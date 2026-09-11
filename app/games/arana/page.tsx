"use client";

import { useEffect, useRef, useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { ARANA_LEVELS, REVEAL_THRESHOLD, START_LIVES } from "@/data/levels/arana";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

type Grid = boolean[][];
type CellPos = { r: number; c: number };

function makeGrid(rows: number, cols: number, value: boolean): Grid {
  return Array.from({ length: rows }, () => Array.from({ length: cols }, () => value));
}

function isSafe(revealed: Grid, r: number, c: number, rows: number, cols: number): boolean {
  if (r < 0 || r >= rows || c < 0 || c >= cols) return true;
  return revealed[r][c];
}

function floodReveal(trail: CellPos[], revealed: Grid, rows: number, cols: number): Grid {
  const trailSet = new Set(trail.map((t) => `${t.r},${t.c}`));
  const reachable = new Set<string>();
  const stack: [number, number][] = [];

  for (let r = 0; r < rows; r++) {
    for (let c = 0; c < cols; c++) {
      const isEdge = r === 0 || c === 0 || r === rows - 1 || c === cols - 1;
      const key = `${r},${c}`;
      if (isEdge && !revealed[r][c] && !trailSet.has(key)) {
        stack.push([r, c]);
        reachable.add(key);
      }
    }
  }

  while (stack.length) {
    const [r, c] = stack.pop()!;
    for (const [dr, dc] of [
      [0, 1],
      [0, -1],
      [1, 0],
      [-1, 0],
    ]) {
      const nr = r + dr;
      const nc = c + dc;
      if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
      const key = `${nr},${nc}`;
      if (revealed[nr][nc] || trailSet.has(key) || reachable.has(key)) continue;
      reachable.add(key);
      stack.push([nr, nc]);
    }
  }

  const next = revealed.map((row) => [...row]);
  for (let r = 0; r < rows; r++) {
    for (let c = 0; c < cols; c++) {
      const key = `${r},${c}`;
      if (!revealed[r][c] && (trailSet.has(key) || !reachable.has(key))) {
        next[r][c] = true;
      }
    }
  }
  return next;
}

function randomHiddenCell(revealed: Grid, rows: number, cols: number): CellPos {
  for (let attempt = 0; attempt < 50; attempt++) {
    const r = Math.floor(Math.random() * rows);
    const c = Math.floor(Math.random() * cols);
    if (!revealed[r][c]) return { r, c };
  }
  return { r: 0, c: 0 };
}

export default function AranaPage() {
  const [level, setLevel] = useState(1);
  const config = ARANA_LEVELS.find((l) => l.level === level)!;
  const [revealed, setRevealed] = useState<Grid>([]);
  const [trail, setTrail] = useState<CellPos[]>([]);
  const [dragging, setDragging] = useState(false);
  const [enemies, setEnemies] = useState<CellPos[]>([]);
  const [lives, setLives] = useState(START_LIVES);
  const [hit, setHit] = useState(false);
  const [showWin, setShowWin] = useState(false);
  const revealedRef = useRef<Grid>([]);
  const trailRef = useRef<CellPos[]>([]);
  const draggingRef = useRef(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("arana");
    startBoard();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function startBoard() {
    const grid = makeGrid(config.rows, config.cols, false);
    setRevealed(grid);
    revealedRef.current = grid;
    setTrail([]);
    trailRef.current = [];
    setDragging(false);
    draggingRef.current = false;
    setLives(START_LIVES);
    setEnemies(
      Array.from({ length: config.enemyCount }, () => randomHiddenCell(grid, config.rows, config.cols))
    );
    setShowWin(false);
  }

  useEffect(() => {
    revealedRef.current = revealed;
  }, [revealed]);
  useEffect(() => {
    trailRef.current = trail;
  }, [trail]);
  useEffect(() => {
    draggingRef.current = dragging;
  }, [dragging]);

  // movimiento de la araña mala
  useEffect(() => {
    const interval = setInterval(() => {
      setEnemies((prev) =>
        prev.map((enemy) => {
          const dirs = [
            [0, 1],
            [0, -1],
            [1, 0],
            [-1, 0],
          ];
          const [dr, dc] = dirs[Math.floor(Math.random() * dirs.length)];
          const nr = enemy.r + dr;
          const nc = enemy.c + dc;
          if (nr < 0 || nr >= config.rows || nc < 0 || nc >= config.cols) return enemy;
          if (revealedRef.current[nr]?.[nc]) return enemy;
          return { r: nr, c: nc };
        })
      );
    }, config.enemyMs);
    return () => clearInterval(interval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  // colisión araña-trazo
  useEffect(() => {
    if (!draggingRef.current || trailRef.current.length === 0) return;
    const collided = enemies.some((e) => trailRef.current.some((t) => t.r === e.r && t.c === e.c));
    if (collided) loseLife();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [enemies]);

  const totalCells = config.rows * config.cols;
  const revealedCount = revealed.flat().filter(Boolean).length;
  const isWin = totalCells > 0 && revealedCount / totalCells >= REVEAL_THRESHOLD;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("arana", 1);
      unlockNextLevel("arana", Math.min(level + 1, ARANA_LEVELS.length));
      // Celebra el acierto y lo oculta con un temporizador: efecto
      // legítimo respondiendo a isWin, no una derivación pura.
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function loseLife() {
    playSound("wrong");
    setHit(true);
    setTimeout(() => setHit(false), 350);
    setTrail([]);
    trailRef.current = [];
    setDragging(false);
    draggingRef.current = false;
    setLives((prev) => {
      const next = prev - 1;
      if (next <= 0) {
        setTimeout(() => startBoard(), 500);
        return START_LIVES;
      }
      return next;
    });
  }

  function neighborsSafe(r: number, c: number) {
    return (
      isSafe(revealed, r - 1, c, config.rows, config.cols) ||
      isSafe(revealed, r + 1, c, config.rows, config.cols) ||
      isSafe(revealed, r, c - 1, config.rows, config.cols) ||
      isSafe(revealed, r, c + 1, config.rows, config.cols)
    );
  }

  function tryStart(r: number, c: number) {
    if (revealed[r]?.[c]) return;
    if (enemies.some((e) => e.r === r && e.c === c)) return;
    if (!neighborsSafe(r, c)) return;
    setDragging(true);
    setTrail([{ r, c }]);
    playSound("click");
  }

  function tryExtend(r: number, c: number) {
    if (!draggingRef.current) return;
    if (revealed[r]?.[c]) return;
    const last = trail[trail.length - 1];
    if (!last) return;
    if (last.r === r && last.c === c) return;
    const adjacent = Math.abs(last.r - r) + Math.abs(last.c - c) === 1;
    if (!adjacent) return;
    if (trail.some((t) => t.r === r && t.c === c)) return;

    if (enemies.some((e) => e.r === r && e.c === c)) {
      loseLife();
      return;
    }

    const newTrail = [...trail, { r, c }];
    setTrail(newTrail);

    if (newTrail.length >= 3 && neighborsSafe(r, c)) {
      setRevealed((prev) => floodReveal(newTrail, prev, config.rows, config.cols));
      setTrail([]);
      setDragging(false);
      playSound("correct");
    }
  }

  function endDrag() {
    setDragging(false);
    setTrail([]);
  }

  function cellFromPoint(clientX: number, clientY: number) {
    const el = document.elementFromPoint(clientX, clientY) as HTMLElement | null;
    if (!el) return null;
    const r = el.getAttribute("data-r");
    const c = el.getAttribute("data-c");
    if (r === null || c === null) return null;
    return { r: Number(r), c: Number(c) };
  }

  const cellPx = config.cols >= 10 ? 28 : config.cols >= 8 ? 34 : config.cols >= 6 ? 42 : 50;

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-purple-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward slug="arana" show={showWin} message="¡La araña descubrió el dibujo!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-indigo-600 sm:text-3xl">
          🕷️ La araña pintora
        </h1>
        <p className="mb-2 text-center text-slate-500">
          Cuidado con la araña mala 🕸️ — si toca tu trazo, pierdes una vida
        </p>
        <p className="mb-3 text-lg">
          {"❤️".repeat(lives)}
          {"🖤".repeat(Math.max(0, START_LIVES - lives))}
        </p>
        <div className="mb-6">
          <LevelSelector
            gameId="arana"
            levels={ARANA_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div
          className={`relative overflow-hidden rounded-3xl bg-white shadow-xl ${hit ? "ring-4 ring-rose-500" : ""}`}
          style={{ width: cellPx * config.cols, height: cellPx * config.rows }}
        >
          <div className="absolute inset-0 flex items-center justify-center text-[5rem]">
            {config.emoji}
          </div>
          <div
            className="absolute inset-0 grid touch-none"
            style={{ gridTemplateColumns: `repeat(${config.cols}, ${cellPx}px)` }}
            onPointerDown={(e) => {
              const cell = cellFromPoint(e.clientX, e.clientY);
              if (cell) tryStart(cell.r, cell.c);
            }}
            onPointerMove={(e) => {
              if (!dragging) return;
              const cell = cellFromPoint(e.clientX, e.clientY);
              if (cell) tryExtend(cell.r, cell.c);
            }}
            onPointerUp={endDrag}
            onPointerLeave={endDrag}
          >
            {revealed.map((row, r) =>
              row.map((isRevealed, c) => {
                const inTrail = trail.some((t) => t.r === r && t.c === c);
                const hasEnemy = enemies.some((e) => e.r === r && e.c === c);
                return (
                  <div
                    key={`${r}-${c}`}
                    data-r={r}
                    data-c={c}
                    style={{ width: cellPx, height: cellPx }}
                    className={
                      isRevealed
                        ? "pointer-events-none flex items-center justify-center bg-transparent"
                        : inTrail
                          ? "flex items-center justify-center border border-white/40 bg-amber-300/90"
                          : "flex items-center justify-center border border-white/40 bg-indigo-300"
                    }
                  >
                    {hasEnemy && <span className="text-sm">🕷️</span>}
                  </div>
                );
              })
            )}
          </div>
        </div>
        <p className="mt-3 text-sm text-slate-400">
          Descubierto: {Math.round((revealedCount / totalCells) * 100)}%
        </p>
      </main>
    </div>
  );
}
