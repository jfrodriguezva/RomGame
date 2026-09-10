"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { SENTIDOS } from "@/data/levels/sentidos";

const ITEMS: ItemQuiz[] = SENTIDOS.map((s) => ({
  id: s.id,
  visual: s.emoji,
  nombre: s.label,
  familia: "sentidos",
}));

export default function SentidosPage() {
  return <MaterialQuiz slug="sentidos" items={ITEMS} pregunta="¿Qué sentido es?" />;
}
