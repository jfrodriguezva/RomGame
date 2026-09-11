"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { SOLIDOS } from "@/data/levels/solidos";

const ITEMS: ItemQuiz[] = SOLIDOS.map((s) => ({
  id: s.id,
  visual: s.id,
  nombre: s.label,
  familia: "solidos",
}));

const CLARO = "#e0b586";
const OSCURO = "#8a5a2b";
const TRAZO = "#5f3e1c";

function Solido({ id, size }: { id: string; size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      {id === "esfera" && (
        <>
          <circle cx={50} cy={50} r={38} fill={CLARO} stroke={TRAZO} strokeWidth={3} />
          <path d="M18 50 A32 18 0 0 0 82 50" fill="none" stroke={TRAZO} strokeWidth={1.5} opacity={0.5} />
          <ellipse cx={38} cy={36} rx={12} ry={8} fill="#fff" opacity={0.35} />
        </>
      )}
      {id === "cubo" && (
        <>
          <polygon points="28,30 68,30 82,44 42,44" fill={CLARO} stroke={TRAZO} strokeWidth={2.5} />
          <polygon points="42,44 82,44 82,80 42,80" fill={OSCURO} stroke={TRAZO} strokeWidth={2.5} />
          <polygon points="28,30 42,44 42,80 28,66" fill="#c9975c" stroke={TRAZO} strokeWidth={2.5} />
        </>
      )}
      {id === "cono" && (
        <>
          <ellipse cx={50} cy={78} rx={30} ry={9} fill={OSCURO} stroke={TRAZO} strokeWidth={2.5} />
          <path d="M50 14 L20 78 A30 9 0 0 0 80 78 Z" fill={CLARO} stroke={TRAZO} strokeWidth={2.5} />
        </>
      )}
      {id === "cilindro" && (
        <>
          <rect x={20} y={28} width={60} height={44} fill={CLARO} stroke={TRAZO} strokeWidth={2.5} />
          <ellipse cx={50} cy={72} rx={30} ry={9} fill={OSCURO} stroke={TRAZO} strokeWidth={2.5} />
          <ellipse cx={50} cy={28} rx={30} ry={9} fill="#f3dfc3" stroke={TRAZO} strokeWidth={2.5} />
        </>
      )}
      {id === "piramide" && (
        <>
          <polygon points="50,12 20,78 50,78" fill={CLARO} stroke={TRAZO} strokeWidth={2.5} />
          <polygon points="50,12 50,78 80,78" fill={OSCURO} stroke={TRAZO} strokeWidth={2.5} />
        </>
      )}
    </svg>
  );
}

export default function SolidosPage() {
  return (
    <MaterialQuiz
      slug="solidos"
      items={ITEMS}
      pregunta="¿Qué cuerpo geométrico es?"
      render={(item, grande) => <Solido id={item.id} size={grande ? 110 : 56} />}
    />
  );
}
