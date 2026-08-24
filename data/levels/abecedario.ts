import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface LetraDef {
  letter: string;
  word: string;
  emoji: string;
}

export const ABECEDARIO: LetraDef[] = [
  { letter: "A", word: "Avión", emoji: "✈️" },
  { letter: "B", word: "Barco", emoji: "🚢" },
  { letter: "C", word: "Casa", emoji: "🏠" },
  { letter: "D", word: "Dado", emoji: "🎲" },
  { letter: "E", word: "Estrella", emoji: "⭐" },
  { letter: "F", word: "Flor", emoji: "🌸" },
  { letter: "G", word: "Gato", emoji: "🐱" },
  { letter: "H", word: "Helado", emoji: "🍦" },
  { letter: "I", word: "Isla", emoji: "🏝️" },
  { letter: "J", word: "Jirafa", emoji: "🦒" },
  { letter: "K", word: "Kiwi", emoji: "🥝" },
  { letter: "L", word: "León", emoji: "🦁" },
  { letter: "M", word: "Mariposa", emoji: "🦋" },
  { letter: "N", word: "Nube", emoji: "☁️" },
  { letter: "O", word: "Oso", emoji: "🐻" },
  { letter: "P", word: "Pato", emoji: "🦆" },
  { letter: "Q", word: "Queso", emoji: "🧀" },
  { letter: "R", word: "Ratón", emoji: "🐭" },
  { letter: "S", word: "Sol", emoji: "☀️" },
  { letter: "T", word: "Tren", emoji: "🚂" },
  { letter: "U", word: "Uva", emoji: "🍇" },
  { letter: "V", word: "Vaca", emoji: "🐄" },
  { letter: "W", word: "Waffle", emoji: "🧇" },
  { letter: "X", word: "Xilófono", emoji: "🎼" },
  { letter: "Y", word: "Yoyo", emoji: "🪀" },
  { letter: "Z", word: "Zapato", emoji: "👟" },
];

export interface AbecedarioLevel {
  level: number;
  options: number;
}

export const ABECEDARIO_LEVELS: AbecedarioLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  options: lerpInt(3, 9, t),
}));
