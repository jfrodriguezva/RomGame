"use client";

import { useEffect, useRef, useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { VOCALES, VOCALES_LEVELS, VocalDef } from "@/data/levels/vocales";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function VocalesPage() {
  const [level, setLevel] = useState(1);
  const config = VOCALES_LEVELS.find((l) => l.level === level)!;
  const [phase, setPhase] = useState<"quiz" | "trace">("quiz");
  const [target, setTarget] = useState<VocalDef | null>(null);
  const [options, setOptions] = useState<VocalDef[]>([]);
  const [progress, setProgress] = useState(0);
  const [tracing, setTracing] = useState(false);
  const [showWin, setShowWin] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("vocales");
    nextRound();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function nextRound() {
    const choices = pickRandom(VOCALES, Math.min(config.options, VOCALES.length));
    const answer = choices[Math.floor(Math.random() * choices.length)];
    setOptions(shuffle(choices));
    setTarget(answer);
    setPhase("quiz");
    setProgress(0);
  }

  function handleGuess(v: VocalDef) {
    if (!target) return;
    if (v.letter === target.letter) {
      playSound("correct");
      setPhase("trace");
    } else {
      playSound("wrong");
    }
  }

  function handlePointer(clientX: number, clientY: number) {
    if (!target) return;
    const rect = containerRef.current?.getBoundingClientRect();
    if (!rect) return;
    const x = ((clientX - rect.left) / rect.width) * 100;
    const y = ((clientY - rect.top) / rect.height) * 100;
    const next = target.points[progress + 1];
    if (!next) return;
    const dist = Math.hypot(next.x - x, next.y - y);
    if (dist < config.tolerance) {
      setProgress((p) => Math.min(p + 1, target.points.length - 1));
      playSound("click");
    }
  }

  const isTraceDone = target ? progress >= target.points.length - 1 : false;

  useEffect(() => {
    if (phase === "trace" && isTraceDone) {
      playSound("win");
      addStars("vocales", 1);
      setShowWin(true);
      setTimeout(() => {
        setShowWin(false);
        nextRound();
      }, 1600);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isTraceDone, phase]);

  const drawn = target
    ? target.points
        .slice(0, progress + 1)
        .map((p) => `${p.x},${p.y}`)
        .join(" ")
    : "";
  const guide = target ? target.points.map((p) => `${p.x},${p.y}`).join(" ") : "";

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-red-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Muy bien trazado!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-red-500 sm:text-3xl">
          🔤 Las vocales
        </h1>
        <div className="mb-6">
          <LevelSelector
            levels={VOCALES_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        {phase === "quiz" && target && (
          <>
            <div className="mb-8 flex flex-col items-center gap-2 rounded-3xl bg-white px-10 py-6 shadow-xl">
              <span className="text-4xl">{target.emoji}</span>
              <span className="text-sm text-slate-400">de &quot;{target.word}&quot;</span>
              <span className="text-sm text-slate-500">¿Cuál vocal es?</span>
            </div>
            <div className="grid grid-cols-3 gap-3">
              {options.map((o) => (
                <button
                  key={o.letter}
                  onClick={() => handleGuess(o)}
                  className="flex h-16 w-16 items-center justify-center rounded-2xl bg-red-500 text-3xl font-extrabold text-white shadow active:scale-90"
                >
                  {o.letter}
                </button>
              ))}
            </div>
          </>
        )}

        {phase === "trace" && target && (
          <>
            <p className="mb-3 text-center text-slate-500">
              Ahora traza la letra {target.letter} siguiendo el punto amarillo
            </p>
            <div
              ref={containerRef}
              className="relative aspect-square w-full max-w-sm touch-none select-none rounded-3xl bg-white shadow-xl"
              onPointerDown={(e) => {
                setTracing(true);
                handlePointer(e.clientX, e.clientY);
              }}
              onPointerMove={(e) => {
                if (tracing) handlePointer(e.clientX, e.clientY);
              }}
              onPointerUp={() => setTracing(false)}
              onPointerLeave={() => setTracing(false)}
            >
              <svg viewBox="0 0 100 100" className="absolute inset-0 h-full w-full">
                <polyline points={guide} fill="none" stroke="#fecaca" strokeWidth={2} strokeDasharray="2,3" />
                <polyline points={drawn} fill="none" stroke="#ef4444" strokeWidth={4} strokeLinecap="round" />
                {target.points[progress + 1] && (
                  <circle
                    cx={target.points[progress + 1].x}
                    cy={target.points[progress + 1].y}
                    r={3.5}
                    fill="#fbbf24"
                  />
                )}
              </svg>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
