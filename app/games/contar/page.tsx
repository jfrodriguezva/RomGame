"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { CONTAR_LEVELS, CONTAR_OBJETOS } from "@/data/levels/contar";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function ContarPage() {
  const [level, setLevel] = useState(1);
  const config = CONTAR_LEVELS.find((l) => l.level === level)!;
  const [count, setCount] = useState(1);
  const [emoji, setEmoji] = useState("⭐");
  const [options, setOptions] = useState<number[]>([]);
  const [showWin, setShowWin] = useState(false);
  const [streak, setStreak] = useState(0);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    nextRound();
    registerPlay("contar");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function nextRound() {
    const n = 1 + Math.floor(Math.random() * config.maxCount);
    setCount(n);
    setEmoji(CONTAR_OBJETOS[Math.floor(Math.random() * CONTAR_OBJETOS.length)]);
    const opts = new Set<number>([n]);
    while (opts.size < Math.min(4, config.maxCount)) {
      const alt = 1 + Math.floor(Math.random() * config.maxCount);
      opts.add(alt);
    }
    setOptions([...opts].sort(() => Math.random() - 0.5));
  }

  function handleAnswer(n: number) {
    if (n === count) {
      playSound("correct");
      const nextStreak = streak + 1;
      setStreak(nextStreak);
      if (nextStreak % 5 === 0) {
        addStars("contar", 1);
        setShowWin(true);
        playSound("win");
        setTimeout(() => setShowWin(false), 1400);
      }
      setTimeout(nextRound, 700);
    } else {
      playSound("wrong");
    }
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-teal-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Sabes contar muy bien!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-teal-600 sm:text-3xl">
          🔢 Contar y tocar
        </h1>
        <div className="mb-6">
          <LevelSelector
            gameId="contar"
            levels={CONTAR_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div className="mb-8 flex flex-wrap justify-center gap-3 rounded-3xl bg-white p-6 shadow-xl">
          {Array.from({ length: count }).map((_, i) => (
            <motion.span
              key={i}
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ delay: i * 0.05 }}
              className="text-4xl"
            >
              {emoji}
            </motion.span>
          ))}
        </div>

        <p className="mb-4 text-lg font-bold text-slate-600">¿Cuántos hay?</p>
        <div className="grid grid-cols-4 gap-3">
          {options.map((n) => (
            <motion.button
              key={n}
              onClick={() => handleAnswer(n)}
              whileTap={{ scale: 0.85 }}
              className="flex h-16 w-16 items-center justify-center rounded-2xl bg-teal-500 text-2xl font-extrabold text-white shadow"
            >
              {n}
            </motion.button>
          ))}
        </div>
      </main>
    </div>
  );
}
