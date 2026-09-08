import { levels100, phasedInt } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/**
 * Doblar la tela: extendida -> a la mitad -> en cuarto -> guardada.
 *
 * Reutiliza el motor de seriación (como el ciclo de la mariposa), pero el
 * orden es una secuencia motriz real, no tamaño ni tiempo. El "pista"
 * queda siempre visible: este material enseña un gesto, no pide recordar
 * un nombre de memoria.
 */
export interface EtapaDoblado {
  valor: number;
  nombre: string;
  ancho: number;
  alto: number;
}

export const ETAPAS_DOBLADO: EtapaDoblado[] = [
  { valor: 1, nombre: "Extendida", ancho: 92, alto: 52 },
  { valor: 2, nombre: "A la mitad", ancho: 46, alto: 52 },
  { valor: 3, nombre: "En cuarto", ancho: 46, alto: 26 },
  { valor: 4, nombre: "Guardada", ancho: 24, alto: 24 },
];

export function etapaDoblado(valor: number): EtapaDoblado {
  return ETAPAS_DOBLADO[Math.min(ETAPAS_DOBLADO.length, Math.max(1, valor)) - 1];
}

export const DOBLAR_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => ({
  cantidad: phasedInt(level, [2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4]),
  invertido: true,
  pista: true,
  cercanos: false,
  desde: 0,
}));
