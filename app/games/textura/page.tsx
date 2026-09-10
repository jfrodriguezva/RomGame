"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  TEXTURA_LEVELS,
  OBJETOS_TEXTURA,
  type ObjetoTextura,
  type TexturaLevel,
} from "@/data/levels/textura";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "aspero", nombre: "Áspero" },
  { clave: "liso", nombre: "Liso" },
];

export default function TexturaPage() {
  return (
    <MaterialClasificar<TexturaLevel, ObjetoTextura>
      slug="textura"
      levels={TEXTURA_LEVELS}
      consigna={() => "¿Áspero o liso?"}
      disponibles={() => OBJETOS_TEXTURA}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.textura}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} es ${item.textura === "aspero" ? "áspero" : "liso"}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
