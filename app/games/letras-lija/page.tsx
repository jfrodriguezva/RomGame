"use client";

import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import TrazoGuiado from "@/components/TrazoGuiado";
import { LETRAS_LIJA_LEVELS, type LetrasLijaLevel } from "@/data/levels/letras-lija";
import { GLIFOS } from "@/data/trazos-letras";
import { ABECEDARIO } from "@/data/levels/abecedario";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar, hablarFonema } from "@/lib/speech";

function palabraDe(letra: string) {
  return ABECEDARIO.find((l) => l.letter === letra);
}

export default function LetrasLijaPage() {
  const material = useMaterial<LetrasLijaLevel>("letras-lija", LETRAS_LIJA_LEVELS);
  const { config, level, logrado, nota } = material;

  const [ronda, setRonda] = useState(0);
  const [letra, setLetra] = useState("A");
  const [celebrando, setCelebrando] = useState(false);

  const nuevaLetra = useCallback(() => {
    const pool = config.pool.filter((l) => GLIFOS[l]);
    const elegida = pool[Math.floor(Math.random() * pool.length)] ?? "A";
    setLetra(elegida);
    setCelebrando(false);
    hablarFonema(elegida);
  }, [config.pool]);

  useEffect(() => {
    // nuevaLetra() elige al azar y habla el fonema: no es una derivación
    // pura que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setRonda(0);
    nuevaLetra();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function completarTrazo() {
    const palabra = palabraDe(letra);
    setCelebrando(true);
    material.acierto("Lo trazaste completo");
    hablar(palabra ? `${palabra.word}` : letra);

    const siguiente = ronda + 1;
    if (siguiente >= config.rondas) {
      setTimeout(() => material.completar(), 1200);
    } else {
      setRonda(siguiente);
      setTimeout(nuevaLetra, 1400);
    }
  }

  const palabra = palabraDe(letra);

  return (
    <GameShell
      slug="letras-lija"
      level={level}
      levels={LETRAS_LIJA_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna="Recorre la letra con tu dedo"
      nota={nota}
      acciones={
        <button
          onClick={() => hablarFonema(letra)}
          className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
        >
          Escuchar el sonido
        </button>
      }
    >
      <ConfettiOverlay show={logrado} slug="letras-lija" />
      <StarReward
        slug="letras-lija"
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

      <div className="relative mx-auto aspect-square w-full max-w-sm rounded-[2rem] bg-[#dde3f0] p-4 shadow-inner">
        <TrazoGuiado
          key={`${level}-${ronda}-${letra}`}
          strokes={GLIFOS[letra]?.strokes ?? []}
          tolerancia={config.tolerancia}
          mostrarPuntos={config.mostrarPuntos}
          onCompleto={completarTrazo}
        />

        {celebrando && palabra && (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="absolute inset-x-0 bottom-4 flex flex-col items-center"
          >
            <span className="text-4xl">{palabra.emoji}</span>
            <span className="text-lg font-extrabold text-[#46578a]">
              {letra} de {palabra.word}
            </span>
          </motion.div>
        )}
      </div>

      <p className="mt-4 text-center text-sm text-stone-400">
        Empieza en el punto que late y sigue el camino sin levantar el dedo.
      </p>
    </GameShell>
  );
}
