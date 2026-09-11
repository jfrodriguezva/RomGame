"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { NUMEROS_LEVELS } from "@/data/levels/numeros";
import { shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface Dot {
  n: number;
  x: number;
  y: number;
}

function buildDots(count: number): Dot[] {
  const cols = Math.ceil(Math.sqrt(count));
  const rows = Math.ceil(count / cols);
  const cells = shuffle(Array.from({ length: cols * rows }, (_, i) => i)).slice(0, count);
  return Array.from({ length: count }, (_, i) => {
    const cell = cells[i];
    const col = cell % cols;
    const row = Math.floor(cell / cols);
    return {
      n: i + 1,
      x: (col + 0.5) * (100 / cols) + (Math.random() * 6 - 3),
      y: (row + 0.5) * (100 / rows) + (Math.random() * 6 - 3),
    };
  });
}

export default function NumerosPage() {
  const [level, setLevel] = useState(1);
  const config = NUMEROS_LEVELS.find((l) => l.level === level)!;
  const [dots, setDots] = useState<Dot[]>([]);
  const [next, setNext] = useState(1);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    // Genera los puntos del nivel: no es una derivación pura que se
    // pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setDots(buildDots(config.count));
    setNext(1);
    setShowWin(false);
    registerPlay("numeros");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const isWin = dots.length > 0 && next > config.count;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("numeros", 1);
      unlockNextLevel("numeros", Math.min(level + 1, NUMEROS_LEVELS.length));
      // Celebra el acierto y lo oculta con un temporizador: efecto
      // legítimo respondiendo a isWin, no una derivación pura.
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function handleTap(dot: Dot) {
    if (dot.n !== next) {
      playSound("wrong");
      return;
    }
    playSound("correct");
    setNext((n) => n + 1);
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-cyan-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward slug="numeros" show={showWin} message="¡Contaste hasta el final!" />
      <main className="mx-auto w-full max-w-xl px-4 pt-20 sm:px-6">
        <h1 className="mb-2 text-center text-2xl font-extrabold text-teal-600 sm:text-3xl">
          🔢 Números en orden
        </h1>
        <p className="mb-4 text-center text-slate-500">Toca el número {next <= config.count ? next : config.count}</p>
        <div className="mb-4">
          <LevelSelector
            gameId="numeros"
            levels={NUMEROS_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div className="relative aspect-square w-full overflow-hidden rounded-3xl bg-white shadow-xl">
          {dots.map((dot) => {
            const done = dot.n < next;
            return (
              <motion.button
                key={dot.n}
                onClick={() => handleTap(dot)}
                whileTap={{ scale: 0.85 }}
                className={`absolute flex h-12 w-12 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full text-lg font-extrabold shadow ${
                  done ? "bg-emerald-300 text-white" : "bg-cyan-500 text-white"
                }`}
                style={{ left: `${dot.x}%`, top: `${dot.y}%` }}
                disabled={done}
              >
                {dot.n}
              </motion.button>
            );
          })}
        </div>
      </main>
    </div>
  );
}
