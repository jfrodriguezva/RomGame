"use client";

import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { BARRAS_LEVELS, type BarrasLevel, colorSegmento } from "@/data/levels/barras-numericas";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { hablar } from "@/lib/speech";
import { shuffle } from "@/lib/shuffle";

const RONDAS = 5;

/** Una barra: tantos segmentos como unidades, alternando rojo y azul. */
function Barra({ largo, ancho = 26 }: { largo: number; ancho?: number }) {
  return (
    <div className="flex overflow-hidden rounded-md shadow-sm ring-1 ring-black/10">
      {Array.from({ length: largo }).map((_, i) => (
        <span
          key={i}
          style={{ width: ancho, height: ancho, backgroundColor: colorSegmento(i) }}
        />
      ))}
    </div>
  );
}

export default function BarrasNumericasPage() {
  const material = useMaterial<BarrasLevel>("barras-numericas", BARRAS_LEVELS);
  const { config, level, logrado, nota } = material;

  const [ronda, setRonda] = useState(0);
  const [a, setA] = useState(1);
  const [b, setB] = useState(1);
  const [opciones, setOpciones] = useState<number[]>([]);
  const [colocadas, setColocadas] = useState<number[]>([]);
  /** Orden fijo del canasto: si se revolviera en cada toque, marearia. */
  const [orden, setOrden] = useState<number[]>([]);

  const respuesta =
    config.modo === "sumar" ? a + b : config.modo === "completar" ? 10 - a : a;

  const nuevaRonda = useCallback(() => {
    const max = config.max;
    if (config.modo === "construir") {
      setColocadas([]);
      setA(max);
      setOrden(shuffle(Array.from({ length: max }, (_, i) => i + 1)));
      return;
    }

    const uno = 1 + Math.floor(Math.random() * max);
    let dos = 1;
    if (config.modo === "sumar") {
      dos = 1 + Math.floor(Math.random() * Math.max(1, Math.min(max, 10 - uno)));
    }
    const correcta =
      config.modo === "sumar" ? uno + dos : config.modo === "completar" ? 10 - uno : uno;

    const posibles = new Set<number>([correcta]);
    while (posibles.size < config.opciones) {
      const candidata = 1 + Math.floor(Math.random() * 10);
      if (candidata !== correcta) posibles.add(candidata);
    }

    setA(uno);
    setB(dos);
    setOpciones(shuffle([...posibles]));

    if (config.modo === "completar") hablar(`¿Cuánto le falta al ${uno} para llegar a diez?`);
    else if (config.modo === "sumar") hablar(`${uno} más ${dos}`);
  }, [config.max, config.modo, config.opciones]);

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
    if (siguiente >= RONDAS) {
      setTimeout(() => material.completar(), 500);
    } else {
      setRonda(siguiente);
      setTimeout(nuevaRonda, 700);
    }
  }

  function responder(valor: number) {
    if (valor === respuesta) {
      material.acierto();
      hablar(String(respuesta));
      avanzar();
    } else {
      material.intento();
    }
  }

  /** Modo construir: colocar las barras del 1 al N en orden. */
  function colocar(valor: number) {
    const esperado = colocadas.length + 1;
    if (valor !== esperado) {
      material.intento("Esa no sigue. Busca la que va después");
      return;
    }
    const siguiente = [...colocadas, valor];
    setColocadas(siguiente);
    if (siguiente.length === config.max) {
      setTimeout(() => material.completar(), 450);
    } else {
      material.acierto();
    }
  }

  const consignas: Record<BarrasLevel["modo"], string> = {
    contar: "Cuenta la barra y toca el número",
    nombrar: "Toca la barra que mide ese número",
    construir: "Arma la escalera del 1 al 10",
    sumar: "¿Cuánto miden las dos juntas?",
    completar: "¿Cuánto le falta para llegar a 10?",
  };

  return (
    <GameShell
      slug="barras-numericas"
      level={level}
      levels={BARRAS_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={consignas[config.modo]}
      nota={nota}
    >
      <ConfettiOverlay show={logrado} />
      <StarReward
        slug="barras-numericas"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={material.repetir}
      />

      {config.modo !== "construir" && (
        <div className="mb-4 flex justify-center gap-1.5">
          {Array.from({ length: RONDAS }).map((_, i) => (
            <span
              key={i}
              className={`h-2.5 w-2.5 rounded-full ${i < ronda ? "bg-amber-400" : "bg-black/10"}`}
            />
          ))}
        </div>
      )}

      {config.modo === "construir" ? (
        <>
          <div className="mb-5 flex min-h-48 flex-col items-start gap-1 rounded-[2rem] bg-white/60 p-4 ring-1 ring-black/5">
            {colocadas.map((v) => (
              <motion.div key={v} layout initial={{ opacity: 0, x: -12 }} animate={{ opacity: 1, x: 0 }}>
                <Barra largo={v} ancho={22} />
              </motion.div>
            ))}
            {colocadas.length === 0 && (
              <span className="text-sm text-stone-400">Empieza por la barra de uno</span>
            )}
          </div>
          <div className="flex flex-col items-start gap-2 rounded-[2rem] bg-white/40 p-4">
            {orden
              .filter((v) => !colocadas.includes(v))
              .map((v) => (
                <motion.button key={v} whileTap={{ scale: 0.94 }} onClick={() => colocar(v)}>
                  <Barra largo={v} ancho={22} />
                </motion.button>
              ))}
          </div>
        </>
      ) : (
        <>
          <div className="mb-7 flex flex-col items-center gap-3 rounded-[2rem] bg-white/70 p-5 ring-1 ring-black/5">
            <Barra largo={a} />
            {config.modo === "sumar" && (
              <>
                <span className="text-2xl font-extrabold text-stone-400">+</span>
                <Barra largo={b} />
              </>
            )}
            {config.modo === "completar" && (
              <span className="text-sm text-stone-400">…y lo que falta hasta diez</span>
            )}
          </div>

          <div className="grid grid-cols-3 gap-3">
            {opciones.map((o) => (
              <motion.button
                key={o}
                whileTap={{ scale: 0.92 }}
                onClick={() => responder(o)}
                className="flex flex-col items-center justify-center gap-2 rounded-3xl bg-white/90 py-4 shadow-sm ring-1 ring-black/5"
              >
                {config.modo === "nombrar" ? (
                  <Barra largo={o} ancho={13} />
                ) : (
                  <span className="text-3xl font-extrabold text-stone-600">{o}</span>
                )}
              </motion.button>
            ))}
          </div>
        </>
      )}
    </GameShell>
  );
}
