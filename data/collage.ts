/**
 * Contenido de "Collage libre": categorías de estampas y escenas de fondo.
 * Sin niveles ni curva de dificultad — es una actividad `libre: true`, el
 * mismo trato que la pizarra o el xilófono.
 */

export interface CategoriaEstampas {
  id: string;
  nombre: string;
  items: string[];
}

export const CATEGORIAS_ESTAMPAS: CategoriaEstampas[] = [
  {
    id: "naturaleza",
    nombre: "Naturaleza",
    items: ["🌸", "🌻", "🌼", "🌷", "🍀", "🌿", "🌳", "🍁", "🍄", "🌾"],
  },
  {
    id: "animales",
    nombre: "Animales",
    items: ["🐞", "🦋", "🐢", "🐝", "🐳", "🐬", "🦁", "🐘", "🦒", "🐧", "🦉", "🐝"],
  },
  {
    id: "cielo",
    nombre: "Cielo",
    items: ["☀️", "🌙", "⭐", "🌈", "🌤️", "☁️", "⚡", "❄️"],
  },
  {
    id: "transporte",
    nombre: "Transporte",
    items: ["🚗", "🚲", "🚀", "✈️", "⛵", "🚂"],
  },
  {
    id: "otros",
    nombre: "Otros",
    items: ["❤️", "🎈", "🎀", "⚽", "🍎", "🧸", "🏠", "🎵"],
  },
];

export interface Escena {
  id: string;
  nombre: string;
  css: string;
}

export const ESCENAS: Escena[] = [
  { id: "papel", nombre: "Papel", css: "#fdfaf5" },
  {
    id: "jardin",
    nombre: "Jardín",
    css: "linear-gradient(to bottom, #cfe8c9 0%, #cfe8c9 58%, #a9d68c 58%, #a9d68c 100%)",
  },
  { id: "cielo", nombre: "Cielo", css: "linear-gradient(to bottom, #bcdcf2 0%, #e6f3fb 100%)" },
  {
    id: "mar",
    nombre: "Mar",
    css: "linear-gradient(to bottom, #bcdcf2 0%, #bcdcf2 50%, #6fb3c9 50%, #4f96ad 100%)",
  },
  { id: "espacio", nombre: "Espacio", css: "#1b2340" },
];
