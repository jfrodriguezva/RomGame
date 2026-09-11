import { levels100, phasedInt, stageOf } from "@/lib/levels";

/** ¿Dónde vive? Clasificación por hábitat: selva, desierto, océano, polo. */
export interface AnimalHabitat {
  emoji: string;
  nombre: string;
  habitat: "selva" | "desierto" | "oceano" | "polo";
}

export const ANIMALES_HABITAT: AnimalHabitat[] = [
  { emoji: "🐒", nombre: "el mono", habitat: "selva" },
  { emoji: "🐆", nombre: "el jaguar", habitat: "selva" },
  { emoji: "🦜", nombre: "el loro", habitat: "selva" },

  { emoji: "🐫", nombre: "el camello", habitat: "desierto" },
  { emoji: "🦂", nombre: "el escorpión", habitat: "desierto" },
  { emoji: "🦎", nombre: "la lagartija", habitat: "desierto" },

  { emoji: "🐬", nombre: "el delfín", habitat: "oceano" },
  { emoji: "🐳", nombre: "la ballena", habitat: "oceano" },
  { emoji: "🐙", nombre: "el pulpo", habitat: "oceano" },

  { emoji: "🐧", nombre: "el pingüino", habitat: "polo" },
  { emoji: "🦭", nombre: "la foca", habitat: "polo" },
];

/** Qué hábitats entran en juego en cada etapa: primero 2, luego los 4. */
const HABITATS_POR_ETAPA: AnimalHabitat["habitat"][][] = [
  ["selva", "oceano"],
  ["selva", "oceano"],
  ["selva", "oceano"],
  ["selva", "desierto", "oceano"],
  ["selva", "desierto", "oceano"],
  ["selva", "desierto", "oceano", "polo"],
  ["selva", "desierto", "oceano", "polo"],
  ["selva", "desierto", "oceano", "polo"],
  ["selva", "desierto", "oceano", "polo"],
  ["selva", "desierto", "oceano", "polo"],
];

export interface HabitatLevel {
  level: number;
  activos: AnimalHabitat["habitat"][];
  cantidad: number;
}

export const HABITAT_LEVELS: HabitatLevel[] = levels100((_, level) => {
  const activos = HABITATS_POR_ETAPA[stageOf(level) - 1];
  const poolMax = ANIMALES_HABITAT.filter((a) => activos.includes(a.habitat)).length;
  return {
    activos,
    cantidad: Math.min(phasedInt(level, [3, 4, 5, 5, 6, 6, 7, 8, 8, 9, 9]), poolMax),
  };
});

export function animalesPara(activos: AnimalHabitat["habitat"][]): AnimalHabitat[] {
  return ANIMALES_HABITAT.filter((a) => activos.includes(a.habitat));
}
