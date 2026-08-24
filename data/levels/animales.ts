import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface AnimalDef {
  name: string;
  emoji: string;
  sound: string;
}

export const ANIMALS: AnimalDef[] = [
  { name: "Vaca", emoji: "🐄", sound: "Muuu" },
  { name: "Perro", emoji: "🐶", sound: "Guau" },
  { name: "Gato", emoji: "🐱", sound: "Miau" },
  { name: "Pato", emoji: "🦆", sound: "Cuac" },
  { name: "León", emoji: "🦁", sound: "Roar" },
  { name: "Oveja", emoji: "🐑", sound: "Beee" },
  { name: "Caballo", emoji: "🐴", sound: "Ihaa" },
  { name: "Cerdo", emoji: "🐷", sound: "Oink" },
  { name: "Rana", emoji: "🐸", sound: "Croac" },
  { name: "Abeja", emoji: "🐝", sound: "Bzzz" },
  { name: "Elefante", emoji: "🐘", sound: "Pruum" },
  { name: "Mono", emoji: "🐵", sound: "Uh uh" },
  { name: "Búho", emoji: "🦉", sound: "Uhu" },
  { name: "Lobo", emoji: "🐺", sound: "Auuu" },
  { name: "Tigre", emoji: "🐯", sound: "Grrr" },
  { name: "Pollito", emoji: "🐥", sound: "Pío" },
];

export interface AnimalesLevel {
  level: number;
  count: number;
}

export const ANIMALES_LEVELS: AnimalesLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  count: lerpInt(3, ANIMALS.length, t),
}));
