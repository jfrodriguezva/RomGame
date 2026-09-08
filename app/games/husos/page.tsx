"use client";

import { useCallback, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { HUSOS_LEVELS, type HusosLevel } from "@/data/levels/husos";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";

const POOL = 9;

export default function HusosPage() {
  const material = useMaterial<HusosLevel>("husos", HUSOS_LEVELS);
  const { config, level, logrado, nota } = material;

  const [origen, setOrigen] = useState<number[]>([]);
  const [destino, setDestino] = useState<number[]>([]);

  const reiniciar = useCallback(() => {
    setDestino([]);
    setOrigen(Array.from({ length: POOL }, (_, i) => i));
  }, []);

  useEffect(() => {
    reiniciar();
  }, [reiniciar, level]);

  function tomar(id: number) {
    if (destino.length >= config.objetivo) {
      material.intento(
        config.objetivo === 0 ? "El 0 no lleva ningún huso" : "Ya son suficientes. Cuenta otra vez"
      );
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

  function confirmarVacio() {
    if (config.objetivo !== 0 || destino.length !== 0) return;
    material.completar();
  }

  return (
    <GameShell
      slug="husos"
      level={level}
      levels={HUSOS_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={`Pon los husos del ${config.objetivo}`}
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

      <div className="mb-6 flex flex-col items-center gap-2">
        <span className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white text-3xl font-extrabold text-stone-600 ring-1 ring-black/5">
          {config.objetivo}
        </span>
        <div className="flex min-h-20 min-w-40 flex-wrap items-center justify-center gap-1.5 rounded-[2rem] bg-white/60 p-4 ring-1 ring-black/5">
          <AnimatePresence>
            {destino.map((id) => (
              <motion.span
                key={id}
                layout
                initial={{ opacity: 0, scale: 0.6, rotate: -8 }}
                animate={{ opacity: 1, scale: 1, rotate: 0 }}
                className="text-2xl"
              >
                🥢
              </motion.span>
            ))}
          </AnimatePresence>
          {destino.length === 0 && (
            <span className="text-sm text-stone-400">
              {config.objetivo === 0 ? "Vacío a propósito" : "Vacío"}
            </span>
          )}
        </div>
        {config.objetivo === 0 && destino.length === 0 && (
          <button
            onClick={confirmarVacio}
            className="rounded-2xl bg-emerald-100 px-4 py-2 text-sm font-bold text-emerald-700 active:scale-95"
          >
            Listo, no lleva ninguno
          </button>
        )}
      </div>

      <div className="mb-2 text-center text-xs font-bold uppercase tracking-wide text-stone-400">
        toma los husos de uno en uno
      </div>
      <div className="flex flex-wrap items-center justify-center gap-2 rounded-[2rem] bg-white/40 p-4">
        {origen.map((id) => (
          <motion.button
            key={id}
            layout
            whileTap={{ scale: 0.85 }}
            onClick={() => tomar(id)}
            aria-label="Tomar un huso"
            className="text-2xl"
          >
            🥢
          </motion.button>
        ))}
      </div>
    </GameShell>
  );
}
