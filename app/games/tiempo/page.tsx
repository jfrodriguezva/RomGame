"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { CONDICIONES_CLIMA } from "@/data/levels/tiempo";

const ITEMS: ItemQuiz[] = CONDICIONES_CLIMA.map((c) => ({
  id: c.id,
  visual: c.emoji,
  nombre: c.label,
  familia: "tiempo",
}));

export default function TiempoPage() {
  return <MaterialQuiz slug="tiempo" items={ITEMS} pregunta="¿Qué tiempo hace?" />;
}
