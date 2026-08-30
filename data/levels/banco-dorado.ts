import { levels100, phasedInt, stageOf } from "@/lib/levels";

/**
 * El banco dorado: perlas sueltas, barras de diez, cuadrados de cien y el
 * cubo de mil.
 *
 * Es el material que hace visible el sistema decimal: mil no es "un uno con
 * tres ceros", es un cubo que pesa en la mano. Primero se compone la cantidad
 * con perlas y solo después se lee el número escrito.
 */
export type ModoBanco = "componer" | "leer";

export interface BancoLevel {
  level: number;
  modo: ModoBanco;
  /** Cuántas cifras tiene el número del nivel. */
  digitos: number;
  /** Admite ceros intermedios (305), que es lo que más cuesta. */
  conCeros: boolean;
  opciones: number;
}

export const BANCO_LEVELS: BancoLevel[] = levels100((_, level) => {
  const etapa = stageOf(level);
  return {
    modo: etapa % 2 === 0 ? "leer" : "componer",
    digitos: phasedInt(level, [1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4]),
    conCeros: etapa >= 6,
    opciones: phasedInt(level, [2, 3, 3, 4, 4, 4, 5, 5, 6, 6, 6]),
  };
});

export const JERARQUIAS = [
  { valor: 1, nombre: "unidad", plural: "unidades", emoji: "\u{1F7E1}", color: "#e3b23c" },
  { valor: 10, nombre: "decena", plural: "decenas", emoji: "\u{1F4CF}", color: "#d9a441" },
  { valor: 100, nombre: "centena", plural: "centenas", emoji: "\u{1F7E8}", color: "#cf9636" },
  { valor: 1000, nombre: "millar", plural: "millares", emoji: "\u{1F9CA}", color: "#c08a2e" },
] as const;
