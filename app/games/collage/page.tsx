"use client";

import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import { CATEGORIAS_ESTAMPAS, ESCENAS } from "@/data/collage";
import { playSound } from "@/lib/audio";
import { vibrar, HAPTIC } from "@/lib/haptics";
import { useProgressStore } from "@/lib/progressStore";

interface Estampa {
  id: number;
  emoji: string;
  x: number;
  y: number;
  rot: number;
  escala: number;
}

let nextId = 0;

/**
 * Composición libre: tocar el lienzo coloca la estampa elegida, tocar una
 * estampa ya puesta la quita. Sin niveles, sin meta, sin "completo" — la
 * actividad libre no necesita una condición de cierre para tener sentido.
 */
export default function CollagePage() {
  const registerPlay = useProgressStore((s) => s.registerPlay);
  const [escena, setEscena] = useState(ESCENAS[0]);
  const [categoria, setCategoria] = useState(CATEGORIAS_ESTAMPAS[0]);
  const [estampa, setEstampa] = useState(CATEGORIAS_ESTAMPAS[0].items[0]);
  const [puestas, setPuestas] = useState<Estampa[]>([]);

  useEffect(() => {
    registerPlay("collage");
  }, [registerPlay]);

  function colocar(e: React.PointerEvent<HTMLDivElement>) {
    const rect = e.currentTarget.getBoundingClientRect();
    const x = ((e.clientX - rect.left) / rect.width) * 100;
    const y = ((e.clientY - rect.top) / rect.height) * 100;
    setPuestas((prev) => [
      ...prev,
      {
        id: nextId++,
        emoji: estampa,
        x,
        y,
        rot: Math.random() * 30 - 15,
        escala: 0.85 + Math.random() * 0.3,
      },
    ]);
    playSound("star");
    vibrar(HAPTIC.toque);
  }

  function quitar(id: number, e: React.PointerEvent<HTMLButtonElement>) {
    e.stopPropagation();
    setPuestas((prev) => prev.filter((p) => p.id !== id));
    playSound("click");
    vibrar(HAPTIC.toque);
  }

  function deshacer() {
    setPuestas((prev) => prev.slice(0, -1));
    playSound("click");
    vibrar(HAPTIC.toque);
  }

  return (
    <GameShell slug="collage" consigna="Arma tu escena, sin reglas">
      <div className="mb-3 flex gap-2 overflow-x-auto pb-1 scroll-suave">
        {ESCENAS.map((es) => (
          <button
            key={es.id}
            onClick={() => setEscena(es)}
            className={`h-11 shrink-0 rounded-2xl px-4 text-xs font-bold text-stone-600 ring-1 ring-black/10 ${
              escena.id === es.id ? "ring-4 ring-stone-500" : ""
            }`}
            style={{ background: es.css }}
          >
            <span className="rounded bg-white/80 px-1.5 py-0.5">{es.nombre}</span>
          </button>
        ))}
      </div>

      <div
        onPointerDown={colocar}
        className="relative mb-3 aspect-[4/3] w-full cursor-crosshair overflow-hidden rounded-[2rem] shadow-sm ring-1 ring-black/5"
        style={{ background: escena.css }}
      >
        <AnimatePresence>
          {puestas.map((p) => (
            <motion.button
              key={p.id}
              onPointerDown={(e) => quitar(p.id, e)}
              initial={{ scale: 0 }}
              animate={{ scale: p.escala, rotate: p.rot }}
              exit={{ scale: 0 }}
              transition={{ type: "spring", stiffness: 400, damping: 20 }}
              aria-label={`Quitar ${p.emoji}`}
              className="absolute text-4xl leading-none"
              style={{ left: `${p.x}%`, top: `${p.y}%`, transform: "translate(-50%, -50%)" }}
            >
              {p.emoji}
            </motion.button>
          ))}
        </AnimatePresence>

        {puestas.length === 0 && (
          <p className="pointer-events-none absolute inset-0 flex items-center justify-center px-8 text-center text-sm font-bold text-stone-500/70">
            Toca aquí para empezar a decorar
          </p>
        )}
      </div>

      <div className="mb-2 flex gap-1.5 overflow-x-auto pb-1 text-xs font-bold scroll-suave">
        {CATEGORIAS_ESTAMPAS.map((c) => (
          <button
            key={c.id}
            onClick={() => setCategoria(c)}
            className={`shrink-0 rounded-full px-3 py-1.5 ${
              categoria.id === c.id ? "bg-stone-700 text-white" : "bg-white text-stone-500 ring-1 ring-black/5"
            }`}
          >
            {c.nombre}
          </button>
        ))}
      </div>

      <div className="mb-4 flex gap-2 overflow-x-auto pb-1 scroll-suave">
        {categoria.items.map((emoji, i) => (
          <button
            key={`${emoji}-${i}`}
            onClick={() => {
              setEstampa(emoji);
              playSound("click");
            }}
            aria-label={`Elegir ${emoji}`}
            className={`flex h-14 w-14 shrink-0 items-center justify-center rounded-2xl bg-white text-2xl ring-1 ring-black/5 ${
              estampa === emoji ? "scale-110 ring-4 ring-stone-500" : ""
            }`}
          >
            {emoji}
          </button>
        ))}
      </div>

      {puestas.length > 0 && (
        <div className="flex justify-center gap-2">
          <button
            onClick={deshacer}
            className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
          >
            ↩︎ Deshacer
          </button>
          <button
            onClick={() => setPuestas([])}
            className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
          >
            Empezar de nuevo
          </button>
        </div>
      )}
    </GameShell>
  );
}
