"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { OBJETOS_LEVELS, OBJETOS_POOL } from "@/data/levels/objetos";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface SceneEmoji {
  id: number;
  emoji: string;
  x: number;
  y: number;
  rotate: number;
}

function buildScene(targets: string[], totalOnScreen: number): SceneEmoji[] {
  const rest = OBJETOS_POOL.filter((e) => !targets.includes(e));
  const filler = Array.from({ length: Math.max(totalOnScreen - targets.length, 0) }, () =>
    rest[Math.floor(Math.random() * rest.length)]
  );
  const emojis = shuffle([...targets, ...filler]);

  const cols = Math.ceil(Math.sqrt(emojis.length));
  const rows = Math.ceil(emojis.length / cols);
  const cellOrder = shuffle(Array.from({ length: cols * rows }, (_, i) => i)).slice(
    0,
    emojis.length
  );

  return emojis.map((emoji, i) => {
    const cell = cellOrder[i];
    const col = cell % cols;
    const row = Math.floor(cell / cols);
    return {
      id: i,
      emoji,
      x: (col + 0.5) * (100 / cols) + (Math.random() * 6 - 3),
      y: (row + 0.5) * (100 / rows) + (Math.random() * 6 - 3),
      rotate: Math.random() * 30 - 15,
    };
  });
}

export default function ObjetosPage() {
  const [level, setLevel] = useState(1);
  const config = OBJETOS_LEVELS.find((l) => l.level === level)!;
  const [targets, setTargets] = useState<string[]>([]);
  const [scene, setScene] = useState<SceneEmoji[]>([]);
  const [found, setFound] = useState<Set<string>>(new Set());
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    const chosen = pickRandom(OBJETOS_POOL, config.targetCount);
    setTargets(chosen);
    setScene(buildScene(chosen, config.totalOnScreen));
    setFound(new Set());
    setShowWin(false);
    registerPlay("objetos");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const isWin = targets.length > 0 && found.size === targets.length;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("objetos", 1);
      unlockNextLevel("objetos", Math.min(level + 1, OBJETOS_LEVELS.length));
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function handleTap(emoji: string) {
    if (!targets.includes(emoji) || found.has(emoji)) {
      if (!targets.includes(emoji)) playSound("wrong");
      return;
    }
    setFound((prev) => new Set(prev).add(emoji));
    playSound("correct");
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-indigo-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward slug="objetos" show={showWin} message="¡Encontraste todo!" />
      <main className="mx-auto w-full max-w-2xl px-4 pt-20 sm:px-6">
        <h1 className="mb-2 text-center text-2xl font-extrabold text-indigo-500 sm:text-3xl">
          🔎 Encuentra los objetos
        </h1>
        <div className="mb-4">
          <LevelSelector
            gameId="objetos"
            levels={OBJETOS_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div className="mb-4 flex flex-wrap justify-center gap-2">
          {targets.map((t) => (
            <span
              key={t}
              className={`flex h-12 w-12 items-center justify-center rounded-2xl text-2xl shadow ${
                found.has(t) ? "bg-emerald-300" : "bg-white"
              }`}
            >
              {t}
            </span>
          ))}
        </div>

        <div className="relative aspect-square w-full overflow-hidden rounded-3xl bg-white shadow-xl">
          {scene.map((item) => (
            <motion.button
              key={item.id}
              onClick={() => handleTap(item.emoji)}
              whileTap={{ scale: 0.8 }}
              className="absolute flex items-center justify-center text-2xl sm:text-3xl"
              style={{
                left: `${item.x}%`,
                top: `${item.y}%`,
                transform: `translate(-50%, -50%) rotate(${item.rotate}deg)`,
                opacity: targets.includes(item.emoji) && found.has(item.emoji) ? 0.35 : 1,
              }}
            >
              {item.emoji}
            </motion.button>
          ))}
        </div>
      </main>
    </div>
  );
}
