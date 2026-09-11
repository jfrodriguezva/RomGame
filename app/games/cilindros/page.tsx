"use client";

import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { CILINDROS_LEVELS, type CilindrosLevel } from "@/data/levels/cilindros";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { shuffle } from "@/lib/shuffle";

/** Diámetro y altura de cada cilindro, del 1 (chico) al 10 (grande). */
function medidas(valor: number, variacion: CilindrosLevel["variacion"]) {
  const diametro = variacion === "altura" ? 46 : 20 + valor * 5;
  const altura = variacion === "diametro" ? 46 : 18 + valor * 5.4;
  return { diametro, altura };
}

export default function CilindrosPage() {
  const material = useMaterial<CilindrosLevel>("cilindros", CILINDROS_LEVELS);
  const { config, level, logrado, nota } = material;

  const [huecos, setHuecos] = useState<number[]>([]);
  const [bandeja, setBandeja] = useState<number[]>([]);
  const [colocados, setColocados] = useState<number[]>([]);
  const [enMano, setEnMano] = useState<number | null>(null);

  const preparar = useCallback(() => {
    const valores = Array.from({ length: config.cantidad }, (_, i) => config.desde + i + 1);
    setHuecos(valores);
    setBandeja(shuffle(valores));
    setColocados([]);
    setEnMano(null);
  }, [config.cantidad, config.desde]);

  useEffect(() => {
    preparar();
  }, [preparar]);

  function meter(hueco: number) {
    if (enMano === null) {
      material.intento("Primero toma un cilindro");
      return;
    }
    if (hueco !== enMano) {
      // El material real tampoco avisa: sencillamente no entra.
      material.intento("Ese no entra en este hueco");
      return;
    }
    const restantes = bandeja.filter((v) => v !== enMano);
    setBandeja(restantes);
    setColocados((c) => [...c, enMano]);
    setEnMano(null);
    if (restantes.length === 0) setTimeout(() => material.completar(), 400);
    else material.acierto();
  }

  return (
    <GameShell
      slug="cilindros"
      level={level}
      levels={CILINDROS_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={
        enMano === null
          ? "Toma un cilindro de la bandeja"
          : "Busca el hueco donde entra justo"
      }
      nota={nota}
      acciones={
        <button
          onClick={preparar}
          className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
        >
          Sacar todos
        </button>
      }
    >
      <ConfettiOverlay show={logrado} />
      <StarReward
        slug="cilindros"
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

      {/* El bloque de madera con sus huecos */}
      <div className="mb-6 flex min-h-36 flex-wrap items-end justify-center gap-3 rounded-[1.5rem] bg-[#c9a87c] p-4 shadow-inner">
        {huecos.map((h) => {
          const { diametro } = medidas(h, config.variacion);
          const lleno = colocados.includes(h);
          return (
            <button
              key={h}
              onClick={() => meter(h)}
              aria-label={`Hueco ${h}`}
              className="flex items-center justify-center rounded-full transition active:scale-95"
              style={{
                width: diametro,
                height: diametro,
                backgroundColor: lleno ? "#8a6244" : "#7a5c3e",
                boxShadow: lleno
                  ? "inset 0 2px 6px rgba(0,0,0,0.25)"
                  : "inset 0 6px 12px rgba(0,0,0,0.45)",
              }}
            >
              {lleno && <span className="h-2.5 w-2.5 rounded-full bg-[#e6d3b3]" />}
            </button>
          );
        })}
      </div>

      {/* La bandeja */}
      <div className="flex min-h-32 flex-wrap items-end justify-center gap-3 rounded-[1.5rem] bg-white/50 p-4">
        {bandeja.map((v) => {
          const { diametro, altura } = medidas(v, config.variacion);
          const tomado = enMano === v;
          return (
            <motion.button
              key={v}
              layout
              whileTap={{ scale: 0.92 }}
              onClick={() => setEnMano(tomado ? null : v)}
              aria-label={`Cilindro ${v}`}
              className="relative flex flex-col items-center"
            >
              <span
                className="mb-1 h-3 w-3 rounded-full"
                style={{ backgroundColor: tomado ? "#8a5a2b" : "#b08a5e" }}
              />
              <span
                className="rounded-b-md rounded-t-sm"
                style={{
                  width: diametro,
                  height: altura,
                  backgroundColor: "#d9b98c",
                  outline: tomado ? "3px solid #8a5a2b" : "none",
                  boxShadow: "inset -6px 0 10px rgba(0,0,0,0.12)",
                }}
              />
            </motion.button>
          );
        })}
        {bandeja.length === 0 && (
          <span className="py-6 text-sm text-stone-400">Bandeja vacía</span>
        )}
      </div>
    </GameShell>
  );
}
