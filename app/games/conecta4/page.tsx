"use client";

import { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  CONECTA4_LEVELS,
  tableroVacio,
  filaDestino,
  hayGanador,
  tableroLleno,
  type Tablero,
  type Ficha,
} from "@/data/levels/conecta4";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

function clonar(t: Tablero): Tablero {
  return t.map((fila) => [...fila]);
}

/** ¿En qué columna debería jugar `ficha` para ganar ya mismo? null si ninguna. */
function jugadaGanadora(tablero: Tablero, cols: number, ficha: Ficha): number | null {
  for (let c = 0; c < cols; c++) {
    const f = filaDestino(tablero, c);
    if (f === -1) continue;
    const copia = clonar(tablero);
    copia[f][c] = ficha;
    if (hayGanador(copia) === ficha) return c;
  }
  return null;
}

export default function Conecta4Page() {
  const [level, setLevel] = useState(1);
  const config = CONECTA4_LEVELS.find((l) => l.level === level)!;
  const [tablero, setTablero] = useState<Tablero>([]);
  const [turno, setTurno] = useState<"jugador" | "cpu">("jugador");
  const [resultado, setResultado] = useState<Ficha | "empate" | null>(null);
  const [showWin, setShowWin] = useState(false);
  const [ultimaFicha, setUltimaFicha] = useState<{ f: number; c: number } | null>(null);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  const tableroRef = useRef<Tablero>([]);

  function resetGame() {
    const vacio = tableroVacio(config.filas, config.cols);
    setTablero(vacio);
    tableroRef.current = vacio;
    setTurno("jugador");
    setResultado(null);
    setUltimaFicha(null);
  }

  useEffect(() => {
    registerPlay("conecta4");
    // Arma un tablero vacío nuevo para el nivel: depende del tamaño de la
    // configuración, no es una derivación pura del render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    resetGame();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function soltar(col: number, quien: "jugador" | "cpu") {
    const f = filaDestino(tableroRef.current, col);
    if (f === -1 || resultado) return;

    const nuevoTablero = clonar(tableroRef.current);
    nuevoTablero[f][col] = quien;
    tableroRef.current = nuevoTablero;
    setTablero(nuevoTablero);
    setUltimaFicha({ f, c: col });
    playSound("click");

    const ganador = hayGanador(nuevoTablero);
    if (ganador) {
      setTimeout(() => {
        setResultado(ganador);
        if (ganador === "jugador") {
          playSound("win");
          addStars("conecta4", 1);
          setShowWin(true);
          setTimeout(() => {
            setShowWin(false);
            resetGame();
          }, 1800);
        } else {
          playSound("wrong");
          setTimeout(resetGame, 2200);
        }
      }, 300);
      return;
    }

    if (tableroLleno(nuevoTablero)) {
      setTimeout(() => {
        setResultado("empate");
        setTimeout(resetGame, 2200);
      }, 300);
      return;
    }

    setTurno(quien === "jugador" ? "cpu" : "jugador");
  }

  // Turno de la computadora: gana si puede, bloquea si el jugador puede
  // ganar en su siguiente tiro, y si no, elige una columna al azar.
  useEffect(() => {
    if (turno !== "cpu" || resultado) return;
    const t = setTimeout(() => {
      const cols = config.cols;
      const propia = jugadaGanadora(tableroRef.current, cols, "cpu");
      const bloqueo = propia === null ? jugadaGanadora(tableroRef.current, cols, "jugador") : null;
      const validas = Array.from({ length: cols }, (_, c) => c).filter(
        (c) => filaDestino(tableroRef.current, c) !== -1
      );
      const columna =
        propia ?? bloqueo ?? validas[Math.floor(Math.random() * validas.length)];
      soltar(columna, "cpu");
    }, 700);
    return () => clearTimeout(t);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turno, resultado]);

  const cellPx = config.cols >= 6 ? 46 : 56;

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-blue-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} slug="conecta4" />
      <StarReward slug="conecta4" show={showWin} message="¡Cuatro en línea!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-blue-600 sm:text-3xl">
          🔵 Cuatro en línea
        </h1>
        <p className="mb-3 text-center text-slate-500">
          {resultado === "jugador"
            ? "¡Ganaste! 🏆"
            : resultado === "cpu"
              ? "Ganó la computadora, ¡otra vez!"
              : resultado === "empate"
                ? "Tablero lleno: empate"
                : turno === "jugador"
                  ? "Tu turno: toca una columna"
                  : "Turno de la computadora"}
        </p>
        <div className="mb-4">
          <LevelSelector
            gameId="conecta4"
            levels={CONECTA4_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <div
          className="grid gap-1 rounded-2xl bg-blue-600 p-2 shadow-xl"
          style={{ gridTemplateColumns: `repeat(${config.cols}, ${cellPx}px)` }}
        >
          {tablero.map((fila, f) =>
            fila.map((celda, c) => (
              <button
                key={`${f}-${c}`}
                onClick={() => turno === "jugador" && soltar(c, "jugador")}
                aria-label={`Columna ${c + 1}`}
                className="flex items-center justify-center rounded-full bg-blue-500/40"
                style={{ width: cellPx, height: cellPx }}
              >
                {celda && (
                  <motion.span
                    initial={{ scale: 0, y: -30 }}
                    animate={{ scale: 1, y: 0 }}
                    transition={{ type: "spring", stiffness: 500, damping: 22 }}
                    className={`block rounded-full shadow ${
                      celda === "jugador" ? "bg-rose-400" : "bg-amber-300"
                    } ${f === ultimaFicha?.f && c === ultimaFicha?.c ? "ring-2 ring-white" : ""}`}
                    style={{ width: cellPx - 8, height: cellPx - 8 }}
                  />
                )}
              </button>
            ))
          )}
        </div>

        <div className="mt-3 flex items-center gap-4 text-xs font-bold text-stone-500">
          <span className="flex items-center gap-1">
            <span className="h-3 w-3 rounded-full bg-rose-400" /> Tú
          </span>
          <span className="flex items-center gap-1">
            <span className="h-3 w-3 rounded-full bg-amber-300" /> Computadora
          </span>
        </div>
      </main>
    </div>
  );
}
