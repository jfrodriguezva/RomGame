"use client";

import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { SONIDOS_LEVELS, type SonidosLevel } from "@/data/levels/sonidos-iniciales";
import { PALABRAS, type Palabra } from "@/data/palabras";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar, hablarFonema, fonemaDe } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

function sonidoDe(p: Palabra, posicion: SonidosLevel["posicion"]) {
  const letra = posicion === "inicial" ? p.palabra[0] : p.palabra[p.palabra.length - 1];
  return letra.toUpperCase();
}

export default function SonidosInicialesPage() {
  const material = useMaterial<SonidosLevel>("sonidos-iniciales", SONIDOS_LEVELS);
  const { config, level, logrado, nota } = material;

  const [ronda, setRonda] = useState(0);
  const [objetivo, setObjetivo] = useState<Palabra | null>(null);
  const [opciones, setOpciones] = useState<Palabra[]>([]);

  const nuevaRonda = useCallback(() => {
    const meta = PALABRAS[Math.floor(Math.random() * PALABRAS.length)];
    const sonido = sonidoDe(meta, config.posicion);
    // Los distractores nunca comparten el sonido buscado: la respuesta es una.
    const otras = shuffle(
      PALABRAS.filter((p) => p.palabra !== meta.palabra && sonidoDe(p, config.posicion) !== sonido)
    ).slice(0, Math.max(1, config.opciones - 1));

    setObjetivo(meta);
    setOpciones(shuffle([meta, ...otras]));
    setTimeout(() => hablarFonema(sonido), 250);
  }, [config.opciones, config.posicion]);

  useEffect(() => {
    setRonda(0);
    nuevaRonda();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function elegir(p: Palabra) {
    if (!objetivo) return;
    const sonido = sonidoDe(objetivo, config.posicion);
    if (sonidoDe(p, config.posicion) === sonido) {
      material.acierto();
      hablar(p.palabra);
      const siguiente = ronda + 1;
      if (siguiente >= config.rondas) setTimeout(() => material.completar(), 700);
      else {
        setRonda(siguiente);
        setTimeout(nuevaRonda, 1000);
      }
    } else {
      material.intento();
      hablar(p.palabra);
    }
  }

  const sonido = objetivo ? sonidoDe(objetivo, config.posicion) : "";

  return (
    <GameShell
      slug="sonidos-iniciales"
      level={level}
      levels={SONIDOS_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={
        config.posicion === "inicial"
          ? "Veo veo… algo que empieza con este sonido"
          : "Veo veo… algo que termina con este sonido"
      }
      nota={nota}
      acciones={
        <button
          onClick={() => hablarFonema(sonido)}
          className="rounded-2xl bg-white px-5 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
        >
          Repetir el sonido
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
        onRepeat={material.repetir}
      />

      <div className="mb-4 flex justify-center gap-1.5">
        {Array.from({ length: config.rondas }).map((_, i) => (
          <span
            key={i}
            className={`h-2.5 w-2.5 rounded-full ${i < ronda ? "bg-amber-400" : "bg-black/10"}`}
          />
        ))}
      </div>

      <button
        onClick={() => hablarFonema(sonido)}
        className="mx-auto mb-6 flex h-28 w-28 flex-col items-center justify-center rounded-full bg-white/90 shadow-sm ring-1 ring-black/5 active:scale-95"
      >
        <span className="text-3xl">🔊</span>
        <span className="text-xl font-extrabold text-[#46578a]">
          {config.mostrarLetra ? sonido : `“${fonemaDe(sonido)}”`}
        </span>
      </button>

      <div className={`grid gap-3 ${config.opciones <= 4 ? "grid-cols-2" : "grid-cols-3"}`}>
        {opciones.map((o) => (
          <motion.button
            key={o.palabra}
            whileTap={{ scale: 0.92 }}
            onClick={() => elegir(o)}
            className="flex flex-col items-center gap-1 rounded-3xl bg-white/90 py-4 shadow-sm ring-1 ring-black/5"
          >
            <span className="text-4xl">{o.emoji}</span>
          </motion.button>
        ))}
      </div>
    </GameShell>
  );
}
