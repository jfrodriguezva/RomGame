"use client";

import { useEffect, useMemo, useState } from "react";
import { motion } from "framer-motion";
import GameShell from "@/components/GameShell";
import StarReward from "@/components/StarReward";
import ConfettiOverlay from "@/components/ConfettiOverlay";
import { COLOREAR_LEVELS, type ColorearLevel, dibujoDeNivel } from "@/data/levels/colorear";
import { useMaterial } from "@/lib/useMaterial";
import { fraseNivelCompleto } from "@/lib/montessori";
import { playSound } from "@/lib/audio";
import { vibrar, HAPTIC } from "@/lib/haptics";

/**
 * Paleta de 24 colores en tonos naturales.
 *
 * Un niño de tres años no necesita un selector de color continuo: necesita
 * pocos colores buenos y bien separados entre sí, como en una caja de ceras.
 */
const PALETA = [
  "#e05c4f", "#e8804a", "#efb04a", "#f2d55c", "#c9d15a", "#8fbf6a",
  "#5aa87a", "#4f9e94", "#4f8fb0", "#5077b5", "#6a6fb5", "#8a68ad",
  "#b0679f", "#d2698c", "#e08a9c", "#f0b3b0", "#c9a87c", "#a3785a",
  "#7a5a44", "#4a4a4a", "#8a8f96", "#c8ccd2", "#ffffff", "#2f2a26",
];

export default function ColorearPage() {
  const material = useMaterial<ColorearLevel>("colorear", COLOREAR_LEVELS);
  const { config, level, logrado, nota } = material;

  const dibujo = useMemo(() => dibujoDeNivel(level), [level]);
  const [color, setColor] = useState(PALETA[0]);
  const [pintado, setPintado] = useState<Record<string, string>>({});

  const paleta = PALETA.slice(0, config.colores);

  useEffect(() => {
    // Reinicia el dibujo y la paleta al cambiar de nivel: sincroniza con
    // una prop que cambia, no es una derivación pura del render actual.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setPintado({});
    setColor(paleta[0]);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [level]);

  const listas = Object.keys(pintado).length;
  const total = dibujo.regiones.length;

  function pintar(id: string) {
    // Volver a pintar una zona nunca es un error: se puede cambiar de opinión.
    // Antes material.completar() se programaba dentro del updater de
    // setPintado, un efecto secundario en una función que React puede
    // invocar más de una vez — mismo patrón que ya causó bugs reales en
    // otros materiales (podía duplicar la estrella y la celebración).
    const siguiente = { ...pintado, [id]: color };
    setPintado(siguiente);
    playSound("click");
    vibrar(HAPTIC.toque);
    if (Object.keys(siguiente).length === total) {
      setTimeout(() => material.completar(), 350);
    }
  }

  return (
    <GameShell
      slug="colorear"
      level={level}
      levels={COLOREAR_LEVELS.map((l) => l.level)}
      onLevel={material.setLevel}
      consigna={
        config.esMandala ? "Pinta el mandala a tu gusto" : `Pinta ${dibujo.nombre.toLowerCase()}`
      }
      nota={nota}
      acciones={
        <>
          <button
            onClick={() => setPintado({})}
            className="rounded-2xl bg-white px-4 py-2.5 text-sm font-bold text-stone-500 ring-1 ring-stone-200 active:scale-95"
          >
            Empezar de nuevo
          </button>
          <span className="text-sm font-bold text-stone-400">
            {listas} / {total}
          </span>
        </>
      }
    >
      <ConfettiOverlay show={logrado} slug="colorear" />
      <StarReward
        slug="colorear"
        show={logrado}
        message={fraseNivelCompleto(level)}
        stars={material.estrellas}
        level={level}
        onNext={material.esUltimo ? undefined : material.siguiente}
        onRepeat={() => {
          setPintado({});
          material.repetir();
        }}
      />

      <div className="mb-4 rounded-[2rem] bg-white p-3 shadow-sm ring-1 ring-black/5">
        <svg viewBox="0 0 200 200" className="h-auto w-full" role="img" aria-label={dibujo.nombre}>
          {dibujo.regiones.map((r) => (
            <path
              key={r.id}
              d={r.d}
              fill={pintado[r.id] ?? "#ffffff"}
              stroke="#57504a"
              strokeWidth={1.4}
              strokeLinejoin="round"
              onPointerDown={() => pintar(r.id)}
              className="cursor-pointer transition-[fill] duration-150"
            />
          ))}
          {dibujo.detalles?.map((d, i) => (
            <path
              key={`detalle-${i}`}
              d={d}
              fill="none"
              stroke="#57504a"
              strokeWidth={1.8}
              strokeLinecap="round"
              pointerEvents="none"
            />
          ))}
        </svg>
      </div>

      <div className="grid grid-cols-8 gap-2">
        {paleta.map((c) => (
          <motion.button
            key={c}
            onClick={() => {
              setColor(c);
              playSound("click");
            }}
            whileTap={{ scale: 0.88 }}
            aria-label={`Color ${c}`}
            className={`aspect-square rounded-full ring-1 ring-black/10 transition ${
              color === c ? "scale-110 ring-4 ring-stone-500" : ""
            }`}
            style={{ backgroundColor: c }}
          />
        ))}
      </div>
    </GameShell>
  );
}
