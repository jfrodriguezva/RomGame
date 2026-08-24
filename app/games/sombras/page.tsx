"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { SOMBRAS_LEVELS, SOMBRAS_POOL } from "@/data/levels/sombras";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function SombrasPage() {
  const [level, setLevel] = useState(1);
  const config = SOMBRAS_LEVELS.find((l) => l.level === level)!;
  const [left, setLeft] = useState<string[]>([]);
  const [right, setRight] = useState<string[]>([]);
  const [selected, setSelected] = useState<{ side: "left" | "right"; emoji: string; idx: number } | null>(null);
  const [matched, setMatched] = useState<Set<string>>(new Set());
  const [wrongIdx, setWrongIdx] = useState<number | null>(null);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    const chosen = pickRandom(SOMBRAS_POOL, config.pairs);
    setLeft(shuffle(chosen));
    setRight(shuffle(chosen));
    setMatched(new Set());
    setSelected(null);
    setShowWin(false);
    registerPlay("sombras");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const isWin = left.length > 0 && matched.size === left.length;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("sombras", 1);
      unlockNextLevel("sombras", Math.min(level + 1, SOMBRAS_LEVELS.length));
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function handleTap(side: "left" | "right", emoji: string, idx: number) {
    if (matched.has(emoji)) return;

    if (!selected) {
      setSelected({ side, emoji, idx });
      playSound("click");
      return;
    }

    if (selected.side === side) {
      setSelected({ side, emoji, idx });
      return;
    }

    if (selected.emoji === emoji) {
      setMatched((prev) => new Set(prev).add(emoji));
      setSelected(null);
      playSound("correct");
    } else {
      playSound("wrong");
      setWrongIdx(side === "right" ? idx + 100 : idx);
      setTimeout(() => setWrongIdx(null), 400);
      setSelected(null);
    }
  }

  function isSelected(side: "left" | "right", idx: number) {
    return selected?.side === side && selected.idx === idx;
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-slate-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Encontraste todas las sombras!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-slate-600 sm:text-3xl">
          🌗 Empareja sombras
        </h1>
        <div className="mb-6">
          <LevelSelector
            levels={SOMBRAS_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div className="grid w-full grid-cols-2 gap-6">
          <div className="flex flex-col items-center gap-3">
            <p className="text-sm font-bold text-slate-400">Objetos</p>
            {left.map((emoji, idx) => (
              <motion.button
                key={idx}
                onClick={() => handleTap("left", emoji, idx)}
                whileTap={{ scale: 0.85 }}
                animate={wrongIdx === idx ? { x: [0, -6, 6, -6, 0] } : {}}
                className={`flex h-14 w-14 items-center justify-center rounded-2xl text-3xl shadow ${
                  matched.has(emoji)
                    ? "bg-emerald-200 opacity-50"
                    : isSelected("left", idx)
                      ? "bg-amber-200"
                      : "bg-white"
                }`}
                disabled={matched.has(emoji)}
              >
                {emoji}
              </motion.button>
            ))}
          </div>
          <div className="flex flex-col items-center gap-3">
            <p className="text-sm font-bold text-slate-400">Sombras</p>
            {right.map((emoji, idx) => (
              <motion.button
                key={idx}
                onClick={() => handleTap("right", emoji, idx)}
                whileTap={{ scale: 0.85 }}
                animate={wrongIdx === idx + 100 ? { x: [0, -6, 6, -6, 0] } : {}}
                className={`flex h-14 w-14 items-center justify-center rounded-2xl text-3xl shadow ${
                  matched.has(emoji)
                    ? "bg-emerald-200 opacity-50"
                    : isSelected("right", idx)
                      ? "bg-amber-200"
                      : "bg-white"
                }`}
                disabled={matched.has(emoji)}
                style={
                  matched.has(emoji)
                    ? {}
                    : { filter: "brightness(0) opacity(0.75)" }
                }
              >
                {emoji}
              </motion.button>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}
