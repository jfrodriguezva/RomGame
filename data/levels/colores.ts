/** Los colores: nomenclatura clásica de las tabletas de color. */
export interface ColorDef {
  id: string;
  label: string;
  hex: string;
}

export const COLORES: ColorDef[] = [
  { id: "rojo", label: "Rojo", hex: "#d9483f" },
  { id: "azul", label: "Azul", hex: "#3f6fd9" },
  { id: "amarillo", label: "Amarillo", hex: "#e8c94a" },
  { id: "verde", label: "Verde", hex: "#4f9d5c" },
  { id: "naranja", label: "Naranja", hex: "#e08a3c" },
  { id: "morado", label: "Morado", hex: "#8a5fc9" },
  { id: "rosa", label: "Rosa", hex: "#e08aa8" },
  { id: "cafe", label: "Café", hex: "#8a5a2b" },
  { id: "negro", label: "Negro", hex: "#3a332c" },
  { id: "blanco", label: "Blanco", hex: "#fdfaf5" },
];
