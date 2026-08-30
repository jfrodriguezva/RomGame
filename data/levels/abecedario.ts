import { QUIZ_LEVELS, type QuizLevel } from "./quiz";

/**
 * Letra, sonido y una palabra que empieza con ella.
 *
 * Las familias agrupan las letras que se confunden entre sí: b/d/p/q se ven
 * casi iguales giradas, y m/n/ñ suenan parecido. En las últimas etapas el
 * material pregunta solo dentro de esos grupos, que es donde está la
 * dificultad real de aprender a leer.
 */
export type FamiliaLetra = "vocales" | "espejo" | "nasales" | "curvas" | "palos" | "silbantes";

export interface LetraDef {
  letter: string;
  word: string;
  emoji: string;
  familia: FamiliaLetra;
}

export const ABECEDARIO: LetraDef[] = [
  { letter: "A", word: "avión", emoji: "✈️", familia: "vocales" },
  { letter: "E", word: "estrella", emoji: "⭐", familia: "vocales" },
  { letter: "I", word: "isla", emoji: "🏝️", familia: "vocales" },
  { letter: "O", word: "oso", emoji: "🐻", familia: "vocales" },
  { letter: "U", word: "uva", emoji: "🍇", familia: "vocales" },

  { letter: "B", word: "barco", emoji: "🚢", familia: "espejo" },
  { letter: "D", word: "dado", emoji: "🎲", familia: "espejo" },
  { letter: "P", word: "pato", emoji: "🦆", familia: "espejo" },
  { letter: "Q", word: "queso", emoji: "🧀", familia: "espejo" },

  { letter: "M", word: "mariposa", emoji: "🦋", familia: "nasales" },
  { letter: "N", word: "nube", emoji: "☁️", familia: "nasales" },
  { letter: "Ñ", word: "ñu", emoji: "🦬", familia: "nasales" },

  { letter: "C", word: "casa", emoji: "🏠", familia: "curvas" },
  { letter: "G", word: "gato", emoji: "🐱", familia: "curvas" },
  { letter: "J", word: "jirafa", emoji: "🦒", familia: "curvas" },
  { letter: "S", word: "sol", emoji: "☀️", familia: "curvas" },

  { letter: "L", word: "león", emoji: "🦁", familia: "palos" },
  { letter: "T", word: "tren", emoji: "🚂", familia: "palos" },
  { letter: "F", word: "flor", emoji: "🌸", familia: "palos" },
  { letter: "H", word: "helado", emoji: "🍦", familia: "palos" },
  { letter: "K", word: "kiwi", emoji: "🥝", familia: "palos" },
  { letter: "R", word: "ratón", emoji: "🐭", familia: "palos" },

  { letter: "V", word: "vaca", emoji: "🐄", familia: "silbantes" },
  { letter: "W", word: "waffle", emoji: "🧇", familia: "silbantes" },
  { letter: "X", word: "xilófono", emoji: "🎼", familia: "silbantes" },
  { letter: "Y", word: "yoyo", emoji: "🪀", familia: "silbantes" },
  { letter: "Z", word: "zapato", emoji: "👟", familia: "silbantes" },
];

export type AbecedarioLevel = QuizLevel;
export const ABECEDARIO_LEVELS: AbecedarioLevel[] = QUIZ_LEVELS;
