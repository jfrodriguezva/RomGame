"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { GATO_LEVELS, LINES } from "@/data/levels/gato";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

type Cell = "X" | "O" | null;

function winner(board: Cell[]): Cell {
  for (const [a, b, c] of LINES) {
    if (board[a] && board[a] === board[b] && board[a] === board[c]) return board[a];
  }
  return null;
}

function bestMove(board: Cell[], me: "O", opponent: "X"): number {
  for (const [a, b, c] of LINES) {
    const line = [board[a], board[b], board[c]];
    if (line.filter((v) => v === me).length === 2 && line.includes(null)) {
      return [a, b, c][line.indexOf(null)];
    }
  }
  for (const [a, b, c] of LINES) {
    const line = [board[a], board[b], board[c]];
    if (line.filter((v) => v === opponent).length === 2 && line.includes(null)) {
      return [a, b, c][line.indexOf(null)];
    }
  }
  if (board[4] === null) return 4;
  const corners = [0, 2, 6, 8].filter((i) => board[i] === null);
  if (corners.length) return corners[Math.floor(Math.random() * corners.length)];
  const empty = board.map((v, i) => (v === null ? i : -1)).filter((i) => i >= 0);
  return empty[Math.floor(Math.random() * empty.length)];
}

export default function GatoPage() {
  const [level, setLevel] = useState(1);
  const config = GATO_LEVELS.find((l) => l.level === level)!;
  const [board, setBoard] = useState<Cell[]>(Array(9).fill(null));
  const [turn, setTurn] = useState<"player" | "cpu">("player");
  const [wins, setWins] = useState(0);
  const [showWin, setShowWin] = useState(false);
  const [message, setMessage] = useState("Tu turno");
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  useEffect(() => {
    registerPlay("gato");
    resetBoard();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function resetBoard() {
    setBoard(Array(9).fill(null));
    setTurn("player");
    setMessage("Tu turno");
  }

  useEffect(() => {
    if (turn !== "cpu") return;
    const t = setTimeout(() => {
      setBoard((prev) => {
        if (winner(prev) || prev.every((v) => v)) return prev;
        const useRandom = Math.random() < config.mistakeChance;
        const empty = prev.map((v, i) => (v === null ? i : -1)).filter((i) => i >= 0);
        const move = useRandom
          ? empty[Math.floor(Math.random() * empty.length)]
          : bestMove(prev, "O", "X");
        const next = [...prev];
        next[move] = "O";
        return next;
      });
      setTurn("player");
    }, 500);
    return () => clearTimeout(t);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turn]);

  useEffect(() => {
    const w = winner(board);
    if (w === "X") {
      playSound("win");
      setMessage("¡Ganaste! 🎉");
      setWins((n) => {
        const next = n + 1;
        if (next % 3 === 0) addStars("gato", 1);
        return next;
      });
      setShowWin(true);
      setTimeout(() => {
        setShowWin(false);
        resetBoard();
      }, 1600);
    } else if (w === "O") {
      playSound("wrong");
      setMessage("Ganó la computadora, ¡inténtalo otra vez!");
      setTimeout(resetBoard, 1600);
    } else if (board.every((v) => v)) {
      setMessage("¡Empate! Buen intento");
      setTimeout(resetBoard, 1600);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [board]);

  function handleTap(i: number) {
    if (turn !== "player" || board[i] || winner(board)) return;
    const next = [...board];
    next[i] = "X";
    setBoard(next);
    playSound("click");
    setTurn("cpu");
  }

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-emerald-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} />
      <StarReward show={showWin} message="¡Ganaste el gato!" />
      <main className="mx-auto flex w-full max-w-md flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-4 text-center text-2xl font-extrabold text-emerald-600 sm:text-3xl">
          ⭕ Gato
        </h1>
        <div className="mb-4">
          <LevelSelector levels={GATO_LEVELS.map((l) => l.level)} active={level} onSelect={setLevel} />
        </div>
        <p className="mb-4 text-lg font-bold text-slate-600">{message}</p>
        <div className="grid grid-cols-3 gap-2">
          {board.map((cell, i) => (
            <motion.button
              key={i}
              onClick={() => handleTap(i)}
              whileTap={{ scale: 0.9 }}
              className="flex h-20 w-20 items-center justify-center rounded-2xl bg-white text-4xl font-extrabold shadow"
            >
              {cell === "X" && <span className="text-sky-500">✖️</span>}
              {cell === "O" && <span className="text-rose-500">⭕</span>}
            </motion.button>
          ))}
        </div>
      </main>
    </div>
  );
}
