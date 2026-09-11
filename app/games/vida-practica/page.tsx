"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import {
  OBJETOS_TRASVASE,
  PARES_CLASIFICAR,
  VIDA_PRACTICA_LEVELS,
  type VidaPracticaLevel,
} from "@/data/levels/vida-practica";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { cycle } from "@/lib/levels";
import { shuffle } from "@/lib/shuffle";

interface ItemClasificar {
  id: string;
  emoji: string;
  grupo: "a" | "b";
}

export default function VidaPracticaPage() {
  const material = useMaterial<VidaPracticaLevel>("vida-practica", VIDA_PRACTICA_LEVELS);
  const { config, level, logrado, nota } = material;

  // Trasvasar y abotonar
  const [hechos, setHechos] = useState(0);

  // Verter
  const [nivelAgua, setNivelAgua] = useState(0);
  const [objetivoAgua, setObjetivoAgua] = useState(60);
  const vertiendo = useRef<ReturnType<typeof setInterval> | null>(null);

  // Clasificar
  const [porClasificar, setPorClasificar] = useState<ItemClasificar[]>([]);
  const [enCanasta, setEnCanasta] = useState<Record<string, number>>({ a: 0, b: 0 });

  const par = cycle(PARES_CLASIFICAR, level);

  const preparar = useCallback(() => {
    setHechos(0);
    setNivelAgua(0);
    setObjetivoAgua(35 + Math.round(Math.random() * 45));
    setEnCanasta({ a: 0, b: 0 });

    const mitad = Math.ceil(config.cantidad / 2);
    const items: ItemClasificar[] = [
      ...Array.from({ length: mitad }, (_, i) => ({
        id: `a${i}`,
        emoji: par.a.items[i % par.a.items.length],
        grupo: "a" as const,
      })),
      ...Array.from({ length: config.cantidad - mitad }, (_, i) => ({
        id: `b${i}`,
        emoji: par.b.items[i % par.b.items.length],
        grupo: "b" as const,
      })),
    ];
    setPorClasificar(shuffle(items));
  }, [config.cantidad, par]);

  useEffect(() => {
    preparar();
  }, [preparar]);

  useEffect(() => {
    return () => {
      if (vertiendo.current) clearInterval(vertiendo.current);
    };
  }, []);

  function avanzarConteo() {
    const siguiente = hechos + 1;
    setHechos(siguiente);
    if (siguiente >= config.cantidad) setTimeout(() => material.completar(), 420);
    else material.acierto();
  }

  // ------------------------------------------------------------------ verter
  function empezarAVerter() {
    if (vertiendo.current) return;
    vertiendo.current = setInterval(() => {
      setNivelAgua((n) => Math.min(118, n + 1.6));
    }, 40);
  }

  function dejarDeVerter() {
    if (vertiendo.current) {
      clearInterval(vertiendo.current);
      vertiendo.current = null;
    }
    const diferencia = Math.abs(nivelAgua - objetivoAgua);
    if (diferencia <= config.precision) {
      material.acierto("Justo en la raya");
      setTimeout(() => material.completar(), 500);
    } else if (nivelAgua > objetivoAgua) {
      material.intento("Se derramó. Vacía y vuelve a intentar");
      setTimeout(() => setNivelAgua(0), 700);
    } else {
      material.intento("Falta un poco más");
    }
  }

  // ------------------------------------------------------------- clasificar
  function clasificar(item: ItemClasificar, canasta: "a" | "b") {
    if (item.grupo !== canasta) {
      material.intento("Ese va en la otra canasta");
      return;
    }
    const restantes = porClasificar.filter((i) => i.id !== item.id);
    setPorClasificar(restantes);
    setEnCanasta((c) => ({ ...c, [canasta]: c[canasta] + 1 }));
    if (restantes.length === 0) setTimeout(() => material.completar(), 420);
    else material.acierto();
  }

  const consignas: Record<VidaPracticaLevel["tarea"], string> = {
    trasvasar: "Pasa todo a la otra bandeja, de uno en uno",
    abotonar: "Abrocha los botones de arriba hacia abajo",
    verter: "Llena el vaso justo hasta la raya",
    clasificar: `Separa ${par.criterio}`,
  };

  return (
    <GameShell
      slug="vida-practica"
      level={level}
      levels={VIDA_PRACTICA_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={consignas[config.tarea]}
      nota={nota}
      acciones={
        <button
          onClick={preparar}
          className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
        >
          Volver a empezar
        </button>
      }
    >
      <ConfettiOverlay show={logrado} />
      <StarReward
        slug="vida-practica"
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

      {config.tarea === "trasvasar" && (
        <div className="grid grid-cols-2 gap-3">
          <div className="min-h-56 rounded-[2rem] bg-white/80 p-4 ring-1 ring-black/5">
            <span className="mb-2 block text-center text-xs font-bold uppercase text-stone-400">
              de aquí
            </span>
            <div className="flex flex-wrap justify-center gap-2">
              {Array.from({ length: config.cantidad - hechos }).map((_, i) => (
                <motion.button
                  key={i}
                  layout
                  whileTap={{ scale: 0.85 }}
                  onClick={avanzarConteo}
                  className="text-3xl"
                  aria-label="Pasar un elemento"
                >
                  {cycle(OBJETOS_TRASVASE, level)}
                </motion.button>
              ))}
            </div>
          </div>
          <div className="min-h-56 rounded-[2rem] bg-[#eef3ec] p-4 ring-1 ring-black/5">
            <span className="mb-2 block text-center text-xs font-bold uppercase text-stone-400">
              para acá
            </span>
            <div className="flex flex-wrap justify-center gap-2">
              {Array.from({ length: hechos }).map((_, i) => (
                <motion.span key={i} layout initial={{ scale: 0 }} animate={{ scale: 1 }} className="text-3xl">
                  {cycle(OBJETOS_TRASVASE, level)}
                </motion.span>
              ))}
            </div>
          </div>
        </div>
      )}

      {config.tarea === "abotonar" && (
        <div className="mx-auto flex w-full max-w-56 flex-col items-center gap-3 rounded-[2rem] bg-[#dbe7d6] p-6 ring-1 ring-black/5">
          {Array.from({ length: config.cantidad }).map((_, i) => {
            const abrochado = i < hechos;
            const toca = i === hechos;
            return (
              <button
                key={i}
                onClick={() => (toca ? avanzarConteo() : material.intento("Sigue el orden, de arriba hacia abajo"))}
                className={`flex h-12 w-full items-center justify-center rounded-full text-xl transition ${
                  abrochado
                    ? "bg-[#4a6b4d] text-white"
                    : toca
                      ? "bg-white ring-4 ring-[#a9c0a0]"
                      : "bg-white/70"
                }`}
                aria-label={`Botón ${i + 1}`}
              >
                {abrochado ? "●" : "○"}
              </button>
            );
          })}
        </div>
      )}

      {config.tarea === "verter" && (
        <div className="flex flex-col items-center gap-5">
          <div className="relative h-40 w-28 overflow-hidden rounded-b-3xl rounded-t-lg bg-white/80 ring-2 ring-stone-300">
            <div
              className="absolute inset-x-0 bottom-0 bg-[#8ec5d6] transition-[height] duration-75"
              style={{ height: `${Math.min(100, (nivelAgua / 120) * 100)}%` }}
            />
            {/* La raya es el control del error: se ve exactamente dónde parar. */}
            <div
              className="absolute inset-x-0 border-t-2 border-dashed border-[#c0392b]"
              style={{ bottom: `${(objetivoAgua / 120) * 100}%` }}
            />
          </div>

          <button
            onPointerDown={empezarAVerter}
            onPointerUp={dejarDeVerter}
            onPointerLeave={dejarDeVerter}
            className="rounded-3xl bg-[#4a6b4d] px-8 py-5 text-lg font-extrabold text-white shadow active:scale-95"
          >
            Mantén para servir
          </button>
          <button
            onClick={() => setNivelAgua(0)}
            className="text-sm font-bold text-stone-400 underline"
          >
            Vaciar el vaso
          </button>
        </div>
      )}

      {config.tarea === "clasificar" && (
        <>
          <div className="mb-5 flex min-h-24 flex-wrap justify-center gap-2 rounded-[2rem] bg-white/60 p-4">
            {porClasificar.map((item, i) => (
              <span key={item.id} className={`text-3xl ${i === 0 ? "" : "opacity-40"}`}>
                {item.emoji}
              </span>
            ))}
            {porClasificar.length === 0 && (
              <span className="py-4 text-sm text-stone-400">Todo clasificado</span>
            )}
          </div>

          <div className="grid grid-cols-2 gap-3">
            {(["a", "b"] as const).map((clave) => (
              <button
                key={clave}
                onClick={() => porClasificar[0] && clasificar(porClasificar[0], clave)}
                className="min-h-32 rounded-[2rem] bg-[#eef3ec] p-4 ring-1 ring-black/5 active:scale-95"
              >
                <span className="mb-2 block text-xs font-bold uppercase text-stone-500">
                  {par[clave].nombre}
                </span>
                <span className="text-2xl">{"●".repeat(enCanasta[clave])}</span>
              </button>
            ))}
          </div>

          <p className="mt-4 text-center text-sm text-stone-400">
            Toma el primero de la bandeja y elige su canasta.
          </p>
        </>
      )}
    </GameShell>
  );
}
