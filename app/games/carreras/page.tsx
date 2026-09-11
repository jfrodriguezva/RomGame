"use client";

import { useEffect, useRef, useState } from "react";
import type Phaser from "phaser";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { CARRERAS_LEVELS } from "@/data/levels/carreras";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";
import type RaceSceneType from "@/phaser/RaceScene";

export default function CarrerasPage() {
  const [level, setLevel] = useState(1);
  const config = CARRERAS_LEVELS.find((l) => l.level === level)!;
  const containerRef = useRef<HTMLDivElement>(null);
  const gameRef = useRef<Phaser.Game | null>(null);
  const sceneRef = useRef<RaceSceneType | null>(null);
  const [score, setScore] = useState(0);
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("carreras");
    let destroyed = false;

    (async () => {
      const [{ default: PhaserLib }, { default: RaceScene }] = await Promise.all([
        import("phaser"),
        import("@/phaser/RaceScene"),
      ]);
      if (destroyed || !containerRef.current) return;

      const scene = new RaceScene(config);
      sceneRef.current = scene;

      const game = new PhaserLib.Game({
        type: PhaserLib.AUTO,
        width: 360,
        height: 560,
        parent: containerRef.current,
        backgroundColor: "#334155",
        physics: { default: "arcade", arcade: { gravity: { x: 0, y: 0 }, debug: false } },
        scene,
        scale: { mode: PhaserLib.Scale.FIT, autoCenter: PhaserLib.Scale.CENTER_BOTH },
      });
      gameRef.current = game;

      game.events.on("score", (s: number) => {
        setScore(s);
        playSound("correct");
      });
      game.events.on("bump", () => playSound("wrong"));
    })();

    return () => {
      destroyed = true;
      gameRef.current?.destroy(true);
      gameRef.current = null;
      sceneRef.current = null;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    // Sincroniza la escena de Phaser (un sistema externo) con el nivel
    // actual y reinicia el marcador: no es una derivación pura.
    sceneRef.current?.setLevelConfig(config);
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setScore(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  useEffect(() => {
    if (score > 0 && score % 10 === 0) {
      addStars("carreras", 1);
      // Celebra cada 10 puntos y lo oculta con un temporizador: efecto
      // legítimo, no una derivación pura.
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setShowWin(true);
      playSound("win");
      setTimeout(() => setShowWin(false), 1400);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [score]);

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-blue-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward slug="carreras" show={showWin} message="¡Buena carrera!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-blue-600 sm:text-3xl">
          🏎️ Carreras
        </h1>
        <p className="mb-4 text-center text-slate-500">Estrellas atrapadas: {score}</p>
        <div className="mb-4">
          <LevelSelector
            gameId="carreras"
            levels={CARRERAS_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div
          ref={containerRef}
          className="mb-4 overflow-hidden rounded-3xl shadow-xl"
          style={{ width: 360, maxWidth: "100%" }}
        />

        <div className="flex gap-6">
          <button
            onPointerDown={() => sceneRef.current?.setDirection(-1)}
            onPointerUp={() => sceneRef.current?.setDirection(0)}
            onPointerLeave={() => sceneRef.current?.setDirection(0)}
            className="flex h-16 w-16 items-center justify-center rounded-2xl bg-blue-500 text-3xl text-white shadow active:scale-90"
          >
            ⬅️
          </button>
          <button
            onPointerDown={() => sceneRef.current?.setDirection(1)}
            onPointerUp={() => sceneRef.current?.setDirection(0)}
            onPointerLeave={() => sceneRef.current?.setDirection(0)}
            className="flex h-16 w-16 items-center justify-center rounded-2xl bg-blue-500 text-3xl text-white shadow active:scale-90"
          >
            ➡️
          </button>
        </div>
      </main>
    </div>
  );
}
