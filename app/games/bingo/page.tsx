"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { BINGO_LEVELS } from "@/data/levels/bingo";
import { OBJETOS_POOL } from "@/data/levels/que-falta";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { shuffle } from "@/lib/shuffle";
import { hablar } from "@/lib/speech";

/** ¿Alguna fila o columna del cartón está completa? */
function hayLineaCompleta(marcadas: Set<number>, tamano: number): boolean {
  for (let r = 0; r < tamano; r++) {
    let completa = true;
    for (let c = 0; c < tamano; c++) if (!marcadas.has(r * tamano + c)) completa = false;
    if (completa) return true;
  }
  for (let c = 0; c < tamano; c++) {
    let completa = true;
    for (let r = 0; r < tamano; r++) if (!marcadas.has(r * tamano + c)) completa = false;
    if (completa) return true;
  }
  return false;
}

export default function BingoPage() {
  const material = useMaterial<(typeof BINGO_LEVELS)[number]>("bingo", BINGO_LEVELS);
  const { config, level, logrado, nota } = material;

  const [carton, setCarton] = useState<string[]>([]);
  const [llamados, setLlamados] = useState<string[]>([]);
  const [indice, setIndice] = useState(0);
  const [marcadas, setMarcadas] = useState<Set<number>>(new Set());

  useEffect(() => {
    const total = config.tamano * config.tamano;
    const nuevoCarton = shuffle(OBJETOS_POOL).slice(0, total);
    const ordenLlamado = shuffle(nuevoCarton);
    // Arma el cartón y el orden de llamado al azar para el nivel: no es
    // una derivación pura que se pueda calcular en el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setCarton(nuevoCarton);
    setLlamados(ordenLlamado);
    setIndice(0);
    setMarcadas(new Set());
    hablar(ordenLlamado[0]);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const llamadoActual = llamados[indice];

  function tocar(i: number) {
    if (marcadas.has(i) || !llamadoActual) return;
    if (carton[i] !== llamadoActual) {
      material.intento("Ese no es el que se pidió. Mira otra vez la imagen grande");
      return;
    }

    const siguientesMarcadas = new Set(marcadas).add(i);
    setMarcadas(siguientesMarcadas);

    const gano = config.lineaSolo
      ? hayLineaCompleta(siguientesMarcadas, config.tamano)
      : siguientesMarcadas.size === carton.length;

    if (gano) {
      setTimeout(() => material.completar(), 400);
    } else {
      material.acierto();
      const siguienteIndice = indice + 1;
      setIndice(siguienteIndice);
      if (llamados[siguienteIndice]) hablar(llamados[siguienteIndice]);
    }
  }

  return (
    <GameShell
      slug="bingo"
      level={level}
      levels={BINGO_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={config.lineaSolo ? "Completa una línea" : "Llena todo el cartón"}
      hablarConsigna
      nota={nota}
    >
      <ConfettiOverlay show={logrado} slug="bingo" />
      <StarReward
        slug="bingo"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={material.repetir}
      />

      <div className="mb-6 flex flex-col items-center">
        <span className="mb-2 text-xs font-bold uppercase tracking-wide text-stone-400">
          Busca esto en tu cartón
        </span>
        <motion.button
          key={llamadoActual}
          onClick={() => llamadoActual && hablar(llamadoActual)}
          initial={{ scale: 0.7, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          aria-label="Escuchar de nuevo"
          className="flex h-24 w-24 items-center justify-center rounded-[2rem] bg-white text-5xl shadow-sm ring-1 ring-black/5"
        >
          {llamadoActual}
        </motion.button>
      </div>

      <div
        className="mx-auto grid max-w-xs gap-2"
        style={{ gridTemplateColumns: `repeat(${config.tamano}, minmax(0, 1fr))` }}
      >
        {carton.map((img, i) => (
          <motion.button
            key={i}
            whileTap={{ scale: 0.9 }}
            onClick={() => tocar(i)}
            className={`flex aspect-square items-center justify-center rounded-2xl text-3xl shadow ring-1 ring-black/5 ${
              marcadas.has(i) ? "bg-emerald-200" : "bg-white"
            }`}
          >
            {img}
          </motion.button>
        ))}
      </div>
    </GameShell>
  );
}
