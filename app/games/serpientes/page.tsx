"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { SERPIENTES_LEVELS, buildBoard, cellPosition, BoardLink } from "@/data/levels/serpientes";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

const DICE_FACES = ["⚀", "⚁", "⚂", "⚃", "⚄", "⚅"];

export default function SerpientesPage() {
  const [level, setLevel] = useState(1);
  const config = SERPIENTES_LEVELS.find((l) => l.level === level)!;
  const [links, setLinks] = useState<Map<number, BoardLink>>(new Map());
  const [playerPos, setPlayerPos] = useState(1);
  const [cpuPos, setCpuPos] = useState(1);
  const [turn, setTurn] = useState<"player" | "cpu">("player");
  const [dice, setDice] = useState(0);
  const [rolling, setRolling] = useState(false);
  const [showWin, setShowWin] = useState(false);
  const [message, setMessage] = useState("¡Tira el dado!");
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("serpientes");
    resetGame();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function resetGame() {
    setLinks(buildBoard(config.length, config.linksCount));
    setPlayerPos(1);
    setCpuPos(1);
    setTurn("player");
    setMessage("¡Tira el dado!");
  }

  function applyLink(pos: number): number {
    const link = links.get(pos);
    return link ? link.to : pos;
  }

  function roll(who: "player" | "cpu") {
    if (rolling) return;
    setRolling(true);
    let count = 0;
    const interval = setInterval(() => {
      setDice(Math.floor(Math.random() * 6));
      count++;
      if (count > 8) {
        clearInterval(interval);
        const value = Math.floor(Math.random() * 6) + 1;
        setDice(value - 1);
        setRolling(false);
        playSound("click");
        movePlayer(who, value);
      }
    }, 70);
  }

  function movePlayer(who: "player" | "cpu", steps: number) {
    const setPos = who === "player" ? setPlayerPos : setCpuPos;
    setPos((prev) => {
      const target = Math.min(config.length, prev + steps);
      const afterLink = applyLink(target);
      const link = links.get(target);
      if (link) {
        setTimeout(() => {
          playSound(link.type === "ladder" ? "win" : "wrong");
        }, 300);
      }

      if (afterLink >= config.length) {
        setTimeout(() => {
          if (who === "player") {
            playSound("win");
            setMessage("¡Llegaste a la meta! 🏆");
            addStars("serpientes", 1);
            setShowWin(true);
            setTimeout(() => {
              setShowWin(false);
              resetGame();
            }, 1800);
          } else {
            setMessage("La computadora llegó primero, ¡otra vez!");
            setTimeout(resetGame, 1800);
          }
        }, 400);
      } else {
        setTimeout(() => {
          if (who === "player") {
            setTurn("cpu");
            setMessage("Turno de la computadora");
          } else {
            setTurn("player");
            setMessage("¡Tu turno!");
          }
        }, 400);
      }
      return afterLink;
    });
  }

  useEffect(() => {
    if (turn === "cpu" && !rolling) {
      const t = setTimeout(() => roll("cpu"), 900);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turn]);

  const cellPx = config.length > 45 ? 40 : 48;
  const rows = Math.ceil(config.length / config.cols);

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-lime-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Llegaste a la meta!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-lime-700 sm:text-3xl">
          🐍 Serpientes y escaleras
        </h1>
        <p className="mb-3 text-center text-slate-500">{message}</p>
        <div className="mb-4">
          <LevelSelector
            gameId="serpientes"
            levels={SERPIENTES_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div
          className="relative mb-4 grid gap-1 rounded-2xl bg-white p-2 shadow-xl"
          style={{
            gridTemplateColumns: `repeat(${config.cols}, ${cellPx}px)`,
            gridTemplateRows: `repeat(${rows}, ${cellPx}px)`,
          }}
        >
          {Array.from({ length: rows * config.cols }).map((_, idx) => {
            const n = idx + 1;
            if (n > config.length) return <div key={idx} />;
            const { row, col } = cellPosition(n, config.cols);
            const link = links.get(n);
            return (
              <div
                key={idx}
                className={`relative flex items-center justify-center rounded-md text-[10px] font-bold ${
                  n === config.length
                    ? "bg-amber-300"
                    : link?.type === "ladder"
                      ? "bg-emerald-200"
                      : link?.type === "snake"
                        ? "bg-rose-200"
                        : "bg-slate-100"
                }`}
                style={{
                  gridRow: rows - row,
                  gridColumn: col + 1,
                  width: cellPx,
                  height: cellPx,
                }}
              >
                {n}
                {link?.type === "ladder" && <span className="absolute text-lg">🪜</span>}
                {link?.type === "snake" && <span className="absolute text-lg">🐍</span>}
                {playerPos === n && <span className="absolute -top-2 left-0 text-xl">🐰</span>}
                {cpuPos === n && <span className="absolute -top-2 right-0 text-xl">🤖</span>}
              </div>
            );
          })}
        </div>

        <motion.button
          onClick={() => roll("player")}
          disabled={turn !== "player" || rolling}
          whileTap={{ scale: 0.9 }}
          animate={rolling ? { rotate: [0, 15, -15, 0] } : {}}
          className="flex h-20 w-20 items-center justify-center rounded-3xl bg-white text-6xl shadow disabled:opacity-40"
        >
          {DICE_FACES[dice]}
        </motion.button>
      </main>
    </div>
  );
}
