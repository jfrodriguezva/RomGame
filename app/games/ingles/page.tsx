"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { INGLES_WORDS } from "@/data/levels/ingles";

const ITEMS: ItemQuiz[] = INGLES_WORDS.map((w) => ({
  id: w.en,
  visual: w.emoji,
  nombre: w.en,
  // El niño lee la palabra en inglés pero la escucha apoyada en su idioma.
  vozTexto: `${w.es}, en inglés ${w.en}`,
  familia: w.category,
}));

export default function InglesPage() {
  return <MaterialQuiz slug="ingles" items={ITEMS} pregunta="¿Cómo se dice en inglés?" />;
}
