"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { BINOMIO_LEVELS, BINOMIO_COLORES, type BinomioLevel } from "@/data/levels/binomio";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { seeded } from "@/lib/levels";
import { shuffle } from "@/lib/shuffle";

interface Pieza {
  id: string;
  color: number;
}

export default function BinomioPage() {
  const material = useMaterial<BinomioLevel>("binomio", BINOMIO_LEVELS);
  const { config, level, logrado, nota } = material;

  /**
   * El modelo es determinista: el nivel 47 arma siempre el mismo cubo, así que
   * repetirlo sirve de verdad para memorizarlo.
   */
  const modelo = useMemo(() => {
    const total = config.lado * config.lado;
    return Array.from({ length: total }, (_, i) => {
      const fila = Math.floor(i / config.lado);
      const col = i % config.lado;
      // La diagonal lleva el color propio; fuera de ella, los prismas mixtos.
      if (fila === col) return fila % BINOMIO_COLORES.length;
      return (
        (Math.floor(seeded(level, fila * 7 + col) * BINOMIO_COLORES.length) +
          config.lado) %
        BINOMIO_COLORES.length
      );
    });
  }, [config.lado, level]);

  const [tablero, setTablero] = useState<(number | null)[]>([]);
  const [piezas, setPiezas] = useState<Pieza[]>([]);
  const [enMano, setEnMano] = useState<Pieza | null>(null);
  const [verModelo, setVerModelo] = useState(true);

  const preparar = useCallback(() => {
    setTablero(Array(modelo.length).fill(null));
    setPiezas(shuffle(modelo.map((color, i) => ({ id: `p${i}`, color }))));
    setEnMano(null);
    setVerModelo(true);
    if (!config.modeloVisible) {
      const t = setTimeout(() => setVerModelo(false), config.vistazo * 1000);
      return () => clearTimeout(t);
    }
  }, [config.modeloVisible, config.vistazo, modelo]);

  useEffect(() => {
    // preparar() arma el patrón al azar (y puede armar un temporizador de
    // memorización): no es una derivación pura para el render.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    const limpiar = preparar();
    return limpiar;
  }, [preparar]);

  function colocar(indice: number) {
    if (!enMano) {
      material.intento("Toma primero una pieza");
      return;
    }
    if (tablero[indice] !== null) {
      // Sacar una pieza mal puesta siempre se puede: corregir no es fallar.
      setTablero((prev) => {
        const copia = [...prev];
        copia[indice] = null;
        return copia;
      });
      setPiezas((p) => [...p, { id: `r${Date.now()}`, color: tablero[indice]! }]);
      return;
    }
    if (modelo[indice] !== enMano.color) {
      material.intento("Ahí va otro color");
      return;
    }

    const siguiente = [...tablero];
    siguiente[indice] = enMano.color;
    setTablero(siguiente);
    setPiezas((p) => p.filter((x) => x.id !== enMano.id));
    setEnMano(null);

    if (siguiente.every((c) => c !== null)) setTimeout(() => material.completar(), 420);
    else material.acierto();
  }

  const lado = config.lado;

  return (
    <GameShell
      slug="binomio"
      level={level}
      levels={BINOMIO_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={verModelo ? "Mira bien el cubo y ármalo igual" : "Ármalo de memoria"}
      nota={nota}
      acciones={
        !config.modeloVisible ? (
          <button
            onClick={() => {
              setVerModelo(true);
              setTimeout(() => setVerModelo(false), 2000);
            }}
            className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
          >
            Ver el modelo un momento
          </button>
        ) : undefined
      }
    >
      <ConfettiOverlay show={logrado} slug="binomio" />
      <StarReward
        slug="binomio"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={() => {
          preparar();
          material.repetir();
        }}
      />

      <div className="mb-5 flex items-start justify-center gap-5">
        <div className="text-center">
          <span className="mb-1 block text-[11px] font-bold uppercase tracking-wide text-stone-400">
            modelo
          </span>
          <div
            className="grid gap-[3px] rounded-xl bg-white/70 p-1.5 ring-1 ring-black/5"
            style={{ gridTemplateColumns: `repeat(${lado}, 1fr)`, width: 108 }}
          >
            {modelo.map((c, i) => (
              <span
                key={i}
                className="aspect-square rounded-[3px] transition"
                style={{
                  backgroundColor: verModelo ? BINOMIO_COLORES[c] : "#e7e0d6",
                }}
              />
            ))}
          </div>
        </div>

        <div className="text-center">
          <span className="mb-1 block text-[11px] font-bold uppercase tracking-wide text-stone-400">
            tu caja
          </span>
          <div
            className="grid gap-[3px] rounded-xl bg-[#c9a87c] p-1.5 shadow-inner"
            style={{ gridTemplateColumns: `repeat(${lado}, 1fr)`, width: 150 }}
          >
            {tablero.map((c, i) => (
              <button
                key={i}
                onClick={() => colocar(i)}
                className="aspect-square rounded-[3px] transition active:scale-95"
                style={{ backgroundColor: c === null ? "#a8875f" : BINOMIO_COLORES[c] }}
                aria-label={`Casilla ${i + 1}`}
              />
            ))}
          </div>
        </div>
      </div>

      <div className="flex flex-wrap justify-center gap-2 rounded-[1.5rem] bg-white/50 p-4">
        {piezas.map((p) => (
          <motion.button
            key={p.id}
            layout
            whileTap={{ scale: 0.9 }}
            onClick={() => setEnMano(enMano?.id === p.id ? null : p)}
            className={`h-12 w-12 rounded-lg transition ${
              enMano?.id === p.id ? "ring-4 ring-stone-600" : "ring-1 ring-black/10"
            }`}
            style={{ backgroundColor: BINOMIO_COLORES[p.color] }}
            aria-label="Pieza"
          />
        ))}
        {piezas.length === 0 && <span className="py-4 text-sm text-stone-400">Caja armada</span>}
      </div>
    </GameShell>
  );
}
