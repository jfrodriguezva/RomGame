/** Instrumentos musicales: nomenclatura, todos con emoji claro. */
export interface InstrumentoDef {
  id: string;
  label: string;
  emoji: string;
}

export const INSTRUMENTOS: InstrumentoDef[] = [
  { id: "tambor", label: "El tambor", emoji: "🥁" },
  { id: "guitarra", label: "La guitarra", emoji: "🎸" },
  { id: "piano", label: "El piano", emoji: "🎹" },
  { id: "trompeta", label: "La trompeta", emoji: "🎺" },
  { id: "violin", label: "El violín", emoji: "🎻" },
];
