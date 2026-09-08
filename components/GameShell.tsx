"use client";

import { type ReactNode } from "react";
import { motion, AnimatePresence } from "framer-motion";
import BackHomeButton from "./BackHomeButton";
import LevelSelector from "./LevelSelector";
import { getGame } from "@/data/games";
import { AREAS } from "@/lib/montessori";
import type { Nota } from "@/lib/useMaterial";

/**
 * Marco común de todos los materiales.
 *
 * El "ambiente preparado" también es visual: mismo lugar para volver, mismo
 * lugar para la consigna, mismo lugar para el nivel. El niño no tiene que
 * reaprender la pantalla en cada actividad, solo el material del centro.
 */
export default function GameShell({
  slug,
  level,
  levels,
  onLevel,
  consigna,
  nota,
  children,
  acciones,
  ancho = "max-w-md",
}: {
  slug: string;
  level?: number;
  levels?: number[];
  onLevel?: (n: number) => void;
  consigna?: string;
  nota?: Nota | null;
  children: ReactNode;
  acciones?: ReactNode;
  ancho?: string;
}) {
  const game = getGame(slug);
  const area = AREAS[game?.area ?? "sensorial"];

  return (
    <div className={`relative min-h-full flex-1 textura-papel ${area.tint}`}>
      <BackHomeButton />

      <header className="px-4 pt-4">
        <div className={`mx-auto flex w-full ${ancho} flex-col items-center gap-2`}>
          <div className="flex items-center gap-2 pl-10 pr-2">
            <span className="text-2xl">{game?.emoji}</span>
            <h1 className={`text-lg font-extrabold leading-tight ${area.text}`}>
              {game?.title ?? slug}
            </h1>
          </div>
          <span
            className={`rounded-full px-2.5 py-0.5 text-[10px] font-bold uppercase tracking-wide ${area.chip}`}
          >
            {area.label}
          </span>

          {levels && level !== undefined && onLevel && (
            <LevelSelector levels={levels} active={level} onSelect={onLevel} gameId={slug} />
          )}
        </div>
      </header>

      {consigna && (
        <p className="mx-auto mt-3 max-w-md px-6 text-center text-base font-bold text-stone-600">
          {consigna}
        </p>
      )}

      <main className={`mx-auto w-full ${ancho} px-4 pb-28 pt-3`}>{children}</main>

      {acciones && (
        <div className="fixed bottom-0 left-0 right-0 z-20 border-t border-black/5 bg-white/85 px-4 py-3 backdrop-blur">
          <div className={`mx-auto flex w-full ${ancho} items-center justify-center gap-2`}>
            {acciones}
          </div>
        </div>
      )}

      {/* Control del error: un aviso breve, nunca rojo ni con signos de alarma. */}
      <AnimatePresence>
        {nota && (
          <motion.div
            initial={{ opacity: 0, y: 14 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 14 }}
            className="pointer-events-none fixed inset-x-0 bottom-24 z-30 flex justify-center px-6"
          >
            <span
              className={`rounded-full px-4 py-2 text-sm font-bold shadow-sm ${
                nota.tipo === "bien"
                  ? "bg-[#e7f0e3] text-[#456b48]"
                  : "bg-[#f4ece2] text-[#8a6c4a]"
              }`}
            >
              {nota.texto}
            </span>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
