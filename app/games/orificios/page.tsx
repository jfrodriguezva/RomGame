"use client";

import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { ORIFICIOS_LEVELS, FORMA_PATH, NOMBRE_FORMA, type FormaId } from "@/data/levels/orificios";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { shuffle } from "@/lib/shuffle";

/**
 * Encaja la figura en su agujero exacto — no en el que se le parece, en el
 * que es igual. El único componente que dibuja las piezas como recorte
 * (fondo oscuro con el hueco de la forma) en vez de reutilizar
 * MaterialClasificar: aquí el "acierto" tiene que verse como que la pieza
 * encaja de verdad, no como elegir una canasta con etiqueta.
 */
export default function OrificiosPage() {
  const material = useMaterial<(typeof ORIFICIOS_LEVELS)[number]>("orificios", ORIFICIOS_LEVELS);
  const { config, level, logrado, nota } = material;

  const [cola, setCola] = useState<FormaId[]>([]);
  const [agujeros, setAgujeros] = useState<FormaId[]>([]);

  function agujerosPara(pieza: FormaId) {
    const distractores = config.activos.filter((f) => f !== pieza);
    const elegidos = shuffle(distractores).slice(0, config.opciones - 1);
    return shuffle([pieza, ...elegidos]);
  }

  useEffect(() => {
    const nuevaCola = shuffle(config.activos);
    // Arma la ronda al azar para el nivel: no es una derivación pura que
    // se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setCola(nuevaCola);
    setAgujeros(agujerosPara(nuevaCola[0]));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const pieza = cola[0];

  function encajar(forma: FormaId) {
    if (!pieza) return;
    if (forma !== pieza) {
      material.intento(`Esa no encaja. Busca ${NOMBRE_FORMA[pieza].toLowerCase()}`);
      return;
    }
    const restante = cola.slice(1);
    setCola(restante);
    if (restante.length === 0) {
      setTimeout(() => material.completar(), 450);
    } else {
      material.acierto();
      setAgujeros(agujerosPara(restante[0]));
    }
  }

  return (
    <GameShell
      slug="orificios"
      level={level}
      levels={ORIFICIOS_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna="Encaja la figura en su agujero exacto"
      nota={nota}
    >
      <ConfettiOverlay show={logrado} slug="orificios" />
      <StarReward
        slug="orificios"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={material.repetir}
      />

      <div className="mb-6 flex flex-col items-center">
        <span className="mb-2 text-xs font-bold uppercase tracking-wide text-stone-400">
          quedan {cola.length}
        </span>
        <div className="flex h-32 w-32 items-center justify-center rounded-[2rem] bg-white/85 shadow-sm ring-1 ring-black/5">
          <AnimatePresence mode="wait">
            {pieza && (
              <motion.svg
                key={pieza}
                viewBox="0 0 100 100"
                className="h-20 w-20"
                initial={{ scale: 0.7, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.5, opacity: 0 }}
              >
                <path d={FORMA_PATH[pieza]} fill="#e0956b" stroke="#a5602f" strokeWidth={3} />
              </motion.svg>
            )}
          </AnimatePresence>
        </div>
      </div>

      <div
        className="mx-auto grid max-w-md gap-3"
        style={{ gridTemplateColumns: `repeat(${Math.min(3, agujeros.length)}, minmax(0, 1fr))` }}
      >
        {agujeros.map((forma) => (
          <motion.button
            key={forma}
            whileTap={{ scale: 0.9 }}
            onClick={() => encajar(forma)}
            aria-label={NOMBRE_FORMA[forma]}
            className="flex aspect-square items-center justify-center rounded-3xl bg-[#3a332c] p-4 shadow-inner"
          >
            <svg viewBox="0 0 100 100" className="h-full w-full">
              <path d={FORMA_PATH[forma]} fill="#1f1b16" stroke="#0f0d0a" strokeWidth={2} />
            </svg>
          </motion.button>
        ))}
      </div>
    </GameShell>
  );
}
