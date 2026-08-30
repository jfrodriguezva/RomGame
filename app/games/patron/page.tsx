"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { PATRON_LEVELS, PATRON_BUTTONS } from "@/data/levels/patron";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

function randomSequence(length: number, buttons: number): number[] {
  return Array.from({ length }, () => Math.floor(Math.random() * buttons));
}

export default function PatronPage() {
  const [level, setLevel] = useState(1);
  const config = PATRON_LEVELS.find((l) => l.level === level)!;
  const [sequence, setSequence] = useState<number[]>([]);
  const [playerIndex, setPlayerIndex] = useState(0);
  const [activeButton, setActiveButton] = useState<number | null>(null);
  const [showing, setShowing] = useState(true);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    startLevel();
    registerPlay("patron");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function startLevel() {
    const seq = randomSequence(config.sequenceLength, config.buttons);
    setSequence(seq);
    setPlayerIndex(0);
    setShowWin(false);
    playSequence(seq);
  }

  function playSequence(seq: number[]) {
    setShowing(true);
    seq.forEach((btn, i) => {
      setTimeout(() => {
        setActiveButton(btn);
        playSound("click");
        setTimeout(() => setActiveButton(null), 400);
      }, i * 700);
    });
    setTimeout(() => setShowing(false), seq.length * 700);
  }

  function handlePress(btn: number) {
    if (showing) return;
    setActiveButton(btn);
    setTimeout(() => setActiveButton(null), 300);

    if (btn === sequence[playerIndex]) {
      playSound("correct");
      if (playerIndex + 1 === sequence.length) {
        addStars("patron", 1);
        unlockNextLevel("patron", Math.min(level + 1, PATRON_LEVELS.length));
        setShowWin(true);
        playSound("win");
        setTimeout(() => setShowWin(false), 1600);
        setTimeout(() => startLevel(), 1700);
      } else {
        setPlayerIndex((p) => p + 1);
      }
    } else {
      playSound("wrong");
      setTimeout(() => startLevel(), 600);
    }
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-lime-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Muy bien! Sigues el patrón" />
      <main className="mx-auto flex w-full max-w-md flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-green-500 sm:text-3xl">
          🔔 Sigue el patrón
        </h1>
        <div className="mb-6">
          <LevelSelector
            gameId="patron"
            levels={PATRON_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
        <p className="mb-6 text-center text-slate-500">
          {showing ? "Observa la secuencia..." : "¡Tu turno! Repite lo que viste"}
        </p>
        <div className="grid grid-cols-3 gap-4">
          {PATRON_BUTTONS.slice(0, config.buttons).map((b, i) => (
            <motion.button
              key={i}
              onClick={() => handlePress(i)}
              animate={{ scale: activeButton === i ? 1.25 : 1 }}
              className={`flex h-20 w-20 items-center justify-center rounded-2xl text-4xl shadow-lg ${b.color} ${
                activeButton === i ? "brightness-125" : ""
              }`}
              disabled={showing}
            >
              {b.emoji}
            </motion.button>
          ))}
        </div>
      </main>
    </div>
  );
}
