"use client";

import { useEffect, useRef, useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { TRAZOS_SHAPES, TRAZOS_LEVELS } from "@/data/levels/trazos";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function TrazosPage() {
  const [level, setLevel] = useState(1);
  const config = TRAZOS_LEVELS.find((l) => l.level === level)!;
  const [shapeIndex, setShapeIndex] = useState(0);
  const shape = TRAZOS_SHAPES[shapeIndex];
  const [progress, setProgress] = useState(0);
  const [tracing, setTracing] = useState(false);
  const [showWin, setShowWin] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("trazos");
    setProgress(0);
    setShowWin(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level, shapeIndex]);

  const isWin = progress >= shape.points.length - 1;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("trazos", 1);
      setShowWin(true);
      setTimeout(() => {
        setShowWin(false);
        setShapeIndex((i) => (i + 1) % TRAZOS_SHAPES.length);
      }, 1600);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function handlePointer(clientX: number, clientY: number) {
    const rect = containerRef.current?.getBoundingClientRect();
    if (!rect) return;
    const x = ((clientX - rect.left) / rect.width) * 100;
    const y = ((clientY - rect.top) / rect.height) * 100;

    const target = shape.points[progress + 1];
    if (!target) return;
    const dist = Math.hypot(target.x - x, target.y - y);
    if (dist < config.tolerance) {
      setProgress((p) => Math.min(p + 1, shape.points.length - 1));
      playSound("click");
    }
  }

  const drawn = shape.points
    .slice(0, progress + 1)
    .map((p) => `${p.x},${p.y}`)
    .join(" ");
  const guide = shape.points.map((p) => `${p.x},${p.y}`).join(" ");

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-rose-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward slug="trazos" show={showWin} message="¡Excelente trazo!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-fuchsia-500 sm:text-3xl">
          ✏️ Traza las formas
        </h1>
        <p className="mb-4 text-center text-slate-500">
          {shape.emoji} {shape.label}
        </p>
        <div className="mb-6">
          <LevelSelector
            gameId="trazos"
            levels={TRAZOS_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

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
            <polyline
              points={guide}
              fill="none"
              stroke="#e5e7eb"
              strokeWidth={2}
              strokeDasharray="2,3"
            />
            <polyline points={drawn} fill="none" stroke="#ec4899" strokeWidth={3} strokeLinecap="round" />
            {shape.points[progress] && (
              <circle
                cx={shape.points[progress].x}
                cy={shape.points[progress].y}
                r={2.5}
                fill="#ec4899"
              />
            )}
            {progress < shape.points.length - 1 && (
              <circle
                cx={shape.points[progress + 1].x}
                cy={shape.points[progress + 1].y}
                r={3.5}
                fill="#fbbf24"
              />
            )}
          </svg>
        </div>
        <p className="mt-4 text-sm text-slate-400">
          Sigue el punto amarillo con tu dedo para dibujar la forma
        </p>
      </main>
    </div>
  );
}
