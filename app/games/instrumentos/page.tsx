"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { INSTRUMENTOS } from "@/data/levels/instrumentos";

const ITEMS: ItemQuiz[] = INSTRUMENTOS.map((i) => ({
  id: i.id,
  visual: i.emoji,
  nombre: i.label,
  familia: "instrumentos",
}));

export default function InstrumentosPage() {
  return <MaterialQuiz slug="instrumentos" items={ITEMS} pregunta="¿Qué instrumento es?" />;
}
