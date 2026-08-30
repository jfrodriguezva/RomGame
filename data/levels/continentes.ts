import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * Mapa de continentes.
 *
 * Los colores son los del material Montessori, y no son decorativos: el niño
 * los reconoce después en el globo terráqueo, en las banderas y en las
 * carpetas de cada continente. Naranja es América del Norte para siempre.
 */
export interface Continente {
  id: string;
  nombre: string;
  color: string;
  /** Contorno simplificado en un lienzo de 200 x 100. */
  path: string;
  animales: string[];
  dato: string;
}

export const CONTINENTES: Continente[] = [
  {
    id: "norteamerica",
    nombre: "América del Norte",
    color: "#e08a3c",
    path: "M12 14 L58 8 L68 26 L52 38 L44 60 L30 46 L14 32 Z",
    animales: ["🦬", "🦅", "🐻‍❄️", "🦫"],
    dato: "Aquí están Canadá, Estados Unidos y México.",
  },
  {
    id: "sudamerica",
    nombre: "América del Sur",
    color: "#e29ab5",
    path: "M46 60 L62 56 L68 78 L56 96 L44 82 Z",
    animales: ["🦙", "🦥", "🦜", "🐆"],
    dato: "Aquí está la selva más grande del mundo, el Amazonas.",
  },
  {
    id: "europa",
    nombre: "Europa",
    color: "#cf4f4f",
    path: "M84 12 L112 8 L118 26 L96 36 L82 28 Z",
    animales: ["🦌", "🐺", "🦉", "🐐"],
    dato: "Es el continente más pequeño después de Oceanía.",
  },
  {
    id: "africa",
    nombre: "África",
    color: "#5f9e5f",
    path: "M86 38 L118 34 L124 60 L104 88 L90 66 Z",
    animales: ["🦁", "🐘", "🦒", "🦓"],
    dato: "Ahí vive el animal terrestre más grande: el elefante.",
  },
  {
    id: "asia",
    nombre: "Asia",
    color: "#e3c34a",
    path: "M120 6 L182 12 L188 42 L146 54 L122 34 Z",
    animales: ["🐼", "🐅", "🐫", "🦧"],
    dato: "Es el continente más grande y donde vive más gente.",
  },
  {
    id: "oceania",
    nombre: "Oceanía",
    color: "#a9743c",
    path: "M156 62 L184 60 L190 78 L162 84 Z",
    animales: ["🦘", "🐨", "🦆", "🐊"],
    dato: "Está formado por Australia y muchísimas islas.",
  },
  {
    id: "antartida",
    nombre: "Antártida",
    color: "#c9d6dd",
    path: "M22 90 L180 90 L174 100 L28 100 Z",
    animales: ["🐧", "🦭", "🐋", "❄️"],
    dato: "Es el lugar más frío de la Tierra y casi nadie vive ahí.",
  },
];

export type ModoContinente = "ubicar" | "nombrar" | "animales";

export interface ContinentesLevel {
  level: number;
  modo: ModoContinente;
  opciones: number;
  rondas: number;
  /** Cada continente conserva su color como pista. */
  conColor: boolean;
}

export const CONTINENTES_LEVELS: ContinentesLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  return {
    modo: etapa <= 3 ? "ubicar" : etapa <= 6 ? "nombrar" : "animales",
    opciones: phasedInt(level, [3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7]),
    rondas: phasedInt(level, [3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7]),
    conColor: etapa <= 8,
  };
});
