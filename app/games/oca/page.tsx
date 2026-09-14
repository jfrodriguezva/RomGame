"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  OCA_LEVELS,
  buildOcaBoard,
  cellPosition,
  RETROCESO_POZO,
  type Casilla,
} from "@/data/levels/oca";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

const DICE_FACES = ["⚀", "⚁", "⚂", "⚃", "⚄", "⚅"];

const ICONO: Record<Casilla["tipo"], string> = {
  oca: "🦢",
  pozo: "💧",
  puente: "🌉",
};

/**
 * Como serpientes y escaleras, pero con tres efectos de casilla en vez de
 * uno: oca (tira otra vez), puente (salta adelante) y pozo (retrocede
 * unos pasos, nunca al inicio — la app no castiga duro).
 */
export default function OcaPage() {
  const [level, setLevel] = useState(1);
  const config = OCA_LEVELS.find((l) => l.level === level)!;
  const [casillas, setCasillas] = useState<Map<number, Casilla>>(new Map());
  const [playerPos, setPlayerPos] = useState(1);
  const [cpuPos, setCpuPos] = useState(1);
  const [turn, setTurn] = useState<"player" | "cpu">("player");
  const [dice, setDice] = useState(0);
  const [rolling, setRolling] = useState(false);
  const [showWin, setShowWin] = useState(false);
  const [message, setMessage] = useState("¡Tira el dado!");
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  function resetGame() {
    setCasillas(buildOcaBoard(config));
    setPlayerPos(1);
    setCpuPos(1);
    setTurn("player");
    setMessage("¡Tira el dado!");
  }

  useEffect(() => {
    registerPlay("oca");
    // Arma un tablero nuevo al azar para el nivel: no es una derivación
    // pura que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    resetGame();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

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
    const prev = who === "player" ? playerPos : cpuPos;
    const target = Math.min(config.length, prev + steps);
    const casilla = casillas.get(target);

    let final = target;
    if (casilla?.tipo === "puente" && casilla.saltaA) {
      final = casilla.saltaA;
    } else if (casilla?.tipo === "pozo") {
      final = Math.max(1, target - RETROCESO_POZO);
    }

    if (who === "player") setPlayerPos(final);
    else setCpuPos(final);

    if (casilla) {
      setTimeout(() => {
        playSound(casilla.tipo === "pozo" ? "wrong" : "win");
      }, 300);
    }

    if (final >= config.length) {
      setTimeout(() => {
        if (who === "player") {
          playSound("win");
          setMessage("¡Llegaste a la meta! 🏆");
          addStars("oca", 1);
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
      return;
    }

    setTimeout(() => {
      if (casilla?.tipo === "oca") {
        setMessage(who === "player" ? "¡Oca! Tiras otra vez" : "La computadora tira otra vez");
        if (who === "cpu") setTimeout(() => roll("cpu"), 900);
      } else if (who === "player") {
        setTurn("cpu");
        setMessage("Turno de la computadora");
      } else {
        setTurn("player");
        setMessage("¡Tu turno!");
      }
    }, 400);
  }

  useEffect(() => {
    if (turn === "cpu" && !rolling) {
      const t = setTimeout(() => roll("cpu"), 900);
      return () => clearTimeout(t);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turn]);

  const cellPx = 54;
  const rows = Math.ceil(config.length / config.cols);

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-amber-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} slug="oca" />
      <StarReward slug="oca" show={showWin} message="¡Llegaste a la meta!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-amber-700 sm:text-3xl">
          🦢 El juego de la oca
        </h1>
        <p className="mb-3 text-center text-slate-500">{message}</p>
        <div className="mb-4">
          <LevelSelector
            gameId="oca"
            levels={OCA_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div className="mb-3 flex flex-wrap justify-center gap-3 text-xs font-bold text-stone-500">
          <span>🦢 tira otra vez</span>
          <span>🌉 salta adelante</span>
          <span>💧 retrocede</span>
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
            const casilla = casillas.get(n);
            return (
              <div
                key={idx}
                className={`relative flex items-center justify-center rounded-md text-[10px] font-bold ${
                  n === config.length
                    ? "bg-amber-300"
                    : casilla?.tipo === "oca"
                      ? "bg-sky-100"
                      : casilla?.tipo === "puente"
                        ? "bg-emerald-100"
                        : casilla?.tipo === "pozo"
                          ? "bg-rose-100"
                          : "bg-slate-100"
                }`}
                style={{ gridRow: rows - row, gridColumn: col + 1, width: cellPx, height: cellPx }}
              >
                {n}
                {casilla && <span className="absolute text-lg">{ICONO[casilla.tipo]}</span>}
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
