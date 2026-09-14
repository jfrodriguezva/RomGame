"use client";

import { useEffect, useRef, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { QUE_FALTA_LEVELS, OBJETOS_POOL, MEMORIZAR_MS } from "@/data/levels/que-falta";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { shuffle } from "@/lib/shuffle";

const RONDAS_POR_NIVEL = 5;

/**
 * El juego de Kim: memorizar una bandeja de objetos, uno desaparece, decir
 * cuál era. La dificultad sube solo en cantidad de objetos, nunca en el
 * tiempo para memorizar — igual que el resto de la app, se aísla una sola
 * variable por vez.
 */
export default function QueFaltaPage() {
  const material = useMaterial<(typeof QUE_FALTA_LEVELS)[number]>("que-falta", QUE_FALTA_LEVELS);
  const { config, level, logrado, nota } = material;

  const [ronda, setRonda] = useState(1);
  const [fase, setFase] = useState<"memorizar" | "responder">("memorizar");
  const [bandeja, setBandeja] = useState<string[]>([]);
  const [faltanteIdx, setFaltanteIdx] = useState<number | null>(null);
  const [opciones, setOpciones] = useState<string[]>([]);

  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  function nuevaRonda(cantidad: number) {
    const nuevaBandeja = shuffle(OBJETOS_POOL).slice(0, cantidad);
    setBandeja(nuevaBandeja);
    setFase("memorizar");
    setFaltanteIdx(null);
    if (timerRef.current) clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => {
      const idx = Math.floor(Math.random() * nuevaBandeja.length);
      const resto = OBJETOS_POOL.filter((o) => !nuevaBandeja.includes(o));
      const distractores = shuffle(resto).slice(0, 3);
      setFaltanteIdx(idx);
      setOpciones(shuffle([nuevaBandeja[idx], ...distractores]));
      setFase("responder");
    }, MEMORIZAR_MS);
  }

  useEffect(() => {
    // Reinicia la ronda y arma la primera bandeja del nivel: no es una
    // derivación pura (usa Math.random y un temporizador).
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setRonda(1);
    nuevaRonda(config.cantidad);
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function responder(objeto: string) {
    if (fase !== "responder" || faltanteIdx === null) return;
    if (objeto !== bandeja[faltanteIdx]) {
      material.intento("Ese no era. Mira otra vez la bandeja");
      return;
    }
    if (ronda >= RONDAS_POR_NIVEL) {
      material.acierto();
      setTimeout(() => material.completar(), 400);
    } else {
      material.acierto();
      const siguienteRonda = ronda + 1;
      setRonda(siguienteRonda);
      nuevaRonda(config.cantidad);
    }
  }

  return (
    <GameShell
      slug="que-falta"
      level={level}
      levels={QUE_FALTA_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={fase === "memorizar" ? "Memoriza la bandeja" : "¿Qué falta?"}
      hablarConsigna
      nota={nota}
    >
      <ConfettiOverlay show={logrado} slug="que-falta" />
      <StarReward
        slug="que-falta"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={material.repetir}
      />

      <p className="mb-3 text-center text-xs font-bold text-stone-400">
        ronda {ronda} / {RONDAS_POR_NIVEL}
      </p>

      <div className="mb-6 flex flex-wrap justify-center gap-3 rounded-[2rem] bg-white/70 p-5 ring-1 ring-black/5">
        {bandeja.map((objeto, i) => (
          <div
            key={i}
            className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white text-3xl shadow-sm ring-1 ring-black/5"
          >
            <AnimatePresence mode="wait">
              {i === faltanteIdx ? (
                <motion.span
                  key="vacio"
                  initial={{ opacity: 1 }}
                  animate={{ opacity: 1 }}
                  className="text-lg text-stone-300"
                >
                  ?
                </motion.span>
              ) : (
                <motion.span key={objeto} initial={{ scale: 0.8 }} animate={{ scale: 1 }}>
                  {objeto}
                </motion.span>
              )}
            </AnimatePresence>
          </div>
        ))}
      </div>

      {fase === "responder" && (
        <div className="flex flex-wrap justify-center gap-3">
          {opciones.map((o) => (
            <motion.button
              key={o}
              whileTap={{ scale: 0.9 }}
              onClick={() => responder(o)}
              aria-label={`Elegir ${o}`}
              className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white text-3xl shadow ring-1 ring-black/5"
            >
              {o}
            </motion.button>
          ))}
        </div>
      )}
    </GameShell>
  );
}
