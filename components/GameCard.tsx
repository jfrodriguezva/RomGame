"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { type GameDef, rutaDeJuego } from "@/data/games";
import { AREAS } from "@/lib/montessori";
import { LEVEL_COUNT } from "@/lib/levels";
import { useProgressStore } from "@/lib/progressStore";

/**
 * Tarjeta de material.
 *
 * El ambiente Montessori es sobrio a propósito: madera, tonos naturales y un
 * solo objeto llamativo por bandeja. Por eso la tarjeta es clara y el color
 * fuerte se reserva para el emoji del material y el avance del niño.
 */
export default function GameCard({ game }: { game: GameDef }) {
  const progreso = useProgressStore((s) => s.games[game.id]);
  const area = AREAS[game.area];
  const isReady = game.status === "ready";

  const completados = progreso?.completed.length ?? 0;
  const nivel = progreso?.unlockedLevel ?? 1;
  const pct = game.libre ? 0 : Math.round((completados / LEVEL_COUNT) * 100);

  const card = (
    <motion.div
      whileTap={isReady ? { scale: 0.96 } : undefined}
      whileHover={isReady ? { y: -3 } : undefined}
      className={`relative flex h-full flex-col gap-1 rounded-3xl border border-black/5 ${area.tint} p-4 text-left shadow-[0_2px_10px_rgba(80,60,40,0.08)] ${
        isReady ? "cursor-pointer" : "opacity-50"
      }`}
    >
      {game.nuevo && (
        <span className="absolute right-3 top-3 rounded-full bg-white/80 px-2 py-0.5 text-[10px] font-extrabold uppercase tracking-wide text-stone-500">
          nuevo
        </span>
      )}

      <span className="mb-1 text-4xl leading-none sm:text-5xl">{game.emoji}</span>

      <span className={`text-base font-extrabold leading-tight ${area.text} sm:text-lg`}>
        {game.title}
      </span>
      <span className="text-xs leading-snug text-stone-500 sm:text-sm">{game.description}</span>

      <div className="mt-auto pt-3">
        {game.libre ? (
          <span className="inline-flex items-center gap-1 rounded-full bg-white/70 px-2 py-1 text-[11px] font-bold text-stone-500">
            actividad libre
          </span>
        ) : (
          <>
            <div className="mb-1 flex items-center justify-between text-[11px] font-bold text-stone-500">
              <span>
                {completados > 0 ? `nivel ${nivel}` : "empezar"}
                <span className="text-stone-400"> / {LEVEL_COUNT}</span>
              </span>
              {completados > 0 && <span className="text-amber-500">★ {progreso?.stars ?? 0}</span>}
            </div>
            <div className="h-1.5 overflow-hidden rounded-full bg-white/70">
              <div
                className="h-full rounded-full bg-stone-400/70 transition-all"
                style={{ width: `${Math.max(pct, completados > 0 ? 4 : 0)}%` }}
              />
            </div>
          </>
        )}
      </div>
    </motion.div>
  );

  if (!isReady) return <div>{card}</div>;

  return (
    <Link href={rutaDeJuego(game)} aria-label={game.title} className="h-full">
      {card}
    </Link>
  );
}
