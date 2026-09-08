"use client";

import { useCallback, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  PARES_IMPARES_LEVELS,
  type CantidadParidad,
  type ParesImparesLevel,
  cantidadesPara,
} from "@/data/levels/pares-impares";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

const CANASTAS: Array<{ clave: "par" | "impar"; nombre: string }> = [
  { clave: "par", nombre: "Par" },
  { clave: "impar", nombre: "Impar" },
];

/** Dos columnas a propósito: si sobra un punto solo, la cantidad es impar. */
function Puntos({ n }: { n: number }) {
  return (
    <div className="grid grid-cols-2 gap-1.5">
      {Array.from({ length: n }).map((_, i) => (
        <span key={i} className="h-3.5 w-3.5 rounded-full bg-[#8a6a44]" />
      ))}
    </div>
  );
}

export default function ParesImparesPage() {
  const material = useMaterial<ParesImparesLevel>("pares-impares", PARES_IMPARES_LEVELS);
  const { config, level, logrado, nota } = material;

  const [pendientes, setPendientes] = useState<CantidadParidad[]>([]);
  const [conteo, setConteo] = useState<Record<string, CantidadParidad[]>>({ par: [], impar: [] });

  const preparar = useCallback(() => {
    const disponibles = cantidadesPara(config.maxNumero);
    setPendientes(shuffle(disponibles).slice(0, Math.min(config.cantidad, disponibles.length)));
    setConteo({ par: [], impar: [] });
  }, [config.cantidad, config.maxNumero]);

  useEffect(() => {
    preparar();
  }, [preparar]);

  function clasificar(clave: "par" | "impar") {
    const actual = pendientes[0];
    if (!actual) return;

    if (actual.paridad !== clave) {
      material.intento(`${actual.cantidad} es ${actual.paridad}`);
      return;
    }

    const restantes = pendientes.slice(1);
    setPendientes(restantes);
    setConteo((c) => ({ ...c, [clave]: [...c[clave], actual] }));
    hablar(`${actual.cantidad}, ${actual.paridad}`);

    if (restantes.length === 0) setTimeout(() => material.completar(), 450);
    else material.acierto();
  }

  const actual = pendientes[0];

  return (
    <GameShell
      slug="pares-impares"
      level={level}
      levels={PARES_IMPARES_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna="¿Es par o impar?"
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
        <div className="flex h-32 w-32 items-center justify-center rounded-[2rem] bg-white/85 shadow-sm ring-1 ring-black/5">
          <AnimatePresence mode="wait">
            {actual && (
              <motion.div
                key={actual.cantidad}
                initial={{ scale: 0.7, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.6, opacity: 0 }}
              >
                <Puntos n={actual.cantidad} />
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3">
        {CANASTAS.map((c) => (
          <motion.button
            key={c.clave}
            whileTap={{ scale: 0.95 }}
            onClick={() => clasificar(c.clave)}
            className="flex min-h-32 flex-col items-center gap-1 rounded-[2rem] bg-[#f1e4d0] p-3 ring-1 ring-black/5"
          >
            <span className="text-lg font-extrabold text-[#6b5233]">{c.nombre}</span>
            <span className="mt-1 flex flex-wrap justify-center gap-1 text-sm font-bold text-[#8a6a44]">
              {conteo[c.clave]?.map((s, i) => (
                <motion.span key={i} initial={{ scale: 0 }} animate={{ scale: 1 }}>
                  {s.cantidad}
                </motion.span>
              ))}
            </span>
          </motion.button>
        ))}
      </div>
    </GameShell>
  );
}
