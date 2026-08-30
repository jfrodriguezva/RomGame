"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { ANIMALS } from "@/data/levels/animales";

const ITEMS: ItemQuiz[] = ANIMALS.map((a) => ({
  id: a.name,
  visual: a.emoji,
  nombre: a.name,
  // La voz dice el nombre y el sonido: el animal "habla" cada vez que aparece.
  vozTexto: `${a.name}. Hace ${a.sound}`,
  familia: a.habitat,
}));

export default function AnimalesPage() {
  return <MaterialQuiz slug="animales" items={ITEMS} pregunta="¿Qué animal es?" />;
}
