"use client";

import Link from "next/link";
import { useMemo, useState } from "react";
import { motion } from "framer-motion";
import GameCard from "@/components/GameCard";
import AnimatedBackground from "@/components/AnimatedBackground";
import Mascot from "@/components/Mascot";
import { games, gamesByArea, getGame, rutaDeJuego, TOTAL_NIVELES } from "@/data/games";
import { AREAS, AREA_ORDER, type Area } from "@/lib/montessori";
import { useProgressStore } from "@/lib/progressStore";
import { useSettings } from "@/lib/settings";

export default function Home() {
  const porJuego = useProgressStore((s) => s.games);
  const nombre = useSettings((s) => s.nombre);
  const [areaActiva, setAreaActiva] = useState<Area | "todas">(AREA_ORDER[0]);

  const resumen = useMemo(() => {
    const entradas = Object.entries(porJuego);
    const estrellas = entradas.reduce((acc, [, p]) => acc + p.stars, 0);
    const niveles = entradas.reduce((acc, [, p]) => acc + p.completed.length, 0);
    const ultimo = entradas
      .filter(([id]) => getGame(id))
      .sort((a, b) => b[1].lastPlayedAt - a[1].lastPlayedAt)[0];
    return {
      estrellas,
      niveles,
      ultimo: ultimo ? { juego: getGame(ultimo[0])!, progreso: ultimo[1] } : null,
    };
  }, [porJuego]);

  const saludo = nombre ? `Hola, ${nombre}` : "Hola";

  return (
    <div className="relative min-h-full flex-1 textura-papel">
      <AnimatedBackground />

      <main className="relative mx-auto w-full max-w-5xl px-4 pb-16 pt-6 sm:px-6">
        <header className="mb-6 flex items-start justify-between gap-3">
          <div className="flex items-center gap-3">
            <Mascot size={46} />
            <div>
              <p className="text-sm font-semibold text-stone-400">{saludo}</p>
              <h1 className="text-2xl font-extrabold leading-tight text-stone-700 sm:text-3xl">
                Mi ambiente
              </h1>
            </div>
          </div>
          <Link
            href="/padres"
            className="mt-1 rounded-2xl bg-white/80 px-3 py-2 text-xs font-bold text-stone-500 shadow-sm ring-1 ring-black/5 active:scale-95"
          >
            Mamá y papá
          </Link>
        </header>

        {/* Elige tú, cuando quieras: nadie empuja al niño a una actividad. */}
        <section className="mb-8 grid gap-3 sm:grid-cols-2">
          <Link href="/pizarra">
            <motion.div
              whileTap={{ scale: 0.97 }}
              className="flex h-full items-center gap-4 rounded-3xl bg-gradient-to-br from-[#f7f0e4] to-[#efe3cc] p-5 shadow-[0_2px_12px_rgba(120,90,50,0.1)] ring-1 ring-black/5"
            >
              <span className="text-5xl">🖍️</span>
              <span className="leading-tight">
                <span className="block text-lg font-extrabold text-[#7a6234]">
                  La pizarra grande
                </span>
                <span className="block text-sm text-stone-500">
                  Dibuja libre, sin niveles y sin prisa
                </span>
              </span>
            </motion.div>
          </Link>

          {resumen.ultimo ? (
            <Link href={rutaDeJuego(resumen.ultimo.juego)}>
              <motion.div
                whileTap={{ scale: 0.97 }}
                className="flex h-full items-center gap-4 rounded-3xl bg-white/85 p-5 shadow-[0_2px_12px_rgba(80,60,40,0.07)] ring-1 ring-black/5"
              >
                <span className="text-5xl">{resumen.ultimo.juego.emoji}</span>
                <span className="leading-tight">
                  <span className="block text-[11px] font-bold uppercase tracking-wide text-stone-400">
                    seguir donde te quedaste
                  </span>
                  <span className="block text-lg font-extrabold text-stone-700">
                    {resumen.ultimo.juego.title}
                  </span>
                  {!resumen.ultimo.juego.libre && (
                    <span className="block text-sm text-stone-500">
                      nivel {resumen.ultimo.progreso.unlockedLevel}
                    </span>
                  )}
                </span>
              </motion.div>
            </Link>
          ) : (
            <div className="flex h-full items-center gap-4 rounded-3xl bg-white/60 p-5 ring-1 ring-black/5">
              <span className="text-4xl">🧺</span>
              <span className="text-sm leading-snug text-stone-500">
                Toma el material que quieras. Puedes repetirlo las veces que necesites: aquí no
                se pierde ni se gana.
              </span>
            </div>
          )}
        </section>

        <div className="mb-8 flex flex-wrap items-center justify-center gap-2 text-center text-xs font-bold text-stone-500">
          <span className="rounded-full bg-white/70 px-3 py-1.5 ring-1 ring-black/5">
            {games.length} materiales
          </span>
          <span className="rounded-full bg-white/70 px-3 py-1.5 ring-1 ring-black/5">
            {TOTAL_NIVELES.toLocaleString("es-MX")} niveles
          </span>
          <span className="rounded-full bg-white/70 px-3 py-1.5 ring-1 ring-black/5">
            {resumen.niveles} completados
          </span>
          <span className="rounded-full bg-amber-50 px-3 py-1.5 text-amber-600 ring-1 ring-amber-200/70">
            ★ {resumen.estrellas}
          </span>
        </div>

        {/* Selector de área: agrupa las 91 tarjetas detrás de un solo tap en
            vez de tenerlas todas expandidas una tras otra — antes había que
            hacer scroll por las 8 áreas completas para llegar a la última. */}
        <div className="mb-6 flex flex-wrap justify-center gap-2">
          {AREA_ORDER.map((areaId) => {
            const area = AREAS[areaId];
            const activa = areaActiva === areaId;
            return (
              <motion.button
                key={areaId}
                whileTap={{ scale: 0.94 }}
                onClick={() => setAreaActiva(areaId)}
                className={`flex items-center gap-1.5 rounded-full px-3.5 py-2 text-sm font-bold shadow-sm ring-1 transition ${
                  activa ? `${area.chip} ring-black/10` : "bg-white/70 text-stone-500 ring-black/5"
                }`}
              >
                <span className="text-lg leading-none">{area.emoji}</span>
                {area.label}
                <span className={activa ? "opacity-70" : "text-stone-400"}>
                  {gamesByArea(areaId).length}
                </span>
              </motion.button>
            );
          })}
          <motion.button
            whileTap={{ scale: 0.94 }}
            onClick={() => setAreaActiva("todas")}
            className={`rounded-full px-3.5 py-2 text-sm font-bold shadow-sm ring-1 transition ${
              areaActiva === "todas"
                ? "bg-stone-700 text-white ring-black/10"
                : "bg-white/70 text-stone-500 ring-black/5"
            }`}
          >
            Ver todas
          </motion.button>
        </div>

        {areaActiva === "todas" ? (
          AREA_ORDER.map((areaId) => <AreaSection key={areaId} areaId={areaId} />)
        ) : (
          <AreaSection areaId={areaActiva} />
        )}

        <footer className="mt-12 text-center text-xs leading-relaxed text-stone-400">
          Inspirado en el método Montessori: el niño elige, repite y se corrige solo.
          <br />
          Todo el progreso se guarda únicamente en este dispositivo.
        </footer>
      </main>
    </div>
  );
}

function AreaSection({ areaId }: { areaId: keyof typeof AREAS }) {
  const area = AREAS[areaId];
  const lista = gamesByArea(areaId);
  if (lista.length === 0) return null;

  return (
    <section className="mb-10">
      <div className="mb-3 flex items-baseline gap-2">
        <span className="text-xl">{area.emoji}</span>
        <h2 className={`text-lg font-extrabold sm:text-xl ${area.text}`}>{area.label}</h2>
        <span className="text-xs text-stone-400">{lista.length}</span>
      </div>
      <p className="mb-4 text-sm text-stone-500">{area.proposito}</p>

      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-4">
        {lista.map((game) => (
          <GameCard key={game.id} game={game} />
        ))}
      </div>
    </section>
  );
}
