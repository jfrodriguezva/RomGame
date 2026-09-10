/**
 * Capa pedagógica Montessori.
 *
 * Tres ideas del método guían toda la app y viven en este archivo:
 *
 * 1. Control del error. El material le muestra al niño su propio error sin que
 *    un adulto lo corrija. Aquí eso significa: nunca hay "perdiste", nada
 *    parpadea en rojo, la pieza equivocada simplemente regresa a su lugar.
 * 2. Lección de tres periodos. Así se presenta un concepto nuevo:
 *    nombrar ("esto es el triángulo"), reconocer ("muéstrame el triángulo") y
 *    recordar ("¿qué es esto?"). Cada material la usa según la etapa.
 * 3. Aislar la dificultad. Un material enseña una sola cosa a la vez; las
 *    demás variables se mantienen constantes.
 */

import { stageOf } from "./levels";

// ---------------------------------------------------------------------------
// Áreas del ambiente preparado
// ---------------------------------------------------------------------------

export type Area =
  | "practica"
  | "sensorial"
  | "lenguaje"
  | "matematicas"
  | "cultura"
  | "creativa"
  | "compania"
  | "movimiento";

export interface AreaInfo {
  id: Area;
  label: string;
  emoji: string;
  /** Qué desarrolla, en una línea, para la vista de mamá y papá. */
  proposito: string;
  /** Colores del ambiente: tonos naturales, nunca estridentes. */
  tint: string;
  ring: string;
  text: string;
  chip: string;
  /** El mismo tono de `ring`, en hexadecimal plano para usar fuera de Tailwind
   * (SVG, estilos inline) — por ejemplo, para que la celebración de fin de
   * nivel se sienta del área del material, no de un color genérico. */
  acento: string;
  /** Versión oscura del mismo tono, con contraste suficiente para texto
   * blanco encima (botones sólidos) — `acento` es demasiado pálido para eso. */
  acentoOscuro: string;
}

export const AREAS: Record<Area, AreaInfo> = {
  practica: {
    id: "practica",
    label: "Vida práctica",
    emoji: "🫗",
    proposito: "Coordinación, orden, concentración e independencia.",
    tint: "bg-[#eef3ec]",
    ring: "ring-[#a9c0a0]",
    text: "text-[#4a6b4d]",
    chip: "bg-[#dbe7d6] text-[#3f5c42]",
    acento: "#a9c0a0",
    acentoOscuro: "#4a6b4d",
  },
  sensorial: {
    id: "sensorial",
    label: "Sensorial",
    emoji: "🔴",
    proposito: "Refinar los sentidos: tamaño, forma, color, sonido y textura.",
    tint: "bg-[#f7edf0]",
    ring: "ring-[#d9a7b4]",
    text: "text-[#8a4b5e]",
    chip: "bg-[#f0dbe1] text-[#7d4356]",
    acento: "#d9a7b4",
    acentoOscuro: "#8a4b5e",
  },
  lenguaje: {
    id: "lenguaje",
    label: "Lenguaje",
    emoji: "✍️",
    proposito: "Del sonido a la letra, y de la letra a la primera palabra.",
    tint: "bg-[#eef1f7]",
    ring: "ring-[#a7b6d9]",
    text: "text-[#46578a]",
    chip: "bg-[#dde3f0] text-[#3f4f7d]",
    acento: "#a7b6d9",
    acentoOscuro: "#46578a",
  },
  matematicas: {
    id: "matematicas",
    label: "Matemáticas",
    emoji: "🔢",
    proposito: "Cantidad concreta antes que número abstracto.",
    tint: "bg-[#fdf1e6]",
    ring: "ring-[#e0b586]",
    text: "text-[#8a5a2b]",
    chip: "bg-[#f7e2cd] text-[#7d5227]",
    acento: "#e0b586",
    acentoOscuro: "#8a5a2b",
  },
  cultura: {
    id: "cultura",
    label: "Cultura y naturaleza",
    emoji: "🌍",
    proposito: "El mundo, los seres vivos y el lugar del niño en ellos.",
    tint: "bg-[#eaf4f2]",
    ring: "ring-[#93c4ba]",
    text: "text-[#31665c]",
    chip: "bg-[#d6eae5] text-[#2c5c53]",
    acento: "#93c4ba",
    acentoOscuro: "#31665c",
  },
  creativa: {
    id: "creativa",
    label: "Expresión libre",
    emoji: "🎨",
    proposito: "Crear sin consigna, sin puntaje y sin prisa.",
    tint: "bg-[#f6f0e6]",
    ring: "ring-[#d8c39a]",
    text: "text-[#7a6234]",
    chip: "bg-[#efe3cc] text-[#6f5930]",
    acento: "#d8c39a",
    acentoOscuro: "#7a6234",
  },
  compania: {
    id: "compania",
    label: "Juegos en compañía",
    emoji: "🤝",
    proposito: "Esperar el turno, ganar y perder con gracia, jugar con otro.",
    tint: "bg-[#f2eff7]",
    ring: "ring-[#bcaed6]",
    text: "text-[#5c4a7d]",
    chip: "bg-[#e5deef] text-[#54427a]",
    acento: "#bcaed6",
    acentoOscuro: "#5c4a7d",
  },
  movimiento: {
    id: "movimiento",
    label: "Movimiento y coordinación",
    emoji: "🤸",
    proposito: "Control del cuerpo, reflejos y coordinación ojo-mano.",
    tint: "bg-[#fdf0ef]",
    ring: "ring-[#e2a89f]",
    text: "text-[#8c4a3f]",
    chip: "bg-[#f6ddd9] text-[#7f4238]",
    acento: "#e2a89f",
    acentoOscuro: "#8c4a3f",
  },
};

/** Las seis áreas del ambiente preparado, en el orden en que se presentan. */
export const AREAS_AMBIENTE: Area[] = [
  "practica",
  "sensorial",
  "lenguaje",
  "matematicas",
  "cultura",
  "creativa",
];

/** Actividades que acompañan al ambiente sin pertenecer a un área clásica. */
export const AREAS_EXTRA: Area[] = ["compania", "movimiento"];

export const AREA_ORDER: Area[] = [...AREAS_AMBIENTE, ...AREAS_EXTRA];

// ---------------------------------------------------------------------------
// Lección de tres periodos
// ---------------------------------------------------------------------------

export type Periodo = 1 | 2 | 3;

/**
 * Qué periodo toca según el nivel:
 *  - Etapas 1-2: el material nombra ("esto es...") antes de preguntar.
 *  - Etapas 3-6: reconocer entre varias opciones.
 *  - Etapas 7-10: recordar sin ayuda visual.
 */
export function periodoDeNivel(level: number): Periodo {
  const etapa = stageOf(level);
  if (etapa <= 2) return 1;
  if (etapa <= 6) return 2;
  return 3;
}

export function consignaTresPeriodos(periodo: Periodo, nombre: string): string {
  if (periodo === 1) return `Esto es ${nombre}`;
  if (periodo === 2) return `Muéstrame ${nombre}`;
  return "¿Qué es esto?";
}

// ---------------------------------------------------------------------------
// Control del error: el lenguaje del acierto y del intento
// ---------------------------------------------------------------------------

const ACIERTOS = [
  "Lo lograste",
  "Ahí está",
  "Muy bien",
  "Exacto",
  "Encontraste el lugar",
  "Perfecto",
  "Eso es",
];

/**
 * Nunca decimos "mal" ni "perdiste": invitamos a mirar de nuevo. La frase
 * describe la acción, no juzga al niño.
 */
const INTENTOS = [
  "Casi. Intenta otra vez",
  "Mira de nuevo con calma",
  "Ese no era. Vuelve a probar",
  "Prueba con otro",
  "Tómate tu tiempo",
];

export function fraseAcierto(seed = Date.now()): string {
  return ACIERTOS[Math.abs(Math.floor(seed)) % ACIERTOS.length];
}

export function fraseIntento(seed = Date.now()): string {
  return INTENTOS[Math.abs(Math.floor(seed)) % INTENTOS.length];
}

/** Mensaje al terminar un nivel, según qué tanto ha avanzado el niño. */
export function fraseNivelCompleto(level: number): string {
  if (level === 100) return "Terminaste el material completo";
  if (level % 10 === 0) return `Completaste la etapa ${level / 10}`;
  return fraseAcierto(level);
}
