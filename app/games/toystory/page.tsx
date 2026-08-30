"use client";

import { useEffect, useRef, useState } from "react";
import type Phaser from "phaser";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import HealthBar from "@/components/HealthBar";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { TOYSTORY_CHARACTERS, TOYSTORY_LEVELS } from "@/data/levels/toystory";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";
import type ToyStorySceneType from "@/phaser/ToyStoryScene";

export default function ToyStoryPage() {
  const [character, setCharacter] = useState(TOYSTORY_CHARACTERS[0]);
  const [started, setStarted] = useState(false);
  const [level, setLevel] = useState(1);
  const config = TOYSTORY_LEVELS.find((l) => l.level === level)!;
  const containerRef = useRef<HTMLDivElement>(null);
  const gameRef = useRef<Phaser.Game | null>(null);
  const sceneRef = useRef<ToyStorySceneType | null>(null);
  const [hp, setHp] = useState(100);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    if (!started) return;
    registerPlay("toystory");
    let destroyed = false;

    (async () => {
      const [{ default: PhaserLib }, { default: ToyStoryScene }] = await Promise.all([
        import("phaser"),
        import("@/phaser/ToyStoryScene"),
      ]);
      if (destroyed || !containerRef.current) return;

      const scene = new ToyStoryScene(character, config);
      sceneRef.current = scene;

      const game = new PhaserLib.Game({
        type: PhaserLib.AUTO,
        width: 360,
        height: 400,
        parent: containerRef.current,
        backgroundColor: "#bae6fd",
        physics: { default: "arcade", arcade: { gravity: { x: 0, y: 700 }, debug: false } },
        scene,
      });
      gameRef.current = game;

      game.events.on("hp", (value: number) => setHp(value));
      game.events.on("hit", () => playSound("wrong"));
      game.events.on("win", () => {
        playSound("win");
        addStars("toystory", 1);
        setShowWin(true);
        setTimeout(() => setShowWin(false), 1800);
      });
    })();

    return () => {
      destroyed = true;
      gameRef.current?.destroy(true);
      gameRef.current = null;
      sceneRef.current = null;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [started, level, character.id]);

  function press(key: "left" | "right" | "jump", value: boolean) {
    sceneRef.current?.setInput(key, value);
  }

  if (!started) {
    return (
      <div className="min-h-full flex-1 bg-gradient-to-b from-red-100 via-white to-white pb-10">
        <BackHomeButton />
        <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
          <h1 className="mb-6 text-center text-2xl font-extrabold text-orange-600 sm:text-3xl">
            🤠 Aventura de juguetes
          </h1>
          <p className="mb-4 text-center text-slate-500">Elige tu personaje</p>
          <div className="mb-8 flex gap-4">
            {TOYSTORY_CHARACTERS.map((c) => (
              <button
                key={c.id}
                onClick={() => setCharacter(c)}
                className={`flex h-20 w-20 items-center justify-center rounded-3xl text-4xl shadow ${
                  character.id === c.id ? "ring-4 ring-orange-400" : ""
                }`}
                style={{ backgroundColor: c.color + "33" }}
              >
                {c.emoji}
              </button>
            ))}
          </div>
          <div className="mb-8">
            <LevelSelector
            gameId="toystory"
              levels={TOYSTORY_LEVELS.map((l) => l.level)}
              active={level}
              onSelect={setLevel}
            />
          </div>
          <button
            onClick={() => setStarted(true)}
            className="rounded-full bg-orange-500 px-8 py-3 text-lg font-bold text-white shadow active:scale-95"
          >
            ¡Jugar! 🚀
          </button>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-sky-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Llegaste a la meta!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-2 text-center text-xl font-extrabold text-orange-600">
          {character.emoji} Nivel {level}
        </h1>
        <div className="mb-4 flex items-center gap-2">
          <span className="text-sm font-bold text-slate-500">Vida:</span>
          <HealthBar hp={hp} />
        </div>

        <div
          ref={containerRef}
          className="mb-4 overflow-hidden rounded-3xl shadow-xl"
          style={{ width: 360, maxWidth: "100%" }}
        />

        <div className="flex w-full max-w-xs items-center justify-between">
          <div className="flex gap-2">
            <button
              onPointerDown={() => press("left", true)}
              onPointerUp={() => press("left", false)}
              onPointerLeave={() => press("left", false)}
              className="flex h-16 w-16 items-center justify-center rounded-2xl bg-orange-500 text-2xl text-white shadow active:scale-90"
            >
              ⬅️
            </button>
            <button
              onPointerDown={() => press("right", true)}
              onPointerUp={() => press("right", false)}
              onPointerLeave={() => press("right", false)}
              className="flex h-16 w-16 items-center justify-center rounded-2xl bg-orange-500 text-2xl text-white shadow active:scale-90"
            >
              ➡️
            </button>
          </div>
          <button
            onPointerDown={() => press("jump", true)}
            onPointerUp={() => press("jump", false)}
            onPointerLeave={() => press("jump", false)}
            className="flex h-16 w-16 items-center justify-center rounded-2xl bg-sky-500 text-2xl text-white shadow active:scale-90"
          >
            ⬆️
          </button>
        </div>
        <p className="mt-3 text-xs text-slate-400">
          Si te quedas quieta un momento, tu vida se recupera 💚
        </p>
      </main>
    </div>
  );
}
