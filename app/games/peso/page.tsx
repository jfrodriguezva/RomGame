"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import { PESO_LEVELS, COSAS_PESO, type CosaPeso, type PesoLevel } from "@/data/levels/peso";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "pesado", nombre: "Pesado" },
  { clave: "ligero", nombre: "Ligero" },
];

export default function PesoPage() {
  return (
    <MaterialClasificar<PesoLevel, CosaPeso>
      slug="peso"
      levels={PESO_LEVELS}
      consigna={() => "¿Es pesado o ligero?"}
      disponibles={() => COSAS_PESO}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.peso}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} es ${item.peso}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
