import { QUIZ_LEVELS, type QuizLevel } from "./quiz";

/**
 * Gabinete de geometría.
 *
 * Las figuras se dibujan en SVG y no con emojis: el niño necesita ver el
 * contorno exacto para poder recorrerlo con el dedo.
 *
 * El orden del arreglo es el orden de presentación, y el material solo ofrece
 * las primeras figuras hasta que el niño avanza: círculo, cuadrado y triángulo
 * en el nivel 1; el triángulo escaleno mucho después. Las familias sirven para
 * las últimas etapas, cuando el material pregunta solo entre triángulos o solo
 * entre cuadriláteros y hay que hilar fino.
 */
export type FamiliaForma = "curvas" | "triangulos" | "cuadrilateros" | "poligonos" | "otras";

export interface FormaDef {
  id: string;
  label: string;
  familia: FamiliaForma;
  /** Trazo en un lienzo de 100 x 100. */
  path: string;
}

export const FORMAS: FormaDef[] = [
  // Las primeras, las que se nombran a los tres años
  { id: "circulo", label: "Círculo", familia: "curvas", path: "M50 8 A42 42 0 1 1 49.9 8 Z" },
  { id: "cuadrado", label: "Cuadrado", familia: "cuadrilateros", path: "M14 14 H86 V86 H14 Z" },
  { id: "equilatero", label: "Triángulo", familia: "triangulos", path: "M50 10 L89 82 H11 Z" },
  { id: "rectangulo", label: "Rectángulo", familia: "cuadrilateros", path: "M8 28 H92 V72 H8 Z" },
  { id: "estrella", label: "Estrella", familia: "otras", path: "M50 6 L61 38 H95 L67 58 L78 92 L50 71 L22 92 L33 58 L5 38 H39 Z" },
  { id: "corazon", label: "Corazón", familia: "otras", path: "M50 90 C6 60 8 22 32 18 C42 16 49 24 50 32 C51 24 58 16 68 18 C92 22 94 60 50 90 Z" },

  // Se suman poco a poco
  { id: "ovalo", label: "Óvalo", familia: "curvas", path: "M50 12 C78 12 90 30 90 50 C90 70 78 88 50 88 C22 88 10 70 10 50 C10 30 22 12 50 12 Z" },
  { id: "rombo", label: "Rombo", familia: "cuadrilateros", path: "M50 8 L88 50 L50 92 L12 50 Z" },
  { id: "cruz", label: "Cruz", familia: "otras", path: "M36 8 H64 V36 H92 V64 H64 V92 H36 V64 H8 V36 H36 Z" },
  { id: "semicirculo", label: "Semicírculo", familia: "curvas", path: "M8 70 A42 42 0 0 1 92 70 Z" },
  { id: "pentagono", label: "Pentágono", familia: "poligonos", path: "M50 8 L90 38 L75 86 H25 L10 38 Z" },
  { id: "hexagono", label: "Hexágono", familia: "poligonos", path: "M30 12 H70 L90 50 L70 88 H30 L10 50 Z" },
  { id: "trapecio", label: "Trapecio", familia: "cuadrilateros", path: "M28 22 H72 L90 78 H10 Z" },
  { id: "octagono", label: "Octágono", familia: "poligonos", path: "M33 8 H67 L92 33 V67 L67 92 H33 L8 67 V33 Z" },
  { id: "paralelogramo", label: "Paralelogramo", familia: "cuadrilateros", path: "M28 24 H94 L72 76 H6 Z" },
  { id: "elipse", label: "Elipse", familia: "curvas", path: "M50 26 C82 26 96 36 96 50 C96 64 82 74 50 74 C18 74 4 64 4 50 C4 36 18 26 50 26 Z" },

  // Las finas: distinguir un triángulo de otro triángulo
  { id: "isosceles", label: "Triángulo isósceles", familia: "triangulos", path: "M50 8 L76 86 H24 Z" },
  { id: "rectangulo-tri", label: "Triángulo rectángulo", familia: "triangulos", path: "M14 86 V14 L86 86 Z" },
  { id: "escaleno", label: "Triángulo escaleno", familia: "triangulos", path: "M18 84 L34 16 L90 62 Z" },
  { id: "obtusangulo", label: "Triángulo obtusángulo", familia: "triangulos", path: "M8 74 L92 74 L66 30 Z" },
];

export type FormasLevel = QuizLevel;
export const FORMAS_LEVELS: FormasLevel[] = QUIZ_LEVELS;
