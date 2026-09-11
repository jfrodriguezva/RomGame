"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import { CLIMA_LEVELS, ROPA_CLIMA, type RopaClima, type ClimaLevel } from "@/data/levels/clima";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "sol", nombre: "Sol", emoji: "☀️" },
  { clave: "lluvia", nombre: "Lluvia", emoji: "🌧️" },
];

export default function ClimaPage() {
  return (
    <MaterialClasificar<ClimaLevel, RopaClima>
      slug="clima"
      levels={CLIMA_LEVELS}
      consigna={() => "¿Para sol o para lluvia?"}
      disponibles={() => ROPA_CLIMA}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.clima}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} es para ${item.clima === "sol" ? "el sol" : "la lluvia"}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
