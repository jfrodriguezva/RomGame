"use client";

import { motion } from "framer-motion";
import { useState } from "react";
import { useSettings } from "@/lib/settings";
import { getGame } from "@/data/games";
import { AREAS } from "@/lib/montessori";

/** Papelitos en tonos naturales; en modo calma no aparecen. */
const COLORES_DEFECTO = ["#e2b6c1", "#cbd7a7", "#e8cf9a", "#a9c4d6", "#d9c2e0", "#efd9b4"];

function mezclar(hexA: string, hexB: string, t: number): string {
  const a = parseInt(hexA.slice(1), 16);
  const b = parseInt(hexB.slice(1), 16);
  const canal = (n: number, shift: number) => (n >> shift) & 255;
  const mezclarCanal = (shift: number) =>
    Math.round(canal(a, shift) + (canal(b, shift) - canal(a, shift)) * t);
  const r = mezclarCanal(16);
  const g = mezclarCanal(8);
  const bl = mezclarCanal(0);
  return `#${[r, g, bl].map((v) => v.toString(16).padStart(2, "0")).join("")}`;
}

/** Cuatro tonos del área (acento y acentoOscuro, con una variación clara de
 * cada uno) en vez de la paleta genérica — mismo criterio que StarReward. */
function paletaDeArea(slug?: string): string[] {
  if (!slug) return COLORES_DEFECTO;
  const area = AREAS[getGame(slug)?.area ?? "sensorial"];
  return [
    area.acento,
    mezclar(area.acento, "#ffffff", 0.35),
    area.acentoOscuro,
    mezclar(area.acentoOscuro, "#ffffff", 0.4),
  ];
}

interface Pieza {
  id: number;
  left: number;
  delay: number;
  duration: number;
  color: string;
  rotate: number;
  size: number;
}

function generarPiezas(colores: string[]): Pieza[] {
  return Array.from({ length: 34 }).map((_, i) => ({
    id: i,
    left: Math.random() * 100,
    delay: Math.random() * 0.5,
    duration: 2 + Math.random() * 1.4,
    color: colores[i % colores.length],
    rotate: Math.random() * 360,
    size: 7 + Math.random() * 6,
  }));
}

export default function ConfettiOverlay({ show, slug }: { show: boolean; slug?: string }) {
  const calma = useSettings((s) => s.calma);
  // Inicializador perezoso de useState: corre una sola vez al montar. A
  // diferencia de useMemo (pensado como caché de una función pura), React
  // documenta este lugar como el correcto para cómputo que puede ser
  // impuro — aquí, las posiciones al azar del confeti.
  const [pieces] = useState(() => generarPiezas(paletaDeArea(slug)));

  if (!show || calma) return null;

  return (
    <div className="pointer-events-none fixed inset-0 z-50 overflow-hidden">
      {pieces.map((p) => (
        <motion.div
          key={p.id}
          initial={{ y: -20, opacity: 1, rotate: 0 }}
          animate={{ y: "110vh", opacity: 1, rotate: p.rotate }}
          transition={{ duration: p.duration, delay: p.delay, ease: "easeIn" }}
          style={{
            position: "absolute",
            left: `${p.left}%`,
            top: 0,
            width: p.size,
            height: p.size * 1.4,
            backgroundColor: p.color,
            borderRadius: 3,
          }}
        />
      ))}
    </div>
  );
}
