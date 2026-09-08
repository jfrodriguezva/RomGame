/**
 * Formas de tierra y agua: nomenclatura clásica de geografía Montessori.
 *
 * Cada término describe una relación entre tierra y agua (isla = tierra
 * rodeada de agua; lago = agua rodeada de tierra...). Se dibujan como
 * diagramas planos de dos colores, igual que el gabinete de geometría: lo
 * que importa es la relación de las formas, no una ilustración realista.
 */
export interface FormaTierraAguaDef {
  id: string;
  label: string;
}

export const FORMAS_TIERRA_AGUA: FormaTierraAguaDef[] = [
  // Las más simples: una sola forma rodeada del otro elemento
  { id: "isla", label: "Isla" },
  { id: "lago", label: "Lago" },
  // Tierra que se mete en el agua
  { id: "peninsula", label: "Península" },
  { id: "cabo", label: "Cabo" },
  // Relaciones más finas
  { id: "golfo", label: "Golfo" },
  { id: "estrecho", label: "Estrecho" },
  { id: "istmo", label: "Istmo" },
  { id: "archipielago", label: "Archipiélago" },
];
