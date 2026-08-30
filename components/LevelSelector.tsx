"use client";

import { useMemo, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { STAGES, STAGE_SIZE, stageOf } from "@/lib/levels";
import { useProgressStore } from "@/lib/progressStore";
import { playSound } from "@/lib/audio";
import { vibrar, HAPTIC } from "@/lib/haptics";

/**
 * Selector de nivel para materiales de 100 niveles.
 *
 * Se muestra plegado (una sola barra) para no robarle espacio al material.
 * Al abrirlo aparecen las 10 etapas, cada una con sus 10 niveles: el niño ve
 * de un vistazo hasta dónde llegó y qué le falta, sin una lista interminable.
 */
export default function LevelSelector({
  levels,
  active,
  onSelect,
  gameId,
}: {
  levels: number[];
  active: number;
  onSelect: (level: number) => void;
  /** Si se pasa, los niveles aún no alcanzados aparecen cerrados. */
  gameId?: string;
}) {
  const [abierto, setAbierto] = useState(false);
  const [etapaVista, setEtapaVista] = useState(() => stageOf(active));

  const progreso = useProgressStore((s) => (gameId ? s.games[gameId] : undefined));
  const desbloqueado = progreso?.unlockedLevel ?? levels.length;
  const completados = useMemo(() => new Set(progreso?.completed ?? []), [progreso]);

  const total = levels.length;
  const etapasVisibles = useMemo(
    () => STAGES.filter((s) => s.from <= total),
    [total]
  );
  const etapa = etapasVisibles.find((s) => s.stage === etapaVista) ?? etapasVisibles[0];
  const info = STAGES[stageOf(active) - 1];

  const nivelesEtapa = levels.filter((l) => l >= etapa.from && l <= etapa.to);
  const hechosEtapa = nivelesEtapa.filter((l) => completados.has(l)).length;

  function elegir(level: number) {
    if (gameId && level > desbloqueado) {
      vibrar(HAPTIC.error);
      return;
    }
    playSound("click");
    vibrar(HAPTIC.toque);
    onSelect(level);
    setAbierto(false);
  }

  function mover(delta: number) {
    const siguiente = Math.min(etapasVisibles.length, Math.max(1, etapaVista + delta));
    setEtapaVista(siguiente);
  }

  return (
    <div className="w-full max-w-md">
      <button
        onClick={() => {
          setAbierto((v) => !v);
          setEtapaVista(stageOf(active));
          playSound("click");
        }}
        className="flex w-full items-center justify-between gap-3 rounded-2xl bg-white/90 px-4 py-2.5 shadow-sm ring-1 ring-black/5 active:scale-[0.98]"
        aria-expanded={abierto}
      >
        <span className="flex items-center gap-2">
          <span className="text-xl">{info.emoji}</span>
          <span className="text-left leading-tight">
            <span className="block text-[11px] font-semibold uppercase tracking-wide text-stone-400">
              Etapa {info.stage} · {info.name}
            </span>
            <span className="block text-base font-extrabold text-stone-700">
              Nivel {active}
              <span className="text-stone-400"> / {total}</span>
            </span>
          </span>
        </span>

        <span className="flex items-center gap-2">
          <span className="flex items-center gap-1 rounded-full bg-stone-100 px-2 py-1 text-xs font-bold text-stone-500">
            {completados.size}
            <span className="text-amber-400">★</span>
          </span>
          <motion.span animate={{ rotate: abierto ? 180 : 0 }} className="text-stone-400">
            ▾
          </motion.span>
        </span>
      </button>

      <AnimatePresence initial={false}>
        {abierto && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="overflow-hidden"
          >
            <div className="mt-2 rounded-2xl bg-white/95 p-3 shadow-sm ring-1 ring-black/5">
              <div className="mb-2 flex items-center justify-between gap-2">
                <button
                  onClick={() => mover(-1)}
                  disabled={etapaVista <= 1}
                  className="h-8 w-8 rounded-full bg-stone-100 text-stone-500 disabled:opacity-30"
                  aria-label="Etapa anterior"
                >
                  ‹
                </button>
                <span className="text-center text-sm font-bold text-stone-600">
                  {etapa.emoji} {etapa.name}
                  <span className="block text-[11px] font-medium text-stone-400">
                    niveles {etapa.from} a {etapa.to} · {hechosEtapa} de {nivelesEtapa.length}
                  </span>
                </span>
                <button
                  onClick={() => mover(1)}
                  disabled={etapaVista >= etapasVisibles.length}
                  className="h-8 w-8 rounded-full bg-stone-100 text-stone-500 disabled:opacity-30"
                  aria-label="Etapa siguiente"
                >
                  ›
                </button>
              </div>

              <div className="grid grid-cols-5 gap-2">
                {nivelesEtapa.map((level) => {
                  const cerrado = Boolean(gameId) && level > desbloqueado;
                  const hecho = completados.has(level);
                  const esActivo = level === active;
                  return (
                    <button
                      key={level}
                      onClick={() => elegir(level)}
                      aria-label={`Nivel ${level}${cerrado ? ", aún cerrado" : ""}`}
                      className={`relative h-12 rounded-xl text-base font-extrabold transition active:scale-90 ${
                        esActivo
                          ? "bg-stone-700 text-white shadow"
                          : cerrado
                            ? "bg-stone-50 text-stone-300"
                            : hecho
                              ? "bg-amber-100 text-amber-700 ring-1 ring-amber-200"
                              : "bg-stone-100 text-stone-600"
                      }`}
                    >
                      {cerrado ? "·" : level}
                      {hecho && !esActivo && (
                        <span className="absolute right-1 top-0.5 text-[10px] text-amber-500">★</span>
                      )}
                    </button>
                  );
                })}
              </div>

              <div className="mt-3 flex gap-1">
                {etapasVisibles.map((s) => {
                  const hechos = levels
                    .filter((l) => l >= s.from && l <= s.to)
                    .filter((l) => completados.has(l)).length;
                  return (
                    <button
                      key={s.stage}
                      onClick={() => setEtapaVista(s.stage)}
                      aria-label={`Etapa ${s.stage}: ${s.name}`}
                      className={`h-1.5 flex-1 rounded-full transition ${
                        s.stage === etapaVista
                          ? "bg-stone-600"
                          : hechos === STAGE_SIZE
                            ? "bg-amber-300"
                            : hechos > 0
                              ? "bg-amber-200"
                              : "bg-stone-200"
                      }`}
                    />
                  );
                })}
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
