"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { PINZA_LEVELS, OBJETOS_PINZA, type PinzaLevel } from "@/data/levels/pinza";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";

export default function PinzaPage() {
  const material = useMaterial<PinzaLevel>("pinza", PINZA_LEVELS);
  const { config, level, logrado, nota } = material;

  const emoji = useMemo(() => OBJETOS_PINZA[level % OBJETOS_PINZA.length], [level]);

  const [origen, setOrigen] = useState<number[]>([]);
  const [destino, setDestino] = useState<number[]>([]);

  const reiniciar = useCallback(() => {
    setDestino([]);
    setOrigen(Array.from({ length: config.origen }, (_, i) => i));
  }, [config.origen]);

  useEffect(() => {
    reiniciar();
  }, [reiniciar]);

  function tomar(id: number) {
    if (destino.length >= config.objetivo) {
      material.intento("Ya son suficientes. Cuenta otra vez");
      return;
    }
    setOrigen((o) => o.filter((v) => v !== id));
    const siguiente = [...destino, id];
    setDestino(siguiente);
    if (siguiente.length === config.objetivo) {
      setTimeout(() => material.completar(), 450);
    } else {
      material.acierto();
    }
  }

  return (
    <GameShell
      slug="pinza"
      level={level}
      levels={PINZA_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={`Transfiere ${config.objetivo} de una en una`}
      nota={nota}
      acciones={
        <button
          onClick={reiniciar}
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
          reiniciar();
          material.repetir();
        }}
      />

      <div className="mb-2 text-center text-xs font-bold uppercase tracking-wide text-stone-400">
        bandeja destino
      </div>
      <div className="mb-6 flex min-h-24 flex-wrap items-center justify-center gap-2 rounded-[2rem] bg-white/60 p-4 ring-1 ring-black/5">
        <AnimatePresence>
          {destino.map((id) => (
            <motion.span
              key={id}
              layout
              initial={{ opacity: 0, scale: 0.6 }}
              animate={{ opacity: 1, scale: 1 }}
              className="text-3xl"
            >
              {emoji}
            </motion.span>
          ))}
        </AnimatePresence>
        {destino.length === 0 && <span className="text-sm text-stone-400">Vacía</span>}
      </div>

      <div className="mb-2 text-center text-xs font-bold uppercase tracking-wide text-stone-400">
        bandeja de origen — toca de una en una
      </div>
      <div className="flex flex-wrap items-center justify-center gap-2 rounded-[2rem] bg-white/40 p-4">
        {origen.map((id) => (
          <motion.button
            key={id}
            layout
            whileTap={{ scale: 0.85 }}
            onClick={() => tomar(id)}
            aria-label="Tomar una pieza"
            className="text-3xl"
          >
            {emoji}
          </motion.button>
        ))}
        {origen.length === 0 && <span className="py-4 text-sm text-stone-400">Bandeja vacía</span>}
      </div>
    </GameShell>
  );
}
