"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { BANDERAS } from "@/data/levels/banderas";

const ITEMS: ItemQuiz[] = BANDERAS.map((b) => ({
  id: b.id,
  visual: b.id,
  nombre: b.label,
  familia: "banderas",
}));

const TRAZO = "#00000022";

function Bandera({ id, size }: { id: string; size: number }) {
  const w = size;
  const h = size * 0.66;
  return (
    <svg width={w} height={h} viewBox="0 0 90 60" aria-hidden="true">
      <rect x={0} y={0} width={90} height={60} fill="#fff" stroke={TRAZO} />

      {id === "mexico" && (
        <>
          <rect x={0} y={0} width={30} height={60} fill="#2e7d4f" />
          <rect x={60} y={0} width={30} height={60} fill="#c1272d" />
        </>
      )}
      {id === "espana" && (
        <>
          <rect x={0} y={0} width={90} height={15} fill="#c1272d" />
          <rect x={0} y={15} width={90} height={30} fill="#f5c518" />
          <rect x={0} y={45} width={90} height={15} fill="#c1272d" />
        </>
      )}
      {id === "argentina" && (
        <>
          <rect x={0} y={0} width={90} height={20} fill="#75aadb" />
          <rect x={0} y={20} width={90} height={20} fill="#fff" />
          <rect x={0} y={40} width={90} height={20} fill="#75aadb" />
          <circle cx={45} cy={30} r={7} fill="#f5c518" stroke="#8a5a2b" strokeWidth={1} />
        </>
      )}
      {id === "brasil" && (
        <>
          <rect x={0} y={0} width={90} height={60} fill="#2e7d4f" />
          <path d="M45 8 L84 30 L45 52 L6 30 Z" fill="#f5c518" />
          <circle cx={45} cy={30} r={12} fill="#2b5fa8" />
        </>
      )}
      {id === "francia" && (
        <>
          <rect x={0} y={0} width={30} height={60} fill="#3b5998" />
          <rect x={30} y={0} width={30} height={60} fill="#fff" />
          <rect x={60} y={0} width={30} height={60} fill="#c1272d" />
        </>
      )}
      {id === "japon" && <circle cx={45} cy={30} r={16} fill="#c1272d" />}
    </svg>
  );
}

export default function BanderasPage() {
  return (
    <MaterialQuiz
      slug="banderas"
      items={ITEMS}
      pregunta="¿De qué país es esta bandera?"
      render={(item, grande) => <Bandera id={item.id} size={grande ? 140 : 68} />}
    />
  );
}
