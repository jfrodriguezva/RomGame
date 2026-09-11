"use client";

import { useEffect, useMemo, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { DIFERENCIAS_LEVELS, DIFERENCIAS_POOL } from "@/data/levels/diferencias";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface SceneItem {
  id: number;
  emoji: string;
  altEmoji: string;
  x: number;
  y: number;
  rotate: number;
  isDiff: boolean;
}

function buildScene(totalItems: number, diffCount: number): SceneItem[] {
  const cols = Math.ceil(Math.sqrt(totalItems));
  const rows = Math.ceil(totalItems / cols);
  const cells = shuffle(
    Array.from({ length: cols * rows }, (_, i) => i)
  ).slice(0, totalItems);

  const diffIndices = new Set(pickRandom(cells.map((_, i) => i), diffCount));

  return cells.map((cell, i) => {
    const col = cell % cols;
    const row = Math.floor(cell / cols);
    const x = (col + 0.5) * (100 / cols) + (Math.random() * 6 - 3);
    const y = (row + 0.5) * (100 / rows) + (Math.random() * 6 - 3);
    const emoji = DIFERENCIAS_POOL[Math.floor(Math.random() * DIFERENCIAS_POOL.length)];
    let altEmoji = emoji;
    if (diffIndices.has(i)) {
      do {
        altEmoji = DIFERENCIAS_POOL[Math.floor(Math.random() * DIFERENCIAS_POOL.length)];
      } while (altEmoji === emoji);
    }
    return {
      id: i,
      emoji,
      altEmoji,
      x,
      y,
      rotate: Math.random() * 30 - 15,
      isDiff: diffIndices.has(i),
    };
  });
}

export default function DiferenciasPage() {
  const [level, setLevel] = useState(1);
  const config = DIFERENCIAS_LEVELS.find((l) => l.level === level)!;
  const [scene, setScene] = useState<SceneItem[]>([]);
  const [found, setFound] = useState<Set<number>>(new Set());
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    // Genera una escena al azar para el nivel: no es una derivación pura
    // que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setScene(buildScene(config.totalItems, config.diffCount));
    setFound(new Set());
    setShowWin(false);
    registerPlay("diferencias");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const diffTotal = useMemo(() => scene.filter((s) => s.isDiff).length, [scene]);
  const isWin = diffTotal > 0 && found.size === diffTotal;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("diferencias", 1);
      unlockNextLevel("diferencias", Math.min(level + 1, DIFERENCIAS_LEVELS.length));
      // Celebra el acierto y lo oculta con un temporizador: efecto
      // legítimo respondiendo a isWin, no una derivación pura.
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function handleTap(item: SceneItem) {
    if (!item.isDiff || found.has(item.id)) {
      if (!item.isDiff) playSound("wrong");
      return;
    }
    setFound((prev) => new Set(prev).add(item.id));
    playSound("correct");
  }

  function renderScene(side: "left" | "right") {
    return (
      <div className="relative aspect-square w-full overflow-hidden rounded-3xl bg-white shadow-xl">
        {scene.map((item) => {
          const isFound = found.has(item.id);
          const showAlt = side === "right" && item.isDiff;
          return (
            <motion.button
              key={item.id}
              onClick={() => handleTap(item)}
              whileTap={{ scale: 0.8 }}
              animate={isFound && item.isDiff ? { scale: [1, 1.4, 1] } : {}}
              className="absolute flex -translate-x-1/2 -translate-y-1/2 items-center justify-center text-2xl sm:text-3xl"
              style={{
                left: `${item.x}%`,
                top: `${item.y}%`,
                transform: `translate(-50%, -50%) rotate(${item.rotate}deg)`,
                filter: isFound && item.isDiff ? "drop-shadow(0 0 6px #22c55e)" : "none",
              }}
            >
              {showAlt ? item.altEmoji : item.emoji}
            </motion.button>
          );
        })}
      </div>
    );
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-violet-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward slug="diferencias" show={showWin} message="¡Encontraste todas las diferencias!" />
      <main className="mx-auto w-full max-w-3xl px-4 pt-20 sm:px-6">
        <h1 className="mb-2 text-center text-2xl font-extrabold text-purple-500 sm:text-3xl">
          🔍 Encuentra las diferencias
        </h1>
        <p className="mb-4 text-center text-slate-500">
          Encontradas: {found.size} / {diffTotal}
        </p>
        <div className="mb-6">
          <LevelSelector
            gameId="diferencias"
            levels={DIFERENCIAS_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {renderScene("left")}
          {renderScene("right")}
        </div>
      </main>
    </div>
  );
}
