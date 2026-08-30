"use client";

import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { TABLA_CIEN_LEVELS, type TablaCienLevel } from "@/data/levels/tabla-cien";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

export default function TablaCienPage() {
  const material = useMaterial<TablaCienLevel>("tabla-cien", TABLA_CIEN_LEVELS);
  const { config, level, logrado, nota } = material;

  const [faltantes, setFaltantes] = useState<number[]>([]);
  const [fichas, setFichas] = useState<number[]>([]);
  const [enMano, setEnMano] = useState<number | null>(null);

  const preparar = useCallback(() => {
    const disponibles = Array.from({ length: config.hasta }, (_, i) => i + 1);
    // El 1 y los múltiplos de diez se quedan si hay guía: son los mojones.
    const candidatos = config.guia
      ? disponibles.filter((n) => n % 10 !== 0 && n !== 1)
      : disponibles;
    const huecos = shuffle(candidatos).slice(0, Math.min(config.huecos, candidatos.length));
    setFaltantes(huecos);
    setFichas(shuffle(huecos));
    setEnMano(null);
  }, [config.guia, config.hasta, config.huecos]);

  useEffect(() => {
    preparar();
  }, [preparar]);

  function tomarFicha(n: number) {
    setEnMano(n);
    hablar(String(n));
  }

  function colocar(casilla: number) {
    if (enMano === null) {
      material.intento("Primero toma una ficha del canasto");
      return;
    }
    if (casilla !== enMano) {
      material.intento("Ahí no va. Mira la fila y la columna");
      return;
    }
    const restantes = fichas.filter((f) => f !== enMano);
    setFichas(restantes);
    setFaltantes((prev) => prev.filter((f) => f !== enMano));
    setEnMano(null);
    if (restantes.length === 0) setTimeout(() => material.completar(), 400);
    else material.acierto();
  }

  const filas = Math.ceil(config.hasta / 10);

  return (
    <GameShell
      slug="tabla-cien"
      level={level}
      levels={TABLA_CIEN_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={
        enMano === null ? "Toma una ficha del canasto" : `Coloca el ${enMano} en su lugar`
      }
      nota={nota}
      acciones={
        <button
          onClick={preparar}
          className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
        >
          Empezar de nuevo
        </button>
      }
    >
      <ConfettiOverlay show={logrado} />
      <StarReward
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={() => {
          preparar();
          material.repetir();
        }}
      />

      <div className="mb-5 grid grid-cols-10 gap-[3px] rounded-2xl bg-white/70 p-2 ring-1 ring-black/5">
        {Array.from({ length: filas * 10 }, (_, i) => i + 1).map((n) => {
          const fueraDeRango = n > config.hasta;
          const vacia = faltantes.includes(n);
          return (
            <button
              key={n}
              disabled={fueraDeRango || !vacia}
              onClick={() => colocar(n)}
              className={`flex aspect-square items-center justify-center rounded-[5px] text-[10px] font-bold transition sm:text-xs ${
                fueraDeRango
                  ? "bg-transparent"
                  : vacia
                    ? "bg-[#f7e2cd] ring-1 ring-dashed ring-[#d3a56d]"
                    : "bg-white text-stone-500"
              }`}
            >
              {fueraDeRango || vacia ? "" : n}
            </button>
          );
        })}
      </div>

      <div className="flex flex-wrap justify-center gap-2 rounded-2xl bg-white/40 p-3">
        {fichas.map((f) => (
          <motion.button
            key={f}
            layout
            whileTap={{ scale: 0.9 }}
            onClick={() => tomarFicha(f)}
            className={`h-11 w-11 rounded-xl text-base font-extrabold shadow-sm transition ${
              enMano === f
                ? "bg-[#8a5a2b] text-white ring-4 ring-[#e0b586]"
                : "bg-white text-stone-600 ring-1 ring-black/5"
            }`}
          >
            {f}
          </motion.button>
        ))}
        {fichas.length === 0 && (
          <span className="py-3 text-sm text-stone-400">Tabla completa</span>
        )}
      </div>
    </GameShell>
  );
}
