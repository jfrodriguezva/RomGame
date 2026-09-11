"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  TEMPERATURA_LEVELS,
  COSAS_TEMPERATURA,
  type CosaTemperatura,
  type TemperaturaLevel,
} from "@/data/levels/temperatura";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "caliente", nombre: "Caliente" },
  { clave: "frio", nombre: "Frío" },
];

export default function TemperaturaPage() {
  return (
    <MaterialClasificar<TemperaturaLevel, CosaTemperatura>
      slug="temperatura"
      levels={TEMPERATURA_LEVELS}
      consigna={() => "¿Está caliente o frío?"}
      disponibles={() => COSAS_TEMPERATURA}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.temperatura}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} está ${item.temperatura === "caliente" ? "caliente" : "frío"}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
