import { levels100, phasedInt } from "@/lib/levels";

/**
 * Artículo y sustantivo, la primera pareja gramatical. Se clasifica igual
 * que ¿Vivo o no vivo? o Cuenta las sílabas: se muestra una palabra y hay
 * que soltarla en la canasta correcta.
 */
export interface PalabraGenero {
  emoji: string;
  nombre: string;
  articulo: "el" | "la";
}

export const PALABRAS_GENERO: PalabraGenero[] = [
  { emoji: "☀️", nombre: "sol", articulo: "el" },
  { emoji: "🐶", nombre: "perro", articulo: "el" },
  { emoji: "📕", nombre: "libro", articulo: "el" },
  { emoji: "🍌", nombre: "plátano", articulo: "el" },
  { emoji: "🐻", nombre: "oso", articulo: "el" },
  { emoji: "🐟", nombre: "pez", articulo: "el" },

  { emoji: "🏠", nombre: "casa", articulo: "la" },
  { emoji: "🌙", nombre: "luna", articulo: "la" },
  { emoji: "🌸", nombre: "flor", articulo: "la" },
  { emoji: "🪑", nombre: "silla", articulo: "la" },
  { emoji: "⚽", nombre: "pelota", articulo: "la" },
  { emoji: "🍎", nombre: "manzana", articulo: "la" },
];

export interface ElLaLevel {
  level: number;
  cantidad: number;
}

export const EL_LA_LEVELS: ElLaLevel[] = levels100((_, level) => ({
  cantidad: phasedInt(level, [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12]),
}));
