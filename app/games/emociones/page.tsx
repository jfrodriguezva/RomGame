"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { EMOCIONES } from "@/data/levels/emociones";

const ITEMS: ItemQuiz[] = EMOCIONES.map((e) => ({
  id: e.id,
  visual: e.emoji,
  nombre: e.label,
  familia: e.familia,
}));

export default function EmocionesPage() {
  return <MaterialQuiz slug="emociones" items={ITEMS} pregunta="¿Cómo se siente?" />;
}
