"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  FRUTA_VERDURA_LEVELS,
  ALIMENTOS,
  type AlimentoTipo,
  type FrutaVerduraLevel,
} from "@/data/levels/fruta-verdura";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "fruta", nombre: "Fruta" },
  { clave: "verdura", nombre: "Verdura" },
];

export default function FrutaVerduraPage() {
  return (
    <MaterialClasificar<FrutaVerduraLevel, AlimentoTipo>
      slug="fruta-verdura"
      levels={FRUTA_VERDURA_LEVELS}
      consigna={() => "¿Es fruta o verdura?"}
      disponibles={() => ALIMENTOS}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.tipo}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} es ${item.tipo}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
