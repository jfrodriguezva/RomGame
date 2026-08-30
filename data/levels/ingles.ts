import { QUIZ_LEVELS, type QuizLevel } from "./quiz";

/**
 * Inglés mínimo, a propósito.
 *
 * Treinta palabras concretas y nada más: colores, animales, familia, comida y
 * los números del uno al cinco. Un niño de tres a seis años está construyendo
 * su lengua materna; el segundo idioma entra despacio, con objetos que puede
 * señalar. Por eso no hay frases, ni gramática, ni traducción escrita larga.
 */
export interface InglesWord {
  en: string;
  es: string;
  emoji: string;
  category: "colores" | "animales" | "numeros" | "familia" | "comida";
}

export const INGLES_WORDS: InglesWord[] = [
  { en: "Red", es: "rojo", emoji: "🔴", category: "colores" },
  { en: "Blue", es: "azul", emoji: "🔵", category: "colores" },
  { en: "Yellow", es: "amarillo", emoji: "🟡", category: "colores" },
  { en: "Green", es: "verde", emoji: "🟢", category: "colores" },
  { en: "Black", es: "negro", emoji: "⚫", category: "colores" },
  { en: "White", es: "blanco", emoji: "⚪", category: "colores" },

  { en: "Dog", es: "perro", emoji: "🐶", category: "animales" },
  { en: "Cat", es: "gato", emoji: "🐱", category: "animales" },
  { en: "Bird", es: "pájaro", emoji: "🐦", category: "animales" },
  { en: "Fish", es: "pez", emoji: "🐟", category: "animales" },
  { en: "Cow", es: "vaca", emoji: "🐄", category: "animales" },
  { en: "Horse", es: "caballo", emoji: "🐴", category: "animales" },

  { en: "One", es: "uno", emoji: "1️⃣", category: "numeros" },
  { en: "Two", es: "dos", emoji: "2️⃣", category: "numeros" },
  { en: "Three", es: "tres", emoji: "3️⃣", category: "numeros" },
  { en: "Four", es: "cuatro", emoji: "4️⃣", category: "numeros" },
  { en: "Five", es: "cinco", emoji: "5️⃣", category: "numeros" },

  { en: "Mom", es: "mamá", emoji: "👩", category: "familia" },
  { en: "Dad", es: "papá", emoji: "👨", category: "familia" },
  { en: "Baby", es: "bebé", emoji: "👶", category: "familia" },
  { en: "Home", es: "casa", emoji: "🏠", category: "familia" },
  { en: "Hand", es: "mano", emoji: "✋", category: "familia" },
  { en: "Sun", es: "sol", emoji: "☀️", category: "familia" },

  { en: "Apple", es: "manzana", emoji: "🍎", category: "comida" },
  { en: "Banana", es: "plátano", emoji: "🍌", category: "comida" },
  { en: "Bread", es: "pan", emoji: "🍞", category: "comida" },
  { en: "Milk", es: "leche", emoji: "🥛", category: "comida" },
  { en: "Water", es: "agua", emoji: "💧", category: "comida" },
  { en: "Egg", es: "huevo", emoji: "🥚", category: "comida" },
];

export type InglesLevel = QuizLevel;
export const INGLES_LEVELS: InglesLevel[] = QUIZ_LEVELS;
