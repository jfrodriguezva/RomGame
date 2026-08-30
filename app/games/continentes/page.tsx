"use client";

import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  CONTINENTES,
  CONTINENTES_LEVELS,
  type Continente,
  type ContinentesLevel,
} from "@/data/levels/continentes";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

export default function ContinentesPage() {
  const material = useMaterial<ContinentesLevel>("continentes", CONTINENTES_LEVELS);
  const { config, level, logrado, nota } = material;

  const [ronda, setRonda] = useState(0);
  const [objetivo, setObjetivo] = useState<Continente>(CONTINENTES[0]);
  const [animal, setAnimal] = useState("🦁");
  const [opciones, setOpciones] = useState<Continente[]>([]);
  const [resaltado, setResaltado] = useState<string | null>(null);

  const nuevaRonda = useCallback(() => {
    const meta = CONTINENTES[Math.floor(Math.random() * CONTINENTES.length)];
    setObjetivo(meta);
    setAnimal(meta.animales[Math.floor(Math.random() * meta.animales.length)]);
    setResaltado(config.modo === "nombrar" ? meta.id : null);

    const otros = shuffle(CONTINENTES.filter((c) => c.id !== meta.id)).slice(
      0,
      Math.max(1, config.opciones - 1)
    );
    setOpciones(shuffle([meta, ...otros]));

    if (config.modo === "ubicar") hablar(`Busca ${meta.nombre}`);
    else if (config.modo === "animales") hablar("¿Dónde vive este animal?");
  }, [config.modo, config.opciones]);

  useEffect(() => {
    setRonda(0);
    nuevaRonda();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function responder(id: string) {
    if (id === objetivo.id) {
      material.acierto();
      hablar(`${objetivo.nombre}. ${objetivo.dato}`);
      const siguiente = ronda + 1;
      if (siguiente >= config.rondas) setTimeout(() => material.completar(), 900);
      else {
        setRonda(siguiente);
        setTimeout(nuevaRonda, 1600);
      }
    } else {
      material.intento();
    }
  }

  const consignas: Record<ContinentesLevel["modo"], string> = {
    ubicar: `Toca ${objetivo.nombre} en el mapa`,
    nombrar: "¿Cómo se llama el continente iluminado?",
    animales: "¿En qué continente vive?",
  };

  return (
    <GameShell
      slug="continentes"
      level={level}
      levels={CONTINENTES_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={consignas[config.modo]}
      nota={nota}
      ancho="max-w-lg"
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

      {config.modo === "animales" && (
        <div className="mb-4 flex justify-center">
          <span className="flex h-24 w-24 items-center justify-center rounded-full bg-white/85 text-5xl shadow-sm ring-1 ring-black/5">
            {animal}
          </span>
        </div>
      )}

      <div className="mb-5 rounded-[2rem] bg-[#d6eae5] p-3 ring-1 ring-black/5">
        <svg viewBox="0 0 200 105" className="h-auto w-full" role="img" aria-label="Mapa del mundo">
          {CONTINENTES.map((c) => {
            const activo = resaltado === c.id;
            const esClicable = config.modo !== "nombrar";
            return (
              <path
                key={c.id}
                d={c.path}
                fill={config.conColor ? c.color : "#b7cfc9"}
                stroke={activo ? "#2c5c53" : "#ffffff"}
                strokeWidth={activo ? 2.5 : 1}
                opacity={resaltado && !activo ? 0.45 : 1}
                onPointerDown={() => esClicable && responder(c.id)}
                className={esClicable ? "cursor-pointer" : ""}
              />
            );
          })}
        </svg>
      </div>

      {config.modo !== "ubicar" && (
        <div className="grid grid-cols-2 gap-3">
          {opciones.map((o) => (
            <motion.button
              key={o.id}
              whileTap={{ scale: 0.94 }}
              onClick={() => responder(o.id)}
              className="rounded-3xl bg-white/90 px-3 py-4 text-sm font-extrabold text-stone-600 shadow-sm ring-1 ring-black/5"
            >
              {o.nombre}
            </motion.button>
          ))}
        </div>
      )}
    </GameShell>
  );
}
