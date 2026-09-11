"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import { RIMAS_LEVELS, PALABRAS_RIMA, type PalabraRima, type RimasLevel } from "@/data/levels/rimas";
import { hablar } from "@/lib/speech";

const CANASTAS = [
  { clave: "ato", nombre: 'Rima con "gato"' },
  { clave: "on", nombre: 'Rima con "ratón"' },
];

export default function RimasPage() {
  return (
    <MaterialClasificar<RimasLevel, PalabraRima>
      slug="rimas"
      levels={RIMAS_LEVELS}
      consigna={() => "¿Con cuál rima?"}
      disponibles={() => PALABRAS_RIMA}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={() => CANASTAS}
      claveDe={(item) => item.familia}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} no rima con eso`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
