"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { PARTES_CUERPO } from "@/data/levels/cuerpo";

const ITEMS: ItemQuiz[] = PARTES_CUERPO.map((p) => ({
  id: p.id,
  visual: p.emoji ?? p.id,
  nombre: p.label,
  familia: "cuerpo",
}));

function Cabeza({ size }: { size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <circle cx={50} cy={50} r={40} fill="#f3dbe3" stroke="#8a4b5e" strokeWidth={3} />
      <circle cx={36} cy={44} r={5} fill="#8a4b5e" />
      <circle cx={64} cy={44} r={5} fill="#8a4b5e" />
      <path d="M36 66 Q50 76 64 66" stroke="#8a4b5e" strokeWidth={3} fill="none" strokeLinecap="round" />
    </svg>
  );
}

function Brazo({ size }: { size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <path
        d="M30 12 C30 40 40 55 70 80"
        stroke="#f3dbe3"
        strokeWidth={20}
        strokeLinecap="round"
        fill="none"
      />
      <path
        d="M30 12 C30 40 40 55 70 80"
        stroke="#8a4b5e"
        strokeWidth={3}
        fill="none"
        strokeLinecap="round"
      />
    </svg>
  );
}

function Icono({ id, emoji, size }: { id: string; emoji?: string; size: number }) {
  if (id === "cabeza") return <Cabeza size={size} />;
  if (id === "brazo") return <Brazo size={size} />;
  return <span style={{ fontSize: size * 0.62, lineHeight: 1 }}>{emoji}</span>;
}

export default function CuerpoPage() {
  return (
    <MaterialQuiz
      slug="cuerpo"
      items={ITEMS}
      pregunta="¿Qué parte del cuerpo es?"
      render={(item, grande) => {
        const parte = PARTES_CUERPO.find((p) => p.id === item.id)!;
        return <Icono id={parte.id} emoji={parte.emoji} size={grande ? 96 : 48} />;
      }}
    />
  );
}
