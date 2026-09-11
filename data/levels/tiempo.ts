/** El tiempo (clima): nomenclatura de condiciones del clima, todas con emoji claro. */
export interface CondicionClima {
  id: string;
  label: string;
  emoji: string;
}

export const CONDICIONES_CLIMA: CondicionClima[] = [
  { id: "soleado", label: "Soleado", emoji: "☀️" },
  { id: "lluvioso", label: "Lluvioso", emoji: "🌧️" },
  { id: "nublado", label: "Nublado", emoji: "☁️" },
  { id: "ventoso", label: "Ventoso", emoji: "💨" },
  { id: "nevado", label: "Nevado", emoji: "❄️" },
  { id: "tormenta", label: "Con tormenta", emoji: "⛈️" },
];
