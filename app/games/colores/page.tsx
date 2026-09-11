"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { COLORES } from "@/data/levels/colores";

const ITEMS: ItemQuiz[] = COLORES.map((c) => ({
  id: c.id,
  visual: c.id,
  nombre: c.label,
  familia: "colores",
}));

function Mancha({ hex, size }: { hex: string; size: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <circle cx={50} cy={50} r={42} fill={hex} stroke="#00000022" strokeWidth={3} />
    </svg>
  );
}

export default function ColoresPage() {
  return (
    <MaterialQuiz
      slug="colores"
      items={ITEMS}
      pregunta="¿De qué color es?"
      render={(item, grande) => {
        const c = COLORES.find((x) => x.id === item.id)!;
        return <Mancha hex={c.hex} size={grande ? 100 : 52} />;
      }}
    />
  );
}
