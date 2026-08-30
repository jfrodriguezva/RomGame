/**
 * Trazos de letras y números, escritos como el niño los escribe.
 *
 * No son contornos: son los recorridos del lápiz, en el orden correcto y con
 * el sentido correcto. Una A se traza en dos movimientos (la montaña y luego
 * la barra), y así es como aparece aquí. Lienzo de 100 x 100.
 */

export interface TrazoGlifo {
  /** Cada cadena es un trazo continuo, en el orden en que se escribe. */
  strokes: string[];
}

export const LETRAS: Record<string, TrazoGlifo> = {
  A: { strokes: ["M20 90 L50 12 L80 90", "M32 62 H68"] },
  B: {
    strokes: [
      "M28 12 V90",
      "M28 12 H56 A18 18 0 0 1 56 48 H28",
      "M28 48 H58 A21 21 0 0 1 58 90 H28",
    ],
  },
  C: { strokes: ["M78 26 A34 34 0 1 0 78 74"] },
  D: { strokes: ["M30 12 V90", "M30 12 H52 A39 39 0 0 1 52 90 H30"] },
  E: { strokes: ["M74 12 H30 V90 H74", "M30 51 H64"] },
  F: { strokes: ["M74 12 H30 V90", "M30 51 H64"] },
  G: { strokes: ["M78 26 A34 34 0 1 0 78 70 V52 H56"] },
  H: { strokes: ["M28 12 V90", "M72 12 V90", "M28 51 H72"] },
  I: { strokes: ["M50 12 V90"] },
  J: { strokes: ["M62 12 V70 A20 20 0 0 1 26 78"] },
  K: { strokes: ["M30 12 V90", "M72 12 L34 52 L74 90"] },
  L: { strokes: ["M32 12 V90 H76"] },
  M: { strokes: ["M24 90 V12 L50 58 L76 12 V90"] },
  N: { strokes: ["M26 90 V12 L74 90 V12"] },
  "Ñ": { strokes: ["M26 90 V30 L74 90 V30", "M32 16 q9 -9 18 0 q9 9 18 0"] },
  O: { strokes: ["M50 12 A38 38 0 1 1 49.9 12"] },
  P: { strokes: ["M30 90 V12 H58 A21 21 0 0 1 58 54 H30"] },
  Q: { strokes: ["M50 12 A38 38 0 1 1 49.9 12", "M62 66 L84 92"] },
  R: { strokes: ["M30 90 V12 H58 A20 20 0 0 1 58 52 H30", "M52 52 L76 90"] },
  S: { strokes: ["M76 24 C60 8 26 14 30 34 C34 54 70 48 74 68 C78 88 40 94 24 78"] },
  T: { strokes: ["M20 14 H80", "M50 14 V90"] },
  U: { strokes: ["M28 12 V62 A22 22 0 0 0 72 62 V12"] },
  V: { strokes: ["M22 12 L50 90 L78 12"] },
  W: { strokes: ["M16 12 L34 90 L50 40 L66 90 L84 12"] },
  X: { strokes: ["M26 12 L74 90", "M74 12 L26 90"] },
  Y: { strokes: ["M26 12 L50 52 L74 12", "M50 52 V90"] },
  Z: { strokes: ["M24 14 H76 L24 88 H76"] },
};

export const NUMEROS: Record<string, TrazoGlifo> = {
  "0": { strokes: ["M50 12 A30 39 0 1 1 49.9 12"] },
  "1": { strokes: ["M34 28 L52 12 V90"] },
  "2": { strokes: ["M24 32 A26 26 0 1 1 70 48 L24 90 H78"] },
  "3": { strokes: ["M26 24 A22 22 0 1 1 50 52 A22 22 0 1 1 26 80"] },
  "4": { strokes: ["M66 90 V12 L20 64 H80"] },
  "5": { strokes: ["M74 14 H34 L30 48 A24 24 0 1 1 26 82"] },
  "6": { strokes: ["M70 16 C40 22 30 46 30 62 A22 22 0 1 0 72 62 A21 21 0 0 0 30 62"] },
  "7": { strokes: ["M24 14 H76 L42 90"] },
  "8": {
    strokes: [
      "M50 12 a19 19 0 1 1 0 38 a21 21 0 1 1 0 42 a21 21 0 1 1 0 -42 a19 19 0 1 1 0 -38",
    ],
  },
  "9": { strokes: ["M70 48 A20 20 0 1 0 30 40 A20 20 0 0 0 70 48 C70 70 60 84 34 88"] },
};

export const GLIFOS: Record<string, TrazoGlifo> = { ...LETRAS, ...NUMEROS };

/** Vocales primero: son las que se presentan al inicio. */
export const VOCALES_ORDEN = ["A", "E", "I", "O", "U"];

/**
 * Orden de presentación de las consonantes: primero las que se pueden alargar
 * con la voz (m, s, l, f) porque son más fáciles de aislar en una palabra.
 */
export const CONSONANTES_ORDEN = [
  "M", "S", "L", "F", "N", "P", "T", "D", "R", "C",
  "B", "G", "V", "J", "Z", "H", "Ñ", "K", "W", "X", "Y", "Q",
];

export const ALFABETO_ORDEN = [...VOCALES_ORDEN, ...CONSONANTES_ORDEN];
