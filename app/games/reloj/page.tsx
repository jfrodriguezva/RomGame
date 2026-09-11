"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { HORAS } from "@/data/levels/reloj";

const ITEMS: ItemQuiz[] = HORAS.map((h) => ({
  id: h.id,
  visual: h.id,
  nombre: h.label,
  familia: "reloj",
}));

function Reloj({ hora, size }: { hora: number; size: number }) {
  const anguloHora = ((hora % 12) * 30 - 90) * (Math.PI / 180);
  const xHora = 50 + 22 * Math.cos(anguloHora);
  const yHora = 50 + 22 * Math.sin(anguloHora);

  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <circle cx={50} cy={50} r={44} fill="#fff" stroke="#8a5a2b" strokeWidth={3} />
      {Array.from({ length: 12 }).map((_, i) => {
        const a = ((i * 30 - 90) * Math.PI) / 180;
        const x = 50 + 36 * Math.cos(a);
        const y = 50 + 36 * Math.sin(a);
        return <circle key={i} cx={x} cy={y} r={2} fill="#8a5a2b" />;
      })}
      <line x1={50} y1={50} x2={xHora} y2={yHora} stroke="#8a5a2b" strokeWidth={4} strokeLinecap="round" />
      <line x1={50} y1={50} x2={50} y2={16} stroke="#c98a4b" strokeWidth={2.5} strokeLinecap="round" />
      <circle cx={50} cy={50} r={3.5} fill="#8a5a2b" />
    </svg>
  );
}

export default function RelojPage() {
  return (
    <MaterialQuiz
      slug="reloj"
      items={ITEMS}
      pregunta="¿Qué hora es?"
      render={(item, grande) => {
        const h = HORAS.find((x) => x.id === item.id)!;
        return <Reloj hora={h.hora} size={grande ? 110 : 56} />;
      }}
    />
  );
}
