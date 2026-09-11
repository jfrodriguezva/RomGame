"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import { MITADES_LEVELS, FIGURAS_FRACCION, type FiguraFraccion, type MitadesLevel } from "@/data/levels/mitades";

const CANASTAS = [
  { clave: "entero", nombre: "Entero" },
  { clave: "mitad", nombre: "Mitad" },
];

const RELLENO = "#c98a4b";
const TRAZO = "#8a5a2b";

function Figura({ item, size }: { item: FiguraFraccion; size: number }) {
  if (item.forma === "circulo") {
    return (
      <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
        <circle cx={50} cy={50} r={42} fill="#fff" stroke={TRAZO} strokeWidth={4} />
        {item.estado === "entero" ? (
          <circle cx={50} cy={50} r={42} fill={RELLENO} />
        ) : (
          <path d="M50 8 A42 42 0 0 0 50 92 Z" fill={RELLENO} />
        )}
        <line x1={50} y1={8} x2={50} y2={92} stroke={TRAZO} strokeWidth={2} strokeDasharray="3,3" />
      </svg>
    );
  }
  if (item.forma === "cuadrado") {
    return (
      <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
        <rect x={8} y={8} width={84} height={84} fill="#fff" stroke={TRAZO} strokeWidth={4} />
        <rect
          x={8}
          y={8}
          width={item.estado === "entero" ? 84 : 42}
          height={84}
          fill={RELLENO}
        />
        <line x1={50} y1={8} x2={50} y2={92} stroke={TRAZO} strokeWidth={2} strokeDasharray="3,3" />
      </svg>
    );
  }
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" aria-hidden="true">
      <path d="M50 8 L92 92 H8 Z" fill="#fff" stroke={TRAZO} strokeWidth={4} strokeLinejoin="round" />
      <path d={item.estado === "entero" ? "M50 8 L92 92 H8 Z" : "M50 8 L50 92 H8 Z"} fill={RELLENO} />
      <line x1={50} y1={8} x2={50} y2={92} stroke={TRAZO} strokeWidth={2} strokeDasharray="3,3" />
    </svg>
  );
}

export default function MitadesPage() {
  return (
    <MaterialClasificar<MitadesLevel, FiguraFraccion>
      slug="mitades"
      levels={MITADES_LEVELS}
      consigna={() => "¿Está entera o a la mitad?"}
      disponibles={() => FIGURAS_FRACCION}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.estado}
      keyDe={(item) => item.id}
      render={(item) => <Figura item={item} size={100} />}
      renderChip={(item) => <Figura item={item} size={26} />}
      mensajeError={(item) => `Esa figura está ${item.estado === "entero" ? "entera" : "a la mitad"}`}
    />
  );
}
