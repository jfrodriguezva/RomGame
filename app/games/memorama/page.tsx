"use client";

import { useEffect, useMemo, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { MEMORAMA_LEVELS } from "@/data/levels/memorama";
import { MEMORAMA_FACES } from "@/data/assets";
import { pickRandom, shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface Card {
  id: number;
  face: string;
}

function buildDeck(pairs: number): Card[] {
  const faces = pickRandom(MEMORAMA_FACES, pairs);
  const deck = shuffle([...faces, ...faces]).map((face, id) => ({ id, face }));
  return deck;
}

export default function MemoramaPage() {
  const [level, setLevel] = useState(1);
  const currentLevel = MEMORAMA_LEVELS.find((l) => l.level === level)!;
  const [deck, setDeck] = useState<Card[]>([]);
  const [flipped, setFlipped] = useState<number[]>([]);
  const [matched, setMatched] = useState<number[]>([]);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    setDeck(buildDeck(currentLevel.pairs));
    setFlipped([]);
    setMatched([]);
    setShowWin(false);
    registerPlay("memorama");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const isWin = matched.length === deck.length && deck.length > 0;

  useEffect(() => {
    if (isWin) {
      playSound("win");
      addStars("memorama", 1);
      unlockNextLevel("memorama", Math.min(level + 1, MEMORAMA_LEVELS.length));
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWin]);

  function handleFlip(id: number) {
    if (flipped.length === 2) return;
    if (flipped.includes(id) || matched.includes(id)) return;

    const next = [...flipped, id];
    setFlipped(next);

    if (next.length === 2) {
      const [a, b] = next;
      const cardA = deck.find((c) => c.id === a)!;
      const cardB = deck.find((c) => c.id === b)!;
      if (cardA.face === cardB.face) {
        playSound("correct");
        setTimeout(() => {
          setMatched((m) => [...m, a, b]);
          setFlipped([]);
        }, 500);
      } else {
        setTimeout(() => setFlipped([]), 800);
      }
    }
  }

  const cols = useMemo(() => {
    if (currentLevel.pairs <= 3) return "grid-cols-3";
    if (currentLevel.pairs <= 4) return "grid-cols-4";
    if (currentLevel.pairs <= 6) return "grid-cols-4";
    return "grid-cols-4 sm:grid-cols-4";
  }, [currentLevel.pairs]);

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-pink-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Encontraste todas las parejas!" />
      <main className="mx-auto w-full max-w-2xl px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-rose-500 sm:text-3xl">
          🧠 Memorama
        </h1>
        <div className="mb-6">
          <LevelSelector
            levels={MEMORAMA_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
        <div className={`grid ${cols} gap-3 sm:gap-4`}>
          {deck.map((card) => {
            const isFlipped = flipped.includes(card.id) || matched.includes(card.id);
            return (
              <button
                key={card.id}
                onClick={() => handleFlip(card.id)}
                className="aspect-square"
                style={{ perspective: 600 }}
                aria-label="carta"
              >
                <motion.div
                  animate={{ rotateY: isFlipped ? 180 : 0 }}
                  transition={{ duration: 0.35 }}
                  className="relative h-full w-full"
                  style={{ transformStyle: "preserve-3d" }}
                >
                  <div
                    className="absolute inset-0 flex items-center justify-center rounded-2xl bg-gradient-to-br from-rose-400 to-pink-500 text-3xl shadow-md"
                    style={{ backfaceVisibility: "hidden" }}
                  >
                    ❓
                  </div>
                  <div
                    className="absolute inset-0 flex items-center justify-center rounded-2xl bg-white text-4xl shadow-md"
                    style={{
                      backfaceVisibility: "hidden",
                      transform: "rotateY(180deg)",
                    }}
                  >
                    {card.face}
                  </div>
                </motion.div>
              </button>
            );
          })}
        </div>
      </main>
    </div>
  );
}
