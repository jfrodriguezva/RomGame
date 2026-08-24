import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface Personaje {
  id: string;
  emoji: string;
  sombrero: boolean;
  lentes: boolean;
  barba: boolean;
  nino: boolean;
}

export const PERSONAJES: Personaje[] = [
  { id: "p1", emoji: "🤓", sombrero: false, lentes: true, barba: false, nino: false },
  { id: "p2", emoji: "😎", sombrero: false, lentes: true, barba: false, nino: false },
  { id: "p3", emoji: "👶", sombrero: false, lentes: false, barba: false, nino: true },
  { id: "p4", emoji: "👦", sombrero: false, lentes: false, barba: false, nino: true },
  { id: "p5", emoji: "👧", sombrero: false, lentes: false, barba: false, nino: true },
  { id: "p6", emoji: "👴", sombrero: false, lentes: false, barba: false, nino: false },
  { id: "p7", emoji: "👵", sombrero: false, lentes: false, barba: false, nino: false },
  { id: "p8", emoji: "🧔", sombrero: false, lentes: false, barba: true, nino: false },
  { id: "p9", emoji: "🤠", sombrero: true, lentes: false, barba: false, nino: false },
  { id: "p10", emoji: "👳", sombrero: true, lentes: false, barba: false, nino: false },
  { id: "p11", emoji: "👲", sombrero: true, lentes: false, barba: false, nino: false },
  { id: "p12", emoji: "🎅", sombrero: true, lentes: false, barba: true, nino: false },
  { id: "p13", emoji: "🧑‍🚀", sombrero: true, lentes: false, barba: false, nino: false },
  { id: "p14", emoji: "🧑‍🎨", sombrero: true, lentes: false, barba: false, nino: false },
  { id: "p15", emoji: "🧑‍🍳", sombrero: true, lentes: false, barba: false, nino: false },
  { id: "p16", emoji: "🧙", sombrero: true, lentes: false, barba: true, nino: false },
  { id: "p17", emoji: "🥷", sombrero: false, lentes: false, barba: false, nino: false },
  { id: "p18", emoji: "🧑‍🌾", sombrero: true, lentes: false, barba: false, nino: false },
  { id: "p19", emoji: "🧛", sombrero: false, lentes: false, barba: false, nino: false },
  { id: "p20", emoji: "🧑‍🔬", sombrero: false, lentes: true, barba: false, nino: false },
];

export const PREGUNTAS: { id: keyof Personaje; label: string }[] = [
  { id: "sombrero", label: "¿Usa sombrero?" },
  { id: "lentes", label: "¿Usa lentes?" },
  { id: "barba", label: "¿Tiene barba?" },
  { id: "nino", label: "¿Es un niño o niña?" },
];

export interface AdivinaLevel {
  level: number;
  poolSize: number;
}

export const ADIVINA_LEVELS: AdivinaLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  poolSize: lerpInt(4, PERSONAJES.length, t),
}));
