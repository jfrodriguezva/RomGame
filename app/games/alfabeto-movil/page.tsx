"use client";

import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { ALFABETO_LEVELS, type AlfabetoLevel } from "@/data/levels/alfabeto-movil";
import { PALABRAS, type Palabra } from "@/data/palabras";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar, hablarFonema } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

const ABC = "ABCDEFGHIJLMNOPRSTUVYZ".split("");

interface Ficha {
  id: string;
  letra: string;
}

export default function AlfabetoMovilPage() {
  const material = useMaterial<AlfabetoLevel>("alfabeto-movil", ALFABETO_LEVELS);
  const { config, level, logrado, nota } = material;

  const [ronda, setRonda] = useState(0);
  const [palabra, setPalabra] = useState<Palabra | null>(null);
  const [escrito, setEscrito] = useState<string[]>([]);
  const [canasto, setCanasto] = useState<Ficha[]>([]);

  const nuevaRonda = useCallback(() => {
    const candidatas = PALABRAS.filter((p) => p.largo <= config.largo);
    const elegida = candidatas[Math.floor(Math.random() * candidatas.length)];
    const letras = elegida.palabra.toUpperCase().split("");

    const extras = Array.from({ length: config.extras }, () =>
      ABC[Math.floor(Math.random() * ABC.length)]
    );
    setPalabra(elegida);
    setEscrito([]);
    setCanasto(
      shuffle([...letras, ...extras]).map((letra, i) => ({ id: `${letra}-${i}`, letra }))
    );
    setTimeout(() => hablar(elegida.palabra), 220);
  }, [config.extras, config.largo]);

  useEffect(() => {
    setRonda(0);
    nuevaRonda();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function poner(ficha: Ficha) {
    if (!palabra) return;
    const objetivo = palabra.palabra.toUpperCase();
    const posicion = escrito.length;

    if (ficha.letra !== objetivo[posicion]) {
      material.intento("Ese sonido no va aquí");
      hablarFonema(ficha.letra);
      return;
    }

    const siguienteEscrito = [...escrito, ficha.letra];
    setEscrito(siguienteEscrito);
    setCanasto((c) => c.filter((f) => f.id !== ficha.id));
    hablarFonema(ficha.letra);

    if (siguienteEscrito.length === objetivo.length) {
      hablar(palabra.palabra);
      const siguiente = ronda + 1;
      if (siguiente >= config.rondas) setTimeout(() => material.completar(), 900);
      else {
        setRonda(siguiente);
        material.acierto("¡La escribiste!");
        setTimeout(nuevaRonda, 1300);
      }
    } else {
      material.acierto();
    }
  }

  function quitar() {
    if (escrito.length === 0) return;
    const ultima = escrito[escrito.length - 1];
    setEscrito((e) => e.slice(0, -1));
    setCanasto((c) => [...c, { id: `${ultima}-${Date.now()}`, letra: ultima }]);
  }

  const objetivo = palabra?.palabra.toUpperCase() ?? "";

  return (
    <GameShell
      slug="alfabeto-movil"
      level={level}
      levels={ALFABETO_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna="Arma la palabra con las letras"
      nota={nota}
      acciones={
        <>
          <button
            onClick={() => palabra && hablar(palabra.palabra)}
            className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
          >
            Escuchar
          </button>
          <button
            onClick={quitar}
            className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
          >
            Quitar la última
          </button>
        </>
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

      <div className="mb-5 flex flex-col items-center gap-3 rounded-[2rem] bg-white/80 py-6 shadow-sm ring-1 ring-black/5">
        <span className="text-6xl">{palabra?.emoji}</span>
        {config.modelo && (
          <span className="text-lg font-extrabold uppercase tracking-[0.3em] text-stone-400">
            {objetivo}
          </span>
        )}
      </div>

      <div className="mb-6 flex flex-wrap justify-center gap-1.5">
        {Array.from({ length: objetivo.length }).map((_, i) => (
          <span
            key={i}
            className={`flex h-12 w-11 items-center justify-center rounded-xl text-xl font-extrabold ${
              escrito[i]
                ? "bg-[#46578a] text-white"
                : "border-2 border-dashed border-stone-300 text-transparent"
            }`}
          >
            {escrito[i] ?? "·"}
          </span>
        ))}
      </div>

      <div className="flex flex-wrap justify-center gap-2 rounded-[1.5rem] bg-white/40 p-4">
        {canasto.map((f) => (
          <motion.button
            key={f.id}
            layout
            whileTap={{ scale: 0.9 }}
            onClick={() => poner(f)}
            className="h-12 w-11 rounded-xl bg-white text-xl font-extrabold text-stone-600 shadow-sm ring-1 ring-black/5"
          >
            {f.letra}
          </motion.button>
        ))}
      </div>
    </GameShell>
  );
}
