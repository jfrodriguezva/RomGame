"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { PUZZLE_LEVELS, PUZZLE_IMAGES } from "@/data/levels/rompecabezas";
import { shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

function pieceColor(index: number, total: number) {
  const hue = Math.round((index / total) * 300);
  return `hsl(${hue}, 80%, 65%)`;
}

export default function RompecabezasPage() {
  const [level, setLevel] = useState(1);
  const config = PUZZLE_LEVELS.find((l) => l.level === level)!;
  const total = config.gridSize * config.gridSize;
  const [board, setBoard] = useState<number[]>([]);
  const [selected, setSelected] = useState<number | null>(null);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    const order = shuffle(Array.from({ length: total }, (_, i) => i));
    // asegura que no arranque ya resuelto
    if (order.every((v, i) => v === i) && total > 1) {
      [order[0], order[1]] = [order[1], order[0]];
    }
    // Baraja el tablero al azar para el nivel: no es una derivación pura
    // que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setBoard(order);
    setSelected(null);
    setShowWin(false);
    registerPlay("rompecabezas");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const isWin = board.length > 0 && board.every((v, i) => v === i);

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("rompecabezas", 1);
      unlockNextLevel("rompecabezas", Math.min(level + 1, PUZZLE_LEVELS.length));
      // Celebra el acierto y lo oculta con un temporizador: efecto
      // legítimo respondiendo a isWin, no una derivación pura.
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function handleTap(slot: number) {
    if (isWin) return;
    if (selected === null) {
      setSelected(slot);
      playSound("click");
      return;
    }
    if (selected === slot) {
      setSelected(null);
      return;
    }
    setBoard((prev) => {
      const next = [...prev];
      [next[selected], next[slot]] = [next[slot], next[selected]];
      return next;
    });
    setSelected(null);
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-sky-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} slug="rompecabezas" />
      <StarReward slug="rompecabezas" show={showWin} message="¡Armaste el rompecabezas!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-blue-500 sm:text-3xl">
          🧩 Rompecabezas
        </h1>
        <div className="mb-4 text-6xl">{PUZZLE_IMAGES[(level - 1) % PUZZLE_IMAGES.length]}</div>
        <div className="mb-6">
          <LevelSelector
            gameId="rompecabezas"
            levels={PUZZLE_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
        <div
          className="grid gap-2"
          style={{ gridTemplateColumns: `repeat(${config.gridSize}, minmax(0, 1fr))` }}
        >
          {board.map((pieceIndex, slot) => (
            <motion.button
              key={slot}
              onClick={() => handleTap(slot)}
              whileTap={{ scale: 0.9 }}
              className="flex aspect-square w-16 items-center justify-center rounded-xl text-lg font-extrabold text-white shadow sm:w-20"
              style={{
                backgroundColor: pieceColor(pieceIndex, total),
                outline: selected === slot ? "4px solid #1e293b" : "none",
              }}
            >
              {pieceIndex + 1}
            </motion.button>
          ))}
        </div>
      </main>
    </div>
  );
}
