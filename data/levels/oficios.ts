/** Oficios y profesiones: nomenclatura clásica de cultura Montessori. */
export interface OficioDef {
  id: string;
  label: string;
  emoji: string;
}

export const OFICIOS: OficioDef[] = [
  { id: "bombero", label: "El bombero", emoji: "👨‍🚒" },
  { id: "doctora", label: "La doctora", emoji: "👩‍⚕️" },
  { id: "cocinero", label: "El cocinero", emoji: "👨‍🍳" },
  { id: "maestra", label: "La maestra", emoji: "👩‍🏫" },
  { id: "policia", label: "El policía", emoji: "👮" },
  { id: "granjera", label: "La granjera", emoji: "👩‍🌾" },
];
