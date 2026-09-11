"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { OFICIOS } from "@/data/levels/oficios";

const ITEMS: ItemQuiz[] = OFICIOS.map((o) => ({
  id: o.id,
  visual: o.emoji,
  nombre: o.label,
  familia: "oficios",
}));

export default function OficiosPage() {
  return <MaterialQuiz slug="oficios" items={ITEMS} pregunta="¿Qué oficio es?" />;
}
