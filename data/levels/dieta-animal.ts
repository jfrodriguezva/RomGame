import { levels100, phasedInt } from "@/lib/levels";

/** ¿Qué come? Clasificación en tres canastas: herbívoro, carnívoro, omnívoro. */
export interface AnimalDieta {
  emoji: string;
  nombre: string;
  dieta: "herbivoro" | "carnivoro" | "omnivoro";
}

export const ANIMALES_DIETA: AnimalDieta[] = [
  { emoji: "🐰", nombre: "el conejo", dieta: "herbivoro" },
  { emoji: "🐄", nombre: "la vaca", dieta: "herbivoro" },
  { emoji: "🦒", nombre: "la jirafa", dieta: "herbivoro" },

  { emoji: "🦁", nombre: "el león", dieta: "carnivoro" },
  { emoji: "🐺", nombre: "el lobo", dieta: "carnivoro" },
  { emoji: "🦈", nombre: "el tiburón", dieta: "carnivoro" },

  { emoji: "🐻", nombre: "el oso", dieta: "omnivoro" },
  { emoji: "🐷", nombre: "el cerdo", dieta: "omnivoro" },
  { emoji: "🐵", nombre: "el mono", dieta: "omnivoro" },
];

export interface DietaAnimalLevel {
  level: number;
  cantidad: number;
}

export const DIETA_ANIMAL_LEVELS: DietaAnimalLevel[] = levels100((_, level) => ({
  cantidad: Math.min(phasedInt(level, [3, 4, 5, 5, 6, 7, 8, 8, 9, 9, 9]), ANIMALES_DIETA.length),
}));
