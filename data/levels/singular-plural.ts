import { levels100, phasedInt } from "@/lib/levels";

/**
 * Singular y plural: la palabra va siempre con su cantidad concreta al
 * lado (un emoji repetido), para que la gramática no se separe de lo que
 * representa.
 */
export interface PalabraNumero {
  nombre: string;
  emoji: string;
  forma: "singular" | "plural";
}

export const PALABRAS_NUMERO: PalabraNumero[] = [
  { nombre: "gato", emoji: "🐱", forma: "singular" },
  { nombre: "gatos", emoji: "🐱🐱", forma: "plural" },
  { nombre: "flor", emoji: "🌸", forma: "singular" },
  { nombre: "flores", emoji: "🌸🌸", forma: "plural" },
  { nombre: "pez", emoji: "🐟", forma: "singular" },
  { nombre: "peces", emoji: "🐟🐟", forma: "plural" },
  { nombre: "casa", emoji: "🏠", forma: "singular" },
  { nombre: "casas", emoji: "🏠🏠", forma: "plural" },
  { nombre: "manzana", emoji: "🍎", forma: "singular" },
  { nombre: "manzanas", emoji: "🍎🍎", forma: "plural" },
];

export interface SingularPluralLevel {
  level: number;
  cantidad: number;
}

export const SINGULAR_PLURAL_LEVELS: SingularPluralLevel[] = levels100((_, level) => ({
  cantidad: phasedInt(level, [2, 3, 4, 4, 5, 6, 7, 8, 9, 10, 10]),
}));
