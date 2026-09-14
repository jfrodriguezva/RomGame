import { levels100, phasedInt } from "@/lib/levels";

/**
 * Partes de la cara: se tocan directo sobre el dibujo, no se eligen de una
 * lista de tarjetas. Los pares (ojo, oreja, ceja, mejilla) comparten un
 * mismo id — tocar cualquiera de los dos cuenta como acierto, porque a
 * esta edad la lateralidad izquierda/derecha no es lo que se enseña aquí.
 */
export const PARTES_POOL = [
  "ojo",
  "nariz",
  "boca",
  "oreja",
  "ceja",
  "mejilla",
  "pelo",
  "menton",
] as const;

export type ParteId = (typeof PARTES_POOL)[number];

export const NOMBRE_PARTE: Record<ParteId, { nombre: string; emoji: string }> = {
  ojo: { nombre: "el ojo", emoji: "👁️" },
  nariz: { nombre: "la nariz", emoji: "👃" },
  boca: { nombre: "la boca", emoji: "👄" },
  oreja: { nombre: "la oreja", emoji: "👂" },
  ceja: { nombre: "la ceja", emoji: "🤨" },
  mejilla: { nombre: "la mejilla", emoji: "😊" },
  pelo: { nombre: "el pelo", emoji: "💇" },
  menton: { nombre: "el mentón", emoji: "🙂" },
};

export interface CaraLevel {
  level: number;
  activos: ParteId[];
}

export const CARA_LEVELS: CaraLevel[] = levels100((_, level) => {
  const n = phasedInt(level, [3, 3, 3, 4, 4, 5, 6, 6, 7, 8, 8]);
  return { activos: PARTES_POOL.slice(0, n) };
});
