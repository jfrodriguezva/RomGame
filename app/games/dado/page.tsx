"use client";

import { useEffect, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import DiceRoller from "@/components/DiceRoller";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { CHALLENGES, DADO_CATEGORIES, Challenge } from "@/data/levels/dado";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function DadoPage() {
  const [category, setCategory] = useState<Challenge["category"] | "todas">("todas");
  const [current, setCurrent] = useState<Challenge | null>(null);
  const [celebrate, setCelebrate] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("dado");
  }, [registerPlay]);

  function pickChallenge() {
    const pool =
      category === "todas"
        ? CHALLENGES
        : CHALLENGES.filter((c) => c.category === category);
    const next = pool[Math.floor(Math.random() * pool.length)];
    setCurrent(next);
    playSound("click");
  }

  function completeChallenge() {
    playSound("star");
    addStars("dado", 1);
    setCelebrate(true);
    setTimeout(() => setCelebrate(false), 1200);
    setCurrent(null);
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-amber-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={celebrate} />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-amber-500 sm:text-3xl">
          🎲 Dado de retos
        </h1>

        <div className="mb-6 flex flex-wrap justify-center gap-2">
          <button
            onClick={() => setCategory("todas")}
            className={`rounded-full px-4 py-2 text-sm font-bold shadow ${
              category === "todas" ? "bg-amber-500 text-white" : "bg-white text-amber-600"
            }`}
          >
            🎯 Todas
          </button>
          {DADO_CATEGORIES.map((c) => (
            <button
              key={c.id}
              onClick={() => setCategory(c.id)}
              className={`rounded-full px-4 py-2 text-sm font-bold shadow ${
                category === c.id ? "bg-amber-500 text-white" : "bg-white text-amber-600"
              }`}
            >
              {c.emoji} {c.label}
            </button>
          ))}
        </div>

        <DiceRoller onRoll={pickChallenge} />

        <AnimatePresence>
          {current && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="mt-8 flex w-full flex-col items-center gap-4 rounded-3xl bg-white p-6 text-center shadow-xl"
            >
              <span className="text-6xl">{current.emoji}</span>
              <p className="text-xl font-bold text-slate-700">{current.text}</p>
              <button
                onClick={completeChallenge}
                className="rounded-full bg-green-500 px-6 py-3 text-lg font-bold text-white shadow active:scale-95"
              >
                ¡Listo! ✅
              </button>
            </motion.div>
          )}
        </AnimatePresence>
      </main>
    </div>
  );
}
