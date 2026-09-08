"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { PARTES_PLANTA } from "@/data/levels/partes-planta";

const ITEMS: ItemQuiz[] = PARTES_PLANTA.map((p) => ({
  id: p.id,
  visual: p.emoji ?? p.id,
  nombre: p.label,
  familia: "planta",
}));

function Raiz({ size }: { size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <path d="M50 6 V46" stroke="#6b8f4e" strokeWidth="6" strokeLinecap="round" />
      <path
        d="M50 46 C40 55 34 60 24 60 M50 46 C60 55 66 62 74 66 M50 46 C46 60 42 70 34 84 M50 46 C54 60 58 72 66 88"
        fill="none"
        stroke="#a9773f"
        strokeWidth="5"
        strokeLinecap="round"
      />
    </svg>
  );
}

function Tallo({ size }: { size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <path d="M50 94 V16" stroke="#6b8f4e" strokeWidth="7" strokeLinecap="round" />
      <path
        d="M50 52 C38 47 30 52 26 61 C38 63 46 59 50 52Z"
        fill="#8fbf6a"
        stroke="#5e7f42"
        strokeWidth="2"
      />
      <path
        d="M50 68 C62 63 70 68 74 77 C62 79 54 75 50 68Z"
        fill="#8fbf6a"
        stroke="#5e7f42"
        strokeWidth="2"
      />
    </svg>
  );
}

function Icono({ id, emoji, size }: { id: string; emoji?: string; size: number }) {
  if (id === "raiz") return <Raiz size={size} />;
  if (id === "tallo") return <Tallo size={size} />;
  return <span style={{ fontSize: size * 0.62, lineHeight: 1 }}>{emoji}</span>;
}

export default function PartesPlantaPage() {
  return (
    <MaterialQuiz
      slug="partes-planta"
      items={ITEMS}
      pregunta="¿Qué parte de la planta es?"
      render={(item, grande) => {
        const parte = PARTES_PLANTA.find((p) => p.id === item.id)!;
        return <Icono id={parte.id} emoji={parte.emoji} size={grande ? 96 : 48} />;
      }}
    />
  );
}
