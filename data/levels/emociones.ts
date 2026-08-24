import { generateLevels, lerpInt, DEFAULT_LEVEL_COUNT } from "@/lib/levels";

export interface EmocionDef {
  id: string;
  label: string;
  emoji: string;
}

export const EMOCIONES: EmocionDef[] = [
  { id: "feliz", label: "Feliz", emoji: "😊" },
  { id: "triste", label: "Triste", emoji: "😢" },
  { id: "enojado", label: "Enojado", emoji: "😠" },
  { id: "asustado", label: "Asustado", emoji: "😨" },
  { id: "cansado", label: "Cansado", emoji: "😴" },
  { id: "sorprendido", label: "Sorprendido", emoji: "😲" },
  { id: "amoroso", label: "Cariñoso", emoji: "🥰" },
  { id: "riendo", label: "Riendo", emoji: "😂" },
  { id: "penoso", label: "Apenado", emoji: "😳" },
  { id: "aburrido", label: "Aburrido", emoji: "😑" },
  { id: "orgulloso", label: "Orgulloso", emoji: "😌" },
  { id: "confundido", label: "Confundido", emoji: "😕" },
];

export interface EmocionesLevel {
  level: number;
  options: number;
}

export const EMOCIONES_LEVELS: EmocionesLevel[] = generateLevels(DEFAULT_LEVEL_COUNT, (t) => ({
  options: lerpInt(3, EMOCIONES.length, t),
}));
