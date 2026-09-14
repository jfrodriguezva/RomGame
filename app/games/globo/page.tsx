"use client";

import { useEffect, useRef, useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import { GLOBO_LEVELS, TICK_MS } from "@/data/levels/globo";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface Balloon {
  id: number;
  x: number;
  y: number;
}

function balloonsIniciales(count: number): Balloon[] {
  return Array.from({ length: count }, (_, i) => ({
    id: i,
    x: ((i + 1) / (count + 1)) * 100,
    y: 20,
  }));
}

export default function GloboPage() {
  const [level, setLevel] = useState(1);
  const config = GLOBO_LEVELS.find((l) => l.level === level)!;
  const [balloons, setBalloons] = useState<Balloon[]>([]);
  const [taps, setTaps] = useState(0);
  const [bestStreak, setBestStreak] = useState(0);
  const streakRef = useRef(0);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("globo");
    // Reinicia los globos y el marcador al cambiar de nivel: sincroniza con
    // una prop que cambia, no es una derivación pura del render actual.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setBalloons(balloonsIniciales(config.balloonCount));
    setTaps(0);
    streakRef.current = 0;
    setBestStreak(0);

    const interval = setInterval(() => {
      setBalloons((prev) =>
        prev.map((b) => {
          const next = b.y + config.fallSpeed;
          if (next >= 92) {
            playSound("wrong");
            streakRef.current = 0;
            return { ...b, y: 15 };
          }
          return { ...b, y: next };
        })
      );
    }, TICK_MS);

    return () => clearInterval(interval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level, config.balloonCount]);

  useEffect(() => {
    if (taps > 0 && taps % 15 === 0) {
      addStars("globo", 1);
      playSound("win");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [taps]);

  function handleTap(id: number) {
    setBalloons((prev) =>
      prev.map((b) => (b.id === id ? { ...b, y: Math.max(b.y - config.bounceStrength, 5) } : b))
    );
    setTaps((t) => t + 1);
    streakRef.current += 1;
    setBestStreak((b) => Math.max(b, streakRef.current));
    playSound("correct");
  }

  return (
    <div className="relative min-h-full flex-1 overflow-hidden bg-gradient-to-b from-sky-100 via-white to-white pb-10">
      <BackHomeButton />
      <main className="mx-auto w-full max-w-2xl px-4 pt-20 sm:px-6">
        <h1 className="mb-2 text-center text-2xl font-extrabold text-sky-500 sm:text-3xl">
          🎈 El globo volador
        </h1>
        <p className="mb-4 text-center text-slate-500">
          {config.balloonCount === 1
            ? "No dejes caer el globo"
            : `No dejes caer ninguno de los ${config.balloonCount} globos`}{" "}
          · Toques: {taps} · Mejor racha: {bestStreak}
        </p>
        <div className="mb-4">
          <LevelSelector
            gameId="globo"
            levels={GLOBO_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
      </main>
      <div className="relative mx-auto h-[50vh] max-w-2xl">
        {balloons.map((b) => (
          <button
            key={b.id}
            onClick={() => handleTap(b.id)}
            className="absolute -translate-x-1/2 -translate-y-1/2 text-7xl drop-shadow-lg transition-[top] duration-75 ease-linear"
            style={{ left: `${b.x}%`, top: `${b.y}%` }}
            aria-label="globo"
          >
            🎈
          </button>
        ))}
        <div className="absolute inset-x-0 bottom-0 h-3 rounded-full bg-emerald-300" />
      </div>
    </div>
  );
}
