"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import { SABOR_LEVELS, COSAS_SABOR, type CosaSabor, type SaborLevel } from "@/data/levels/sabor";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "dulce", nombre: "Dulce" },
  { clave: "salado", nombre: "Salado" },
];

export default function SaborPage() {
  return (
    <MaterialClasificar<SaborLevel, CosaSabor>
      slug="sabor"
      levels={SABOR_LEVELS}
      consigna={() => "¿Dulce o salado?"}
      disponibles={() => COSAS_SABOR}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.sabor}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} es ${item.sabor}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
