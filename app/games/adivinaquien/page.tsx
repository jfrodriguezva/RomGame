"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { PERSONAJES, PREGUNTAS, ADIVINA_LEVELS, Personaje } from "@/data/levels/adivinaquien";
import { pickRandom } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function AdivinaQuienPage() {
  const [level, setLevel] = useState(1);
  const config = ADIVINA_LEVELS.find((l) => l.level === level)!;
  const [pool, setPool] = useState<Personaje[]>([]);
  const [secret, setSecret] = useState<Personaje | null>(null);
  const [eliminated, setEliminated] = useState<Set<string>>(new Set());
  const [askedIds, setAskedIds] = useState<Set<string>>(new Set());
  const [lastAnswer, setLastAnswer] = useState<string | null>(null);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("adivinaquien");
    startRound();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function startRound() {
    const chosen = pickRandom(PERSONAJES, config.poolSize);
    setPool(chosen);
    setSecret(chosen[Math.floor(Math.random() * chosen.length)]);
    setEliminated(new Set());
    setAskedIds(new Set());
    setLastAnswer(null);
  }

  function askQuestion(attr: keyof Personaje, label: string) {
    if (!secret || askedIds.has(attr as string)) return;
    const answer = Boolean(secret[attr]);
    setLastAnswer(`${label} ${answer ? "Sí" : "No"}`);
    playSound("click");
    setAskedIds((prev) => new Set(prev).add(attr as string));
    setEliminated((prev) => {
      const next = new Set(prev);
      pool.forEach((p) => {
        if (Boolean(p[attr]) !== answer) next.add(p.id);
      });
      return next;
    });
  }

  function guess(p: Personaje) {
    if (!secret) return;
    if (p.id === secret.id) {
      playSound("win");
      addStars("adivinaquien", 1);
      setShowWin(true);
      setTimeout(() => {
        setShowWin(false);
        startRound();
      }, 1600);
    } else {
      playSound("wrong");
      setEliminated((prev) => new Set(prev).add(p.id));
    }
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-teal-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Adivinaste quién es!" />
      <main className="mx-auto flex w-full max-w-2xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-teal-600 sm:text-3xl">
          🕵️ Adivina quién es
        </h1>
        <p className="mb-4 text-center text-slate-500">
          {lastAnswer ?? "Haz una pregunta y luego toca a quién crees que es"}
        </p>

        <div className="mb-4">
          <LevelSelector
            levels={ADIVINA_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div className="mb-6 flex flex-wrap justify-center gap-2">
          {PREGUNTAS.map((q) => (
            <button
              key={q.id}
              onClick={() => askQuestion(q.id, q.label)}
              disabled={askedIds.has(q.id as string)}
              className="rounded-full bg-teal-500 px-4 py-2 text-sm font-bold text-white shadow disabled:opacity-30"
            >
              {q.label}
            </button>
          ))}
        </div>

        <div className="grid grid-cols-4 gap-3 sm:grid-cols-5">
          {pool.map((p) => {
            const isOut = eliminated.has(p.id);
            return (
              <motion.button
                key={p.id}
                onClick={() => guess(p)}
                whileTap={!isOut ? { scale: 0.85 } : undefined}
                disabled={isOut}
                className={`flex h-16 w-16 items-center justify-center rounded-2xl text-3xl shadow ${
                  isOut ? "bg-slate-100 opacity-30 grayscale" : "bg-white"
                }`}
              >
                {p.emoji}
              </motion.button>
            );
          })}
        </div>
      </main>
    </div>
  );
}
