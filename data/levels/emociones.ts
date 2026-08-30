import { QUIZ_LEVELS, type QuizLevel } from "./quiz";

/**
 * Gracia y cortesía: poner nombre a lo que se siente.
 *
 * Las familias agrupan emociones cercanas (triste / decepcionado / solo). En
 * las últimas etapas el material solo ofrece opciones de la misma familia, que
 * es cuando el niño empieza a distinguir matices y no solo opuestos.
 */
export type FamiliaEmocion = "alegres" | "tristes" | "enojo" | "miedo" | "calma" | "sorpresa";

export interface EmocionDef {
  id: string;
  label: string;
  emoji: string;
  familia: FamiliaEmocion;
}

export const EMOCIONES: EmocionDef[] = [
  // Primero los contrastes grandes: son los que un nino de tres anos distingue
  { id: "feliz", label: "Feliz", emoji: "\u{1F60A}", familia: "alegres" },
  { id: "triste", label: "Triste", emoji: "\u{1F622}", familia: "tristes" },
  { id: "enojado", label: "Enojado", emoji: "\u{1F620}", familia: "enojo" },
  { id: "asustado", label: "Asustado", emoji: "\u{1F628}", familia: "miedo" },
  { id: "cansado", label: "Cansado", emoji: "\u{1F634}", familia: "calma" },
  { id: "sorprendido", label: "Sorprendido", emoji: "\u{1F632}", familia: "sorpresa" },

  // Despues los matices dentro de cada familia
  { id: "riendo", label: "Riendo", emoji: "\u{1F602}", familia: "alegres" },
  { id: "amoroso", label: "Cari\u00f1oso", emoji: "\u{1F970}", familia: "alegres" },
  { id: "llorando", label: "Llorando", emoji: "\u{1F62D}", familia: "tristes" },
  { id: "tranquilo", label: "Tranquilo", emoji: "\u{1F60C}", familia: "calma" },
  { id: "furioso", label: "Furioso", emoji: "\u{1F621}", familia: "enojo" },
  { id: "penoso", label: "Apenado", emoji: "\u{1F633}", familia: "miedo" },
  { id: "aburrido", label: "Aburrido", emoji: "\u{1F611}", familia: "calma" },
  { id: "confundido", label: "Confundido", emoji: "\u{1F615}", familia: "sorpresa" },
  { id: "emocionado", label: "Emocionado", emoji: "\u{1F929}", familia: "alegres" },
  { id: "decepcionado", label: "Decepcionado", emoji: "\u{1F61E}", familia: "tristes" },
  { id: "orgulloso", label: "Orgulloso", emoji: "\u{1F60E}", familia: "alegres" },
  { id: "nervioso", label: "Nervioso", emoji: "\u{1F630}", familia: "miedo" },
  { id: "solo", label: "Solito", emoji: "\u{1F97A}", familia: "tristes" },
  { id: "fastidiado", label: "Fastidiado", emoji: "\u{1F624}", familia: "enojo" },
  { id: "curioso", label: "Curioso", emoji: "\u{1F9D0}", familia: "sorpresa" },
  { id: "pensativo", label: "Pensativo", emoji: "\u{1F914}", familia: "calma" },
  { id: "agradecido", label: "Agradecido", emoji: "\u{1F64F}", familia: "alegres" },
];

export type EmocionesLevel = QuizLevel;
export const EMOCIONES_LEVELS: EmocionesLevel[] = QUIZ_LEVELS;
