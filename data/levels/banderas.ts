/**
 * Banderas del mundo: material clásico de geografía. Se dibujan planas, sin
 * escudo ni detalle — el color y el patrón son lo que hay que reconocer,
 * igual que en el material real impreso.
 */
export interface BanderaDef {
  id: string;
  label: string;
}

export const BANDERAS: BanderaDef[] = [
  { id: "mexico", label: "México" },
  { id: "espana", label: "España" },
  { id: "argentina", label: "Argentina" },
  { id: "brasil", label: "Brasil" },
  { id: "francia", label: "Francia" },
  { id: "japon", label: "Japón" },
];
