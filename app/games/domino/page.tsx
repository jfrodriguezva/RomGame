"use client";

import { useEffect, useRef, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { DOMINO_LEVELS, generarFichas, type Ficha } from "@/data/levels/domino";
import { shuffle } from "@/lib/shuffle";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface Tramo {
  izq: string;
  der: string;
}

/**
 * Dominó de imágenes, simplificado a propósito: la fila solo crece hacia
 * la derecha (el dominó real permite los dos lados), así el modelo mental
 * es "agrega tu ficha al final", no "elige por dónde entra".
 */
export default function DominoPage() {
  const [level, setLevel] = useState(1);
  const config = DOMINO_LEVELS.find((l) => l.level === level)!;
  const [manoJugador, setManoJugador] = useState<Ficha[]>([]);
  const [manoCpu, setManoCpu] = useState<Ficha[]>([]);
  const [tira, setTira] = useState<Tramo[]>([]);
  const [finAbierto, setFinAbierto] = useState<string | null>(null);
  const [turno, setTurno] = useState<"jugador" | "cpu">("jugador");
  const [mensaje, setMensaje] = useState("Elige una ficha para empezar");
  const [showWin, setShowWin] = useState(false);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);

  const manoJugadorRef = useRef<Ficha[]>([]);
  const manoCpuRef = useRef<Ficha[]>([]);
  const finAbiertoRef = useRef<string | null>(null);
  const pasesSeguidosRef = useRef(0);

  function resetGame() {
    const fichas = shuffle(generarFichas(config.imagenes));
    const jugador = fichas.slice(0, config.fichasPorJugador);
    const cpu = fichas.slice(config.fichasPorJugador, config.fichasPorJugador * 2);
    setManoJugador(jugador);
    setManoCpu(cpu);
    manoJugadorRef.current = jugador;
    manoCpuRef.current = cpu;
    setTira([]);
    finAbiertoRef.current = null;
    setFinAbierto(null);
    pasesSeguidosRef.current = 0;
    setTurno("jugador");
    setMensaje("Elige una ficha para empezar");
  }

  useEffect(() => {
    registerPlay("domino");
    // Reparte las fichas al azar para el nivel: no es una derivación pura
    // que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    resetGame();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function puedeJugar(ficha: Ficha, finAbierto: string | null) {
    if (finAbierto === null) return true;
    return ficha.a === finAbierto || ficha.b === finAbierto;
  }

  function terminar(ganador: "jugador" | "cpu" | "empate") {
    setMensaje(
      ganador === "jugador"
        ? "¡Colocaste todas tus fichas! 🏆"
        : ganador === "cpu"
          ? "La computadora terminó primero, ¡otra vez!"
          : "Nadie tiene ficha que encaje: empate"
    );
    if (ganador === "jugador") {
      playSound("win");
      addStars("domino", 1);
      setShowWin(true);
      setTimeout(() => {
        setShowWin(false);
        resetGame();
      }, 1800);
    } else {
      setTimeout(resetGame, 2200);
    }
  }

  function jugarTurno(quien: "jugador" | "cpu", ficha: Ficha) {
    const finAbierto = finAbiertoRef.current;
    if (!puedeJugar(ficha, finAbierto)) return;

    const tramo: Tramo =
      finAbierto === null || ficha.a === finAbierto
        ? { izq: ficha.a, der: ficha.b }
        : { izq: ficha.b, der: ficha.a };

    setTira((prev) => [...prev, tramo]);
    finAbiertoRef.current = tramo.der;
    setFinAbierto(tramo.der);
    pasesSeguidosRef.current = 0;
    playSound("correct");

    if (quien === "jugador") {
      const siguiente = manoJugadorRef.current.filter((f) => f.id !== ficha.id);
      manoJugadorRef.current = siguiente;
      setManoJugador(siguiente);
    } else {
      const siguiente = manoCpuRef.current.filter((f) => f.id !== ficha.id);
      manoCpuRef.current = siguiente;
      setManoCpu(siguiente);
    }

    setTimeout(() => continuar(quien), 500);
  }

  function continuar(quienJugo: "jugador" | "cpu") {
    const manoQuien = quienJugo === "jugador" ? manoJugadorRef.current : manoCpuRef.current;
    if (manoQuien.length === 0) {
      terminar(quienJugo);
      return;
    }

    const otro = quienJugo === "jugador" ? "cpu" : "jugador";
    const manoOtro = otro === "jugador" ? manoJugadorRef.current : manoCpuRef.current;
    const otroPuede = manoOtro.some((f) => puedeJugar(f, finAbiertoRef.current));

    if (otroPuede) {
      setTurno(otro);
      setMensaje(otro === "jugador" ? "Tu turno" : "Turno de la computadora");
      return;
    }

    pasesSeguidosRef.current += 1;
    if (pasesSeguidosRef.current >= 2) {
      const ganador =
        manoJugadorRef.current.length < manoCpuRef.current.length
          ? "jugador"
          : manoCpuRef.current.length < manoJugadorRef.current.length
            ? "cpu"
            : "empate";
      terminar(ganador);
      return;
    }

    setMensaje(
      `${otro === "jugador" ? "Tú" : "La computadora"} no tiene ficha: sigue ${quienJugo === "jugador" ? "tu turno" : "el turno de la computadora"}`
    );
    setTurno(quienJugo);
  }

  function tocarFicha(ficha: Ficha) {
    if (turno !== "jugador") return;
    jugarTurno("jugador", ficha);
  }

  // Turno de la computadora: juega la primera ficha que le encaje.
  useEffect(() => {
    if (turno !== "cpu") return;
    const t = setTimeout(() => {
      const jugable = manoCpuRef.current.find((f) => puedeJugar(f, finAbiertoRef.current));
      if (jugable) jugarTurno("cpu", jugable);
    }, 900);
    return () => clearTimeout(t);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turno]);

  return (
    <div className="min-h-full flex-1 bg-gradient-to-b from-violet-100 via-white to-white pb-10">
      <BackHomeButton />
      <ConfettiOverlay show={showWin} slug="domino" />
      <StarReward slug="domino" show={showWin} message="¡Colocaste todas tus fichas!" />
      <main className="mx-auto flex w-full max-w-xl flex-col items-center px-4 pt-20 sm:px-6">
        <h1 className="mb-1 text-center text-2xl font-extrabold text-violet-600 sm:text-3xl">
          🁣 Dominó de imágenes
        </h1>
        <p className="mb-3 text-center text-slate-500">{mensaje}</p>
        <div className="mb-4">
          <LevelSelector
            gameId="domino"
            levels={DOMINO_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>

        <p className="mb-2 text-xs font-bold text-stone-400">🤖 La computadora tiene {manoCpu.length} fichas</p>

        <div className="mb-4 flex min-h-16 max-w-full flex-wrap justify-center gap-1 overflow-x-auto rounded-2xl bg-white/70 p-3 ring-1 ring-black/5">
          <AnimatePresence>
            {tira.length === 0 && <span className="text-sm text-stone-400">La fila empieza aquí</span>}
            {tira.map((t, i) => (
              <motion.div
                key={i}
                initial={{ scale: 0, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                className="flex shrink-0 overflow-hidden rounded-xl bg-white shadow ring-1 ring-black/10"
              >
                <span className="flex h-12 w-12 items-center justify-center text-2xl">{t.izq}</span>
                <span className="w-px bg-black/10" />
                <span className="flex h-12 w-12 items-center justify-center text-2xl">{t.der}</span>
              </motion.div>
            ))}
          </AnimatePresence>
        </div>

        <p className="mb-2 text-xs font-bold text-stone-400">Tus fichas</p>
        <div className="flex flex-wrap justify-center gap-2">
          {manoJugador.map((f) => {
            const jugable = turno === "jugador" && puedeJugar(f, finAbierto);
            return (
              <motion.button
                key={f.id}
                whileTap={jugable ? { scale: 0.92 } : undefined}
                onClick={() => tocarFicha(f)}
                className={`flex overflow-hidden rounded-xl bg-white shadow ring-1 ring-black/10 ${
                  jugable ? "" : "opacity-40"
                }`}
              >
                <span className="flex h-14 w-14 items-center justify-center text-3xl">{f.a}</span>
                <span className="w-px bg-black/10" />
                <span className="flex h-14 w-14 items-center justify-center text-3xl">{f.b}</span>
              </motion.button>
            );
          })}
        </div>
      </main>
    </div>
  );
}
