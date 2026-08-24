"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import type { GameDef } from "@/data/games";
import { useProgressStore } from "@/lib/progressStore";

export default function GameCard({ game }: { game: GameDef }) {
  const stars = useProgressStore((s) => s.getProgress(game.id).stars);
  const isReady = game.status === "ready";

  const card = (
    <motion.div
      whileTap={isReady ? { scale: 0.94 } : undefined}
      whileHover={isReady ? { scale: 1.04 } : undefined}
      className={`relative flex flex-col items-center justify-center gap-2 rounded-[2rem] border-4 border-white/40 bg-gradient-to-br ${game.from} ${game.to} p-6 text-white shadow-xl aspect-square ${
        isReady ? "cursor-pointer" : "opacity-60"
      }`}
    >
      {stars > 0 && (
        <div className="absolute top-2 right-2 flex items-center gap-1 rounded-full bg-black/20 px-2 py-1 text-sm font-bold">
          ⭐ {stars}
        </div>
      )}
      <span className="text-5xl sm:text-6xl">{game.emoji}</span>
      <span className="text-center text-lg font-extrabold leading-tight sm:text-xl">
        {game.title}
      </span>
      <span className="text-center text-xs font-medium opacity-90 sm:text-sm">
        {isReady ? game.description : "Próximamente"}
      </span>
    </motion.div>
  );

  if (!isReady) {
    return <div>{card}</div>;
  }

  return (
    <Link href={`/games/${game.slug}`} aria-label={game.title}>
      {card}
    </Link>
  );
}
