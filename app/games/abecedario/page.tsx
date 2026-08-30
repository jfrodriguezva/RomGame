"use client";

import MaterialQuiz, { type ItemQuiz } from "@/components/MaterialQuiz";
import { ABECEDARIO } from "@/data/levels/abecedario";
import { fonemaDe } from "@/lib/speech";

const ITEMS: ItemQuiz[] = ABECEDARIO.map((l) => ({
  id: l.letter,
  visual: l.emoji,
  nombre: l.letter,
  // Montessori enseña el sonido de la letra, no su nombre: "mmm", no "eme".
  vozTexto: `${fonemaDe(l.letter)}, de ${l.word}`,
  familia: l.familia,
}));

export default function AbecedarioPage() {
  return (
    <MaterialQuiz slug="abecedario" items={ITEMS} pregunta="¿Con qué letra empieza?" />
  );
}
