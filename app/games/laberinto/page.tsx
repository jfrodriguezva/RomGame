"use client";

import { useEffect, useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { LABERINTO_LEVELS, generateMaze, MazeCell } from "@/data/levels/laberinto";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function LaberintoPage() {
  const [level, setLevel] = useState(1);
  const config = LABERINTO_LEVELS.find((l) => l.level === level)!;
  const [maze, setMaze] = useState<MazeCell[][]>([]);
  const [pos, setPos] = useState({ r: 0, c: 0 });
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    setMaze(generateMaze(config.size));
    setPos({ r: 0, c: 0 });
    setShowWin(false);
    registerPlay("laberinto");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const goal = { r: config.size - 1, c: config.size - 1 };
  const isWin = pos.r === goal.r && pos.c === goal.c;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("laberinto", 1);
      unlockNextLevel("laberinto", Math.min(level + 1, LABERINTO_LEVELS.length));
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function move(dr: number, dc: number) {
    if (Math.abs(dr) + Math.abs(dc) !== 1) return;
    const cell = maze[pos.r]?.[pos.c];
    if (!cell) return;
    if (dr === -1 && cell.top) return;
    if (dr === 1 && cell.bottom) return;
    if (dc === -1 && cell.left) return;
    if (dc === 1 && cell.right) return;
    const nr = pos.r + dr;
    const nc = pos.c + dc;
    if (nr < 0 || nr >= config.size || nc < 0 || nc >= config.size) return;
    setPos({ r: nr, c: nc });
    playSound("click");
  }

  const cellPx = config.size >= 8 ? 32 : config.size >= 6 ? 40 : 52;

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-emerald-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward slug="laberinto" show={showWin} message="¡Llegaste a la meta!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-teal-600 sm:text-3xl">
          🌀 Laberinto
        </h1>
        <div className="mb-6">
          <LevelSelector
            gameId="laberinto"
            levels={LABERINTO_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div
          className="mb-6 grid bg-white shadow-xl"
          style={{ gridTemplateColumns: `repeat(${config.size}, ${cellPx}px)` }}
        >
          {maze.map((row, r) =>
            row.map((cell, c) => (
              <button
                key={`${r}-${c}`}
                onClick={() => move(r - pos.r, c - pos.c)}
                className="relative flex items-center justify-center"
                style={{
                  width: cellPx,
                  height: cellPx,
                  borderTop: cell.top ? "2px solid #0f766e" : "none",
                  borderLeft: cell.left ? "2px solid #0f766e" : "none",
                  borderRight: cell.right ? "2px solid #0f766e" : "none",
                  borderBottom: cell.bottom ? "2px solid #0f766e" : "none",
                }}
              >
                {r === goal.r && c === goal.c && <span className="text-lg">🏁</span>}
                {pos.r === r && pos.c === c && <span className="text-xl">🐰</span>}
              </button>
            ))
          )}
        </div>
        <p className="mb-3 text-xs text-slate-400">
          Toca una casilla junto al conejo para moverlo, o usa las flechas
        </p>

        <div className="grid grid-cols-3 gap-2">
          <div />
          <button
            onClick={() => move(-1, 0)}
            className="flex h-14 w-14 items-center justify-center rounded-2xl bg-teal-500 text-2xl text-white shadow active:scale-90"
          >
            ⬆️
          </button>
          <div />
          <button
            onClick={() => move(0, -1)}
            className="flex h-14 w-14 items-center justify-center rounded-2xl bg-teal-500 text-2xl text-white shadow active:scale-90"
          >
            ⬅️
          </button>
          <button
            onClick={() => move(1, 0)}
            className="flex h-14 w-14 items-center justify-center rounded-2xl bg-teal-500 text-2xl text-white shadow active:scale-90"
          >
            ⬇️
          </button>
          <button
            onClick={() => move(0, 1)}
            className="flex h-14 w-14 items-center justify-center rounded-2xl bg-teal-500 text-2xl text-white shadow active:scale-90"
          >
            ➡️
          </button>
        </div>
      </main>
    </div>
  );
}
