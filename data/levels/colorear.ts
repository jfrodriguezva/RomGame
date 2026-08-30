import { levels100, phasedInt, seeded, stageOf } from "@/lib/levels";

/**
 * Páginas para colorear.
 *
 * Las primeras etapas traen dibujos figurativos con pocas zonas grandes, que
 * es lo que una mano de tres años puede llenar sin frustrarse. A partir de la
 * mitad aparecen mandalas generados con geometría: el número de anillos y de
 * sectores crece nivel a nivel, así que hay cien láminas distintas y cada una
 * pide un poco más de paciencia que la anterior.
 */

export interface Region {
  id: string;
  d: string;
}

export interface Dibujo {
  id: string;
  nombre: string;
  regiones: Region[];
  /** Trazos decorativos que no se colorean (ojos, tallos, detalles). */
  detalles?: string[];
}

// ---------------------------------------------------------------------------
// Dibujos figurativos
// ---------------------------------------------------------------------------

export const DIBUJOS: Dibujo[] = [
  {
    id: "sol",
    nombre: "El sol",
    regiones: [
      { id: "centro", d: "M100 55 A45 45 0 1 1 99.9 55 Z" },
      { id: "rayo1", d: "M100 8 L112 46 H88 Z" },
      { id: "rayo2", d: "M192 100 L154 112 V88 Z" },
      { id: "rayo3", d: "M100 192 L88 154 H112 Z" },
      { id: "rayo4", d: "M8 100 L46 88 V112 Z" },
      { id: "rayo5", d: "M165 35 L142 66 L134 58 Z" },
      { id: "rayo6", d: "M165 165 L134 142 L142 134 Z" },
      { id: "rayo7", d: "M35 165 L58 134 L66 142 Z" },
      { id: "rayo8", d: "M35 35 L66 58 L58 66 Z" },
    ],
    detalles: ["M84 92 a4 4 0 1 0 0.1 0", "M116 92 a4 4 0 1 0 0.1 0", "M82 115 q18 16 36 0"],
  },
  {
    id: "casa",
    nombre: "La casa",
    regiones: [
      { id: "techo", d: "M100 22 L182 88 H18 Z" },
      { id: "pared", d: "M34 88 H166 V180 H34 Z" },
      { id: "puerta", d: "M86 122 H114 V180 H86 Z" },
      { id: "ventana-i", d: "M50 104 H74 V128 H50 Z" },
      { id: "ventana-d", d: "M126 104 H150 V128 H126 Z" },
      { id: "chimenea", d: "M136 34 H154 V62 H136 Z" },
      { id: "pasto", d: "M4 180 H196 V194 H4 Z" },
    ],
  },
  {
    id: "flor",
    nombre: "La flor",
    regiones: [
      { id: "petalo1", d: "M100 30 C126 30 134 62 100 84 C66 62 74 30 100 30 Z" },
      { id: "petalo2", d: "M156 74 C164 99 136 118 106 100 C118 66 148 56 156 74 Z" },
      { id: "petalo3", d: "M134 142 C114 158 84 142 92 108 C126 106 146 122 134 142 Z" },
      { id: "petalo4", d: "M66 142 C54 122 74 106 108 108 C116 142 86 158 66 142 Z" },
      { id: "petalo5", d: "M44 74 C52 56 82 66 94 100 C64 118 36 99 44 74 Z" },
      { id: "centro", d: "M100 82 a18 18 0 1 0 0.1 0 Z" },
      { id: "tallo", d: "M94 116 H106 V190 H94 Z" },
      { id: "hoja", d: "M106 150 C138 134 152 152 130 168 C116 178 106 166 106 150 Z" },
    ],
  },
  {
    id: "pez",
    nombre: "El pez",
    regiones: [
      { id: "cuerpo", d: "M32 100 C58 52 132 52 152 100 C132 148 58 148 32 100 Z" },
      { id: "cola", d: "M152 100 L190 66 V134 Z" },
      { id: "aleta-sup", d: "M84 62 C96 34 118 40 118 62 Z" },
      { id: "aleta-inf", d: "M84 138 C96 166 118 160 118 138 Z" },
      { id: "burbuja1", d: "M28 60 a9 9 0 1 0 0.1 0 Z" },
      { id: "burbuja2", d: "M14 40 a6 6 0 1 0 0.1 0 Z" },
    ],
    detalles: ["M66 92 a5 5 0 1 0 0.1 0"],
  },
  {
    id: "mariposa",
    nombre: "La mariposa",
    regiones: [
      { id: "ala-si", d: "M96 96 C60 40 12 44 18 84 C22 112 62 116 96 96 Z" },
      { id: "ala-sd", d: "M104 96 C140 40 188 44 182 84 C178 112 138 116 104 96 Z" },
      { id: "ala-ii", d: "M96 104 C62 140 24 148 26 118 C28 100 68 104 96 104 Z" },
      { id: "ala-id", d: "M104 104 C138 140 176 148 174 118 C172 100 132 104 104 104 Z" },
      { id: "cuerpo", d: "M94 52 C106 52 110 60 106 148 H94 C90 60 82 52 94 52 Z" },
    ],
    detalles: ["M96 52 L78 26", "M104 52 L122 26"],
  },
  {
    id: "arbol",
    nombre: "El árbol",
    regiones: [
      { id: "copa1", d: "M100 16 L156 82 H44 Z" },
      { id: "copa2", d: "M100 54 L170 128 H30 Z" },
      { id: "tronco", d: "M86 128 H114 V186 H86 Z" },
      { id: "pasto", d: "M6 186 H194 V196 H6 Z" },
      { id: "manzana1", d: "M70 100 a8 8 0 1 0 0.1 0 Z" },
      { id: "manzana2", d: "M128 108 a8 8 0 1 0 0.1 0 Z" },
    ],
  },
  {
    id: "barco",
    nombre: "El barco",
    regiones: [
      { id: "casco", d: "M22 130 H178 L154 176 H46 Z" },
      { id: "vela-g", d: "M100 24 L100 122 H30 Z" },
      { id: "vela-p", d: "M108 46 L108 122 H164 Z" },
      { id: "mastil", d: "M96 18 H104 V128 H96 Z" },
      { id: "mar", d: "M4 176 H196 V196 H4 Z" },
      { id: "sol", d: "M168 40 a20 20 0 1 0 0.1 0 Z" },
    ],
  },
  {
    id: "gato",
    nombre: "El gatito",
    regiones: [
      { id: "cabeza", d: "M100 54 C142 54 160 86 160 112 C160 148 132 170 100 170 C68 170 40 148 40 112 C40 86 58 54 100 54 Z" },
      { id: "oreja-i", d: "M46 74 L52 24 L88 56 Z" },
      { id: "oreja-d", d: "M154 74 L148 24 L112 56 Z" },
      { id: "hocico", d: "M78 118 C90 108 110 108 122 118 C122 140 78 140 78 118 Z" },
      { id: "cuerpo", d: "M62 168 C62 190 138 190 138 168 Z" },
    ],
    detalles: [
      "M76 104 a6 6 0 1 0 0.1 0",
      "M124 104 a6 6 0 1 0 0.1 0",
      "M92 124 h16 l-8 8 z",
      "M40 122 H10",
      "M40 134 H14",
      "M160 122 H190",
      "M160 134 H186",
    ],
  },
];

// ---------------------------------------------------------------------------
// Mandalas generados
// ---------------------------------------------------------------------------

function punto(cx: number, cy: number, r: number, ang: number): [number, number] {
  return [cx + r * Math.cos(ang), cy + r * Math.sin(ang)];
}

function sectorAnular(
  cx: number,
  cy: number,
  rInt: number,
  rExt: number,
  a1: number,
  a2: number
): string {
  const [x1, y1] = punto(cx, cy, rExt, a1);
  const [x2, y2] = punto(cx, cy, rExt, a2);
  const [x3, y3] = punto(cx, cy, rInt, a2);
  const [x4, y4] = punto(cx, cy, rInt, a1);
  const grande = a2 - a1 > Math.PI ? 1 : 0;
  return [
    `M${x1.toFixed(2)} ${y1.toFixed(2)}`,
    `A${rExt} ${rExt} 0 ${grande} 1 ${x2.toFixed(2)} ${y2.toFixed(2)}`,
    `L${x3.toFixed(2)} ${y3.toFixed(2)}`,
    `A${rInt} ${rInt} 0 ${grande} 0 ${x4.toFixed(2)} ${y4.toFixed(2)}`,
    "Z",
  ].join(" ");
}

/** Mandala determinista: el mismo nivel produce siempre la misma lámina. */
export function mandala(level: number, anillos: number, sectores: number): Dibujo {
  const cx = 100;
  const cy = 100;
  const rMax = 94;
  const regiones: Region[] = [{ id: "centro", d: `M${cx} ${cy - 16} a16 16 0 1 0 0.1 0 Z` }];

  for (let anillo = 0; anillo < anillos; anillo++) {
    const rInt = 16 + ((rMax - 16) * anillo) / anillos;
    const rExt = 16 + ((rMax - 16) * (anillo + 1)) / anillos;
    // Cada anillo se gira un poco: el mandala deja de verse como una reja.
    const giro = (Math.PI / sectores) * (anillo % 2) + seeded(level, anillo) * 0.2;
    const n = anillo % 2 === 0 ? sectores : Math.max(4, Math.round(sectores * 0.75));
    for (let s = 0; s < n; s++) {
      const a1 = giro + (Math.PI * 2 * s) / n;
      const a2 = giro + (Math.PI * 2 * (s + 1)) / n;
      regiones.push({
        id: `a${anillo}-s${s}`,
        d: sectorAnular(cx, cy, rInt, rExt, a1, a2),
      });
    }
  }

  return { id: `mandala-${level}`, nombre: "Mandala", regiones };
}

// ---------------------------------------------------------------------------
// Niveles
// ---------------------------------------------------------------------------

export interface ColorearLevel {
  level: number;
  /** Cuántos colores ofrece la paleta. */
  colores: number;
  anillos: number;
  sectores: number;
  /** A partir de la etapa 4 las láminas son mandalas generados. */
  esMandala: boolean;
}

export const COLOREAR_LEVELS: ColorearLevel[] = levels100((_, level) => ({
  colores: phasedInt(level, [4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24]),
  anillos: phasedInt(level, [2, 2, 2, 2, 3, 3, 4, 4, 5, 5, 6]),
  sectores: phasedInt(level, [6, 6, 8, 8, 10, 12, 12, 14, 16, 18, 20]),
  esMandala: stageOf(level) >= 4,
}));

/** Lámina que corresponde a un nivel. */
export function dibujoDeNivel(level: number): Dibujo {
  const config = COLOREAR_LEVELS[Math.min(COLOREAR_LEVELS.length, Math.max(1, level)) - 1];
  if (!config.esMandala) return DIBUJOS[(level - 1) % DIBUJOS.length];
  return mandala(level, config.anillos, config.sectores);
}
