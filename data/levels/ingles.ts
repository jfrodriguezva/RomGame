import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface InglesWord {
  en: string;
  emoji: string;
  category: "colores" | "animales" | "numeros" | "formas" | "familia" | "comida";
}

export const INGLES_WORDS: InglesWord[] = [
  { en: "Red", emoji: "🔴", category: "colores" },
  { en: "Blue", emoji: "🔵", category: "colores" },
  { en: "Yellow", emoji: "🟡", category: "colores" },
  { en: "Green", emoji: "🟢", category: "colores" },
  { en: "Purple", emoji: "🟣", category: "colores" },
  { en: "Orange", emoji: "🟠", category: "colores" },
  { en: "Black", emoji: "⚫", category: "colores" },
  { en: "White", emoji: "⚪", category: "colores" },
  { en: "Dog", emoji: "🐶", category: "animales" },
  { en: "Cat", emoji: "🐱", category: "animales" },
  { en: "Bird", emoji: "🐦", category: "animales" },
  { en: "Fish", emoji: "🐟", category: "animales" },
  { en: "Cow", emoji: "🐄", category: "animales" },
  { en: "Pig", emoji: "🐷", category: "animales" },
  { en: "Horse", emoji: "🐴", category: "animales" },
  { en: "Duck", emoji: "🦆", category: "animales" },
  { en: "One", emoji: "1️⃣", category: "numeros" },
  { en: "Two", emoji: "2️⃣", category: "numeros" },
  { en: "Three", emoji: "3️⃣", category: "numeros" },
  { en: "Four", emoji: "4️⃣", category: "numeros" },
  { en: "Five", emoji: "5️⃣", category: "numeros" },
  { en: "Six", emoji: "6️⃣", category: "numeros" },
  { en: "Seven", emoji: "7️⃣", category: "numeros" },
  { en: "Eight", emoji: "8️⃣", category: "numeros" },
  { en: "Nine", emoji: "9️⃣", category: "numeros" },
  { en: "Ten", emoji: "🔟", category: "numeros" },
  { en: "Circle", emoji: "⚪", category: "formas" },
  { en: "Square", emoji: "🟦", category: "formas" },
  { en: "Triangle", emoji: "🔺", category: "formas" },
  { en: "Star", emoji: "⭐", category: "formas" },
  { en: "Heart", emoji: "❤️", category: "formas" },
  { en: "Mom", emoji: "👩", category: "familia" },
  { en: "Dad", emoji: "👨", category: "familia" },
  { en: "Baby", emoji: "👶", category: "familia" },
  { en: "Grandpa", emoji: "👴", category: "familia" },
  { en: "Grandma", emoji: "👵", category: "familia" },
  { en: "Apple", emoji: "🍎", category: "comida" },
  { en: "Banana", emoji: "🍌", category: "comida" },
  { en: "Bread", emoji: "🍞", category: "comida" },
  { en: "Milk", emoji: "🥛", category: "comida" },
  { en: "Water", emoji: "💧", category: "comida" },
];

export interface InglesLevel {
  level: number;
  categories: InglesWord["category"][];
  options: number;
}

const ALL_CATEGORIES: InglesWord["category"][] = [
  "colores",
  "animales",
  "numeros",
  "formas",
  "familia",
  "comida",
];

export const INGLES_LEVELS: InglesLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  categories: ALL_CATEGORIES.slice(0, Math.max(1, lerpInt(1, ALL_CATEGORIES.length, t))),
  options: lerpInt(3, 6, t),
}));
