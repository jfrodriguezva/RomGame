/**
 * Ocho barras, una por cada área del ambiente (mismo orden que AREA_ORDER en
 * lib/montessori.ts): el instrumento reúne visualmente el ambiente entero en
 * una sola fila. Sin niveles, sin puntaje: es exploración sonora libre.
 */
export interface BarraXilofono {
  nota: number;
  color: string;
  etiqueta: string;
}

export const BARRAS_XILOFONO: BarraXilofono[] = [
  { nota: 0, color: "#a9c0a0", etiqueta: "Vida práctica" },
  { nota: 1, color: "#d9a7b4", etiqueta: "Sensorial" },
  { nota: 2, color: "#a7b6d9", etiqueta: "Lenguaje" },
  { nota: 3, color: "#e0b586", etiqueta: "Matemáticas" },
  { nota: 4, color: "#93c4ba", etiqueta: "Cultura" },
  { nota: 5, color: "#d8c39a", etiqueta: "Expresión libre" },
  { nota: 6, color: "#bcaed6", etiqueta: "Compañía" },
  { nota: 7, color: "#e2a89f", etiqueta: "Movimiento" },
];
