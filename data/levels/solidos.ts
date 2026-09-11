/** Cuerpos geométricos: los sólidos geométricos, en versión plana con sombreado. */
export interface SolidoDef {
  id: string;
  label: string;
}

export const SOLIDOS: SolidoDef[] = [
  { id: "esfera", label: "Esfera" },
  { id: "cubo", label: "Cubo" },
  { id: "cono", label: "Cono" },
  { id: "cilindro", label: "Cilindro" },
  { id: "piramide", label: "Pirámide" },
];
