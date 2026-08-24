"use client";

import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import { BURBUJAS_LEVELS, BUBBLE_COLORS } from "@/data/levels/burbujas";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface Bubble {
  id: number;
  left: number;
  size: number;
  color: string;
  duration: number;
}

interface Burst {
  id: number;
  x: number;
  y: number;
  size: number;
}

let nextId = 0;

const PARTICLES_PER_BURST = 8;

export default function BurbujasPage() {
  const [level, setLevel] = useState(1);
  const config = BURBUJAS_LEVELS.find((l) => l.level === level)!;
  const [bubbles, setBubbles] = useState<Bubble[]>([]);
  const [bursts, setBursts] = useState<Burst[]>([]);
  const [popped, setPopped] = useState(0);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("burbujas");
    setPopped(0);
    setBubbles([]);
    setBursts([]);

    const interval = setInterval(() => {
      setBubbles((prev) => {
        if (prev.length >= config.maxBubbles) return prev;
        const bubble: Bubble = {
          id: nextId++,
          left: 5 + Math.random() * 85,
          size: 50 + Math.random() * 40,
          color: BUBBLE_COLORS[Math.floor(Math.random() * BUBBLE_COLORS.length)],
          duration: 4 + Math.random() * 3,
        };
        return [...prev, bubble];
      });
    }, config.spawnMs);

    return () => clearInterval(interval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  useEffect(() => {
    if (popped > 0 && popped % 15 === 0) {
      addStars("burbujas", 1);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [popped]);

  function popBubble(bubble: Bubble, e: React.MouseEvent<HTMLButtonElement>) {
    const rect = e.currentTarget.getBoundingClientRect();
    const burstId = nextId++;
    setBursts((prev) => [
      ...prev,
      { id: burstId, x: rect.left + rect.width / 2, y: rect.top + rect.height / 2, size: bubble.size },
    ]);
    setTimeout(() => {
      setBursts((prev) => prev.filter((b) => b.id !== burstId));
    }, 550);

    setBubbles((prev) => prev.filter((b) => b.id !== bubble.id));
    setPopped((p) => p + 1);
    playSound("correct");
  }

  return (
    <div className="relative min-h-full flex-1 overflow-hidden bg-gradient-to-b from-cyan-100 via-white to-white pb-10">
      <BackHomeButton />
      <main className="mx-auto w-full max-w-2xl px-4 pt-20 sm:px-6">
        <h1 className="mb-2 text-center text-2xl font-extrabold text-sky-500 sm:text-3xl">
          🫧 Burbujas
        </h1>
        <p className="mb-4 text-center text-slate-500">Reventadas: {popped}</p>
        <div className="mb-6">
          <LevelSelector
            levels={BURBUJAS_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
      </main>
      <div className="pointer-events-none absolute inset-x-0 top-40 bottom-0">
        <AnimatePresence>
          {bubbles.map((b) => (
            <motion.button
              key={b.id}
              initial={{ y: "100vh", opacity: 1, scale: 1 }}
              animate={{ y: "-20vh", scale: 1 }}
              exit={{ opacity: 0, scale: 1.8, transition: { duration: 0.22 } }}
              transition={{ duration: b.duration, ease: "linear" }}
              onClick={(e) => popBubble(b, e)}
              className={`pointer-events-auto absolute rounded-full bg-gradient-to-br shadow-lg ${b.color}`}
              style={{ left: `${b.left}%`, width: b.size, height: b.size }}
            />
          ))}
        </AnimatePresence>
      </div>

      {/* Efecto de tronido: partículas que salen disparadas desde la burbuja tocada */}
      <div className="pointer-events-none fixed inset-0 z-40">
        <AnimatePresence>
          {bursts.map((burst) => (
            <div key={burst.id} style={{ position: "fixed", left: burst.x, top: burst.y }}>
              {Array.from({ length: PARTICLES_PER_BURST }).map((_, i) => {
                const angle = (i / PARTICLES_PER_BURST) * Math.PI * 2;
                const distance = burst.size * 0.7;
                return (
                  <motion.span
                    key={i}
                    initial={{ x: 0, y: 0, opacity: 1, scale: 1 }}
                    animate={{
                      x: Math.cos(angle) * distance,
                      y: Math.sin(angle) * distance,
                      opacity: 0,
                      scale: 0.3,
                    }}
                    transition={{ duration: 0.5, ease: "easeOut" }}
                    className="absolute h-2.5 w-2.5 rounded-full bg-white shadow"
                    style={{ marginLeft: -5, marginTop: -5 }}
                  />
                );
              })}
              <motion.span
                initial={{ scale: 0.4, opacity: 1 }}
                animate={{ scale: 1.6, opacity: 0 }}
                transition={{ duration: 0.35, ease: "easeOut" }}
                className="absolute text-3xl"
                style={{ marginLeft: -16, marginTop: -20 }}
              >
                ✨
              </motion.span>
            </div>
          ))}
        </AnimatePresence>
      </div>
    </div>
  );
}
