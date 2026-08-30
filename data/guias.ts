/**
 * Guías punteadas de la pizarra.
 *
 * Son el equivalente digital de los resaques metálicos y las letras de lija:
 * una figura para repasar con el dedo tantas veces como el niño quiera. La
 * guía vive debajo del dibujo, así que el trazo puede salirse sin "arruinar"
 * nada y el borrador nunca la elimina.
 */

export interface Guia {
  id: string;
  nombre: string;
  icono: string;
  tipo: "texto" | "path";
  contenido: string;
}

function letra(l: string): Guia {
  return { id: `l-${l}`, nombre: `Letra ${l}`, icono: l, tipo: "texto", contenido: l };
}

function numero(n: number): Guia {
  return { id: `n-${n}`, nombre: `Número ${n}`, icono: String(n), tipo: "texto", contenido: String(n) };
}

const VOCALES = ["A", "E", "I", "O", "U"].map(letra);

const CONSONANTES = [
  "M", "P", "S", "L", "T", "D", "N", "F", "B", "C",
  "R", "G", "V", "J", "Ñ", "H", "K", "Q", "W", "X", "Y", "Z",
].map(letra);

const NUMEROS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 0].map(numero);

/** Trazos previos a la escritura: líneas, ondas y bucles. */
const FORMAS: Guia[] = [
  {
    id: "f-circulo",
    nombre: "Círculo",
    icono: "○",
    tipo: "path",
    contenido: "M50 12 A38 38 0 1 1 49.9 12 Z",
  },
  {
    id: "f-cuadrado",
    nombre: "Cuadrado",
    icono: "□",
    tipo: "path",
    contenido: "M15 15 H85 V85 H15 Z",
  },
  {
    id: "f-triangulo",
    nombre: "Triángulo",
    icono: "△",
    tipo: "path",
    contenido: "M50 12 L88 84 H12 Z",
  },
  {
    id: "f-rombo",
    nombre: "Rombo",
    icono: "◇",
    tipo: "path",
    contenido: "M50 10 L88 50 L50 90 L12 50 Z",
  },
  {
    id: "f-estrella",
    nombre: "Estrella",
    icono: "☆",
    tipo: "path",
    contenido:
      "M50 8 L61 38 H93 L67 57 L77 88 L50 69 L23 88 L33 57 L7 38 H39 Z",
  },
  {
    id: "f-corazon",
    nombre: "Corazón",
    icono: "♡",
    tipo: "path",
    contenido:
      "M50 86 C10 58 12 24 34 20 C43 18 49 25 50 32 C51 25 57 18 66 20 C88 24 90 58 50 86 Z",
  },
  {
    id: "f-ola",
    nombre: "Ondas",
    icono: "〰",
    tipo: "path",
    contenido: "M8 50 Q23 20 38 50 T68 50 T98 50",
  },
  {
    id: "f-zigzag",
    nombre: "Zigzag",
    icono: "⟋",
    tipo: "path",
    contenido: "M8 70 L26 30 L44 70 L62 30 L80 70 L94 40",
  },
  {
    id: "f-espiral",
    nombre: "Espiral",
    icono: "🌀",
    tipo: "path",
    contenido:
      "M50 50 m0 0 a4 4 0 1 1 6 4 a10 10 0 1 1 -14 6 a18 18 0 1 1 26 -12 a28 28 0 1 1 -40 -8 a38 38 0 1 1 58 22",
  },
  {
    id: "f-bucles",
    nombre: "Bucles",
    icono: "🪢",
    tipo: "path",
    contenido:
      "M10 60 c6 -22 18 -22 24 0 c6 22 18 22 24 0 c6 -22 18 -22 24 0 c4 14 8 16 8 16",
  },
  {
    id: "f-casa",
    nombre: "Casa",
    icono: "🏠",
    tipo: "path",
    contenido: "M20 88 V44 L50 18 L80 44 V88 Z M40 88 V62 H60 V88",
  },
  {
    id: "f-sol",
    nombre: "Sol",
    icono: "☀",
    tipo: "path",
    contenido:
      "M50 28 A22 22 0 1 1 49.9 28 Z M50 4 V14 M50 86 V96 M4 50 H14 M86 50 H96 M18 18 L25 25 M75 75 L82 82 M82 18 L75 25 M25 75 L18 82",
  },
];

export const GUIAS: Guia[] = [...VOCALES, ...CONSONANTES, ...NUMEROS, ...FORMAS];

export const GUIAS_POR_GRUPO = {
  vocales: VOCALES,
  consonantes: CONSONANTES,
  numeros: NUMEROS,
  formas: FORMAS,
};
