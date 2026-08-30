"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { FORMAS } from "@/data/levels/formas";

const ITEMS: ItemQuiz[] = FORMAS.map((f) => ({
  id: f.id,
  visual: f.path,
  nombre: f.label,
  familia: f.familia,
}));

/** El contorno importa más que el relleno: por eso la figura va delineada. */
function Figura({ item, grande }: { item: ItemQuiz; grande: boolean }) {
  const lado = grande ? 118 : 58;
  return (
    <svg width={lado} height={lado} viewBox="0 0 100 100" aria-hidden>
      <path
        d={item.visual}
        fill="#f3dbe3"
        stroke="#8a4b5e"
        strokeWidth={grande ? 3 : 4}
        strokeLinejoin="round"
      />
    </svg>
  );
}

export default function FormasPage() {
  return (
    <MaterialQuiz
      slug="formas"
      items={ITEMS}
      pregunta="¿Qué figura es?"
      render={(item, grande) => <Figura item={item} grande={grande} />}
    />
  );
}
