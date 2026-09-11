"use client";

import { type ReactNode, useCallback, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "./GameShell";
import StarReward from "./StarReward";
import ConfettiOverlay from "./ConfettiOverlay";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";

/**
 * Transferencia por cantidad exacta: una bandeja de origen y una de
 * destino, se toma de a una pieza, y pasarse del objetivo es el error. No
 * hay un límite impuesto por la interfaz: es el mismo material el que "no
 * tiene dónde poner" la pieza de más, igual que en el ambiente real.
 *
 * Patrón compartido por Pinza de transferencia y Los husos. Antes de
 * extraerlo cada uno tenía su propia copia de esta página — ver la nota en
 * docs/MANUAL-TECNICO.md, sección 4.
 */
export default function MaterialTransferir<Level extends { level: number; objetivo: number }>({
  slug,
  levels,
  origenInicial,
  consigna,
  mensajeError,
  renderPieza,
  etiquetaDestinoVacio = "Vacía",
  extra,
}: {
  slug: string;
  levels: Level[];
  /** Cuántas piezas hay en la bandeja de origen al empezar el nivel. */
  origenInicial: (config: Level) => number;
  consigna: (config: Level) => string;
  /** Mensaje del control del error al tomar una pieza de más. */
  mensajeError?: (config: Level) => string;
  /** Cómo se ve una pieza; el mismo dibujo se usa en las dos bandejas. */
  renderPieza: (config: Level) => ReactNode;
  /** Texto cuando la bandeja destino está vacía. */
  etiquetaDestinoVacio?: string;
  /** Contenido extra sobre las bandejas (p. ej. el número del compartimento
   * en Los husos). Recibe una función `completar` para los casos donde el
   * nivel se cierra sin tomar ninguna pieza (el cero de los husos). */
  extra?: (config: Level, destino: number[], completar: () => void) => ReactNode;
}) {
  const material = useMaterial<Level>(slug, levels);
  const { config, level, logrado, nota } = material;

  const [origen, setOrigen] = useState<number[]>([]);
  const [destino, setDestino] = useState<number[]>([]);

  const reiniciar = useCallback(() => {
    setDestino([]);
    setOrigen(Array.from({ length: origenInicial(config) }, (_, i) => i));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [config]);

  useEffect(() => {
    // reiniciar() reconstruye la bandeja de origen según config.origen, que
    // cambia con el nivel — no es una derivación pura para el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    reiniciar();
  }, [reiniciar]);

  function tomar(id: number) {
    if (destino.length >= config.objetivo) {
      material.intento(mensajeError?.(config) ?? "Ya son suficientes. Cuenta otra vez");
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

  const pieza = renderPieza(config);

  return (
    <GameShell
      slug={slug}
      level={level}
      levels={levels.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={consigna(config)}
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

      {extra?.(config, destino, material.completar)}

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
            >
              {pieza}
            </motion.span>
          ))}
        </AnimatePresence>
        {destino.length === 0 && <span className="text-sm text-stone-400">{etiquetaDestinoVacio}</span>}
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
          >
            {pieza}
          </motion.button>
        ))}
        {origen.length === 0 && <span className="py-4 text-sm text-stone-400">Bandeja vacía</span>}
      </div>
    </GameShell>
  );
}
