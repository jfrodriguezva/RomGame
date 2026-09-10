"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "./GameShell";
import StarReward from "./StarReward";
import ConfettiOverlay from "./ConfettiOverlay";
import { type OrdenarLevel } from "@/data/levels/ordenar";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { shuffle } from "@/lib/shuffle";

export interface PiezaSerie {
  /** 1 es la más pequeña. */
  valor: number;
}

/**
 * Material de seriación.
 *
 * El niño toma una pieza del canasto y la coloca en la serie. Si no es la que
 * toca, la pieza regresa sola al canasto: ese es el control del error del
 * material real, donde la torre simplemente no se sostiene. No hay vidas, ni
 * intentos contados, ni forma de "perder".
 */
export default function MaterialOrdenar({
  slug,
  levels,
  consigna,
  consignaInvertida,
  render,
  orientacion = "vertical",
}: {
  slug: string;
  levels: OrdenarLevel[];
  consigna: string;
  consignaInvertida: string;
  /** Dibuja una pieza según su valor (1 a 10). */
  render: (valor: number, opciones: { colocada: boolean; pista: boolean }) => React.ReactNode;
  orientacion?: "vertical" | "horizontal";
}) {
  const material = useMaterial<OrdenarLevel>(slug, levels);
  const { config, level, logrado, nota } = material;

  const serie = useMemo(() => {
    const valores = Array.from({ length: config.cantidad }, (_, i) => config.desde + i + 1);
    return config.invertido ? valores : [...valores].reverse();
  }, [config.cantidad, config.desde, config.invertido]);

  const [canasto, setCanasto] = useState<number[]>([]);
  const [colocadas, setColocadas] = useState<number[]>([]);

  const reiniciar = useCallback(() => {
    setColocadas([]);
    setCanasto(shuffle(serie));
  }, [serie]);

  useEffect(() => {
    reiniciar();
  }, [reiniciar]);

  function tomar(valor: number) {
    const esperado = serie[colocadas.length];
    if (valor !== esperado) {
      material.intento("Esa todavía no. Busca otra");
      return;
    }
    const siguiente = [...colocadas, valor];
    setColocadas(siguiente);
    setCanasto((c) => c.filter((v) => v !== valor));
    if (siguiente.length === serie.length) {
      setTimeout(() => material.completar(), 450);
    } else {
      material.acierto();
    }
  }

  return (
    <GameShell
      slug={slug}
      level={level}
      levels={levels.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={config.invertido ? consignaInvertida : consigna}
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
        slug={slug}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={() => {
          reiniciar();
          material.repetir();
        }}
      />

      {/* La serie que se va construyendo */}
      <div
        className={`mb-6 flex min-h-52 items-center justify-center rounded-[2rem] bg-white/60 p-4 ring-1 ring-black/5 ${
          orientacion === "vertical" ? "flex-col justify-end" : "flex-col gap-1"
        }`}
      >
        <AnimatePresence>
          {colocadas.map((valor) => (
            <motion.div
              key={valor}
              layout
              initial={{ opacity: 0, scale: 0.8, y: -12 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              className="flex w-full justify-center"
            >
              {render(valor, { colocada: true, pista: config.pista })}
            </motion.div>
          ))}
        </AnimatePresence>
        {colocadas.length === 0 && (
          <span className="text-sm text-stone-400">Empieza aquí</span>
        )}
      </div>

      {/* El canasto */}
      <div className="flex flex-wrap items-end justify-center gap-3 rounded-[2rem] bg-white/40 p-4">
        {canasto.map((valor) => (
          <motion.button
            key={valor}
            layout
            whileTap={{ scale: 0.9 }}
            onClick={() => tomar(valor)}
            aria-label={`Pieza ${valor}`}
          >
            {render(valor, { colocada: false, pista: config.pista })}
          </motion.button>
        ))}
        {canasto.length === 0 && (
          <span className="py-4 text-sm text-stone-400">Canasto vacío</span>
        )}
      </div>
    </GameShell>
  );
}
