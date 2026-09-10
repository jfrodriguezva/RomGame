/**
 * Los cinco sentidos: nomenclatura simple, tres periodos vía MaterialQuiz.
 * Todos tienen emoji claro, no hace falta dibujar nada a mano.
 */
export interface SentidoDef {
  id: string;
  label: string;
  emoji: string;
}

export const SENTIDOS: SentidoDef[] = [
  { id: "vista", label: "La vista", emoji: "👁️" },
  { id: "oido", label: "El oído", emoji: "👂" },
  { id: "olfato", label: "El olfato", emoji: "👃" },
  { id: "gusto", label: "El gusto", emoji: "👅" },
  { id: "tacto", label: "El tacto", emoji: "✋" },
];
