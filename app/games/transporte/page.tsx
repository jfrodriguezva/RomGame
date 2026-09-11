"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import { TRANSPORTE_LEVELS, VEHICULOS, type Vehiculo, type TransporteLevel } from "@/data/levels/transporte";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "tierra", nombre: "Tierra", emoji: "🛣️" },
  { clave: "aire", nombre: "Aire", emoji: "☁️" },
  { clave: "agua", nombre: "Agua", emoji: "🌊" },
];

export default function TransportePage() {
  return (
    <MaterialClasificar<TransporteLevel, Vehiculo>
      slug="transporte"
      levels={TRANSPORTE_LEVELS}
      consigna={() => "¿Por dónde se mueve?"}
      disponibles={() => VEHICULOS}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.medio}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} se mueve por ${item.medio === "tierra" ? "tierra" : item.medio === "aire" ? "aire" : "agua"}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
