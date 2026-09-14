"use client";

import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import StarReward from "@/components/StarReward";
import {
  VESTIR_ITEMS,
  VESTIR_SLOTS,
  VESTIR_SETS,
  VESTIR_BACKGROUNDS,
  VestirItem,
} from "@/data/levels/vestir";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

const SLOT_LABELS: Record<VestirItem["slot"], string> = {
  corona: "Corona",
  vestido: "Vestido",
  zapatos: "Zapatos",
  accesorio: "Accesorio",
};

export default function VestirPage() {
  const [outfit, setOutfit] = useState<Record<string, VestirItem>>({});
  const [background, setBackground] = useState(VESTIR_BACKGROUNDS[0]);
  const [showWin, setShowWin] = useState(false);
  const [celebratedOutfits, setCelebratedOutfits] = useState<Set<string>>(new Set());
  const stars = useProgressStore((s) => s.getProgress("vestir").stars);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("vestir");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const isComplete = VESTIR_SLOTS.every((slot) => outfit[slot]);
  const outfitKey = VESTIR_SLOTS.map((slot) => outfit[slot]?.id ?? "-").join("|");

  useEffect(() => {
    if (isComplete && !celebratedOutfits.has(outfitKey)) {
      playSound("win");
      addStars("vestir", 1);
      // Celebra el vestuario completo la primera vez que se arma: efecto
      // legítimo respondiendo a isComplete, no una derivación pura.
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setShowWin(true);
      setCelebratedOutfits((prev) => new Set(prev).add(outfitKey));
      const t = setTimeout(() => setShowWin(false), 1600);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isComplete, outfitKey]);

  function choose(item: VestirItem, unlocked: boolean) {
    if (!unlocked) {
      playSound("wrong");
      return;
    }
    setOutfit((prev) => ({ ...prev, [item.slot]: item }));
    playSound("click");
  }

  function surprise() {
    const random: Record<string, VestirItem> = {};
    for (const slot of VESTIR_SLOTS) {
      const options = VESTIR_ITEMS.filter(
        (i) => i.slot === slot && stars >= (VESTIR_SETS.find((s) => s.id === i.setId)?.starsRequired ?? 0)
      );
      random[slot] = options[Math.floor(Math.random() * options.length)];
    }
    setOutfit(random);
    playSound("click");
  }

  return (
    <div
      className={`min-h-full flex-1 bg-gradient-to-b ${background.from} ${background.to} pb-10`}
    >
      <BackHomeButton />
      <ConfettiOverlay show={showWin} slug="vestir" />
      <StarReward slug="vestir" show={showWin} message="¡Qué princesa tan elegante!" />
      <main className="mx-auto flex w-full max-w-2xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-rose-500 sm:text-3xl">
          👗 Vestir a la princesa
        </h1>
        <p className="mb-4 flex items-center gap-1 text-sm font-bold text-amber-500">
          ⭐ {stars} estrellas ganadas
        </p>

        {/* Selector de escenario */}
        <div className="mb-4 flex flex-wrap justify-center gap-2">
          {VESTIR_BACKGROUNDS.map((bg) => (
            <button
              key={bg.id}
              onClick={() => setBackground(bg)}
              className={`flex items-center gap-1 rounded-full px-3 py-1.5 text-sm font-bold shadow ${
                background.id === bg.id ? "bg-rose-500 text-white" : "bg-white text-rose-500"
              }`}
            >
              {bg.emoji} {bg.label}
            </button>
          ))}
        </div>

        {/* Escenario con la princesa */}
        <div className="relative mb-3 flex h-64 w-52 flex-col items-center justify-center rounded-3xl bg-white/70 shadow-xl backdrop-blur">
          <span className="absolute top-3 text-4xl opacity-60">{background.emoji}</span>
          <motion.div
            key={outfitKey}
            initial={{ scale: 0.9 }}
            animate={{ scale: 1 }}
            className="relative flex flex-col items-center"
          >
            <span className="text-4xl">{outfit.corona?.emoji ?? "😊"}</span>
            <span className="-mt-2 text-7xl">{outfit.vestido?.emoji ?? "🧍‍♀️"}</span>
            <span className="-mt-3 text-2xl">{outfit.zapatos?.emoji ?? ""}</span>
            {outfit.accesorio && (
              <motion.span
                initial={{ scale: 0, rotate: -20 }}
                animate={{ scale: 1, rotate: 0 }}
                className="absolute -right-6 top-4 text-3xl"
              >
                {outfit.accesorio.emoji}
              </motion.span>
            )}
          </motion.div>
        </div>

        <button
          onClick={surprise}
          className="mb-8 rounded-full bg-amber-400 px-5 py-2 text-sm font-bold text-white shadow active:scale-95"
        >
          🎲 Sorpréndeme
        </button>

        {VESTIR_SLOTS.map((slot) => (
          <div key={slot} className="mb-6 w-full">
            <p className="mb-2 text-center font-bold text-slate-500">{SLOT_LABELS[slot]}</p>
            {VESTIR_SETS.map((set) => {
              const items = VESTIR_ITEMS.filter((i) => i.slot === slot && i.setId === set.id);
              const unlocked = stars >= set.starsRequired;
              return (
                <div key={set.id} className="mb-2 flex flex-col items-center gap-1">
                  {set.starsRequired > 0 && (
                    <span className={`text-xs font-bold ${unlocked ? "text-emerald-500" : "text-slate-400"}`}>
                      {unlocked ? `✓ ${set.label}` : `🔒 ${set.label} (${set.starsRequired}⭐)`}
                    </span>
                  )}
                  <div className="flex justify-center gap-3">
                    {items.map((item) => (
                      <motion.button
                        key={item.id}
                        onClick={() => choose(item, unlocked)}
                        whileTap={unlocked ? { scale: 0.85 } : undefined}
                        className={`relative flex h-16 w-16 items-center justify-center rounded-2xl text-3xl shadow ${
                          outfit[slot]?.id === item.id ? "bg-rose-300" : "bg-white"
                        } ${unlocked ? "" : "opacity-40 grayscale"}`}
                      >
                        {item.emoji}
                        {!unlocked && (
                          <span className="absolute -right-1 -top-1 text-sm">🔒</span>
                        )}
                      </motion.button>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        ))}
      </main>
    </div>
  );
}
