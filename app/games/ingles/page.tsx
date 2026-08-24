"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { INGLES_WORDS, INGLES_LEVELS, InglesWord } from "@/data/levels/ingles";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function InglesPage() {
  const [level, setLevel] = useState(1);
  const config = INGLES_LEVELS.find((l) => l.level === level)!;
  const pool = INGLES_WORDS.filter((w) => config.categories.includes(w.category));
  const [target, setTarget] = useState<InglesWord | null>(null);
  const [options, setOptions] = useState<InglesWord[]>([]);
  const [streak, setStreak] = useState(0);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    nextRound();
    registerPlay("ingles");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function nextRound() {
    const choices = pickRandom(pool, Math.min(config.options, pool.length));
    setOptions(shuffle(choices));
    setTarget(choices[Math.floor(Math.random() * choices.length)]);
  }

  function handleGuess(word: InglesWord) {
    if (!target) return;
    if (word.en === target.en) {
      playSound("correct");
      const nextStreak = streak + 1;
      setStreak(nextStreak);
      if (nextStreak % 5 === 0) {
        addStars("ingles", 1);
        setShowWin(true);
        playSound("win");
        setTimeout(() => setShowWin(false), 1400);
      }
      setTimeout(nextRound, 800);
    } else {
      playSound("wrong");
    }
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-blue-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="Very good! ¡Muy bien!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-indigo-600 sm:text-3xl">
          🇬🇧 Inglés para pequeños
        </h1>
        <div className="mb-6">
          <LevelSelector
            levels={INGLES_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        {target && (
          <div className="mb-8 flex flex-col items-center gap-1 rounded-3xl bg-white px-10 py-6 shadow-xl">
            <span className="text-sm text-slate-400">Toca:</span>
            <span className="text-3xl font-extrabold text-indigo-600">{target.en}</span>
          </div>
        )}

        <div className="grid grid-cols-3 gap-3">
          {options.map((o) => (
            <motion.button
              key={o.en}
              onClick={() => handleGuess(o)}
              whileTap={{ scale: 0.85 }}
              className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white text-3xl shadow"
            >
              {o.emoji}
            </motion.button>
          ))}
        </div>
      </main>
    </div>
  );
}
