"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { BLOQUES_LEVELS, BLOQUE_COLORS } from "@/data/levels/bloques";
import { shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface Block {
  size: number; // 1..count, 1 = más chico
  color: string;
}

export default function BloquesPage() {
  const [level, setLevel] = useState(1);
  const config = BLOQUES_LEVELS.find((l) => l.level === level)!;
  const [blocks, setBlocks] = useState<Block[]>([]);
  const [next, setNext] = useState(1);
  const [wrongSize, setWrongSize] = useState<number | null>(null);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    const sizes = Array.from({ length: config.count }, (_, i) => i + 1);
    const shuffledSizes = shuffle(sizes);
    // Baraja los bloques al azar para el nivel actual: no es una
    // derivación pura que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setBlocks(
      shuffledSizes.map((size, i) => ({
        size,
        color: BLOQUE_COLORS[i % BLOQUE_COLORS.length],
      }))
    );
    setNext(1);
    setShowWin(false);
    registerPlay("bloques");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const isWin = blocks.length > 0 && next > config.count;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("bloques", 1);
      unlockNextLevel("bloques", Math.min(level + 1, BLOQUES_LEVELS.length));
      // Celebra el acierto y lo oculta con un temporizador: efecto
      // legítimo respondiendo a isWin, no una derivación pura.
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function handleTap(block: Block) {
    if (block.size < next) return;
    if (block.size !== next) {
      playSound("wrong");
      setWrongSize(block.size);
      setTimeout(() => setWrongSize(null), 350);
      return;
    }
    playSound("correct");
    setNext((n) => n + 1);
  }

  const minPx = 28;
  const maxPx = 92;

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-orange-50 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward slug="bloques" show={showWin} message="¡Los ordenaste todos!" />
      <main className="mx-auto flex w-full max-w-2xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-orange-600 sm:text-3xl">
          🧱 Acomodar bloques
        </h1>
        <p className="mb-4 text-center text-slate-500">
          Toca los bloques del más chico al más grande
        </p>
        <div className="mb-6">
          <LevelSelector
            gameId="bloques"
            levels={BLOQUES_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div className="flex w-full flex-wrap items-end justify-center gap-2">
          {blocks.map((block, i) => {
            const px = minPx + ((maxPx - minPx) * (block.size - 1)) / Math.max(1, config.count - 1);
            const done = block.size < next;
            return (
              <motion.button
                key={i}
                onClick={() => handleTap(block)}
                whileTap={{ scale: 0.92 }}
                animate={wrongSize === block.size ? { x: [0, -6, 6, -6, 0] } : {}}
                disabled={done}
                className="rounded-t-lg shadow-md"
                style={{
                  width: px,
                  height: px,
                  backgroundColor: block.color,
                  opacity: done ? 0.35 : 1,
                }}
              />
            );
          })}
        </div>
      </main>
    </div>
  );
}
