"use client";

import { useEffect, useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { COLOREAR_LEVELS, COLOREAR_PALETTE } from "@/data/levels/colorear";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

export default function ColorearPage() {
  const [level, setLevel] = useState(1);
  const config = COLOREAR_LEVELS.find((l) => l.level === level)!;
  const [colors, setColors] = useState<Record<string, string>>({});
  const [selectedColor, setSelectedColor] = useState(COLOREAR_PALETTE[0]);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const unlockNextLevel = useProgressStore((s) => s.unlockNextLevel);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    setColors({});
    setShowWin(false);
    registerPlay("colorear");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const allColored = config.zones.every((z) => colors[z.id]);

  useEffect(() => {
    if (allColored) {
      playSound("win");
      addStars("colorear", 1);
      unlockNextLevel("colorear", Math.min(level + 1, COLOREAR_LEVELS.length));
      setShowWin(true);
      const t = setTimeout(() => setShowWin(false), 1800);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [allColored]);

  function paintZone(id: string) {
    setColors((prev) => ({ ...prev, [id]: selectedColor }));
    playSound("click");
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-fuchsia-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Qué bonito dibujo!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-pink-500 sm:text-3xl">
          🎨 Colorear
        </h1>
        <div className="mb-6">
          <LevelSelector
            levels={COLOREAR_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <svg viewBox="0 0 300 280" className="mb-6 w-full max-w-sm rounded-3xl bg-white shadow-xl">
          {config.zones.map((z) => (
            <circle
              key={z.id}
              cx={z.cx}
              cy={z.cy}
              r={z.r}
              fill={colors[z.id] ?? "#f1f5f9"}
              stroke="#94a3b8"
              strokeWidth={2}
              onClick={() => paintZone(z.id)}
              className="cursor-pointer"
            />
          ))}
        </svg>

        <div className="flex flex-wrap justify-center gap-2">
          {COLOREAR_PALETTE.map((c) => (
            <button
              key={c}
              onClick={() => setSelectedColor(c)}
              className="h-10 w-10 rounded-full shadow active:scale-90"
              style={{
                backgroundColor: c,
                outline: selectedColor === c ? "3px solid #1e293b" : "none",
                outlineOffset: 2,
              }}
              aria-label={`color ${c}`}
            />
          ))}
        </div>
      </main>
    </div>
  );
}
