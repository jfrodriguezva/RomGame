import { levels100, phasedInt, stageOf } from "@/lib/levels";
import type { OrdenarLevel } from "./ordenar";

/**
 * Ciclo de vida de la mariposa: huevo -> oruga -> crisálida -> mariposa.
 *
 * Reutiliza el motor de seriación de torre rosa / escalera marrón, pero en
 * vez de tamaño el orden es temporal. El "control del error" es el mismo:
 * la pieza no encaja si no le toca todavía. `invertido` se deja siempre en
 * true para que el componente arme la fila en orden ascendente
 * (huevo -> mariposa); aquí no tiene el sentido de "invertir la serie" que
 * tiene en los materiales de tamaño, es solo cómo pide las piezas el motor
 * genérico. `cercanos` y `desde` no aplican (solo hay 4 etapas posibles).
 */
export interface EtapaMariposa {
  valor: number;
  nombre: string;
}

export const ETAPAS_MARIPOSA: EtapaMariposa[] = [
  { valor: 1, nombre: "Huevo" },
  { valor: 2, nombre: "Oruga" },
  { valor: 3, nombre: "Crisálida" },
  { valor: 4, nombre: "Mariposa" },
];

export function etapaMariposa(valor: number): EtapaMariposa {
  return ETAPAS_MARIPOSA[Math.min(ETAPAS_MARIPOSA.length, Math.max(1, valor)) - 1];
}

export const CICLO_VIDA_LEVELS: OrdenarLevel[] = levels100<OrdenarLevel>((_, level) => {
  const etapa = stageOf(level);
  return {
    cantidad: phasedInt(level, [2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4]),
    invertido: true,
    pista: etapa <= 3,
    cercanos: false,
    desde: 0,
  };
});
