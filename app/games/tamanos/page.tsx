"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  TAMANOS_LEVELS,
  COSAS_TAMANO,
  type CosaTamano,
  type TamanosLevel,
} from "@/data/levels/tamanos";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "grande", nombre: "Grande" },
  { clave: "mediano", nombre: "Mediano" },
  { clave: "chico", nombre: "Chico" },
];

export default function TamanosPage() {
  return (
    <MaterialClasificar<TamanosLevel, CosaTamano>
      slug="tamanos"
      levels={TAMANOS_LEVELS}
      consigna={() => "¿Es grande, mediano o chico?"}
      disponibles={() => COSAS_TAMANO}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.tamano}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} es ${item.tamano}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
