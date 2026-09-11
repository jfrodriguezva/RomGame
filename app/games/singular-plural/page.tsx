"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  SINGULAR_PLURAL_LEVELS,
  PALABRAS_NUMERO,
  type PalabraNumero,
  type SingularPluralLevel,
} from "@/data/levels/singular-plural";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "singular", nombre: "Uno" },
  { clave: "plural", nombre: "Varios" },
];

export default function SingularPluralPage() {
  return (
    <MaterialClasificar<SingularPluralLevel, PalabraNumero>
      slug="singular-plural"
      levels={SINGULAR_PLURAL_LEVELS}
      consigna={() => "¿Uno o varios?"}
      disponibles={() => PALABRAS_NUMERO}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.forma}
      keyDe={(item) => item.nombre}
      render={(item) => (
        <div className="flex flex-col items-center gap-2">
          <span className="text-5xl">{item.emoji}</span>
          <span className="text-2xl font-extrabold text-stone-700">{item.nombre}</span>
        </div>
      )}
      renderChip={(item) => <span className="text-xs font-bold">{item.nombre}</span>}
      mensajeError={(item) => `"${item.nombre}" es ${item.forma === "singular" ? "singular" : "plural"}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
      tamanoCaja="h-40 w-44"
    />
  );
}
