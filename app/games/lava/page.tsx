"use client";

import { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  LAVA_LEVELS,
  TILE_WIDTH,
  TICK_MS,
  JUMP_MS,
  PLAYER_X,
  START_LIVES,
  buildTrack,
} from "@/data/levels/lava";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

type PlayerState = "ground" | "air" | "falling";

export default function LavaPage() {
  const [level, setLevel] = useState(1);
  const config = LAVA_LEVELS.find((l) => l.level === level)!;
  const [track, setTrack] = useState<boolean[]>([]);
  const [scrollX, setScrollX] = useState(0);
  const [playerState, setPlayerState] = useState<PlayerState>("ground");
  const [lives, setLives] = useState(START_LIVES);
  const [crossed, setCrossed] = useState(0);
  const [milestone, setMilestone] = useState(false);
  const stateRef = useRef<PlayerState>("ground");
  const scrollRef = useRef(0);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("lava");
    resetRun();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function resetRun() {
    setTrack(buildTrack(config.gapEvery));
    setScrollX(0);
    scrollRef.current = 0;
    setPlayerState("ground");
    stateRef.current = "ground";
    setLives(START_LIVES);
    setCrossed(0);
  }

  useEffect(() => {
    const tick = setInterval(() => {
      if (stateRef.current === "falling") return;
      scrollRef.current += config.speed;
      setScrollX(scrollRef.current);

      const tileIndex = Math.floor((scrollRef.current + PLAYER_X) / TILE_WIDTH);
      const isSafe = track[tileIndex] ?? true;

      if (!isSafe && stateRef.current === "ground") {
        fall();
      }
    }, TICK_MS);
    return () => clearInterval(tick);
  }, [config.speed, track]);

  function fall() {
    stateRef.current = "falling";
    setPlayerState("falling");
    playSound("wrong");
    setLives((prev) => {
      const next = prev - 1;
      setTimeout(() => {
        if (next <= 0) {
          resetRun();
        } else {
          // reponer una baldosa segura justo bajo el jugador y continuar
          setTrack((t) => {
            const idx = Math.floor((scrollRef.current + PLAYER_X) / TILE_WIDTH);
            const copy = [...t];
            if (copy[idx] !== undefined) copy[idx] = true;
            return copy;
          });
          stateRef.current = "ground";
          setPlayerState("ground");
        }
      }, 700);
      return Math.max(0, next);
    });
  }

  function jump() {
    if (stateRef.current !== "ground") return;
    stateRef.current = "air";
    setPlayerState("air");
    playSound("click");
    setTimeout(() => {
      if (stateRef.current !== "air") return;
      const tileIndex = Math.floor((scrollRef.current + PLAYER_X) / TILE_WIDTH);
      const isSafe = track[tileIndex] ?? true;
      if (isSafe) {
        stateRef.current = "ground";
        setPlayerState("ground");
        setCrossed((c) => {
          const next = c + 1;
          if (next % 20 === 0) {
            addStars("lava", 1);
            playSound("win");
            setMilestone(true);
            setTimeout(() => setMilestone(false), 1200);
          }
          return next;
        });
      } else {
        fall();
      }
    }, JUMP_MS);
  }

  return (
    <div
      className="relative min-h-full flex-1 touch-none select-none overflow-hidden bg-gradient-to-b from-orange-200 via-amber-100 to-white pb-10"
      onPointerDown={jump}
    >
      <BackHomeButton />
      <ConfettiOverlay show={milestone} />
      <main className="pointer-events-none mx-auto w-full max-w-xl px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-orange-700 sm:text-3xl">
          🌋 El piso es lava
        </h1>
        <p className="mb-3 text-center text-slate-600">
          Toca la pantalla para saltar los huecos de lava
        </p>
        <div className="mb-3 flex items-center justify-center gap-4">
          <span className="text-lg">
            {"❤️".repeat(lives)}
            {"🖤".repeat(Math.max(0, START_LIVES - lives))}
          </span>
          <span className="rounded-full bg-white px-3 py-1 text-sm font-bold text-orange-600 shadow">
            Baldosas cruzadas: {crossed}
          </span>
        </div>
        <div className="pointer-events-auto mb-4 flex justify-center">
          <LevelSelector
            gameId="lava"
            levels={LAVA_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
      </main>

      <div className="relative mx-auto h-40 max-w-2xl overflow-hidden">
        {track.map((safe, i) => {
          const x = i * TILE_WIDTH - scrollX;
          if (x < -TILE_WIDTH || x > 500) return null;
          return (
            <div
              key={i}
              className={`absolute bottom-0 h-10 rounded-t-lg ${
                safe ? "bg-gradient-to-b from-emerald-300 to-emerald-500" : "bg-transparent"
              }`}
              style={{ left: x, width: TILE_WIDTH - 6 }}
            >
              {!safe && (
                <div className="absolute -bottom-2 h-10 w-full rounded-t-lg bg-gradient-to-t from-red-600 to-orange-400" />
              )}
            </div>
          );
        })}

        <motion.div
          animate={
            playerState === "air"
              ? { y: [0, -60, 0] }
              : playerState === "falling"
                ? { y: [0, 40], opacity: [1, 0] }
                : { y: 0, opacity: 1 }
          }
          transition={{ duration: playerState === "air" ? JUMP_MS / 1000 : 0.5 }}
          className="absolute bottom-8 text-4xl"
          style={{ left: PLAYER_X }}
        >
          🐰
        </motion.div>
      </div>
    </div>
  );
}
