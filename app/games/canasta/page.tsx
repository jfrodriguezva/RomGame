"use client";

import { useEffect, useRef, useState } from "react";
import BackHomeButton from "@/components/BackHomeButton";
import LevelSelector from "@/components/LevelSelector";
import { CANASTA_LEVELS, TICK_MS, CATCH_RADIUS } from "@/data/levels/canasta";
import { playSound } from "@/lib/audio";
import { useProgressStore } from "@/lib/progressStore";

interface FallingStar {
  id: number;
  x: number;
  y: number;
}

let nextId = 0;

export default function CanastaPage() {
  const [level, setLevel] = useState(1);
  const config = CANASTA_LEVELS.find((l) => l.level === level)!;
  const [basketX, setBasketX] = useState(50);
  const [stars, setStars] = useState<FallingStar[]>([]);
  const [caught, setCaught] = useState(0);
  const addStars = useProgressStore((s) => s.addStars);
  const registerPlay = useProgressStore((s) => s.registerPlay);
  const trackRef = useRef<HTMLDivElement>(null);
  const starsRef = useRef<FallingStar[]>([]);
  const basketXRef = useRef(50);

  function moveBasketTo(clientX: number) {
    const rect = trackRef.current?.getBoundingClientRect();
    if (!rect) return;
    const pct = ((clientX - rect.left) / rect.width) * 100;
    const clamped = Math.max(8, Math.min(92, pct));
    basketXRef.current = clamped;
    setBasketX(clamped);
  }

  useEffect(() => {
    registerPlay("canasta");
    // Reinicia la cesta y el marcador al cambiar de nivel: sincroniza con
    // una prop que cambia, no es una derivación pura del render actual.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setBasketX(50);
    basketXRef.current = 50;
    setStars([]);
    starsRef.current = [];
    setCaught(0);

    const spawnInterval = setInterval(() => {
      const siguientes = [...starsRef.current, { id: nextId++, x: 10 + Math.random() * 80, y: 0 }];
      starsRef.current = siguientes;
      setStars(siguientes);
    }, config.spawnMs);

    return () => clearInterval(spawnInterval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  /**
   * Antes esto vivía dentro del updater de setStars (`setStars(prev => {...
   * setCaught(...); playSound(...) ...})`), el mismo efecto secundario
   * dentro de una función que React puede invocar más de una vez que causó
   * el bug real en serpientes y en globo — el punto podía sonar doble o
   * contarse de más. También dependía de `basketX` en el arreglo de
   * dependencias, así que el intervalo se destruía y se recreaba en cada
   * movimiento de la cesta al arrastrar. Ahora lee la posición de la cesta
   * desde una ref (siempre al día, sin recrear el intervalo) y hace el
   * sonido/conteo una sola vez, fuera del setState.
   */
  useEffect(() => {
    const tick = setInterval(() => {
      let atrapadas = 0;
      const siguientes: FallingStar[] = [];
      for (const star of starsRef.current) {
        const ny = star.y + config.fallSpeed;
        if (ny >= 88 && Math.abs(star.x - basketXRef.current) < CATCH_RADIUS) {
          atrapadas++;
          continue;
        }
        if (ny >= 100) continue;
        siguientes.push({ ...star, y: ny });
      }
      starsRef.current = siguientes;
      setStars(siguientes);
      if (atrapadas > 0) {
        playSound("correct");
        setCaught((c) => c + atrapadas);
      }
    }, TICK_MS);
    return () => clearInterval(tick);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  useEffect(() => {
    if (caught > 0 && caught % 12 === 0) {
      addStars("canasta", 1);
      playSound("win");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [caught]);

  return (
    <div className="relative min-h-full flex-1 overflow-hidden bg-gradient-to-b from-yellow-100 via-white to-white pb-10">
      <BackHomeButton />
      <main className="mx-auto w-full max-w-2xl px-4 pt-20 sm:px-6">
        <h1 className="mb-2 text-center text-2xl font-extrabold text-amber-600 sm:text-3xl">
          🧺 Atrapa las estrellas
        </h1>
        <p className="mb-4 text-center text-slate-500">Atrapadas: {caught}</p>
        <div className="mb-4">
          <LevelSelector
            gameId="canasta"
            levels={CANASTA_LEVELS.map((l) => l.level)}
            active={level}
            onSelect={setLevel}
          />
        </div>
      </main>

      <div
        ref={trackRef}
        className="relative mx-auto h-[45vh] max-w-2xl touch-none select-none"
        onPointerDown={(e) => moveBasketTo(e.clientX)}
        onPointerMove={(e) => {
          if (e.buttons === 1 || e.pointerType === "touch") moveBasketTo(e.clientX);
        }}
      >
        {stars.map((star) => (
          <span
            key={star.id}
            className="absolute -translate-x-1/2 -translate-y-1/2 text-3xl transition-[top] duration-75 ease-linear"
            style={{ left: `${star.x}%`, top: `${star.y}%` }}
          >
            ⭐
          </span>
        ))}
        <span
          className="absolute -translate-x-1/2 text-6xl transition-[left] duration-100 ease-linear"
          style={{ left: `${basketX}%`, top: "85%" }}
        >
          🧺
        </span>
      </div>

      <p className="mt-4 text-center text-sm text-slate-400">
        Toca o desliza donde quieras que vaya la canasta
      </p>
    </div>
  );
}
