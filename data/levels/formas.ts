import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface FormaDef {
  id: string;
  label: string;
  emoji: string;
}

export const FORMAS: FormaDef[] = [
  { id: "circulo", label: "Círculo", emoji: "⚪" },
  { id: "cuadrado", label: "Cuadrado", emoji: "🟦" },
  { id: "triangulo", label: "Triángulo", emoji: "🔺" },
  { id: "estrella", label: "Estrella", emoji: "⭐" },
  { id: "corazon", label: "Corazón", emoji: "❤️" },
  { id: "rombo", label: "Rombo", emoji: "🔶" },
  { id: "diamante", label: "Diamante", emoji: "🔷" },
  { id: "cruz", label: "Cruz", emoji: "➕" },
  { id: "luna", label: "Luna", emoji: "🌙" },
  { id: "ovalo", label: "Óvalo", emoji: "🥚" },
];

export interface FormasLevel {
  level: number;
  options: number;
}

export const FORMAS_LEVELS: FormasLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  options: lerpInt(3, FORMAS.length, t),
}));
