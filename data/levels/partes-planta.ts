/**
 * Partes de la planta: nomenclatura clásica de botánica Montessori.
 * Raíz y tallo se dibujan a mano (no hay un emoji claro para ninguna de las
 * dos); hoja, flor y fruto ya tienen un emoji reconocible.
 */
export interface ParteVisual {
  id: string;
  label: string;
  emoji?: string;
}

export const PARTES_PLANTA: ParteVisual[] = [
  { id: "raiz", label: "Raíz" },
  { id: "tallo", label: "Tallo" },
  { id: "hoja", label: "Hoja", emoji: "🍃" },
  { id: "flor", label: "Flor", emoji: "🌸" },
  { id: "fruto", label: "Fruto", emoji: "🍎" },
];
