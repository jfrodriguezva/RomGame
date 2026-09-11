/** Partes del cuerpo: nomenclatura clásica de conocimiento de sí mismo. */
export interface ParteCuerpo {
  id: string;
  label: string;
  emoji?: string;
}

export const PARTES_CUERPO: ParteCuerpo[] = [
  { id: "cabeza", label: "Cabeza" },
  { id: "ojo", label: "Ojo", emoji: "👁️" },
  { id: "mano", label: "Mano", emoji: "✋" },
  { id: "brazo", label: "Brazo" },
  { id: "pierna", label: "Pierna", emoji: "🦵" },
  { id: "pie", label: "Pie", emoji: "🦶" },
];
