"use client";

import { useEffect, useRef, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { RPS_CHOICES, RPS_LEVELS, RpsChoice } from "@/data/levels/rps";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function RpsPage() {
  const [level, setLevel] = useState(1);
  const config = RPS_LEVELS.find((l) => l.level === level)!;
  const [playerChoice, setPlayerChoice] = useState<RpsChoice | null>(null);
  const [cpuChoice, setCpuChoice] = useState<RpsChoice | null>(null);
  const [result, setResult] = useState<"win" | "lose" | "draw" | null>(null);
  const [history, setHistory] = useState<Record<RpsChoice, number>>({ piedra: 0, papel: 0, tijera: 0 });
  const [, setWins] = useState(0);
  const winsRef = useRef(0);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("rps");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // pickCpuChoice() solo se llama desde play(), un manejador de clic —
  // nunca durante el render — así que el azar aquí es seguro aunque el
  // linter no pueda rastrear esa garantía estáticamente.
  function pickCpuChoice(freq: Record<RpsChoice, number>): RpsChoice {
    // eslint-disable-next-line react-hooks/purity
    if (Math.random() < config.counterChance) {
      const mostUsed = (Object.keys(freq) as RpsChoice[]).sort((a, b) => freq[b] - freq[a])[0];
      const counter = RPS_CHOICES.find((c) => c.beats === mostUsed);
      if (counter) return counter.id;
    }
    const all: RpsChoice[] = ["piedra", "papel", "tijera"];
    // eslint-disable-next-line react-hooks/purity
    return all[Math.floor(Math.random() * all.length)];
  }

  function play(choice: RpsChoice) {
    const nextFreq = { ...history, [choice]: history[choice] + 1 };
    setHistory(nextFreq);
    const cpu = pickCpuChoice(history);
    setPlayerChoice(choice);
    setCpuChoice(cpu);
    playSound("click");

    setTimeout(() => {
      const playerDef = RPS_CHOICES.find((c) => c.id === choice)!;
      let outcome: "win" | "lose" | "draw";
      if (choice === cpu) outcome = "draw";
      else if (playerDef.beats === cpu) outcome = "win";
      else outcome = "lose";
      setResult(outcome);

      if (outcome === "win") {
        playSound("win");
        // Antes esto vivía dentro del updater de setWins, un efecto
        // secundario (addStars, setShowWin) en una función que React puede
        // invocar más de una vez — mismo patrón que ya causó bugs reales
        // en serpientes, globo, canasta y mesa-silencio.
        const next = winsRef.current + 1;
        winsRef.current = next;
        setWins(next);
        if (next % 3 === 0) {
          addStars("rps", 1);
          setShowWin(true);
          setTimeout(() => setShowWin(false), 1400);
        }
      } else if (outcome === "lose") {
        playSound("wrong");
      } else {
        playSound("correct");
      }

      setTimeout(() => {
        setPlayerChoice(null);
        setCpuChoice(null);
        setResult(null);
      }, 1400);
    }, 700);
  }

  const resultText = { win: "¡Ganaste! 🎉", lose: "Ganó la computadora", draw: "¡Empate!" };

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-yellow-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} slug="rps" />
      <StarReward slug="rps" show={showWin} message="¡Sigue así!" />
      <main className="mx-auto flex w-full max-w-md flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-amber-600 sm:text-3xl">
          🪨📄✂️ Piedra, papel o tijera
        </h1>
        <div className="mb-6">
          <LevelSelector gameId="rps" levels={RPS_LEVELS.map((l) => l.level)} active={level} onSelect={setLevel} />
        </div>

        <div className="mb-8 flex h-28 w-full items-center justify-center gap-8">
          <AnimatePresence mode="wait">
            {playerChoice ? (
              <motion.span
                key="p"
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                className="text-6xl"
              >
                {RPS_CHOICES.find((c) => c.id === playerChoice)?.emoji}
              </motion.span>
            ) : (
              <span className="text-4xl opacity-30">❓</span>
            )}
          </AnimatePresence>
          <span className="text-2xl font-bold text-slate-400">vs</span>
          <AnimatePresence mode="wait">
            {cpuChoice ? (
              <motion.span
                key="c"
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                className="text-6xl"
              >
                {RPS_CHOICES.find((c) => c.id === cpuChoice)?.emoji}
              </motion.span>
            ) : (
              <span className="text-4xl opacity-30">❓</span>
            )}
          </AnimatePresence>
        </div>

        {result && (
          <p className="mb-4 text-xl font-extrabold text-slate-700">{resultText[result]}</p>
        )}

        <div className="flex gap-4">
          {RPS_CHOICES.map((c) => (
            <motion.button
              key={c.id}
              onClick={() => play(c.id)}
              whileTap={{ scale: 0.85 }}
              disabled={!!playerChoice}
              className="flex h-20 w-20 items-center justify-center rounded-3xl bg-white text-4xl shadow disabled:opacity-50"
            >
              {c.emoji}
            </motion.button>
          ))}
        </div>
      </main>
    </div>
  );
}
