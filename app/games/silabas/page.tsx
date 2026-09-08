"use client";

import { useCallback, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  CANASTAS_SILABAS,
  SILABAS_LEVELS,
  type PalabraSilabas,
  type SilabasLevel,
  palabrasPara,
} from "@/data/levels/silabas";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

export default function SilabasPage() {
  const material = useMaterial<SilabasLevel>("silabas", SILABAS_LEVELS);
  const { config, level, logrado, nota } = material;

  const [pendientes, setPendientes] = useState<PalabraSilabas[]>([]);
  const [conteo, setConteo] = useState<Record<number, PalabraSilabas[]>>({});

  const canastas = CANASTAS_SILABAS[config.criterio];

  const preparar = useCallback(() => {
    const disponibles = palabrasPara(config.criterio);
    setPendientes(shuffle(disponibles).slice(0, Math.min(config.cantidad, disponibles.length)));
    setConteo(Object.fromEntries(canastas.map((n) => [n, []])));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [config.cantidad, config.criterio]);

  useEffect(() => {
    preparar();
  }, [preparar]);

  function clasificar(n: number) {
    const actual = pendientes[0];
    if (!actual) return;

    if (actual.silabas !== n) {
      material.intento(`${actual.nombre}: cuenta otra vez`);
      return;
    }

    const restantes = pendientes.slice(1);
    setPendientes(restantes);
    setConteo((c) => ({ ...c, [n]: [...c[n], actual] }));
    hablar(actual.nombre);

    if (restantes.length === 0) setTimeout(() => material.completar(), 450);
    else material.acierto();
  }

  const actual = pendientes[0];
  const columnas = canastas.length === 3 ? "grid-cols-3" : "grid-cols-2";

  return (
    <GameShell
      slug="silabas"
      level={level}
      levels={SILABAS_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna="¿Cuántas sílabas tiene?"
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
        <button
          type="button"
          onClick={() => actual && hablar(actual.nombre)}
          aria-label="Escuchar la palabra"
          className="flex h-40 w-40 items-center justify-center rounded-[2rem] bg-white/85 shadow-sm ring-1 ring-black/5"
        >
          <AnimatePresence mode="wait">
            {actual && (
              <motion.span
                key={actual.nombre + pendientes.length}
                initial={{ scale: 0.7, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.6, opacity: 0 }}
                className="text-7xl"
              >
                {actual.emoji}
              </motion.span>
            )}
          </AnimatePresence>
        </button>
      </div>

      <div className={`grid gap-3 ${columnas}`}>
        {canastas.map((n) => (
          <motion.button
            key={n}
            whileTap={{ scale: 0.95 }}
            onClick={() => clasificar(n)}
            className="flex min-h-32 flex-col items-center gap-1 rounded-[2rem] bg-[#f1e4d0] p-3 ring-1 ring-black/5"
          >
            <span className="flex gap-0.5 text-lg text-[#8a6a44]">{"●".repeat(n)}</span>
            <span className="text-xs font-extrabold text-[#6b5233]">
              {n === 1 ? "1 sílaba" : `${n} sílabas`}
            </span>
            <span className="mt-1 flex flex-wrap justify-center gap-0.5 text-lg">
              {conteo[n]?.map((s, i) => (
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
