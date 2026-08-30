import { levels100, phased } from "@/lib/levels";

export interface Point {
  x: number;
  y: number;
}

// Redondeado a 2 decimales para que el string SVG sea idéntico entre servidor y cliente
// (evita mismatches de hidratación por diferencias de precisión de punto flotante).
function round2(n: number): number {
  return Math.round(n * 100) / 100;
}

function circlePoints(n = 20): Point[] {
  const pts: Point[] = [];
  for (let i = 0; i <= n; i++) {
    const angle = (i / n) * Math.PI * 2 - Math.PI / 2;
    pts.push({ x: round2(50 + 35 * Math.cos(angle)), y: round2(50 + 35 * Math.sin(angle)) });
  }
  return pts;
}

function starPoints(): Point[] {
  const pts: Point[] = [];
  const outer = 38;
  const inner = 16;
  for (let i = 0; i <= 10; i++) {
    const angle = (i / 10) * Math.PI * 2 - Math.PI / 2;
    const r = i % 2 === 0 ? outer : inner;
    pts.push({ x: round2(50 + r * Math.cos(angle)), y: round2(50 + r * Math.sin(angle)) });
  }
  return pts;
}

function heartPoints(): Point[] {
  const pts: Point[] = [];
  const n = 24;
  for (let i = 0; i <= n; i++) {
    const t = (i / n) * Math.PI * 2;
    const x = 16 * Math.pow(Math.sin(t), 3);
    const y = -(13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t));
    pts.push({ x: round2(50 + x * 1.7), y: round2(48 + y * 1.7) });
  }
  return pts;
}

export interface TrazoShape {
  id: string;
  label: string;
  emoji: string;
  points: Point[];
}

export const TRAZOS_SHAPES: TrazoShape[] = [
  { id: "circulo", label: "Círculo", emoji: "⚪", points: circlePoints() },
  { id: "estrella", label: "Estrella", emoji: "⭐", points: starPoints() },
  { id: "corazon", label: "Corazón", emoji: "❤️", points: heartPoints() },
];

export interface TrazosLevel {
  level: number;
  tolerance: number;
}

/**
 * Resaques metalicos: la mano se afina despacio. La tolerancia baja de
 * dieciocho pixeles a cuatro a lo largo de los cien niveles.
 */
export const TRAZOS_LEVELS: TrazosLevel[] = levels100((_, level) => ({
  tolerance: phased(level, [18, 16, 15, 13, 12, 11, 9, 8, 7, 6, 4]),
}));
