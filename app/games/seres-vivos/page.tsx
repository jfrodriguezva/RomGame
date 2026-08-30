"use client";

import { useCallback, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  CANASTAS,
  SERES_LEVELS,
  type SerDef,
  type SeresLevel,
  canastaDe,
  seresPara,
} from "@/data/levels/seres-vivos";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

export default function SeresVivosPage() {
  const material = useMaterial<SeresLevel>("seres-vivos", SERES_LEVELS);
  const { config, level, logrado, nota } = material;

  const [pendientes, setPendientes] = useState<SerDef[]>([]);
  const [conteo, setConteo] = useState<Record<string, SerDef[]>>({});

  const canastas = CANASTAS[config.criterio];

  const preparar = useCallback(() => {
    const disponibles = seresPara(config.criterio);
    setPendientes(shuffle(disponibles).slice(0, Math.min(config.cantidad, disponibles.length)));
    setConteo(Object.fromEntries(CANASTAS[config.criterio].map((c) => [c.clave, []])));
  }, [config.cantidad, config.criterio]);

  useEffect(() => {
    preparar();
  }, [preparar]);

  function clasificar(clave: string) {
    const actual = pendientes[0];
    if (!actual) return;

    if (canastaDe(actual, config.criterio) !== clave) {
      material.intento(`El ${actual.nombre} va en otra canasta`);
      return;
    }

    const restantes = pendientes.slice(1);
    setPendientes(restantes);
    setConteo((c) => ({ ...c, [clave]: [...c[clave], actual] }));
    hablar(actual.nombre);

    if (restantes.length === 0) setTimeout(() => material.completar(), 450);
    else material.acierto();
  }

  const consignas: Record<SeresLevel["criterio"], string> = {
    vivo: "¿Está vivo o no está vivo?",
    reino: "¿Es un animal o una planta?",
    medio: "¿Camina, nada o vuela?",
  };

  const actual = pendientes[0];

  return (
    <GameShell
      slug="seres-vivos"
      level={level}
      levels={SERES_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={consignas[config.criterio]}
      nota={nota}
      acciones={
        <button
          onClick={preparar}
          className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
        >
          Volver a empezar
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

      <div className="mb-6 flex flex-col items-center">
        <span className="mb-2 text-xs font-bold uppercase tracking-wide text-stone-400">
          quedan {pendientes.length}
        </span>
        <div className="flex h-40 w-40 items-center justify-center rounded-[2rem] bg-white/85 shadow-sm ring-1 ring-black/5">
          <AnimatePresence mode="wait">
            {actual && (
              <motion.span
                key={actual.emoji + pendientes.length}
                initial={{ scale: 0.7, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.6, opacity: 0 }}
                className="text-7xl"
              >
                {actual.emoji}
              </motion.span>
            )}
          </AnimatePresence>
        </div>
      </div>

      <div className={`grid gap-3 ${canastas.length === 3 ? "grid-cols-3" : "grid-cols-2"}`}>
        {canastas.map((c) => (
          <motion.button
            key={c.clave}
            whileTap={{ scale: 0.95 }}
            onClick={() => clasificar(c.clave)}
            className="flex min-h-32 flex-col items-center gap-1 rounded-[2rem] bg-[#d6eae5] p-3 ring-1 ring-black/5"
          >
            <span className="text-2xl">{c.emoji}</span>
            <span className="text-xs font-extrabold text-[#31665c]">{c.nombre}</span>
            <span className="mt-1 flex flex-wrap justify-center gap-0.5 text-lg">
              {conteo[c.clave]?.map((s, i) => (
                <motion.span key={i} initial={{ scale: 0 }} animate={{ scale: 1 }}>
                  {s.emoji}
                </motion.span>
              ))}
            </span>
          </motion.button>
        ))}
      </div>
    </GameShell>
  );
}
