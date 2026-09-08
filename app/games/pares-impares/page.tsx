"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  PARES_IMPARES_LEVELS,
  type CantidadParidad,
  type ParesImparesLevel,
  cantidadesPara,
} from "@/data/levels/pares-impares";

const CANASTAS = [
  { clave: "par", nombre: "Par" },
  { clave: "impar", nombre: "Impar" },
];

/** Dos columnas a propósito: si sobra un punto solo, la cantidad es impar. */
function Puntos({ n }: { n: number }) {
  return (
    <div className="grid grid-cols-2 gap-1.5">
      {Array.from({ length: n }).map((_, i) => (
        <span key={i} className="h-3.5 w-3.5 rounded-full bg-[#8a6a44]" />
      ))}
    </div>
  );
}

export default function ParesImparesPage() {
  return (
    <MaterialClasificar<ParesImparesLevel, CantidadParidad>
      slug="pares-impares"
      levels={PARES_IMPARES_LEVELS}
      consigna={() => "¿Es par o impar?"}
      disponibles={(config) => cantidadesPara(config.maxNumero)}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.paridad}
      keyDe={(item) => String(item.cantidad)}
      render={(item) => <Puntos n={item.cantidad} />}
      renderChip={(item) => <span className="text-sm font-bold">{item.cantidad}</span>}
      mensajeError={(item) => `${item.cantidad} es ${item.paridad}`}
      textoVoz={(item) => `${item.cantidad}, ${item.paridad}`}
      tamanoCaja="h-32 w-32"
    />
  );
}
