export interface VestirItem {
  id: string;
  emoji: string;
  slot: "corona" | "vestido" | "zapatos" | "accesorio";
  setId: number;
}

export interface VestirSet {
  id: number;
  label: string;
  starsRequired: number;
}

export const VESTIR_SETS: VestirSet[] = [
  { id: 1, label: "Set inicial", starsRequired: 0 },
  { id: 2, label: "Set mágico", starsRequired: 3 },
  { id: 3, label: "Set real", starsRequired: 8 },
];

export const VESTIR_ITEMS: VestirItem[] = [
  // Set 1 - siempre disponible
  { id: "corona-1", emoji: "👑", slot: "corona", setId: 1 },
  { id: "corona-2", emoji: "🎀", slot: "corona", setId: 1 },
  { id: "vestido-1", emoji: "👗", slot: "vestido", setId: 1 },
  { id: "vestido-2", emoji: "🥻", slot: "vestido", setId: 1 },
  { id: "zapatos-1", emoji: "👠", slot: "zapatos", setId: 1 },
  { id: "zapatos-2", emoji: "🥿", slot: "zapatos", setId: 1 },
  { id: "accesorio-1", emoji: "💎", slot: "accesorio", setId: 1 },
  { id: "accesorio-2", emoji: "🧣", slot: "accesorio", setId: 1 },
  // Set 2 - se desbloquea con estrellas
  { id: "corona-3", emoji: "🌸", slot: "corona", setId: 2 },
  { id: "corona-4", emoji: "✨", slot: "corona", setId: 2 },
  { id: "vestido-3", emoji: "🩱", slot: "vestido", setId: 2 },
  { id: "vestido-4", emoji: "👘", slot: "vestido", setId: 2 },
  { id: "zapatos-3", emoji: "👢", slot: "zapatos", setId: 2 },
  { id: "zapatos-4", emoji: "🩰", slot: "zapatos", setId: 2 },
  { id: "accesorio-3", emoji: "🕶️", slot: "accesorio", setId: 2 },
  { id: "accesorio-4", emoji: "👛", slot: "accesorio", setId: 2 },
  // Set 3 - se desbloquea con más estrellas
  { id: "corona-5", emoji: "👸", slot: "corona", setId: 3 },
  { id: "corona-6", emoji: "🦄", slot: "corona", setId: 3 },
  { id: "vestido-5", emoji: "🧚", slot: "vestido", setId: 3 },
  { id: "vestido-6", emoji: "🐚", slot: "vestido", setId: 3 },
  { id: "zapatos-5", emoji: "💫", slot: "zapatos", setId: 3 },
  { id: "zapatos-6", emoji: "🌟", slot: "zapatos", setId: 3 },
  { id: "accesorio-5", emoji: "🪄", slot: "accesorio", setId: 3 },
  { id: "accesorio-6", emoji: "📿", slot: "accesorio", setId: 3 },
];

export const VESTIR_SLOTS: VestirItem["slot"][] = [
  "corona",
  "vestido",
  "zapatos",
  "accesorio",
];

export interface VestirBackground {
  id: string;
  label: string;
  emoji: string;
  from: string;
  to: string;
}

export const VESTIR_BACKGROUNDS: VestirBackground[] = [
  { id: "castillo", label: "Castillo", emoji: "🏰", from: "from-violet-200", to: "to-indigo-100" },
  { id: "jardin", label: "Jardín", emoji: "🌷", from: "from-lime-200", to: "to-emerald-100" },
  { id: "playa", label: "Playa", emoji: "🏖️", from: "from-cyan-200", to: "to-sky-100" },
  { id: "noche", label: "Noche estrellada", emoji: "🌙", from: "from-indigo-300", to: "to-purple-200" },
];
