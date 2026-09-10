"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  MAYUSCULAS_LEVELS,
  letrasPara,
  type LetraCaso,
  type MayusculasLevel,
} from "@/data/levels/mayusculas";

const CANASTAS = [
  { clave: "mayuscula", nombre: "Mayúscula" },
  { clave: "minuscula", nombre: "minúscula" },
];

function textoDe(item: LetraCaso): string {
  return item.caso === "mayuscula" ? item.letra : item.letra.toLowerCase();
}

export default function MayusculasPage() {
  return (
    <MaterialClasificar<MayusculasLevel, LetraCaso>
      slug="mayusculas"
      levels={MAYUSCULAS_LEVELS}
      consigna={() => "¿Mayúscula o minúscula?"}
      disponibles={(config) => letrasPara(config.letras)}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.caso}
      keyDe={(item) => `${item.letra}-${item.caso}`}
      render={(item) => (
        <span className="text-7xl font-black text-stone-700">{textoDe(item)}</span>
      )}
      renderChip={(item) => <span className="font-black">{textoDe(item)}</span>}
      mensajeError={(item) => `Esa es ${item.caso === "mayuscula" ? "mayúscula" : "minúscula"}`}
    />
  );
}
