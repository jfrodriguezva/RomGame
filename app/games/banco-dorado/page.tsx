"use client";

import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { BANCO_LEVELS, type BancoLevel, JERARQUIAS } from "@/data/levels/banco-dorado";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

const RONDAS = 4;

type Bandeja = Record<number, number>;

const VACIA: Bandeja = { 1: 0, 10: 0, 100: 0, 1000: 0 };

function digitosDe(n: number): Bandeja {
  return {
    1: n % 10,
    10: Math.floor(n / 10) % 10,
    100: Math.floor(n / 100) % 10,
    1000: Math.floor(n / 1000) % 10,
  };
}

export default function BancoDoradoPage() {
  const material = useMaterial<BancoLevel>("banco-dorado", BANCO_LEVELS);
  const { config, level, logrado, nota } = material;

  const [ronda, setRonda] = useState(0);
  const [numero, setNumero] = useState(1);
  const [bandeja, setBandeja] = useState<Bandeja>(VACIA);
  const [opciones, setOpciones] = useState<number[]>([]);

  const jerarquias = JERARQUIAS.slice(0, config.digitos);

  const nuevaRonda = useCallback(() => {
    // Se genera cifra por cifra para poder controlar si hay ceros intermedios.
    let valor = 0;
    for (let i = config.digitos - 1; i >= 0; i--) {
      const potencia = Math.pow(10, i);
      const minimo = i === config.digitos - 1 ? 1 : config.conCeros ? 0 : 1;
      const cifra = minimo + Math.floor(Math.random() * (9 - minimo + 1));
      valor += cifra * potencia;
    }
    setNumero(valor);
    setBandeja({ ...VACIA });

    if (config.modo === "leer") {
      const posibles = new Set<number>([valor]);
      while (posibles.size < config.opciones) {
        // Los distractores cambian una sola cifra: obligan a mirar el lugar.
        const potencia = Math.pow(10, Math.floor(Math.random() * config.digitos));
        const delta = (1 + Math.floor(Math.random() * 3)) * potencia;
        const candidato = Math.random() < 0.5 ? valor + delta : Math.max(1, valor - delta);
        if (candidato !== valor) posibles.add(candidato);
      }
      setOpciones(shuffle([...posibles]));
    } else {
      hablar(String(valor));
    }
  }, [config.conCeros, config.digitos, config.modo, config.opciones]);

  useEffect(() => {
    // nuevaRonda() elige al azar y habla en voz alta: no es una derivación
    // pura que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setRonda(0);
    nuevaRonda();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  function avanzar() {
    const siguiente = ronda + 1;
    if (siguiente >= RONDAS) setTimeout(() => material.completar(), 500);
    else {
      setRonda(siguiente);
      setTimeout(nuevaRonda, 800);
    }
  }

  function poner(valor: number, delta: number) {
    setBandeja((prev) => {
      const cantidad = Math.max(0, Math.min(9, prev[valor] + delta));
      const siguiente = { ...prev, [valor]: cantidad };
      const total =
        siguiente[1] + siguiente[10] * 10 + siguiente[100] * 100 + siguiente[1000] * 1000;
      if (total === numero) {
        material.acierto();
        hablar(String(numero));
        avanzar();
      }
      return siguiente;
    });
  }

  function responder(valor: number) {
    if (valor === numero) {
      material.acierto();
      hablar(String(numero));
      avanzar();
    } else {
      material.intento();
    }
  }

  const objetivo = digitosDe(numero);

  return (
    <GameShell
      slug="banco-dorado"
      level={level}
      levels={BANCO_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={
        config.modo === "componer"
          ? "Trae del banco esa cantidad"
          : "¿Qué número hay en la bandeja?"
      }
      nota={nota}
    >
      <ConfettiOverlay show={logrado} />
      <StarReward
        slug="banco-dorado"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={material.repetir}
      />

      <div className="mb-4 flex justify-center gap-1.5">
        {Array.from({ length: RONDAS }).map((_, i) => (
          <span
            key={i}
            className={`h-2.5 w-2.5 rounded-full ${i < ronda ? "bg-amber-400" : "bg-black/10"}`}
          />
        ))}
      </div>

      {config.modo === "componer" ? (
        <>
          <div className="mb-6 flex flex-col items-center rounded-[2rem] bg-white/80 py-6 shadow-sm ring-1 ring-black/5">
            <span className="text-xs font-bold uppercase tracking-wide text-stone-400">
              tarjeta del número
            </span>
            <span className="text-5xl font-extrabold text-[#8a5a2b]">{numero}</span>
          </div>

          <div className="grid gap-3">
            {[...jerarquias].reverse().map((j) => (
              <div
                key={j.valor}
                className="flex items-center gap-3 rounded-2xl bg-white/70 p-3 ring-1 ring-black/5"
              >
                <span className="w-24 text-xs font-bold text-stone-500">{j.plural}</span>

                <div className="flex flex-1 flex-wrap gap-1">
                  {Array.from({ length: bandeja[j.valor] }).map((_, i) => (
                    <motion.span
                      key={i}
                      initial={{ scale: 0 }}
                      animate={{ scale: 1 }}
                      className="text-xl"
                    >
                      {j.emoji}
                    </motion.span>
                  ))}
                </div>

                <div className="flex items-center gap-1">
                  <button
                    onClick={() => poner(j.valor, -1)}
                    className="h-9 w-9 rounded-xl bg-stone-100 text-lg font-extrabold text-stone-500 active:scale-90"
                    aria-label={`Quitar una ${j.nombre}`}
                  >
                    −
                  </button>
                  <span className="w-6 text-center font-extrabold text-stone-600">
                    {bandeja[j.valor]}
                  </span>
                  <button
                    onClick={() => poner(j.valor, 1)}
                    className="h-9 w-9 rounded-xl text-lg font-extrabold text-white active:scale-90"
                    style={{ backgroundColor: j.color }}
                    aria-label={`Poner una ${j.nombre}`}
                  >
                    +
                  </button>
                </div>
              </div>
            ))}
          </div>

          <p className="mt-4 text-center text-xs text-stone-400">
            Puedes quitar y volver a poner las veces que quieras.
          </p>
        </>
      ) : (
        <>
          <div className="mb-6 grid gap-2 rounded-[2rem] bg-white/80 p-4 ring-1 ring-black/5">
            {[...jerarquias].reverse().map((j) => (
              <div key={j.valor} className="flex items-center gap-2">
                <span className="w-24 text-xs font-bold text-stone-500">{j.plural}</span>
                <span className="flex flex-wrap gap-1 text-xl">
                  {Array.from({ length: objetivo[j.valor] }).map((_, i) => (
                    <span key={i}>{j.emoji}</span>
                  ))}
                  {objetivo[j.valor] === 0 && (
                    <span className="text-sm text-stone-300">ninguna</span>
                  )}
                </span>
              </div>
            ))}
          </div>

          <div className="grid grid-cols-2 gap-3">
            {opciones.map((o) => (
              <motion.button
                key={o}
                whileTap={{ scale: 0.94 }}
                onClick={() => responder(o)}
                className="rounded-3xl bg-white/90 py-5 text-3xl font-extrabold text-stone-600 shadow-sm ring-1 ring-black/5"
              >
                {o}
              </motion.button>
            ))}
          </div>
        </>
      )}
    </GameShell>
  );
}
