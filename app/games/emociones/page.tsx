"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { EMOCIONES, EMOCIONES_LEVELS, EmocionDef } from "@/data/levels/emociones";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function EmocionesPage() {
  const [level, setLevel] = useState(1);
  const config = EMOCIONES_LEVELS.find((l) => l.level === level)!;
  const [target, setTarget] = useState<EmocionDef | null>(null);
  const [options, setOptions] = useState<EmocionDef[]>([]);
  const [streak, setStreak] = useState(0);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    nextRound();
    registerPlay("emociones");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function nextRound() {
    const choices = pickRandom(EMOCIONES, Math.min(config.options, EMOCIONES.length));
    setOptions(shuffle(choices));
    setTarget(choices[Math.floor(Math.random() * choices.length)]);
  }

  function handleGuess(emocion: EmocionDef) {
    if (!target) return;
    if (emocion.id === target.id) {
      playSound("correct");
      const nextStreak = streak + 1;
      setStreak(nextStreak);
      if (nextStreak % 5 === 0) {
        addStars("emociones", 1);
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
    <div className="min-h-full flex-1 bg-gradient-to-b from-amber-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Reconoces las emociones!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-rose-500 sm:text-3xl">
          😊 ¿Cómo te sientes?
        </h1>
        <div className="mb-6">
          <LevelSelector
            levels={EMOCIONES_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        {target && (
          <div className="mb-8 flex flex-col items-center gap-2 rounded-3xl bg-white px-10 py-6 shadow-xl">
            <span className="text-6xl">{target.emoji}</span>
            <span className="text-sm text-slate-500">¿Cómo se siente?</span>
          </div>
        )}

        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
          {options.map((o) => (
            <motion.button
              key={o.id}
              onClick={() => handleGuess(o)}
              whileTap={{ scale: 0.85 }}
              className="rounded-2xl bg-white px-4 py-3 text-lg font-bold text-slate-600 shadow"
            >
              {o.label}
            </motion.button>
          ))}
        </div>
      </main>
    </div>
  );
}
