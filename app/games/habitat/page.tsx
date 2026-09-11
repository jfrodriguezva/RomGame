"use client";

import MaterialClasificar from "@/components/MaterialClasificar";
import {
  HABITAT_LEVELS,
  animalesPara,
  type AnimalHabitat,
  type HabitatLevel,
} from "@/data/levels/habitat";
import { hablar } from "@/lib/speech";

const NOMBRES: Record<AnimalHabitat["habitat"], { nombre: string; emoji: string }> = {
  selva: { nombre: "Selva", emoji: "🌴" },
  desierto: { nombre: "Desierto", emoji: "🏜️" },
  oceano: { nombre: "Océano", emoji: "🌊" },
  polo: { nombre: "Polo", emoji: "🧊" },
};

export default function HabitatPage() {
  return (
    <MaterialClasificar<HabitatLevel, AnimalHabitat>
      slug="habitat"
      levels={HABITAT_LEVELS}
      consigna={() => "¿Dónde vive este animal?"}
      disponibles={(config) => animalesPara(config.activos)}
      cantidadPorRonda={(config) => config.cantidad}
      canastas={(config) => config.activos.map((h) => ({ clave: h, ...NOMBRES[h] }))}
      claveDe={(item) => item.habitat}
      keyDe={(item) => item.nombre}
      render={(item) => <span className="text-7xl">{item.emoji}</span>}
      renderChip={(item) => <span>{item.emoji}</span>}
      mensajeError={(item) => `${item.nombre} vive en el ${NOMBRES[item.habitat].nombre.toLowerCase()}`}
      textoVoz={(item) => item.nombre}
      onEscuchar={(item) => hablar(item.nombre)}
    />
  );
}
