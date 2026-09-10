"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  DIA_NOCHE_LEVELS,
  COSAS_DIA_NOCHE,
  type CosaDiaNoche,
  type DiaNocheLevel,
} from "@/data/levels/dia-noche";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "dia", nombre: "Día", emoji: "☀️" },
  { clave: "noche", nombre: "Noche", emoji: "🌙" },
];

export default function DiaNochePage() {
  return (
    <MaterialClasificar<DiaNocheLevel, CosaDiaNoche>
      slug="dia-noche"
      levels={DIA_NOCHE_LEVELS}
      consigna={() => "¿Es de día o de noche?"}
      disponibles={() => COSAS_DIA_NOCHE}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.momento}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} es de ${item.momento === "dia" ? "día" : "noche"}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
