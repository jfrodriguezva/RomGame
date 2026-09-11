"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  ESTADOS_AGUA_LEVELS,
  COSAS_ESTADO,
  type CosaEstado,
  type EstadosAguaLevel,
} from "@/data/levels/estados-agua";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "solido", nombre: "Sólido" },
  { clave: "liquido", nombre: "Líquido" },
  { clave: "gas", nombre: "Gas" },
];

export default function EstadosAguaPage() {
  return (
    <MaterialClasificar<EstadosAguaLevel, CosaEstado>
      slug="estados-agua"
      levels={ESTADOS_AGUA_LEVELS}
      consigna={() => "¿Sólido, líquido o gas?"}
      disponibles={() => COSAS_ESTADO}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.estado}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} es ${item.estado === "solido" ? "sólido" : item.estado === "liquido" ? "líquido" : "gas"}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
