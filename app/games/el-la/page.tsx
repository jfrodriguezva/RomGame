"use client";

import { useCallback, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  EL_LA_LEVELS,
  PALABRAS_GENERO,
  type PalabraGenero,
  type ElLaLevel,
} from "@/data/levels/el-la";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

const CANASTAS: Array<{ clave: "el" | "la"; nombre: string }> = [
  { clave: "el", nombre: "El" },
  { clave: "la", nombre: "La" },
];

export default function ElLaPage() {
  const material = useMaterial<ElLaLevel>("el-la", EL_LA_LEVELS);
  const { config, level, logrado, nota } = material;

  const [pendientes, setPendientes] = useState<PalabraGenero[]>([]);
  const [conteo, setConteo] = useState<Record<string, PalabraGenero[]>>({ el: [], la: [] });

  const preparar = useCallback(() => {
    setPendientes(
      shuffle(PALABRAS_GENERO).slice(0, Math.min(config.cantidad, PALABRAS_GENERO.length))
    );
    setConteo({ el: [], la: [] });
  }, [config.cantidad]);

  useEffect(() => {
    preparar();
  }, [preparar]);

  function clasificar(clave: "el" | "la") {
    const actual = pendientes[0];
    if (!actual) return;

    if (actual.articulo !== clave) {
      material.intento(`Se dice "${actual.articulo} ${actual.nombre}"`);
      return;
    }

    const restantes = pendientes.slice(1);
    setPendientes(restantes);
    setConteo((c) => ({ ...c, [clave]: [...c[clave], actual] }));
    hablar(`${actual.articulo} ${actual.nombre}`);

    if (restantes.length === 0) setTimeout(() => material.completar(), 450);
    else material.acierto();
  }

  const actual = pendientes[0];

  return (
    <GameShell
      slug="el-la"
      level={level}
      levels={EL_LA_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna="¿Se dice el o la?"
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

      <div className="grid grid-cols-2 gap-3">
        {CANASTAS.map((c) => (
          <motion.button
            key={c.clave}
            whileTap={{ scale: 0.95 }}
            onClick={() => clasificar(c.clave)}
            className="flex min-h-32 flex-col items-center gap-1 rounded-[2rem] bg-[#f1e4d0] p-3 ring-1 ring-black/5"
          >
            <span className="text-2xl font-extrabold text-[#6b5233]">{c.nombre}</span>
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
