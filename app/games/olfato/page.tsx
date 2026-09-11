"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import { OLFATO_LEVELS, COSAS_OLOR, type CosaOlor, type OlfatoLevel } from "@/data/levels/olfato";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "bien", nombre: "Huele bien" },
  { clave: "mal", nombre: "Huele mal" },
];

export default function OlfatoPage() {
  return (
    <MaterialClasificar<OlfatoLevel, CosaOlor>
      slug="olfato"
      levels={OLFATO_LEVELS}
      consigna={() => "¿Huele bien o mal?"}
      disponibles={() => COSAS_OLOR}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.olor}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} huele ${item.olor === "bien" ? "bien" : "mal"}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
