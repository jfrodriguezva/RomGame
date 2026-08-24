"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { ANIMALS, ANIMALES_LEVELS, AnimalDef } from "@/data/levels/animales";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function AnimalesPage() {
  const [level, setLevel] = useState(1);
  const config = ANIMALES_LEVELS.find((l) => l.level === level)!;
  const [round, setRound] = useState<AnimalDef[]>([]);
  const [target, setTarget] = useState<AnimalDef | null>(null);
  const [feedback, setFeedback] = useState<"correct" | "wrong" | null>(null);
  const [showWin, setShowWin] = useState(false);
  const [streak, setStreak] = useState(0);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    nextRound();
    registerPlay("animales");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function nextRound() {
    const choices = pickRandom(ANIMALS, config.count);
    const answer = choices[Math.floor(Math.random() * choices.length)];
    setRound(shuffle(choices));
    setTarget(answer);
    setFeedback(null);
  }

  function playTargetSound() {
    if (!target) return;
    playSound("click");
  }

  function handleGuess(animal: AnimalDef) {
    if (!target) return;
    if (animal.name === target.name) {
      setFeedback("correct");
      playSound("correct");
      const nextStreak = streak + 1;
      setStreak(nextStreak);
      if (nextStreak % 5 === 0) {
        addStars("animales", 1);
        setShowWin(true);
        playSound("win");
        setTimeout(() => setShowWin(false), 1400);
      }
      setTimeout(nextRound, 900);
    } else {
      setFeedback("wrong");
      playSound("wrong");
    }
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-yellow-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Eres un experto en animales!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-amber-600 sm:text-3xl">
          🐮 Sonidos de animales
        </h1>
        <div className="mb-6">
          <LevelSelector
            levels={ANIMALES_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <motion.button
          onClick={playTargetSound}
          whileTap={{ scale: 0.9 }}
          className="mb-8 flex flex-col items-center gap-2 rounded-3xl bg-white px-8 py-6 shadow-xl"
        >
          <span className="text-2xl">🔊</span>
          <span className="text-lg font-bold text-slate-600">
            {target ? target.sound : "..."}
          </span>
          <span className="text-sm text-slate-400">Toca para escuchar</span>
        </motion.button>

        <div className={`grid gap-4 ${config.count > 6 ? "grid-cols-5" : "grid-cols-3"}`}>
          {round.map((animal) => (
            <motion.button
              key={animal.name}
              onClick={() => handleGuess(animal)}
              whileTap={{ scale: 0.85 }}
              className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white text-3xl shadow sm:h-20 sm:w-20"
            >
              {animal.emoji}
            </motion.button>
          ))}
        </div>

        {feedback === "wrong" && (
          <p className="mt-6 text-lg font-bold text-rose-500">Intenta de nuevo 🙂</p>
        )}
      </main>
    </div>
  );
}
